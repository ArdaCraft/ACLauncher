package com.skcraft.launcher.util;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class ComponentScaler {

    public static float guessScale() {
        return getResolution() / 110F;
    }

    public static int getResolution() {
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    public static void scale(Component component, float scale) {
        if (component instanceof Container) {
            Container container = (Container) component;
            scaleInsets(container.getInsets(), scale);
            for (Component child : container.getComponents()) {
                scale(child, scale);
            }
        }

        if (component.getClass() == JPanel.class) {
            return;
        }

        Dimension size = scaleDimension(component.getSize(), scale);
        Dimension preferred = scaleDimension(component.getPreferredSize(), scale);
        Dimension min = scaleDimension(component.getMinimumSize(), scale);
        Dimension max = scaleDimension(component.getMaximumSize(), scale);
        Font font = scaleFont(component.getFont(), scale);

        component.setSize(size);
        component.setPreferredSize(preferred);
        component.setMinimumSize(min);
        component.setMaximumSize(max);
        component.setFont(font);

        if (component instanceof JFrame) {
            JFrame frame = (JFrame) component;
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
    }

    private static Font scaleFont(Font font, float scale) {
        if (font == null) {
            return null;
        }

        Map<TextAttribute, ?> attributes = font.getAttributes();
        Map map = attributes;
        Float size = (Float) map.get(TextAttribute.SIZE);
        if (size == null) {
            return font;
        }

        map.put(TextAttribute.SIZE, scale * size);
        return new Font(attributes);
    }

    private static Dimension scaleDimension(Dimension dimension, float scale) {
        if (dimension == null) {
            return null;
        }
        return new Dimension(Math.round(dimension.width * scale), Math.round(dimension.height * scale));
    }

    private static Insets scaleInsets(Insets insets, float scale) {
        if (insets == null) {
            return null;
        }
        int l = Math.round(scale * insets.left);
        int t = Math.round(scale * insets.top);
        int r = Math.round(scale * insets.right);
        int b = Math.round(scale * insets.bottom);
        insets.set(t, l, b, r);
        return insets;
    }
}
