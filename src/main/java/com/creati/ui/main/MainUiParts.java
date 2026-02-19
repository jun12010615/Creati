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
        private final JLabel valueLabel = new JLabel(""); 

        ChartCard(String title) {
            super(new BorderLayout());
            setOpaque(true);
            setBackground(new Color(250, 250, 252));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(235, 235, 242), 1, true),
                    new EmptyBorder(12, 12, 12, 12)
            ));

            // 타이틀 라벨
            JLabel t = new JLabel(title);
            t.setFont(UITheme.BODY_MED);
            t.setForeground(UITheme.TEXT);

            valueLabel.setFont(UITheme.H2 != null ? UITheme.H2.deriveFont(20f) : new Font("Dialog", Font.BOLD, 20));
            valueLabel.setForeground(UITheme.ACCENT_PURPLE);

            hint.setFont(UITheme.CAPTION);
            hint.setForeground(new Color(140, 140, 140));

            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            titlePanel.setOpaque(false);
            titlePanel.add(t);
            titlePanel.add(valueLabel);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(titlePanel, BorderLayout.WEST);
            header.add(hint, BorderLayout.EAST);

            chartHolder.setOpaque(false);
            chartHolder.setBorder(new EmptyBorder(10, 0, 0, 0));

            add(header, BorderLayout.NORTH);
            add(chartHolder, BorderLayout.CENTER);

            setPreferredSize(new Dimension(10, 260));
        }

        /**
         * 외부(MiniLineChart 등)에서 마우스 오버 시 숫자를 업데이트하기 위한 메서드
         * @param val 표시할 문자열 (예: ": 3")
         */
        
        public void updateValue(String val) {
            valueLabel.setText(val);
        }

        void setChart(JComponent chart) {
            chartHolder.removeAll();
            chartHolder.add(chart, BorderLayout.CENTER);
            
            // 차트 컴포넌트에 이 카드의 참조를 전달하여 통신 가능하게 함
            if (chart instanceof MiniLineChart) {
                ((MiniLineChart) chart).setParentCard(this);
            }
        }

        void setHint(String text) {
            hint.setText(text);
        }
    }

    public static class MiniLineChart extends JComponent {
        private final int[] data;
        private int hoverIndex = -1;
        private ChartCard parentCard; // 부모 ChartCard 참조를 위한 변수

        public MiniLineChart(int[] data) {
            this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(10, 120));

            // 마우스 움직임에 따라 부모 카드의 숫자를 업데이트
            addMouseMotionListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int n = data.length;
                    if (n == 0) return;

                    int pad = 14;
                    int gx0 = pad, gx1 = getWidth() - pad;
                    double segmentW = (n == 1) ? 0 : (double) (gx1 - gx0) / (n - 1);
                    int index = (segmentW == 0) ? 0 : (int) Math.round((e.getX() - gx0) / segmentW);

                    if (index >= 0 && index < n) {
                        if (hoverIndex != index) {
                            hoverIndex = index;
                            // 부모 카드가 설정되어 있다면 숫자를 ": 3" 형태로 전달
                            if (parentCard != null) {
                                parentCard.updateValue(": " + data[index]);
                            }
                            repaint();
                        }
                    }
                }
            });

            // 마우스가 그래프 영역을 나가면 숫자를 초기화
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hoverIndex = -1;
                    if (parentCard != null) {
                        parentCard.updateValue(""); // 숫자 숨김
                    }
                    repaint();
                }
            });
        }

        // ChartCard에서 자신을 등록할 때 사용하는 메서드
        public void setParentCard(ChartCard card) {
            this.parentCard = card;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 배경 및 테두리
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(230, 230, 238));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

            int pad = 14;
            int gx0 = pad, gy0 = pad, gx1 = w - pad, gy1 = h - pad;

            // 데이터 좌표 계산
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

            // 꺾은선 그리기
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(UITheme.ACCENT_PURPLE);
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }

            // 포인트(점) 그리기 및 호버 효과
            for (int i = 0; i < n; i++) {
                boolean isHover = (i == hoverIndex);
                int r = isHover ? 7 : 5; // 선택된 점은 더 크게

                if (isHover) {
                    // 선택된 지점 강조 (보라색 원)
                    g2.setColor(UITheme.ACCENT_PURPLE);
                    g2.fillOval(xs[i] - r, ys[i] - r, r * 2, r * 2);
                    
                    // 수직 점선 가이드 추가 (선택 사항, 가독성 향상)
                    g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                    g2.drawLine(xs[i], gy0, xs[i], gy1);
                } else {
                    // 일반 점 (흰색 바탕 + 보라색 테두리)
                    g2.setColor(Color.WHITE);
                    g2.fillOval(xs[i] - r, ys[i] - r, r * 2, r * 2);
                    g2.setColor(UITheme.ACCENT_PURPLE);
                    g2.drawOval(xs[i] - r, ys[i] - r, r * 2, r * 2);
                }
            }

            g2.dispose();
        }
    }

        public static class MiniBarChart extends JComponent {
            private final String[] labels;
            private final int[] values;
            private int hoverIndex = -1; // 마우스가 위치한 막대 인덱스

            public MiniBarChart(String[] labels, int[] values) {
                this.labels = labels;
                this.values = values;
                setOpaque(false);
                setPreferredSize(new Dimension(10, 120));

                addMouseMotionListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int n = values.length;
                        int padX = 14;
                        int gap = 10;
                        int barW = Math.max(12, (getWidth() - padX * 2 - gap * (n - 1)) / n);

                        // 마우스 X좌표를 기준으로 어떤 막대 위에 있는지 계산
                        int index = (e.getX() - padX) / (barW + gap);
                        
                        if (index >= 0 && index < n) {
                            // 실제 막대의 가로 범위 안에 있는지 체크
                            int barStart = padX + index * (barW + gap);
                            if (e.getX() >= barStart && e.getX() <= barStart + barW) {
                                if (hoverIndex != index) {
                                    hoverIndex = index;
                                    repaint();
                                }
                                return;
                            }
                        }
                        if (hoverIndex != -1) {
                            hoverIndex = -1;
                            repaint();
                        }
                    }
                });

                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoverIndex = -1;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                // 배경 (기존 코드 유지)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 16, 16);
                g2.setColor(new Color(230, 230, 238));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

                int padX = 14, padTop = 14, padBottom = 24;
                int gx0 = padX, gy1 = h - padBottom;
                int max = 1;
                for (int v : values) max = Math.max(max, v);

                int n = values.length, gap = 10;
                int barW = Math.max(12, (w - padX * 2 - gap * (n - 1)) / n);

                int x = gx0;
                for (int i = 0; i < n; i++) {
                    int v = values[i];
                    int bh = (int) ((gy1 - padTop) * (v / (double) max));
                    int y = gy1 - bh;

                    // 호버 시 색상 강조
                    boolean isHover = (i == hoverIndex);
                    Color fill = (i % 2 == 0) ? UITheme.ACCENT_PURPLE : new Color(0xCFC9FF);
                    if (isHover) fill = fill.darker();
                    
                    g2.setColor(fill);
                    g2.fillRoundRect(x, y, barW, bh, 10, 10);

                    // 텍스트 그리기
                    g2.setColor(isHover ? UITheme.ACCENT_PURPLE : new Color(120, 120, 120));
                    g2.setFont(isHover ? UITheme.CAPTION.deriveFont(Font.BOLD) : UITheme.CAPTION);
                    String lab = isHover ? v + "%" : labels[i]; // 호버 시 % 수치 표시
                    
                    int tw = g2.getFontMetrics().stringWidth(lab);
                    g2.drawString(lab, x + (barW - tw) / 2, h - 8);

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
