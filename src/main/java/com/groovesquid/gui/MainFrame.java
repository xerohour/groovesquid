package com.groovesquid.gui;

import com.apple.eawt.*;
import com.groovesquid.Groovesquid;
import com.groovesquid.gui.style.Style;
import com.groovesquid.model.*;
import com.groovesquid.model.Config.DownloadComplete;
import com.groovesquid.service.DownloadListener;
import com.groovesquid.service.PlayService;
import com.groovesquid.service.PlaybackListener;
import com.groovesquid.util.GuiUtils;
import com.groovesquid.util.I18n;
import org.divxdede.swing.busy.BusyModel;
import org.divxdede.swing.busy.BusySwingWorker;
import org.divxdede.swing.busy.JBusyComponent;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.rollover.RolloverProducer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class MainFrame extends JFrame {

    private Style style;

    protected JLabel albumCoverLabel;
    protected JLabel currentDurationLabel;
    protected JLabel currentlyPlayingSongLabel, currentlyPlayingArtistLabel;
    protected JPanel downloadPanel;
    protected JScrollPane downloadScrollPane;
    protected JTable downloadTable;
    protected JPopupMenu downloadTablePopupMenu;
    protected JMenu selectMenu;
    protected JMenuItem selectAllMenuItem;
    protected JMenuItem selectCompletedMenuItem;
    protected JMenuItem selectFailedMenuItem;
    protected JLabel durationLabel;
    protected JMenuBar menuBar;
    protected JMenu fileMenu;
    protected JMenu editMenu;
    protected JMenuItem openDirectoryMenuItem;
    protected JMenuItem openFileMenuItem;
    protected JMenuItem downloadSongMenuItem, downloadAlbumMenuItem, downloadArtistSongsMenuItem;
    protected JMenuItem playSongMenuItem, playAlbumMenuItem, playArtistSongsMenuItem;
    protected JMenuItem showAlbumSongsMenuItem, showArtistSongsMenuItem, showArtistAlbumsMenuItem;
    protected JButton playPauseButton;
    protected JButton nextButton;
    protected JButton previousButton;
    protected JPanel playerPanel;
    protected JMenuItem removeFromDiskMenuItem;
    protected JMenuItem removeFromListMenuItem;
    protected JMenuItem retryFailedDownloadsMenuItem;
    protected JButton searchButton;
    protected JPanel searchPanel;
    protected JScrollPane searchScrollPane;
    protected JBusyComponent<JScrollPane> busySearchScrollPane;
    protected JTable searchTable;
    protected JPopupMenu songTablePopupMenu;
    protected JPopupMenu albumTablePopupMenu;
    protected JPopupMenu artistTablePopupMenu;
    protected JTextField searchTextField;
    protected JComboBox searchTypeComboBox;
    protected JSlider trackSlider;
    protected JLabel volumeOffLabel;
    protected JLabel volumeOnLabel;
    protected JSlider volumeSlider;
    protected JEditorPane adPane;
    protected JTabbedPane tabbedPane;
    protected JPanel homePanel;
    protected JTable homeFirstTopTable;
    protected JTable homeSecondTopTable;
    protected JScrollPane homeFirstTopScrollPane;
    protected JBusyComponent<JScrollPane> busyFirstTopScrollPane;
    protected JScrollPane homeSecondTopScrollPane;
    protected JBusyComponent<JScrollPane> busySecondTopScrollPane;
    protected JComboBox homeFirstTopComboBox;
    protected JComboBox homeSecondTopComboBox;
    protected List<String> homeTopSources = new ArrayList<String>() {{
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "Billboard"));
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "Beatport"));
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "Hypem"));
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (US)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (UK)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (DE)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (FR)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (IT)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (ES)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (AU)");
        add(String.format(I18n.getLocaleString("TOP_SONGS"), "iTunes") + " (BR)");
    }};
    
    /**
     * Creates new form GUI
     */
    public MainFrame() {
        style = Groovesquid.getStyle();

        loadResources();
        initComponents();

        // title
        setTitle("Groovesquid");

        // center screen
        setLocationRelativeTo(null);

        setVisible(true);

        // background fix
        getContentPane().setBackground(getBackground());

        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        // tables
        ((DownloadTableModel) downloadTable.getModel()).setSongDownloads(Groovesquid.getConfig().getDownloads());

        Groovesquid.getPlayService().setListener(playbackListener);
    }

    protected void loadResources() {

    }

    protected void initComponents() {
        setBackground(style.getMainFrameBackground());

        downloadSongMenuItem = new JMenuItem(I18n.getLocaleString("DOWNLOAD"));
        downloadSongMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadMenuItemActionPerformed(evt);
            }
        });

        downloadAlbumMenuItem = new JMenuItem(I18n.getLocaleString("DOWNLOAD"));
        downloadAlbumMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadMenuItemActionPerformed(evt);
            }
        });

        downloadArtistSongsMenuItem = new JMenuItem(I18n.getLocaleString("DOWNLOAD"));
        downloadArtistSongsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downloadMenuItemActionPerformed(evt);
            }
        });

        playSongMenuItem = new JMenuItem(I18n.getLocaleString("PLAY"));
        playSongMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playMenuItemActionPerformed(evt);
            }
        });

        playAlbumMenuItem = new JMenuItem(I18n.getLocaleString("PLAY"));
        playAlbumMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playMenuItemActionPerformed(evt);
            }
        });

        playArtistSongsMenuItem = new JMenuItem(I18n.getLocaleString("PLAY"));
        playArtistSongsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playMenuItemActionPerformed(evt);
            }
        });

        showAlbumSongsMenuItem = new JMenuItem(I18n.getLocaleString("SHOW_SONGS"));
        showAlbumSongsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showSongsMenuItemActionPerformed(evt);
            }
        });

        showArtistSongsMenuItem = new JMenuItem(I18n.getLocaleString("SHOW_SONGS"));
        showArtistSongsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showSongsMenuItemActionPerformed(evt);
            }
        });

        showArtistAlbumsMenuItem = new JMenuItem(I18n.getLocaleString("SHOW_ALBUMS"));
        showArtistAlbumsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showAlbumsMenuItemActionPerformed(evt);
            }
        });

        songTablePopupMenu = new JPopupMenu();
        songTablePopupMenu.add(downloadSongMenuItem);
        songTablePopupMenu.add(playSongMenuItem);

        albumTablePopupMenu = new JPopupMenu();
        albumTablePopupMenu.add(showAlbumSongsMenuItem);
        albumTablePopupMenu.addSeparator();
        albumTablePopupMenu.add(downloadAlbumMenuItem);
        albumTablePopupMenu.add(playAlbumMenuItem);

        artistTablePopupMenu = new JPopupMenu();
        artistTablePopupMenu.add(showArtistAlbumsMenuItem);
        artistTablePopupMenu.add(showArtistSongsMenuItem);
        artistTablePopupMenu.addSeparator();
        artistTablePopupMenu.add(downloadArtistSongsMenuItem);
        artistTablePopupMenu.add(playArtistSongsMenuItem);

        removeFromListMenuItem = new JMenuItem(I18n.getLocaleString("REMOVE_FROM_LIST"));
        removeFromListMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeFromListMenuItemActionPerformed(evt);
            }
        });
        removeFromListMenuItem.setAccelerator(KeyStroke.getKeyStroke("BACK_SPACE"));

        removeFromDiskMenuItem = new JMenuItem(I18n.getLocaleString("REMOVE_FROM_DISK"));
        removeFromDiskMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeFromDiskMenuItemActionPerformed(evt);
            }
        });
        removeFromDiskMenuItem.setAccelerator(KeyStroke.getKeyStroke("BACK_SPACE"));

        openFileMenuItem = new JMenuItem(I18n.getLocaleString("OPEN_FILE"));
        openFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openFileMenuItemActionPerformed(evt);
            }
        });

        openDirectoryMenuItem = new JMenuItem(I18n.getLocaleString("OPEN_DIRECTORY"));
        openDirectoryMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openDirectoryMenuItemActionPerformed(evt);
            }
        });

        retryFailedDownloadsMenuItem = new JMenuItem(I18n.getLocaleString("RETRY_FAILED_DOWNLOADS"));
        retryFailedDownloadsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                retryFailedDownloadsMenuItemActionPerformed(evt);
            }
        });

        selectAllMenuItem = new JMenuItem(I18n.getLocaleString("ALL"));
        selectAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                selectAllMenuItemActionPerformed(evt);
            }
        });

        selectCompletedMenuItem = new JMenuItem(I18n.getLocaleString("COMPLETED"));
        selectCompletedMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                selectCompletedMenuItemActionPerformed(evt);
            }
        });

        selectFailedMenuItem = new JMenuItem(I18n.getLocaleString("FAILED"));
        selectFailedMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                selectFailedMenuItemActionPerformed(evt);
            }
        });

        selectMenu = new JMenu(I18n.getLocaleString("SELECT"));
        selectMenu.add(selectAllMenuItem);
        selectMenu.add(selectCompletedMenuItem);
        selectMenu.add(selectFailedMenuItem);

        downloadTablePopupMenu = new JPopupMenu();
        downloadTablePopupMenu.add(removeFromListMenuItem);
        downloadTablePopupMenu.add(removeFromDiskMenuItem);
        downloadTablePopupMenu.addSeparator();
        downloadTablePopupMenu.add(openFileMenuItem);
        downloadTablePopupMenu.add(openDirectoryMenuItem);
        downloadTablePopupMenu.addSeparator();
        downloadTablePopupMenu.add(selectMenu);
        downloadTablePopupMenu.addSeparator();
        downloadTablePopupMenu.add(retryFailedDownloadsMenuItem);

        playerPanel = new JPanel();
        playerPanel.setBackground(style.getPlayerPanelBackground());
        playerPanel.setForeground(style.getPlayerPanelForeground());
        playerPanel.setPreferredSize(new Dimension(673, 60));

        playPauseButton = new JButton();
        playPauseButton.setIcon(style.getPlayIcon());
        playPauseButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        playPauseButton.setContentAreaFilled(false);
        playPauseButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        playPauseButton.setFocusable(false);
        playPauseButton.setRequestFocusEnabled(false);
        playPauseButton.setVerticalTextPosition(SwingConstants.TOP);
        playPauseButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                playPauseButtonMousePressed(evt);
            }
        });
        playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playPauseButtonActionPerformed(evt);
            }
        });

        currentDurationLabel = new JLabel("0:00");
        currentDurationLabel.setFont(new Font(currentDurationLabel.getFont().getName(), Font.PLAIN, 10));
        currentDurationLabel.setOpaque(false);
        currentDurationLabel.setForeground(style.getPlayerPanelForeground());
        currentDurationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        currentlyPlayingSongLabel = new JLabel("");
        currentlyPlayingSongLabel.setFont(currentlyPlayingSongLabel.getFont().deriveFont(Font.BOLD, 13));
        currentlyPlayingSongLabel.setForeground(style.getPlayerPanelForeground());
        currentlyPlayingSongLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        currentlyPlayingArtistLabel = new JLabel("");
        currentlyPlayingArtistLabel.setFont(currentlyPlayingSongLabel.getFont().deriveFont(Font.PLAIN, 12));
        currentlyPlayingArtistLabel.setForeground(style.getPlayerPanelForeground());
        currentlyPlayingArtistLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        trackSlider = new JSlider();
        trackSlider.setUI(style.getSliderUI(trackSlider, 11));
        trackSlider.setOpaque(false);
        trackSlider.setValue(0);
        trackSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        trackSlider.setEnabled(false);
        trackSlider.setFocusable(false);
        trackSlider.setRequestFocusEnabled(false);
        trackSlider.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                trackSliderMouseDragged(evt);
            }
        });

        durationLabel = new JLabel("00:00");
        durationLabel.setFont(new Font(durationLabel.getFont().getName(), Font.PLAIN, 10));
        durationLabel.setForeground(style.getPlayerPanelForeground());

        albumCoverLabel = new JLabel();
        albumCoverLabel.setOpaque(false);

        volumeSlider = new JSlider();
        volumeSlider.setUI(style.getSliderUI(volumeSlider, 7));
        volumeSlider.setOpaque(false);
        volumeSlider.setMaximum(6);
        volumeSlider.setMinimum(-80);
        volumeSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        volumeSlider.setFocusable(false);
        volumeSlider.setRequestFocusEnabled(false);
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                volumeSliderStateChanged(evt);
            }
        });

        volumeOffLabel = new JLabel();
        volumeOffLabel.setIcon(style.getVolumeOffIcon());

        volumeOnLabel = new JLabel();
        volumeOnLabel.setIcon(style.getVolumeOnIcon());

        previousButton = new JButton();
        previousButton.setIcon(style.getPreviousIcon());
        previousButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        previousButton.setContentAreaFilled(false);
        previousButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        previousButton.setFocusable(false);
        previousButton.setRequestFocusEnabled(false);
        previousButton.setVerticalTextPosition(SwingConstants.TOP);
        previousButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                previousButtonMousePressed(evt);
            }
        });
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        nextButton = new JButton();
        nextButton.setIcon(style.getNextIcon());
        nextButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        nextButton.setContentAreaFilled(false);
        nextButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        nextButton.setFocusable(false);
        nextButton.setRequestFocusEnabled(false);
        nextButton.setVerticalTextPosition(SwingConstants.TOP);
        nextButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                nextButtonMousePressed(evt);
            }
        });
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        GroupLayout playerPanelLayout = new GroupLayout(playerPanel);
        playerPanel.setLayout(playerPanelLayout);
        playerPanelLayout.setHorizontalGroup(
                playerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(playerPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGap(18, 18, 18)
                                .addComponent(previousButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playPauseButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addGroup(playerPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(currentlyPlayingSongLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(currentlyPlayingArtistLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(volumeOffLabel, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(volumeOnLabel, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(currentDurationLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(trackSlider, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(durationLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(albumCoverLabel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
        );
        playerPanelLayout.setVerticalGroup(
                playerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(previousButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(playPauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nextButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(playerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(currentlyPlayingSongLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(currentlyPlayingArtistLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
                        .addGroup(GroupLayout.Alignment.TRAILING, playerPanelLayout.createSequentialGroup()
                                .addGroup(playerPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(currentDurationLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(volumeOnLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(volumeOffLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(trackSlider, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                                        .addComponent(volumeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(durationLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(1, 1, 1)
                        .addComponent(albumCoverLabel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        homePanel = new JPanel();
        homePanel.setOpaque(false);

        DefaultComboBoxModel homeFirstTopComboBoxModel = new DefaultComboBoxModel(homeTopSources.toArray());
        DefaultComboBoxModel homeSecondTopComboBoxModel = new DefaultComboBoxModel(homeTopSources.toArray());

        homeFirstTopComboBoxModel.setSelectedItem(homeTopSources.get(Groovesquid.getConfig().getFirstTopTableType()));
        homeSecondTopComboBoxModel.setSelectedItem(homeTopSources.get(Groovesquid.getConfig().getSecondTopTableType()));

        homeFirstTopComboBox = new JComboBox();
        homeFirstTopComboBox.setFont(new Font(homeFirstTopComboBox.getFont().getName(), Font.PLAIN, 12));
        homeFirstTopComboBox.setUI(style.getSearchTypeComboBoxUI(homeFirstTopComboBox));
        homeFirstTopComboBox.setModel(homeFirstTopComboBoxModel);
        homeFirstTopComboBox.setBorder(style.getSearchTypeComboBoxBorder());
        homeFirstTopComboBox.setFocusable(false);
        homeFirstTopComboBox.setEnabled(false);
        homeFirstTopComboBox.setRequestFocusEnabled(false);
        homeFirstTopComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                homeTopComboBoxActionPerformed(evt);
            }
        });

        homeSecondTopComboBox = new JComboBox();
        homeSecondTopComboBox.setFont(new Font(homeSecondTopComboBox.getFont().getName(), Font.PLAIN, 12));
        homeSecondTopComboBox.setUI(style.getSearchTypeComboBoxUI(homeSecondTopComboBox));
        homeSecondTopComboBox.setModel(homeSecondTopComboBoxModel);
        homeSecondTopComboBox.setBorder(style.getSearchTypeComboBoxBorder());
        homeSecondTopComboBox.setFocusable(false);
        homeSecondTopComboBox.setEnabled(false);
        homeSecondTopComboBox.setRequestFocusEnabled(false);
        homeSecondTopComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                homeTopComboBoxActionPerformed(evt);
            }
        });

        homeFirstTopTable = new SquidTable();
        homeFirstTopTable.setModel(new TopSongTableModel());
        homeFirstTopTable.setFillsViewportHeight(true);
        homeFirstTopTable.setGridColor(new Color(204, 204, 204));
        homeFirstTopTable.setIntercellSpacing(new Dimension(0, 0));
        homeFirstTopTable.setRowMargin(1);
        homeFirstTopTable.setRowHeight(22);
        homeFirstTopTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        homeFirstTopTable.setShowHorizontalLines(false);
        homeFirstTopTable.setShowVerticalLines(false);
        homeFirstTopTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        homeFirstTopTable.setSelectionForeground(style.getSearchTableSelectionForeground());
        homeFirstTopTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tableMousePressed(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                tableMouseReleased(evt);
            }
        });

        homeFirstTopScrollPane = new JScrollPane();
        homeFirstTopScrollPane.getVerticalScrollBar().setUI(style.getSearchScrollBarUI(homeFirstTopScrollPane.getVerticalScrollBar()));
        homeFirstTopScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        homeFirstTopScrollPane.setOpaque(false);
        homeFirstTopScrollPane.setViewportView(homeFirstTopTable);

        busyFirstTopScrollPane = new JBusyComponent<JScrollPane>(homeFirstTopScrollPane);

        homeTopComboBoxActionPerformed(new ActionEvent(homeFirstTopComboBox, 0, null));

        homeSecondTopTable = new SquidTable();
        homeSecondTopTable.setModel(new TopSongTableModel());
        homeSecondTopTable.setFillsViewportHeight(true);
        homeSecondTopTable.setGridColor(new Color(204, 204, 204));
        homeSecondTopTable.setIntercellSpacing(new Dimension(0, 0));
        homeSecondTopTable.setRowMargin(1);
        homeSecondTopTable.setRowHeight(22);
        homeSecondTopTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        homeSecondTopTable.setShowHorizontalLines(false);
        homeSecondTopTable.setShowVerticalLines(false);
        homeSecondTopTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        homeSecondTopTable.setSelectionForeground(style.getSearchTableSelectionForeground());
        homeSecondTopTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tableMousePressed(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                tableMouseReleased(evt);
            }
        });

        homeSecondTopScrollPane = new JScrollPane();
        homeSecondTopScrollPane.getVerticalScrollBar().setUI(style.getSearchScrollBarUI(homeSecondTopScrollPane.getVerticalScrollBar()));
        homeSecondTopScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        homeSecondTopScrollPane.setOpaque(false);
        homeSecondTopScrollPane.setViewportView(homeSecondTopTable);

        busySecondTopScrollPane = new JBusyComponent<JScrollPane>(homeSecondTopScrollPane);

        homeTopComboBoxActionPerformed(new ActionEvent(homeSecondTopComboBox, 0, null));

        adPane = new JEditorPane();
        adPane.setOpaque(false);
        adPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        adPane.setEditable(false);
        adPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
            }
        });

        GroupLayout homePanelLayout = new GroupLayout(homePanel);
        homePanel.setLayout(homePanelLayout);

        homePanelLayout.setAutoCreateGaps(true);
        homePanelLayout.setAutoCreateContainerGaps(true);

        homePanelLayout.setHorizontalGroup(homePanelLayout.createSequentialGroup()
                        .addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                //.addComponent(adPane)
                                .addGroup(homePanelLayout.createSequentialGroup()
                                        .addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(homeFirstTopComboBox)
                                                .addComponent(busyFirstTopScrollPane))
                                        .addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(homeSecondTopComboBox)
                                                .addComponent(busySecondTopScrollPane))))
        );

        homePanelLayout.setVerticalGroup(homePanelLayout.createSequentialGroup()
                        .addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(homePanelLayout.createSequentialGroup()
                                        .addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(homeFirstTopComboBox)
                                                .addComponent(homeSecondTopComboBox))
                                        .addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(busyFirstTopScrollPane)
                                                .addComponent(busySecondTopScrollPane))))
                        /*.addGroup(homePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(adPane, 90, 90, 90))*/
        );

        searchPanel = new JPanel();
        searchPanel.setOpaque(false);

        searchTable = new SquidTable();
        if (style.getSearchTableHeaderCellRendererColor() != null) {
            searchTable.getTableHeader().setDefaultRenderer(new TableHeaderCellRenderer(searchTable.getTableHeader().getDefaultRenderer(), style.getSearchTableHeaderCellRendererColor()));
        }
        searchTable.setAutoCreateRowSorter(true);
        searchTable.setModel(new SongSearchTableModel());
        searchTable.setFillsViewportHeight(true);
        searchTable.setGridColor(new Color(204, 204, 204));
        searchTable.setIntercellSpacing(new Dimension(0, 0));
        searchTable.setRowMargin(1);
        searchTable.setRowHeight(22);
        searchTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        searchTable.setShowHorizontalLines(false);
        searchTable.setShowVerticalLines(false);
        searchTable.getTableHeader().setReorderingAllowed(false);
        searchTable.setSelectionBackground(style.getSearchTableSelectionBackground());
        searchTable.setSelectionForeground(style.getSearchTableSelectionForeground());
        searchTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tableMousePressed(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                tableMouseReleased(evt);
            }
        });
        AbstractHyperlinkAction<Object> act = new AbstractHyperlinkAction<Object>() {
            public void actionPerformed(ActionEvent ev) {
                if (target instanceof Artist) {
                    BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                        @Override
                        protected List<Song> doInBackground() {
                            List<Song> songs = new ArrayList<Song>();
                            songs.addAll(Groovesquid.getSearchService().getSongsByArtist((Artist) target));
                            return songs;
                        }

                        @Override
                        protected void done() {
                            try {
                                searchTable.setModel(new SongSearchTableModel(get()));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                } else if (target instanceof Album) {
                    BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                        @Override
                        protected List<Song> doInBackground() {
                            List<Song> songs = new ArrayList<Song>();
                            songs.addAll(Groovesquid.getSearchService().getSongsByAlbum((Album) target));
                            return songs;
                        }

                        @Override
                        protected void done() {
                            try {
                                searchTable.setModel(new SongSearchTableModel(get()));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                }
            }
        };
        HyperlinkProvider hp = new HyperlinkProvider(act) {
            @Override
            protected JXHyperlink createRendererComponent() {
                JXRendererHyperlink renderer = new JXRendererHyperlink();
                renderer.setUnclickedColor(Color.BLACK);
                return renderer;
            }

            @Override
            protected void configureState(CellContext context) {
                if (context.getComponent() == null) {
                    return;
                }
                Point p = (Point) context.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
                if (p != null && (p.x >= 0)
                        && (p.x == context.getColumn()) && (p.y == context.getRow())
                        && SquidTable.isRolloverText((JXTable) context.getComponent(), p.y, p.x)
                        && !rendererComponent.getModel().isRollover()) {
                    rendererComponent.getModel().setRollover(true);
                } else if (rendererComponent.getModel().isRollover()) {
                    rendererComponent.getModel().setRollover(false);
                }
            }
        };
        searchTable.setDefaultRenderer(Album.class, new DefaultTableRenderer(hp));
        searchTable.setDefaultRenderer(Artist.class, new DefaultTableRenderer(hp));

        searchScrollPane = new JScrollPane();
        searchScrollPane.getVerticalScrollBar().setUI(style.getSearchScrollBarUI(searchScrollPane.getVerticalScrollBar()));
        searchScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        searchScrollPane.setOpaque(false);
        searchScrollPane.setViewportView(searchTable);

        busySearchScrollPane = new JBusyComponent<JScrollPane>(searchScrollPane);

        searchTypeComboBox = new JComboBox();
        searchTypeComboBox.setFont(new Font(searchTypeComboBox.getFont().getName(), Font.PLAIN, 12));
        searchTypeComboBox.setUI(style.getSearchTypeComboBoxUI(searchTypeComboBox));
        DefaultComboBoxModel searchTypeComboBoxModel = new DefaultComboBoxModel();
        searchTypeComboBox.setModel(searchTypeComboBoxModel);
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("SONGS"));
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("ALBUMS"));
        searchTypeComboBoxModel.addElement(I18n.getLocaleString("ARTISTS"));
        searchTypeComboBox.setBorder(style.getSearchTypeComboBoxBorder());
        searchTypeComboBox.setFocusable(false);
        searchTypeComboBox.setPreferredSize(new Dimension(63, 26));
        searchTypeComboBox.setRequestFocusEnabled(false);
        searchTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchTypeComboBoxActionPerformed(evt);
            }
        });

        searchButton = new JButton(I18n.getLocaleString("SEARCH"));
        searchButton.setFont(new Font(searchButton.getFont().getName(), Font.PLAIN, 12));
        if (style.usesButtonBackgrounds()) {
            searchButton.setIcon(GuiUtils.stretchImage(style.getSearchButtonsBackground(), 90, 27, this));
            searchButton.setRolloverIcon(GuiUtils.stretchImage(style.getSearchButtonsHoverBackground(), 90, 27, this));
            searchButton.setPressedIcon(GuiUtils.stretchImage(style.getSearchButtonsPressedBackground(), 90, 27, this));
            searchButton.setBorder(null);
            searchButton.setBorderPainted(false);
            searchButton.setContentAreaFilled(false);
            searchButton.setForeground(style.getSearchButtonsForeground());
        }
        searchButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        searchButton.setFocusable(false);
        searchButton.setHorizontalTextPosition(SwingConstants.CENTER);
        searchButton.setPreferredSize(new Dimension(90, 27));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        searchTextField = new JTextField();
        searchTextField.setFont(new Font(searchTextField.getFont().getName(), Font.PLAIN, 12));
        searchTextField.setUI(style.getSearchTextFieldUI(searchTextField));
        if (style.getSearchTextFieldBorder() != null) {
            searchTextField.setBorder(style.getSearchTextFieldBorder());
        }
        searchTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchTextFieldActionPerformed(evt);
            }
        });
        searchTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                searchTextFieldKeyReleased(evt);
            }
        });

        GroupLayout searchPanelLayout = new GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
                searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(busySearchScrollPane, GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
                                        .addGroup(searchPanelLayout.createSequentialGroup()
                                                .addComponent(searchTypeComboBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(searchTextField))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
                searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(searchTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(busySearchScrollPane, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addContainerGap())
        );

        downloadPanel = new JPanel();
        downloadPanel.setOpaque(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        tabbedPane.addTab(I18n.getLocaleString("HOME"), null, homePanel, null);
        tabbedPane.addTab(I18n.getLocaleString("SEARCH"), null, searchPanel, null);
        tabbedPane.addTab(I18n.getLocaleString("DOWNLOADS"), null, downloadPanel, null);
        if (Groovesquid.getConfig().getStartTab() == Config.StartTab.SEARCH.ordinal()) {
            tabbedPane.setSelectedIndex(1);
        }

        downloadScrollPane = new JScrollPane();
        downloadScrollPane.getVerticalScrollBar().setUI(style.getDownloadScrollBarUI(downloadScrollPane.getVerticalScrollBar()));
        downloadScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        downloadScrollPane.setOpaque(false);

        downloadTable = new SquidTable();
        if (style.getDownloadTableHeaderCellRendererColor() != null) {
            downloadTable.getTableHeader().setDefaultRenderer(new TableHeaderCellRenderer(downloadTable.getTableHeader().getDefaultRenderer(), style.getDownloadTableHeaderCellRendererColor()));
        }
        downloadTable.setAutoCreateRowSorter(true);
        downloadTable.setModel(new DownloadTableModel());
        downloadTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        downloadTable.setFillsViewportHeight(true);
        downloadTable.setGridColor(new Color(204, 204, 204));
        downloadTable.setIntercellSpacing(new Dimension(0, 0));
        downloadTable.setRowMargin(1);
        downloadTable.setRowHeight(22);
        downloadTable.setSelectionBackground(style.getDownloadTableSelectionBackground());
        downloadTable.setSelectionForeground(style.getDownloadTableSelectionForeground());
        downloadTable.setShowHorizontalLines(false);
        downloadTable.setShowVerticalLines(false);
        downloadTable.getTableHeader().setReorderingAllowed(false);
        downloadTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tableMousePressed(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                tableMouseReleased(evt);
            }
        });
        downloadTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                downloadTableKeyReleased(evt);
            }
        });
        downloadScrollPane.setViewportView(downloadTable);

        GroupLayout downloadPanelLayout = new GroupLayout(downloadPanel);
        downloadPanel.setLayout(downloadPanelLayout);

        downloadPanelLayout.setAutoCreateGaps(true);
        downloadPanelLayout.setAutoCreateContainerGaps(true);

        downloadPanelLayout.setHorizontalGroup(
                downloadPanelLayout.createSequentialGroup()
                        .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(adPane)
                        .addGroup(downloadPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(downloadScrollPane, GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE))
                                .addContainerGap()))
        );
        downloadPanelLayout.setVerticalGroup(downloadPanelLayout.createSequentialGroup()
                        .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(downloadPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(downloadScrollPane, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                                        .addContainerGap()))
                        .addGroup(downloadPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(adPane, 90, 90, 90))
        );

        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        setJMenuBar(menuBar);

        fileMenu = new JMenu(I18n.getLocaleString("FILE"));
        editMenu = new JMenu(I18n.getLocaleString("EDIT"));
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        if (GuiUtils.getSystem() == GuiUtils.OperatingSystem.WINDOWS || GuiUtils.getSystem() == GuiUtils.OperatingSystem.LINUX) {
            JMenuItem aboutMenuItem = new JMenuItem(I18n.getLocaleString("ABOUT"));
            aboutMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Groovesquid.getAboutFrame().setVisible(true);
                }
            });
            fileMenu.add(aboutMenuItem);

            JMenuItem batchMenuItem = new JMenuItem(I18n.getLocaleString("BATCH"));
            batchMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new BatchFrame().setVisible(true);
                }
            });
            fileMenu.add(batchMenuItem);

            JMenuItem quitMenuItem = new JMenuItem(I18n.getLocaleString("QUIT"));
            quitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    formWindowClosing(null);
                }
            });
            fileMenu.add(quitMenuItem);

            JMenuItem settingsMenuItem = new JMenuItem(I18n.getLocaleString("SETTINGS"));
            settingsMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Groovesquid.getSettingsFrame().setVisible(true);
                }
            });
            editMenu.add(settingsMenuItem);

        } else if (GuiUtils.getSystem() == GuiUtils.OperatingSystem.MAC) {
            JMenuItem batchMenuItem = new JMenuItem(I18n.getLocaleString("BATCH"));
            batchMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new BatchFrame().setVisible(true);
                }
            });
            fileMenu.add(batchMenuItem);

            Application.getApplication().setAboutHandler(new AboutHandler() {
                public void handleAbout(AppEvent.AboutEvent aboutEvent) {
                    Groovesquid.getAboutFrame().setVisible(true);
                }
            });

            Application.getApplication().setPreferencesHandler(new PreferencesHandler() {
                public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
                    Groovesquid.getSettingsFrame().setVisible(true);
                }
            });

            Application.getApplication().setQuitHandler(new QuitHandler() {
                public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
                    Groovesquid.getMainFrame().formWindowClosing(null);
                }
            });
        }

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(playerPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tabbedPane)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPane))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
        );

        setMinimumSize(new Dimension(600, 300));

        pack();
    }

    private void homeTopComboBoxActionPerformed(ActionEvent evt) {
        final JComboBox comboBox = (JComboBox) evt.getSource();

        BusyModel busyModel = null;
        if (comboBox.equals(homeFirstTopComboBox)) {
            busyModel = busyFirstTopScrollPane.getBusyModel();
        } else if (comboBox.equals(homeSecondTopComboBox)) {
            busyModel = busySecondTopScrollPane.getBusyModel();
        }

        new BusySwingWorker<List<Song>, Void>(busyModel) {
            @Override
            protected List<Song> doInBackground() {
                comboBox.setEnabled(false);
                if (comboBox.getSelectedItem().equals(homeTopSources.get(0))) {
                    return Groovesquid.getSearchService().getTopBillboardSongs();
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(1))) {
                    return Groovesquid.getSearchService().getTopBeatportSongs();
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(2))) {
                    return Groovesquid.getSearchService().getTopHypemSongs();
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(3))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("US");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(4))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("UK");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(5))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("DE");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(6))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("FR");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(7))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("IT");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(8))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("ES");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(9))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("AU");
                } else if (comboBox.getSelectedItem().equals(homeTopSources.get(10))) {
                    return Groovesquid.getSearchService().getTopItunesSongs("BR");
                }
                return new ArrayList<Song>();
            }

            @Override
            protected void done() {
                try {
                    comboBox.setEnabled(true);
                    if (comboBox.equals(homeFirstTopComboBox)) {
                        homeFirstTopTable.setModel(new TopSongTableModel(get()));
                        Groovesquid.getConfig().setFirstTopTableType(comboBox.getSelectedIndex());
                    } else if (comboBox.equals(homeSecondTopComboBox)) {
                        homeSecondTopTable.setModel(new TopSongTableModel(get()));
                        Groovesquid.getConfig().setSecondTopTableType(comboBox.getSelectedIndex());
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.execute();
    }

    private final PlaybackListener playbackListener = new PlaybackListener() {
        public void playbackStarted(Track track) {
            playPauseButton.setIcon(style.getPauseIcon());
        }

        public void playbackPaused(Track track, int audioPosition) {
            playPauseButton.setIcon(style.getPlayIcon());
        }

        public void playbackFinished(Track track, int audioPosition) {
            resetPlayInfo();
        }

        public void positionChanged(Track track, int audioPosition) {
            trackSlider.setValue(audioPosition);
            String currentPos = String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(audioPosition), TimeUnit.MILLISECONDS.toSeconds(audioPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(audioPosition)));
            currentDurationLabel.setText(currentPos);
        }

        public void exception(Track track, Exception ex) {

        }

        public void statusChanged(Track track) {
            if (track.getStatus() == Track.Status.ERROR) {
                resetPlayInfo();
                Groovesquid.getPlayService().stop();
            } else if (track.getStatus() == Track.Status.INITIALIZING) {
                updateCurrentlyPlayingTrack(track);
            } else if (track.getStatus() == Track.Status.DOWNLOADING || track.getStatus() == Track.Status.FINISHED) {
                trackSlider.setMaximum(Long.valueOf(track.getSong().getDuration()).intValue());
                durationLabel.setText(track.getSong().getReadableDuration());
            }
        }

        public void downloadedBytesChanged(Track track) {

        }

        private void updateCurrentlyPlayingTrack(final Track track) {
            currentlyPlayingSongLabel.setText(track.getSong().getName());
            currentlyPlayingArtistLabel.setText(track.getSong().getArtistNames());
            trackSlider.setEnabled(true);
            trackSlider.setMaximum(Long.valueOf(track.getSong().getDuration()).intValue());
            durationLabel.setText(track.getSong().getReadableDuration());

            SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>() {
                @Override
                protected Image doInBackground() {
                    return Groovesquid.getSearchService().getAlbumCover(track.getSong().getAlbum());
                }

                @Override
                protected void done() {
                    try {
                        Image img = get();
                        if (img != null) {
                            img = img.getScaledInstance(albumCoverLabel.getSize().width, albumCoverLabel.getSize().height, Image.SCALE_AREA_AVERAGING);
                            albumCoverLabel.setIcon(new ImageIcon(img));
                        } else {
                            
                            albumCoverLabel.setIcon(null);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            };
            worker.execute();
        }
    };

    public void playMenuItemActionPerformed(ActionEvent evt) {
        JTable table;
        if (evt.getSource() instanceof JMenuItem) {
            table = (JTable) ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).getInvoker();
        } else if (evt.getSource() instanceof JTable) {
            table = (JTable) evt.getSource();
        } else {
            table = searchTable;
        }
        int[] selectedRows = table.getSelectedRows();

        if (table.getModel() instanceof SongSearchTableModel || table.getModel() instanceof TopSongTableModel) {
            List<Song> songs = new ArrayList<Song>();
            if (table.getModel() instanceof SongSearchTableModel) {
                for (int selectedRow : selectedRows) {
                    selectedRow = table.convertRowIndexToModel(selectedRow);
                    songs.add(((SongSearchTableModel) table.getModel()).getSongs().get(selectedRow));
                }
            } else if (table.getModel() instanceof TopSongTableModel) {
                for (int selectedRow : selectedRows) {
                    selectedRow = table.convertRowIndexToModel(selectedRow);
                    songs.add(((TopSongTableModel) table.getModel()).getSongs().get(selectedRow));
                }
            }
            play(songs);

        } else if (table.getModel() instanceof AlbumSearchTableModel) {
            AlbumSearchTableModel model = (AlbumSearchTableModel) searchTable.getModel();
            final List<Album> albums = new ArrayList<Album>();

            for (int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                albums.add(model.getAlbums().get(selectedRow));
            }

            BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for (Album album : albums) {
                        songs.addAll(Groovesquid.getSearchService().getSongsByAlbum(album));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        play(get());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            worker.execute();

        } else if (searchTable.getModel() instanceof ArtistSearchTableModel) {
            ArtistSearchTableModel model = (ArtistSearchTableModel) searchTable.getModel();
            final List<Artist> artists = new ArrayList<Artist>();

            for (int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                artists.add(model.getArtists().get(selectedRow));
            }

            BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for (Artist artist : artists) {
                        songs.addAll(Groovesquid.getSearchService().getSongsByArtist(artist));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        play(get());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            worker.execute();
        }

        searchTable.getSelectionModel().clearSelection();
    }

    public void showSongsMenuItemActionPerformed(ActionEvent evt) {
        JTable table;
        if (evt.getSource() instanceof JMenuItem) {
            table = (JTable) ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).getInvoker();
        } else if (evt.getSource() instanceof JTable) {
            table = (JTable) evt.getSource();
        } else {
            table = searchTable;
        }
        int[] selectedRows = table.getSelectedRows();

        if (table.getModel() instanceof AlbumSearchTableModel) {
            AlbumSearchTableModel model = (AlbumSearchTableModel) searchTable.getModel();
            final List<Album> albums = new ArrayList<Album>();

            for (int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                albums.add(model.getAlbums().get(selectedRow));
            }

            BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for (Album album : albums) {
                        songs.addAll(Groovesquid.getSearchService().getSongsByAlbum(album));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            worker.execute();

        } else if (table.getModel() instanceof ArtistSearchTableModel) {
            ArtistSearchTableModel model = (ArtistSearchTableModel) searchTable.getModel();
            final List<Artist> artists = new ArrayList<Artist>();

            for (int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                artists.add(model.getArtists().get(selectedRow));
            }

            BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = new ArrayList<Song>();
                    for (Artist artist : artists) {
                        songs.addAll(Groovesquid.getSearchService().getSongsByArtist(artist));
                    }
                    return songs;
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            worker.execute();
        }
    }

    public void showAlbumsMenuItemActionPerformed(ActionEvent evt) {
        JTable table;
        if (evt.getSource() instanceof JMenuItem) {
            table = (JTable) ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).getInvoker();
        } else if (evt.getSource() instanceof JTable) {
            table = (JTable) evt.getSource();
        } else {
            table = searchTable;
        }
        int[] selectedRows = table.getSelectedRows();

        if (table.getModel() instanceof ArtistSearchTableModel) {
            ArtistSearchTableModel model = (ArtistSearchTableModel) searchTable.getModel();
            final List<Artist> artists = new ArrayList<Artist>();

            for (int selectedRow : selectedRows) {
                selectedRow = searchTable.convertRowIndexToModel(selectedRow);
                artists.add(model.getArtists().get(selectedRow));
            }

            BusySwingWorker<List<Album>, Void> worker = new BusySwingWorker<List<Album>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Album> doInBackground() {
                    List<Album> albums = new ArrayList<Album>();
                    for (Artist artist : artists) {
                        albums.addAll(Groovesquid.getSearchService().getAlbumsByArtist(artist));
                    }
                    return albums;
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new AlbumSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            worker.execute();
        }
    }

    public DownloadListener getDownloadListener(final DownloadTableModel downloadTableModel) {
        return new DownloadListener() {
            public void downloadedBytesChanged(Track track) {
                int row = downloadTableModel.getSongDownloads().indexOf(track);
                if(row >= 0) {
                    downloadTableModel.fireTableCellUpdated(row, 5);
                }
            }

            public void statusChanged(Track track) {
                int row = downloadTableModel.getSongDownloads().indexOf(track);
                if(row >= 0) {
                    downloadTableModel.fireTableRowsUpdated(row, row);
                }
                downloadTableModel.updateSongDownloads();

                // fire download completed action
                if(track.getStatus() == Track.Status.FINISHED) {
                    if (Groovesquid.getConfig().getDownloadComplete() == DownloadComplete.OPEN_FILE.ordinal()) {
                        try {
                            // open file
                            Desktop.getDesktop().open(new File(track.getPath()));
                        } catch (IOException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (Groovesquid.getConfig().getDownloadComplete() == DownloadComplete.OPEN_DIRECTORY.ordinal()) {
                        try {
                            // open dir
                            Desktop.getDesktop().open(new File(track.getPath()).getParentFile());
                        } catch (IOException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
    }

    public void downloadMenuItemActionPerformed(ActionEvent evt) {
        JTable table;
        if (evt.getSource() instanceof JMenuItem) {
            table = (JTable) ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).getInvoker();
        } else if (evt.getSource() instanceof JTable) {
            table = (JTable) evt.getSource();
        } else {
            table = searchTable;
        }
        int[] selectedRows = table.getSelectedRows();

        final DownloadTableModel downloadTableModel = (DownloadTableModel) downloadTable.getModel();
        if (table.getModel() instanceof SongSearchTableModel) {
            for (int selectedRow : selectedRows) {
                selectedRow = table.convertRowIndexToModel(selectedRow);
                Song song = ((SongSearchTableModel) table.getModel()).getSongs().get(selectedRow);
                downloadTableModel.addRow(0, Groovesquid.getDownloadService().download(song, getDownloadListener(downloadTableModel)));
            }
        } else if (table.getModel() instanceof TopSongTableModel) {
            for (int selectedRow : selectedRows) {
                selectedRow = table.convertRowIndexToModel(selectedRow);
                Song song = ((TopSongTableModel) table.getModel()).getSongs().get(selectedRow);
                downloadTableModel.addRow(0, Groovesquid.getDownloadService().download(song, getDownloadListener(downloadTableModel)));
            }
        } else if (table.getModel() instanceof AlbumSearchTableModel) {
            AlbumSearchTableModel albumSearchTableModel = (AlbumSearchTableModel) table.getModel();
            for (int selectedRow : selectedRows) {
                selectedRow = table.convertRowIndexToModel(selectedRow);
                final Album album = albumSearchTableModel.getAlbums().get(selectedRow);
                SwingWorker<List<Song>, Void> worker = new SwingWorker<List<Song>, Void>() {

                    @Override
                    protected List<Song> doInBackground() {
                        return Groovesquid.getSearchService().getSongsByAlbum(album);
                    }

                    @Override
                    protected void done() {
                        try {
                            for (Song song : get()) {
                                downloadTableModel.addRow(0, Groovesquid.getDownloadService().download(song, getDownloadListener(downloadTableModel)));
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                };
                worker.execute();
            }
        }
        searchTable.getSelectionModel().clearSelection();
    }

    public void retryFailedDownloadsMenuItemActionPerformed(ActionEvent evt) {
        List<Track> failedDownloads = new ArrayList<Track>();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int row = downloadTable.convertRowIndexToModel(i);
            Track track = model.getSongDownloads().get(row);
            if(track.getStatus() == Track.Status.ERROR || track.getProgress() == 0) {
                failedDownloads.add(track);
            }
        }
        
        final DownloadTableModel downloadTableModel = (DownloadTableModel) downloadTable.getModel();
        for (Track track : failedDownloads) {
            Groovesquid.getDownloadService().cancelDownload(track, false);
            downloadTableModel.removeRow(track);
            downloadTableModel.addRow(0, Groovesquid.getDownloadService().download(track.getSong(), getDownloadListener(downloadTableModel)));
        }
        downloadTable.clearSelection();
    }

    public void searchTypeComboBoxActionPerformed(ActionEvent evt) {

    }

    public void searchButtonActionPerformed(ActionEvent evt) {
        searchTypeComboBox.setEnabled(false);
        searchTextField.setEnabled(false);
        searchButton.setEnabled(false);

        // Songs
        if (searchTypeComboBox.getSelectedIndex() == 0) {
            BusySwingWorker<List<Song>, Void> worker = new BusySwingWorker<List<Song>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Song> doInBackground() {
                    List<Song> songs = Groovesquid.getSearchService().getSongsByQuery(searchTextField.getText());
                    if (songs != null && songs.size() > 0) {
                        return songs;
                    } else {
                        showError(String.format(I18n.getLocaleString("ERROR_NO_SEARCH_RESULTS"), searchTextField.getText()));
                        return new ArrayList<Song>();
                    }
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new SongSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();

        // Albums
        } else if (searchTypeComboBox.getSelectedIndex() == 1) {
            BusySwingWorker<List<Album>, Void> worker = new BusySwingWorker<List<Album>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Album> doInBackground() {
                    return Groovesquid.getSearchService().getAlbumsByQuery(searchTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new AlbumSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
            
        // Artists
        } else if (searchTypeComboBox.getSelectedIndex() == 2) {
            BusySwingWorker<List<Artist>, Void> worker = new BusySwingWorker<List<Artist>, Void>(busySearchScrollPane.getBusyModel()) {
                @Override
                protected List<Artist> doInBackground() {
                    return Groovesquid.getSearchService().getArtistsByQuery(searchTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        searchTable.setModel(new ArtistSearchTableModel(get()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchTable.setEnabled(true);
                    searchTypeComboBox.setEnabled(true);
                    searchTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                }
            };
            worker.execute();
        }
    }

    public void selectAllMenuItemActionPerformed(ActionEvent evt) {
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        downloadTable.setRowSelectionInterval(0, model.getRowCount() - 1);
    }

    public void selectCompletedMenuItemActionPerformed(ActionEvent evt) {
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getSongDownloads().get(i).getProgress() == 100) {
                if (downloadTable.getSelectedRows().length > 0) {
                    downloadTable.addRowSelectionInterval(i, i);
                } else {
                    downloadTable.setRowSelectionInterval(i, i);
                }
            }
        }
    }

    public void selectFailedMenuItemActionPerformed(ActionEvent evt) {
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getSongDownloads().get(i).getStatus() == Track.Status.ERROR) {
                if (downloadTable.getSelectedRows().length > 0) {
                    downloadTable.addRowSelectionInterval(i, i);
                } else {
                    downloadTable.setRowSelectionInterval(i, i);
                }
            }
        }
    }

    public void formWindowClosing(WindowEvent evt) {
        if (Groovesquid.getDownloadService().areCurrentlyRunningDownloads()) {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            if (JOptionPane.showConfirmDialog(this, I18n.getLocaleString("ALERT_DOWNLOADS_IN_PROGRESS"), I18n.getLocaleString("ALERT"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                System.exit(0);
            } else {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
        } else {
            System.exit(0);
        }
    }

    public void searchTextFieldKeyReleased(KeyEvent evt) {
        /*if (Groovesquid.getConfig().getAutocompleteEnabled()) {
            if (evt.getKeyCode() >= KeyEvent.VK_A && evt.getKeyCode() <= KeyEvent.VK_Z && (evt.getModifiers() & ActionEvent.CTRL_MASK) != ActionEvent.CTRL_MASK && ! evt.isControlDown()) {
                SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {

                    @Override
                    protected List<String> doInBackground() {
                        return Groovesquid.getDiscogsService().autocompleteByQuery(searchTextField.getText());
                    }

                    @Override
                    protected void done() {
                        try {
                            autocompleteList = (ArrayList<String>) get();
                            if(autocompleteList.size() > 0) {
                                String autocomplete = autocompleteList.get(0);
                                String autocompleteSub = autocomplete.substring(searchTextField.getText().length());
                                String newText = searchTextField.getText() + autocompleteSub;
                                searchTextField.setText(newText);
                                searchTextField.select(newText.length() - autocompleteSub.length(), newText.length());
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                worker.execute();
            }
        }*/
    }

    public void searchTextFieldActionPerformed(ActionEvent evt) {
        for (ActionListener a : searchButton.getActionListeners()) {
            a.actionPerformed(evt);
        }
    }

    public void downloadTableKeyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            int[] selectedRows = downloadTable.getSelectedRows();
            Object[] options = {I18n.getLocaleString("REMOVE_FROM_LIST"), I18n.getLocaleString("REMOVE_FROM_LIST_AND_DISK"), I18n.getLocaleString("CANCEL")};
            int selectedValue = JOptionPane.showOptionDialog(this, String.format(I18n.getLocaleString("ALERT_REMOVE_FROM_LIST_OR_DISK"), selectedRows.length), I18n.getLocaleString("ALERT"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if(selectedValue == 0) {
                removeFromList(false);
            } else if(selectedValue == 1) {
                removeFromList(true);
            }
        }
    }

    public void playPauseButtonMousePressed(MouseEvent evt) {
        if (Groovesquid.getPlayService().isPlaying()) {
            playPauseButton.setIcon(style.getPauseIconActive());
        } else {
            playPauseButton.setIcon(style.getPlayIconActive());
        }
    }

    public void playPauseButtonActionPerformed(ActionEvent evt) {
        if (Groovesquid.getPlayService().isPaused()) {
            Groovesquid.getPlayService().resume();
        } else if (Groovesquid.getPlayService().isPlaying()) {
            Groovesquid.getPlayService().pause();
        } else {
            if (Groovesquid.getPlayService().getPlaylist().size() > 0) {
                Groovesquid.getPlayService().play();
            } else {
                playPauseButton.setIcon(style.getPlayIcon());
            }
        }
    }

    public void nextButtonMousePressed(MouseEvent evt) {
        nextButton.setIcon(style.getNextIconActive());
    }

    public void nextButtonActionPerformed(ActionEvent evt) {
        nextButton.setIcon(style.getNextIcon());
        if (Groovesquid.getPlayService().getCurrentSongIndex() < Groovesquid.getPlayService().getPlaylist().size() - 1) {
            Groovesquid.getPlayService().skipForward();
        } else {
            Groovesquid.getPlayService().clearPlaylist();
            resetPlayInfo();
        }
    }

    public void previousButtonMousePressed(MouseEvent evt) {
        previousButton.setIcon(style.getPreviousIconActive());
    }

    public void previousButtonActionPerformed(ActionEvent evt) {
        previousButton.setIcon(style.getPreviousIcon());
        Groovesquid.getPlayService().skipBackward();
    }

    public void trackSliderMouseDragged(MouseEvent evt) {
        if (Groovesquid.getPlayService().getCurrentTrack() != null) {
            Groovesquid.getPlayService().setCurrentPosition(trackSlider.getValue());
        }
    }

    public void volumeSliderStateChanged(ChangeEvent evt) {
        Groovesquid.getPlayService().setVolume(volumeSlider.getValue());
    }

    public void removeFromListMenuItemActionPerformed(ActionEvent evt) {
        removeFromList(false);
    }

    public void removeFromDiskMenuItemActionPerformed(ActionEvent evt) {
        int[] selectedRows = downloadTable.getSelectedRows();
        Object[] options = {I18n.getLocaleString("YES"), I18n.getLocaleString("NO")};
        if (JOptionPane.showOptionDialog(null, String.format(I18n.getLocaleString("ALERT_REMOVE_FILES_FROM_DISK"), Integer.toString(selectedRows.length)), I18n.getLocaleString("ALERT"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == 0) {
            removeFromList(true);
        }
    }

    public void tableMousePressed(MouseEvent evt) {
        JTable table = (JTable) evt.getSource();
        int row = table.rowAtPoint(evt.getPoint());
        boolean isSongTable = table.getModel() instanceof SongSearchTableModel || table.getModel() instanceof TopSongTableModel;
        boolean isArtistTable = table.getModel() instanceof ArtistSearchTableModel;
        boolean isAlbumTable = table.getModel() instanceof AlbumSearchTableModel;
        boolean isDownloadTable = table.getModel() instanceof DownloadTableModel;

        if (row >= 0) {
            if (evt.getClickCount() == 2 && (evt.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
                if (isSongTable) {
                    Object[] options = {I18n.getLocaleString("PLAY"), I18n.getLocaleString("DOWNLOAD"), I18n.getLocaleString("CANCEL")};
                    int selectedValue = JOptionPane.showOptionDialog(this, I18n.getLocaleString("ALERT_DOWNLOAD_OR_PLAY"), I18n.getLocaleString("SONG"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    if (selectedValue == 0) {
                        playMenuItemActionPerformed(new ActionEvent(table, 0, null));
                    } else if (selectedValue == 1) {
                        downloadMenuItemActionPerformed(new ActionEvent(table, 0, null));
                    }
                } else if (isAlbumTable) {
                    showSongsMenuItemActionPerformed(new ActionEvent(table, 0, null));
                } else if (isArtistTable) {
                    showAlbumsMenuItemActionPerformed(new ActionEvent(table, 0, null));
                }
            }

            if (evt.isPopupTrigger()) {
                tableShowPopupMenu(evt);
            }
        } else {
            table.clearSelection();
        }
    }

    public void tableMouseReleased(MouseEvent evt) {
        if (GuiUtils.getSystem().equals(GuiUtils.OperatingSystem.WINDOWS) && evt.isPopupTrigger()) {
            tableShowPopupMenu(evt);
        }
    }

    private void tableShowPopupMenu(MouseEvent evt) {
        JTable table = (JTable) evt.getSource();
        boolean isSongTable = table.getModel() instanceof SongSearchTableModel || table.getModel() instanceof TopSongTableModel;
        boolean isArtistTable = table.getModel() instanceof ArtistSearchTableModel;
        boolean isAlbumTable = table.getModel() instanceof AlbumSearchTableModel;
        boolean isDownloadTable = table.getModel() instanceof DownloadTableModel;

        int row = table.rowAtPoint(evt.getPoint());
        int column = table.columnAtPoint(evt.getPoint());

        if (table.getSelectedRowCount() == 0 || !table.isRowSelected(row)) {
            table.changeSelection(row, column, false, false);
        }

        if (isSongTable) {
            songTablePopupMenu.show(table, evt.getX(), evt.getY());
        } else if (isAlbumTable) {
            albumTablePopupMenu.show(table, evt.getX(), evt.getY());
        } else if (isArtistTable) {
            artistTablePopupMenu.show(table, evt.getX(), evt.getY());
        } else if (isDownloadTable) {
            downloadTablePopupMenu.show(table, evt.getX(), evt.getY());
        }
    }

    public void openDirectoryMenuItemActionPerformed(ActionEvent evt) {
        int[] selectedRows = downloadTable.getSelectedRows();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            int selectedRow = downloadTable.convertRowIndexToModel(selectedRows[i] - i);
            Track track = model.getSongDownloads().get(selectedRow);
            File dir = new File(track.getPath()).getParentFile();
            if (dir.exists()) {
                try {
                    // open dir
                    Desktop.getDesktop().open(dir);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                showError(I18n.getLocaleString("INVALID_DOWNLOAD_DIRECTORY"));
            }
        }
        downloadTable.clearSelection();
    }

    public void openFileMenuItemActionPerformed(ActionEvent evt) {
        int[] selectedRows = downloadTable.getSelectedRows();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            int selectedRow = downloadTable.convertRowIndexToModel(selectedRows[i] - i);
            Track track = model.getSongDownloads().get(selectedRow);
            File file = new File(track.getPath());
            if (file.exists()) {
                try {
                    // open file
                    Desktop.getDesktop().open(new File(track.getPath()));
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                showError(I18n.getLocaleString("INVALID_DOWNLOAD_DIRECTORY"));
            }
        }
        downloadTable.clearSelection();
    }

    public void linkLabelMousePressed(MouseEvent evt) {
        try {
            Desktop.getDesktop().browse(java.net.URI.create(((JLabel) evt.getSource()).getToolTipText()));
        } catch (IOException ex) {
            Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void removeFromList(boolean andDisk) {
        int[] selectedRows = downloadTable.getSelectedRows();
        DownloadTableModel model = (DownloadTableModel) downloadTable.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            int selectedRow = downloadTable.convertRowIndexToModel(selectedRows[i] - i);
            Track track = model.getSongDownloads().get(selectedRow);
            if(andDisk) {
                Groovesquid.getDownloadService().cancelDownload(track, true, true);
            } else {
                Groovesquid.getDownloadService().cancelDownload(track, false);
            }
            model.removeRow(selectedRow);
        }
        downloadTable.getSelectionModel().clearSelection();
    }

    public void play(List<Song> songs) {
        Track currentTrack = Groovesquid.getPlayService().getCurrentTrack();
        if (currentTrack != null && currentTrack.getSong().equals(songs.get(0)) && Groovesquid.getPlayService().isPaused()) {
            Groovesquid.getPlayService().resume();
        } else {
            if (Groovesquid.getPlayService().isPlaying()) {
                Object[] options = {I18n.getLocaleString("PLAY_NOW"), I18n.getLocaleString("ADD_TO_QUEUE"), I18n.getLocaleString("CANCEL")};
                int selectedValue = JOptionPane.showOptionDialog(this, I18n.getLocaleString("ALERT_PLAY_NOW_OR_QUEUE"), I18n.getLocaleString("PLAY"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (selectedValue == 0) {
                    Groovesquid.getPlayService().add(songs, PlayService.AddMode.NOW);
                } else if (selectedValue == 1) {
                    Groovesquid.getPlayService().add(songs, PlayService.AddMode.NEXT);
                }
            } else {
                Groovesquid.getPlayService().add(songs, PlayService.AddMode.NOW);
            }
        }
    }

    public void play(Song song) {
        play(Arrays.asList(new Song[]{song}));
    }
    
    public void resetPlayInfo() {
        currentlyPlayingSongLabel.setText("");
        currentlyPlayingArtistLabel.setText("");
        playPauseButton.setIcon(style.getPlayIcon());
        trackSlider.setValue(0);
        trackSlider.setMaximum(0);
        trackSlider.setEnabled(false);
        currentDurationLabel.setText("0:00");
        durationLabel.setText("0:00");
        albumCoverLabel.setIcon(null);
    }

    public JTable getDownloadTable() {
        return downloadTable;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, I18n.getLocaleString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    public Style getStyle() {
        return style;
    }

    public JEditorPane getAdPane() {
        return adPane;
    }
}
