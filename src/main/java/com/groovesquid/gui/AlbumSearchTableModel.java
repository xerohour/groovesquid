package com.groovesquid.gui;

import com.groovesquid.model.Album;
import com.groovesquid.util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AlbumSearchTableModel extends AbstractTableModel {

    private String[] columnNames = {I18n.getLocaleString("NAME"), I18n.getLocaleString("ARTIST"), I18n.getLocaleString("SONGS"), I18n.getLocaleString("YEAR")};
    
    private List<Album> albums = new ArrayList<Album>();

    public AlbumSearchTableModel() {
        
    }
    
    public AlbumSearchTableModel(List<Album> albums) {
        this.albums = albums;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return albums.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Album album = albums.get(row);

        switch (col) {
            case 0:
                return album;
            case 1:
                return album.getArtists() != null ? album.getArtistNames() : "";
            case 2:
                return album.getSongCount();
            case 3:
                return album.getReleaseYear();
        }
        return null;
    }
    
    public List<Album> getAlbums() {
        return albums;
    }
        
    public void removeRow(int row) {
        albums.remove(row);
        fireTableStructureChanged();
    }
    
    public void removeRow(Album song) {
        albums.remove(song);
        fireTableStructureChanged();
    }
    
    public void addRow(Album song) {
        albums.add(song);
        fireTableStructureChanged();
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (albums.size() > 0 && getRowCount() > 0) {
            return getValueAt(0, column).getClass();
        } else {
            return Object.class;
        }
    }
}
