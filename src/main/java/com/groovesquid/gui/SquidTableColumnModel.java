package com.groovesquid.gui;

import com.groovesquid.Groovesquid;
import com.groovesquid.model.Song;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SquidTableColumnModel extends DefaultTableColumnModel {

    private JTable table;
    
    public SquidTableColumnModel(JTable table) {
        this.table = table;
        this.columnMargin = 0;
    }

    @Override
    public void addColumn(TableColumn tc) {
        if (table.getModel() instanceof TopSongTableModel) {
            if (tc.getModelIndex() == 0) {
                tc.setMaxWidth(34);

            } else if (tc.getModelIndex() == 1) {
                tc.setMaxWidth(28);

                Action download = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        int modelRow = Integer.valueOf(e.getActionCommand());
                        DownloadTableModel downloadTableModel = (DownloadTableModel) Groovesquid.getMainFrame().getDownloadTable().getModel();
                        Song song = ((TopSongTableModel) table.getModel()).getSongs().get(modelRow);
                        downloadTableModel.addRow(0, Groovesquid.getDownloadService().download(song, Groovesquid.getMainFrame().getDownloadListener(downloadTableModel)));
                    }
                };
                SquidButtonEditor buttonColumn = new SquidButtonEditor(table, download, FontAwesomeIcon.DOWNLOAD_ICON);

                tc.setCellRenderer(buttonColumn);
                tc.setCellEditor(buttonColumn);

            } else if (tc.getModelIndex() == 2) {
                tc.setMaxWidth(28);

                Action play = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        int modelRow = Integer.valueOf(e.getActionCommand());
                        Song song = ((TopSongTableModel) table.getModel()).getSongs().get(modelRow);
                        Groovesquid.getMainFrame().play(song);
                    }
                };
                SquidButtonEditor buttonColumn = new SquidButtonEditor(table, play, FontAwesomeIcon.PLAY_ICON);

                tc.setCellRenderer(buttonColumn);
                tc.setCellEditor(buttonColumn);
            }

        } else if (table.getModel() instanceof SongSearchTableModel) {
            if (tc.getModelIndex() == 0) {
                tc.setMaxWidth(28);

                Action download = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        int modelRow = Integer.valueOf(e.getActionCommand());
                        DownloadTableModel downloadTableModel = (DownloadTableModel) Groovesquid.getMainFrame().getDownloadTable().getModel();
                        Song song = ((SongSearchTableModel) table.getModel()).getSongs().get(modelRow);
                        downloadTableModel.addRow(0, Groovesquid.getDownloadService().download(song, Groovesquid.getMainFrame().getDownloadListener(downloadTableModel)));
                    }
                };
                SquidButtonEditor buttonColumn = new SquidButtonEditor(table, download, FontAwesomeIcon.DOWNLOAD_ICON);

                tc.setCellRenderer(buttonColumn);
                tc.setCellEditor(buttonColumn);

            } else if (tc.getModelIndex() == 1) {
                tc.setMaxWidth(28);

                Action play = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        int modelRow = Integer.valueOf(e.getActionCommand());
                        Song song = ((SongSearchTableModel) table.getModel()).getSongs().get(modelRow);
                        Groovesquid.getMainFrame().play(song);
                    }
                };
                SquidButtonEditor buttonColumn = new SquidButtonEditor(table, play, FontAwesomeIcon.PLAY_ICON);

                tc.setCellRenderer(buttonColumn);
                tc.setCellEditor(buttonColumn);

            } else if (tc.getModelIndex() >= 5) {
                tc.setMaxWidth(100);
            }

        } else if(table.getModel() instanceof DownloadTableModel) {
            if (tc.getModelIndex() == 5) {
                tc.setCellRenderer(new ProgressCellRenderer());
                tc.setMinWidth(170);
            }
        } else if (table.getModel() instanceof AlbumSearchTableModel) {
            if (tc.getModelIndex() >= 2) {
                tc.setMaxWidth(100);
            }
        }
        super.addColumn(tc);
    }
}
