/*
 * SKCraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher;

import com.skcraft.launcher.dialog.LauncherFrame;
import com.skcraft.launcher.swing.*;
import lombok.NonNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class RedditLauncherFrame extends LauncherFrame {

    public RedditLauncherFrame(@NonNull final Launcher launcher) {
        super(launcher);
        setIcons();
    }

    @Override
    public WebpagePanel createNewsPanel() {
        return WebpagePanel.forHTML("");
    }

    @Override
    public JButton createPrimaryButton(String name) {
        JButton button = new ColoredButton(name, Theme.primary, Theme.primaryAlt);
        button.setFont(new Font(button.getFont().getName(), Font.PLAIN, Theme.primarySize));
        button.setForeground(Theme.primaryText);
        button.setPreferredSize(Theme.primaryButtonSize);
        return button;
    }

    @Override
    protected JButton createSecondaryButton(String name) {
        JButton button = new ColoredButton(name, Theme.secondary, Theme.secondaryAlt);
        button.setFont(new Font(button.getFont().getName(), Font.PLAIN, Theme.secondarySize));
        button.setForeground(Theme.secondaryText);
        button.setPreferredSize(Theme.secondaryButtonSize);
        return button;
    }

    @Override
    protected JCheckBox createCheckBox(String name) {
        JCheckBox box = new JCheckBox(name);
        box.setFont(new Font(box.getFont().getName(), Font.PLAIN, Theme.secondarySize));
        box.setBackground(Theme.secondary);
        box.setForeground(Theme.secondaryText);
        box.setPreferredSize(Theme.secondaryButtonSize);
        box.setHorizontalAlignment(SwingConstants.CENTER);
        return box;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        getContentPane().removeAll();
        instancesTable.setBackground(new Color(0, 0, 0, 0));
        instancesTable.setSelectionBackground(Theme.primary);
        instancesTable.setSelectionForeground(Theme.primaryText);
        instancesTable.setForeground(Theme.secondaryText);
        instancesTable.setFont(new Font(instancesTable.getFont().getName(), Font.PLAIN, Theme.secondarySize));
        instancesTable.setOpaque(false);
        redditInit();
    }

    private void redditInit() {
        RedditBackgroundPanel root = new RedditBackgroundPanel(Theme.subreddit, Theme.postCount, Theme.randomise, Theme.interval, Theme.fade);

        JPanel launchControls = new JPanel();
        launchControls.setOpaque(false);
        launchControls.add(selfUpdateButton);
        launchControls.add(optionsButton);
        launchControls.add(launchButton);

        JPanel updateControls = new JPanel();
        updateControls.add(refreshButton);
        updateControls.add(updateCheck);
        updateControls.setBackground(getAltFrostColor(Theme.frost));

        JPanel left = new FrostPanel(root, Theme.frost);
        left.setLayout(new BorderLayout());
        left.setPreferredSize(new Dimension(250, 300));
        left.add(instancesTable, BorderLayout.CENTER);
        left.add(updateControls, BorderLayout.PAGE_END);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout());
        center.add(getHeaderImage(), Theme.headerAlignY);
        center.add(launchControls, BorderLayout.PAGE_END);

        root.setLayout(new BorderLayout());
        root.add(left, BorderLayout.WEST);
        root.add(center, BorderLayout.CENTER);

        add(root);

        setMinimumSize(new Dimension(600, 350));
        setPreferredSize(new Dimension(700, 420));
    }

    private void setIcons() {
        Image mainIcon = SwingHelper.createImage(LauncherFrame.class, "/com/skcraft/launcher/icon.png");
        Image titleIcon = SwingHelper.createImage(LauncherFrame.class, "/com/skcraft/launcher/title.png");
        ArrayList<Image> icons = new ArrayList<Image>();
        if (mainIcon != null) {
            icons.add(mainIcon);
        }
        if (titleIcon != null) {
            icons.add(titleIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        }
        setIconImages(icons);
    }

    private JLabel getHeaderImage() {
        JLabel label = new JLabel();
        try {
            BufferedImage image = ImageIO.read(RedditLauncher.class.getResourceAsStream("/com/skcraft/launcher/header.png"));
            int width = Math.min(image.getWidth(), 350);
            label.setIcon(new ImageIcon(image.getScaledInstance(width, -1, Image.SCALE_SMOOTH)));
            label.setHorizontalAlignment(Theme.headerAlignX);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return label;
    }

    private Color getAltFrostColor(Color c) {
        int r = Math.min(c.getRed() + 20, 255);
        int g = Math.min(c.getGreen() + 20, 255);
        int b = Math.min(c.getBlue() + 20, 255);
        int a = Math.min(c.getAlpha() + 40, 255);
        return new Color(r, g, b, a);
    }
}
