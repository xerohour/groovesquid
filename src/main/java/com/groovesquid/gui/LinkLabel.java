package com.groovesquid.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinkLabel extends JLabel {

    private URI link;

    public LinkLabel() {
        setForeground(Color.blue);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (link == null) {
                    link = URI.create(((JLabel) evt.getSource()).getText());
                }
                try {
                    Desktop.getDesktop().browse(link);
                } catch (IOException ex) {
                    Logger.getLogger(LinkLabel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void setLink(String link) {
        this.link = URI.create(link);
    }
}
