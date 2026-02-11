package com.creati.ui.main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.creati.util.UITheme;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/**
 * MainFrame에서 길어지는 요소(컴포넌트/차트/이미지유틸)만 모아둔 파일
 */
public class MainUiParts {

    public static final Color LAVENDER_HOVER  = new Color(0xEAE6FF);
    public static final Color LAVENDER_BORDER = new Color(0xCFC9FF);

    // =========================
    // Cards / Charts
    // =========================

    public static class HomeCard extends JPanel {
        private final JPanel bodyWrap;

        public HomeCard(String title) {
            super(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 235), 1, true),
                    new EmptyBorder(16, 16, 16, 16)
            ));

            JLabel t = new JLabel(title);
            t.setFont(UITheme.BODY_MED);
            t.setForeground(UITheme.TEXT);

            JPanel head = new JPanel(new BorderLayout());
            head.setOpaque(false);
            head.add(t, BorderLayout.WEST);

            bodyWrap = new JPanel(new BorderLayout());
            bodyWrap.setOpaque(false);
            bodyWrap.setBorder(new EmptyBorder(12, 0, 8, 0));

            add(head, BorderLayout.NORTH);
            add(bodyWrap, BorderLayout.CENTER);
        }

        public void setBody(JComponent body) {
            bodyWrap.removeAll();
            bodyWrap.add(body, BorderLayout.CENTER);
            bodyWrap.revalidate();
            bodyWrap.repaint();
        }
    }

        static class ChartCard extends JPanel {
            private final JPanel chartHolder = new JPanel(new BorderLayout());
            private final JLabel hint = new JLabel(" ");

            ChartCard(String title) {
                super(new BorderLayout());
                setOpaque(true);
                setBackground(new Color(250, 250, 252));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(235, 235, 242), 1, true),
                        new EmptyBorder(12, 12, 12, 12)
                ));

                JLabel t = new JLabel(title);
                t.setFont(UITheme.BODY_MED);
                t.setForeground(UITheme.TEXT);

                hint.setFont(UITheme.CAPTION);
                hint.setForeground(new Color(140, 140, 140));

                JPanel header = new JPanel(new BorderLayout(10, 0));
                header.setOpaque(false);
                header.add(t, BorderLayout.WEST);
                header.add(hint, BorderLayout.EAST);

                chartHolder.setOpaque(false);
                chartHolder.setBorder(new EmptyBorder(10, 0, 0, 0));

                add(header, BorderLayout.NORTH);
                add(chartHolder, BorderLayout.CENTER);

                setPreferredSize(new Dimension(10, 260));
            }

            void setChart(JComponent chart) {
                chartHolder.removeAll();
                chartHolder.add(chart, BorderLayout.CENTER);
            }

            void setHint(String text) {
                hint.setText(text);
            }
        }

    public static class MiniLineChart extends JComponent {
        private final int[] data;

        public MiniLineChart(int[] data) {
            this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(10, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(230, 230, 238));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

            int pad = 14;
            int gx0 = pad, gy0 = pad, gx1 = w - pad, gy1 = h - pad;

            g2.setColor(new Color(242, 242, 248));
            for (int i = 1; i <= 3; i++) {
                int y = gy0 + (gy1 - gy0) * i / 4;
                g2.drawLine(gx0, y, gx1, y);
            }

            int max = 1;
            for (int v : data) max = Math.max(max, v);

            int n = data.length;
            int[] xs = new int[n];
            int[] ys = new int[n];

            for (int i = 0; i < n; i++) {
                double tx = (n == 1) ? 0 : (double) i / (n - 1);
                xs[i] = (int) (gx0 + (gx1 - gx0) * tx);

                double ty = (double) data[i] / max;
                ys[i] = (int) (gy1 - (gy1 - gy0) * ty);
            }

            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(UITheme.ACCENT_PURPLE);
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }

            g2.setColor(Color.WHITE);
            for (int i = 0; i < n; i++) {
                g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
            }
            g2.setColor(UITheme.ACCENT_PURPLE);
            for (int i = 0; i < n; i++) {
                g2.drawOval(xs[i] - 5, ys[i] - 5, 10, 10);
            }

            g2.dispose();
        }
    }

    public static class MiniBarChart extends JComponent {
        private final String[] labels;
        private final int[] values;

        public MiniBarChart(String[] labels, int[] values) {
            this.labels = labels;
            this.values = values;
            setOpaque(false);
            setPreferredSize(new Dimension(10, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(230, 230, 238));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

            int padX = 14;
            int padTop = 14;
            int padBottom = 24;

            int gx0 = padX;
            int gx1 = w - padX;
            int gy0 = padTop;
            int gy1 = h - padBottom;

            int max = 1;
            for (int v : values) max = Math.max(max, v);

            int n = values.length;
            int gap = 10;
            int barW = Math.max(12, (gx1 - gx0 - gap * (n - 1)) / n);

            int x = gx0;
            for (int i = 0; i < n; i++) {
                int v = values[i];
                int bh = (int) ((gy1 - gy0) * (v / (double) max));
                int y = gy1 - bh;

                Color fill = (i % 2 == 0) ? UITheme.ACCENT_PURPLE : new Color(0xCFC9FF);
                g2.setColor(fill);
                g2.fillRoundRect(x, y, barW, bh, 10, 10);

                g2.setColor(new Color(120, 120, 120));
                g2.setFont(UITheme.CAPTION);
                String lab = labels[i];
                int tw = g2.getFontMetrics().stringWidth(lab);
                int lx = x + (barW - tw) / 2;
                g2.drawString(lab, lx, h - 8);

                x += barW + gap;
            }

            g2.dispose();
        }
    }

    // =========================
    // Small UI parts
    // =========================

    public static class ShadowLabel extends JLabel {
        private final int shadowAlpha;
        private final Color shadowBase;

        public ShadowLabel(String text, int shadowAlpha, Color shadowBase) {
            super(text);
            this.shadowAlpha = shadowAlpha;
            this.shadowBase = shadowBase;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = getInsets().top + fm.getAscent();

            g2.setColor(new Color(shadowBase.getRed(), shadowBase.getGreen(), shadowBase.getBlue(), shadowAlpha));
            g2.drawString(getText(), x + 1, y + 1);

            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    public static class CircleAvatar extends JComponent {
        private final Image image;

        public CircleAvatar(Image image) {
            this.image = image;
            setPreferredSize(new Dimension(34, 34));
            setMaximumSize(new Dimension(34, 34));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            Shape clip = new Ellipse2D.Float(0, 0, w, h);
            g2.setClip(clip);

            if (image != null) {
                g2.drawImage(image, 0, 0, w, h, this);
            } else {
                g2.setColor(new Color(235, 235, 245));
                g2.fillOval(0, 0, w, h);
            }

            g2.setClip(null);
            g2.setColor(new Color(220, 220, 232));
            g2.drawOval(0, 0, w - 1, h - 1);
            g2.dispose();
        }
    }

    public static class RoundedButton extends JButton {
        private final int arc = 18;

        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public static class EllipsisButton extends JButton {
        public EllipsisButton() {
            super("● ● ●");
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(new Color(100, 100, 110));
            setFont(new Font("Dialog", Font.BOLD, 14));
            setPreferredSize(new Dimension(52, 32));
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    // =========================
    // Image Utils
    // =========================

    public static Icon createHiDPIIcon(Path srcPath, int logicalSizePx, boolean trim) {
        BufferedImage src = loadBuffered(srcPath);
        if (src == null) return null;

        if (trim) src = trimTransparent(src);

        BufferedImage img1x = scaleFitHQ(src, logicalSizePx);
        BufferedImage img2x = scaleFitHQ(src, logicalSizePx * 2);

        Image mri = new BaseMultiResolutionImage(img1x, img2x);
        return new ImageIcon(mri);
    }

    public static BufferedImage scaleFitHQ(BufferedImage src, int target) {
        int sw = src.getWidth();
        int sh = src.getHeight();

        double scale = (double) target / Math.max(sw, sh);
        int w = (int) Math.round(sw * scale);
        int h = (int) Math.round(sh * scale);

        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dst.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return dst;
    }

    public static BufferedImage loadBuffered(Path path) {
        try {
            java.io.File f = path.toFile();
            if (!f.exists()) return null;
            return ImageIO.read(f);
        } catch (Exception e) {
            return null;
        }
    }

    public static Image loadImage(Path path) {
        return loadBuffered(path);
    }

    public static BufferedImage trimTransparent(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int top = h, left = w, right = -1, bottom = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = (src.getRGB(x, y) >> 24) & 0xFF;
                if (a != 0) {
                    if (x < left) left = x;
                    if (x > right) right = x;
                    if (y < top) top = y;
                    if (y > bottom) bottom = y;
                }
            }
        }
        if (right < left || bottom < top) return src;

        BufferedImage trimmed = src.getSubimage(left, top, (right - left + 1), (bottom - top + 1));
        BufferedImage copy = new BufferedImage(trimmed.getWidth(), trimmed.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(trimmed, 0, 0, null);
        g.dispose();
        return copy;
    }

    public static void attachHeight(JComponent c, int h) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        c.setPreferredSize(new Dimension(10, h));
        c.setMinimumSize(new Dimension(10, h));
    }
}
