package com.groovesquid.gui;

import com.groovesquid.Groovesquid;
import com.groovesquid.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings({"serial", "rawtypes"})
public class AboutFrame extends JFrame {

    private JLabel websiteTextLabel;
    private LinkLabel websiteLabel;
    private JLabel facebookTextLabel;
    private LinkLabel facebookLabel;
    private JLabel twitterTextLabel;
    private LinkLabel twitterLabel;
    private JLabel copyrightLabel;
    private JLabel iconLabel;
    private JLabel versionLabel;
    private JButton closeButton;
    private LinkLabel donationLabel;

    public AboutFrame() {
        initComponents();
        
        // center screen
        setLocationRelativeTo(null);
        
        // icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/icon.png")));

        versionLabel.setText("Version " + Groovesquid.getVersion());
    }

    private void initComponents() {
        setTitle(I18n.getLocaleString("ABOUT"));
        setResizable(false);

        websiteTextLabel = new JLabel("Website:");
        websiteTextLabel.setFont(new Font(websiteTextLabel.getFont().getName(), Font.PLAIN, 11));

        websiteLabel = new LinkLabel();
        websiteLabel.setFont(new Font(websiteLabel.getFont().getName(), Font.PLAIN, 11));
        websiteLabel.setText("http://groovesquid.com");

        facebookTextLabel = new JLabel("Facebook:");
        facebookTextLabel.setFont(new Font(facebookTextLabel.getFont().getName(), Font.PLAIN, 11));

        facebookLabel = new LinkLabel();
        facebookLabel.setFont(new Font(facebookLabel.getFont().getName(), Font.PLAIN, 11));
        facebookLabel.setText("http://facebook.com/groovesquid");

        twitterTextLabel = new JLabel();
        twitterTextLabel.setFont(new Font(twitterTextLabel.getFont().getName(), Font.PLAIN, 11));
        twitterTextLabel.setText("Twitter:");

        twitterLabel = new LinkLabel();
        twitterLabel.setFont(new Font(twitterLabel.getFont().getName(), Font.PLAIN, 11));
        twitterLabel.setText("http://twitter.com/groovesquid");

        copyrightLabel = new JLabel();
        copyrightLabel.setFont(new Font(copyrightLabel.getFont().getName(), Font.PLAIN, 10));
        copyrightLabel.setForeground(new Color(102, 102, 102));
        copyrightLabel.setText("Copyright (c) 2015 by GBHRDT Development. All rights reserved.");

        closeButton = new JButton();
        closeButton.setText(I18n.getLocaleString("CLOSE"));
        closeButton.setFocusable(false);
        closeButton.setRequestFocusEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        iconLabel = new JLabel();
        iconLabel.setIcon(new ImageIcon(getClass().getResource("/gui/logo.png")));

        versionLabel = new JLabel("Version");
        versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.PLAIN, 11));

        donationLabel = new LinkLabel();
        donationLabel.setFont(new Font(versionLabel.getFont().getName(), Font.PLAIN, 11));
        donationLabel.setText("If you like Groovesquid, it would be very nice if you would donate by clicking here.");
        donationLabel.setLink("http://groovesquid.com/#donate");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iconLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(websiteTextLabel)
                                                                        .addComponent(facebookTextLabel)
                                                                        .addComponent(twitterTextLabel))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(websiteLabel)
                                                                        .addComponent(facebookLabel)
                                                                        .addComponent(twitterLabel))))
                                                .addGap(64, 64, 64))
                                        .addComponent(copyrightLabel, GroupLayout.Alignment.LEADING))
                                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(donationLabel)
                        .addComponent(versionLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(websiteTextLabel)
                                            .addComponent(websiteLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(facebookTextLabel)
                                            .addComponent(facebookLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(twitterTextLabel)
                                            .addComponent(twitterLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(donationLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(versionLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(copyrightLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(closeButton))
                    .addComponent(iconLabel))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        dispose();
    }

}
