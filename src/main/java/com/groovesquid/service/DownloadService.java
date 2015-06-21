package com.groovesquid.service;

import com.groovesquid.Groovesquid;
import com.groovesquid.model.*;
import com.groovesquid.service.hoster.Deezer;
import com.groovesquid.service.hoster.Hoster;
import com.groovesquid.service.hoster.Netease;
import com.groovesquid.service.hoster.Soundcloud;
import com.groovesquid.util.FilenameSchemeParser;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class DownloadService extends HttpService {

    private final static Logger log = Logger.getLogger(Groovesquid.class.getName());

    private final ExecutorService executorService;
    private final ExecutorService executorServiceForPlay;
    private final List<DownloadTask> currentlyRunningDownloads = new ArrayList<DownloadTask>();
    private final FilenameSchemeParser filenameSchemeParser;
    private List<Class> hosters = new ArrayList<Class>();

    private long nextSongMustSleepUntil;

    public DownloadService() {
        executorService = Executors.newFixedThreadPool(Groovesquid.getConfig().getMaxParallelDownloads());
        executorServiceForPlay = Executors.newFixedThreadPool(1);
        filenameSchemeParser = new FilenameSchemeParser();

        hosters.add(Deezer.class);
        hosters.add(Netease.class);
        hosters.add(Soundcloud.class);
    }

    public FilenameSchemeParser getFilenameSchemeParser() {
        return filenameSchemeParser;
    }

    public synchronized Track download(Song song) {
        return download(song, null);
    }

    public synchronized Track download(Song song, DownloadListener downloadListener) {
        File downloadDir = new File(Groovesquid.getConfig().getDownloadDirectory());
        String fileName = filenameSchemeParser.parse(song, Groovesquid.getConfig().getFileNameScheme());
        Store store = new FileStore(fileName, downloadDir);
        song.setDownloaded(true);
        return download(song, store, downloadListener, false);
    }

    public synchronized Track downloadToMemory(Song song) {
        return downloadToMemory(song, null);
    }

    public synchronized Track downloadToMemory(Song song, DownloadListener downloadListener) {
        Store store = new MemoryStore(song.toString());
        return download(song, store, downloadListener, true);
    }

    private Track download(Song song, Store store, DownloadListener downloadListener, boolean forPlay) {
        Track track = new Track(song, store);
        int additionalAbortDelay = 0;
        boolean downloadWasInterrupted = cancelDownload(track, true);
        if (downloadWasInterrupted && !forPlay)
            additionalAbortDelay += 5000;
        additionalAbortDelay += Math.max(nextSongMustSleepUntil - System.currentTimeMillis(), 0);
        DownloadTask downloadTask = new DownloadTask(track, additionalAbortDelay, downloadListener);
        currentlyRunningDownloads.add(downloadTask);
        if (forPlay) {
            executorServiceForPlay.submit(downloadTask);
        } else {
            executorService.submit(downloadTask);
        }
        nextSongMustSleepUntil = Math.max(System.currentTimeMillis(), nextSongMustSleepUntil + 1000);
        return track;
    }
    
    public synchronized boolean cancelDownload(Track track, boolean deleteStore) {
        return cancelDownload(track, deleteStore, false);
    }

    public synchronized boolean cancelDownload(Track track, boolean deleteStore, boolean safeDelete) {
        DownloadTask downloadTask = findDownloadTask(track);
        if(safeDelete) {
            if(downloadTask == null) {
                downloadTask = new DownloadTask(track, 0, null);
                currentlyRunningDownloads.add(downloadTask);
            }
        }
        return cancelDownload(downloadTask, deleteStore);
    }

    private synchronized boolean cancelDownload(DownloadTask downloadTask, boolean deleteStore) {
        boolean downloadWasInterrupted = false;
        if (downloadTask != null) {
            currentlyRunningDownloads.remove(downloadTask);
            downloadWasInterrupted = downloadTask.abort();
            if (deleteStore) {
                if(downloadTask.track.getStore() == null) {
                    File downloadDir = new File(Groovesquid.getConfig().getDownloadDirectory());
                    Store store = new FileStore(downloadTask.track.getPath(), downloadDir);
                    downloadTask.track.setStore(store);
                }
                downloadTask.track.getStore().deleteStore();
            }
            downloadTask.track.setStatus(Track.Status.CANCELLED);
            downloadTask.fireDownloadStatusChanged();
        }
        return downloadWasInterrupted;
    }

    private DownloadTask findDownloadTask(Track track) {
        for (DownloadTask downloadTask : currentlyRunningDownloads) {
            if (downloadTask.track.getStore().isSameLocation(track.getStore())) {
                return downloadTask;
            }
        }
        return null;
    }

    public void shutdown() {
        executorService.shutdownNow();
        executorServiceForPlay.shutdownNow();
        ArrayList<DownloadTask> downloadsCopy = new ArrayList<DownloadTask>(currentlyRunningDownloads);
        for (DownloadTask downloadTask : downloadsCopy) {
            cancelDownload(downloadTask, true);
        }
    }
    
    public boolean areCurrentlyRunningDownloads() {
        return currentlyRunningDownloads.size() > 0;
    }


    public class DownloadTask implements Runnable {
        private final Track track;
        private final int initialDelay;
        private final DownloadListener downloadListener;
        private volatile HttpGet httpGet;
        private volatile boolean aborted;

        public DownloadTask(Track track, int initialDelay, DownloadListener downloadListener) {
            this.track = track;
            this.initialDelay = initialDelay;
            this.downloadListener = downloadListener;
        }

        public void run() {
            try {
                if (track.getStatus() == Track.Status.CANCELLED)
                    return;
                Thread.sleep(initialDelay);
                if (track.getStatus() == Track.Status.CANCELLED)
                    return;

                for (Class hosterClass : hosters) {
                    Hoster hoster = (Hoster) hosterClass.getConstructor().newInstance();

                    if (track.getStatus() == Track.Status.FINISHED) {
                        break;
                    }
                    track.setStatus(Track.Status.INITIALIZING);
                    fireDownloadStatusChanged();

                    String downloadUrl = hoster.getDownloadUrl(track);
                    if (downloadUrl != null) {
                        try {
                            track.setHoster(hoster.getName());
                            track.setDownloadUrl(downloadUrl);

                            track.setStatus(Track.Status.DOWNLOADING);
                            track.setStartDownloadTime(System.currentTimeMillis());
                            fireDownloadStatusChanged();

                            hoster.download(track, new MonitoredOutputStream(track.getStore().getOutputStream()));

                            track.updateDuration();

                            track.setStatus(Track.Status.FINISHED);
                            fireDownloadStatusChanged();
                            log.info("download completed: " + track.toString() + " (" + hoster.getName() + ")");

                            break;
                        } catch (Exception ex) {
                            log.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }

            } catch (Exception ex) {
                if (aborted || ex instanceof InterruptedException) {
                    log.info("cancel download by request: " + track);
                    track.setStatus(Track.Status.CANCELLED);
                } else {
                    log.log(Level.SEVERE, "error download track " + track, ex);
                    track.setStatus(Track.Status.ERROR);
                    //track.setFault(ex);
                }
                track.getStore().deleteStore();
                fireDownloadStatusChanged();
            } finally {
                track.setStopDownloadTime(System.currentTimeMillis());
                synchronized (DownloadService.this) {
                    currentlyRunningDownloads.remove(this);
                }
                synchronized (this) {
                    httpGet = null;
                }
            }
        }

        public synchronized boolean abort() {
            if (httpGet != null) {
                aborted = true;
                httpGet.abort();
                return true;
            }
            return false;
        }

        private void fireDownloadStatusChanged() {
            if (downloadListener != null)
                downloadListener.statusChanged(track);
        }

        private void fireDownloadBytesChanged() {
            if (downloadListener != null)
                downloadListener.downloadedBytesChanged(track);
        }

        public class MonitoredOutputStream extends OutputStream {
            private final OutputStream outputStream;

            public MonitoredOutputStream(OutputStream outputStream) {
                this.outputStream = outputStream;
            }

            @Override
            public void close() throws IOException {
                outputStream.close();
            }

            @Override
            public void flush() throws IOException {
                outputStream.flush();
            }

            @Override
            public void write(byte[] b) throws IOException {
                outputStream.write(b);
                track.incDownloadedBytes(b.length);
                fireDownloadBytesChanged();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                outputStream.write(b, off, len);
                track.incDownloadedBytes(len);
                fireDownloadBytesChanged();
            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
                track.incDownloadedBytes(1);
                fireDownloadBytesChanged();
            }
        }
    }
}
