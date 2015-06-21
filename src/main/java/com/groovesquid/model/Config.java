package com.groovesquid.model;

import com.groovesquid.Groovesquid;
import com.groovesquid.util.FilenameSchemeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Config {

    public enum DownloadComplete {
        DO_NOTHING, OPEN_FILE, OPEN_DIRECTORY, ADD_ITUNES, ADD_ITUNES_REMOVE;
        public static String[] names() {
            DownloadComplete[] states = values();
            String[] names = new String[states.length];

            for (int i = 0; i < states.length; i++) {
                names[i] = states[i].name();
            }

            return names;
        }
    }

    public enum FileExists {
        DO_NOTHING, OVERWRITE, RENAME;
        public static String[] names() {
            FileExists[] states = values();
            String[] names = new String[states.length];

            for (int i = 0; i < states.length; i++) {
                names[i] = states[i].name();
            }

            return names;
        }
    }

    public enum Hoster {
        DEEZER, NETEASE, SOUNDCLOUD;

        public static String[] names() {
            Hoster[] states = values();
            String[] names = new String[states.length];

            for (int i = 0; i < states.length; i++) {
                names[i] = states[i].name();
            }

            return names;
        }
    }

    public enum StartTab {
        HOME, SEARCH;

        public static String[] names() {
            StartTab[] states = values();
            String[] names = new String[states.length];

            for (int i = 0; i < states.length; i++) {
                names[i] = states[i].name();
            }

            return names;
        }
    }
    
    private String version, originalVersion;
    private List<Track> downloads, originalDownloads;
    private String downloadDirectory, originalDownloadDirectory;
    private int maxParallelDownloads, originalMaxParallelDownloads;
    private String fileNameScheme, originalFileNameScheme;
    private boolean autocompleteEnabled, originalAutocompleteEnabled;
    private int downloadComplete, originalDownloadComplete;
    private int startTab, originalStartTab;
    private int fileExists, originalFileExists;
    private int firstTopTableType = 3, secondTopTableType = 0;
    private String locale, originalLocale;
    private String proxyHost, originalProxyHost;
    private Integer proxyPort, originalProxyPort;
    private String preferredHoster, originalPreferredHoster;

    public Config() {
        originalVersion = Groovesquid.getVersion();
        originalDownloads = new ArrayList<Track>();
        originalDownloadDirectory = System.getProperty("user.home");
        originalMaxParallelDownloads = 10;
        originalFileNameScheme = FilenameSchemeParser.DEFAULT_FILENAME_SCHEME;
        originalAutocompleteEnabled = false;
        originalDownloadComplete = DownloadComplete.DO_NOTHING.ordinal();
        originalStartTab = StartTab.HOME.ordinal();
        originalFileExists = FileExists.RENAME.ordinal();
        originalLocale = Locale.getDefault().toString();
        originalProxyHost = null;
        originalProxyPort = null;
        resetSettings();
    }
    
    public final void resetSettings() {
        version = originalVersion;
        downloads = originalDownloads;
        downloadDirectory = originalDownloadDirectory;
        maxParallelDownloads = originalMaxParallelDownloads;
        fileNameScheme = originalFileNameScheme;
        autocompleteEnabled = originalAutocompleteEnabled;
        downloadComplete = originalDownloadComplete;
        startTab = originalStartTab;
        fileExists = originalFileExists;
        locale = originalLocale;
        proxyHost = originalProxyHost;
        proxyPort = originalProxyPort;
    }
    
    public synchronized List<Track> getDownloads() {
        return downloads;
    }

    public synchronized void setDownloads(List<Track> downloads) {
        this.downloads = downloads;
        Groovesquid.saveConfig();
    }
    
    public synchronized String getDownloadDirectory() {
        return downloadDirectory;
    }
    
    public synchronized void setDownloadDirectory(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
        Groovesquid.saveConfig();
    }

    public synchronized int getMaxParallelDownloads() {
        return maxParallelDownloads;
    }
    
    public synchronized void setMaxParallelDownloads(int maxParallelDownloads) {
        this.maxParallelDownloads = maxParallelDownloads;
        Groovesquid.saveConfig();
    }
    
    public synchronized void setFileNameScheme(String fileNameScheme) {
        this.fileNameScheme = fileNameScheme;
        Groovesquid.saveConfig();
    }
    
    public synchronized String getFileNameScheme() {
        return fileNameScheme;
    }

    public boolean getAutocompleteEnabled() {
        return autocompleteEnabled;
    }

    public void setAutocompleteEnabled(boolean autocompleteEnabled) {
        this.autocompleteEnabled = autocompleteEnabled;
    }
    
    public int getDownloadComplete() {
        return downloadComplete;
    }
    
    public int getFileExists() {
        return fileExists;
    }
    
    public void setDownloadComplete(int downloadComplete) {
        this.downloadComplete = downloadComplete;
    }
    
    public String getLocale() {
        return locale;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
        Groovesquid.saveConfig();
    }
    
    public String getProxyHost() {
        return proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public Integer getProxyPort() {
        return proxyPort;
    }
    
    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setStartTab(int startTab) {
        this.startTab = startTab;
    }

    public int getStartTab() {
        return startTab;
    }

    public int getFirstTopTableType() {
        return firstTopTableType;
    }

    public void setFirstTopTableType(int firstTopTableType) {
        this.firstTopTableType = firstTopTableType;
        Groovesquid.saveConfig();
    }

    public int getSecondTopTableType() {
        return secondTopTableType;
    }

    public void setSecondTopTableType(int secondTopTableType) {
        this.secondTopTableType = secondTopTableType;
        Groovesquid.saveConfig();
    }
}
