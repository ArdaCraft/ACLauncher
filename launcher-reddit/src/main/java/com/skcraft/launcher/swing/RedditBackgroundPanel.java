package com.skcraft.launcher.swing;

import com.skcraft.launcher.util.Closer;
import com.skcraft.launcher.util.RedditUtils;
import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author dags <dags@dags.me>
 */
@Log
public class RedditBackgroundPanel extends JPanel implements Runnable, ActionListener, Paintable {

    private static final ImageFader DEFAULT = defaultFader();

    private final AtomicReference<ImageFader> reference;
    private final AtomicBoolean showing;
    private final AtomicBoolean repaint;
    private final String subreddit;
    private final int postCount;
    private final boolean random;
    private final Timer timer;
    private final long delay;
    private final long fade;

    public RedditBackgroundPanel(String subreddit, int postCount, boolean randomise, long interval, long fade) {
        this.reference = new AtomicReference<ImageFader>(DEFAULT);
        this.showing = new AtomicBoolean(true);
        this.repaint = new AtomicBoolean(true);
        this.timer = new Timer(200, this);
        this.subreddit = subreddit;
        this.postCount = postCount;
        this.random = randomise;
        this.delay = interval;
        this.fade = fade;
        this.timer.start();
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        ImageFader image = reference.get();
        image.paint(this, graphics, getWidth(), getHeight());
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        if (reference.get().isFading()) {
            repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isShowing()) {
            if (repaint.get()) {
                repaint.set(false);
                repaint();
            }
        } else {
            timer.stop();
            showing.set(false);
        }
    }

    @Override
    public void run() {
        log.info(String.format("Fetching backgrounds from subreddit: %s", subreddit));

        int index = 0;
        List<String> targets = RedditUtils.getBackgrounds(subreddit, postCount);
        log.info(String.format("Found %s/%s backgrounds for subreddit: %s", targets.size(), postCount, subreddit));
        if (random) {
            Collections.shuffle(targets);
        }

        // async
        while (showing.get() && index < targets.size()) {
            try {
                String next = targets.get(index);
                ImageFader image = getImage(next);

                if (image != null) {
                    reference.set(image);
                    repaint.set(true);
                }

                if (++index >= targets.size()) {
                    index = 0;
                }

                Thread.sleep(delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("Stopping async background image loader thread...");
    }

    private Component self() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private ImageFader getImage(String address) {
        InputStream inputStream = null;
        try {
            ImageFader current = reference.get();
            BufferedImage from = current != null ? current.getTo() : null;
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
            BufferedImage to = ImageIO.read(inputStream);
            return new ImageFader(from, to, fade);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closer.close(inputStream);
        }
        return null;
    }

    private static ImageFader defaultFader() {
        BufferedImage blank = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        return new ImageFader(blank, blank, 1000L);
    }
}
