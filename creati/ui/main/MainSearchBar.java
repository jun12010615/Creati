package creati.ui.main;

import creati.util.FontKit;
import creati.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

/**
 * MainSearchBar (버튼 없는 즉시 검색)
 * - 왼쪽: 검색 아이콘
 * - 가운데: 입력창
 *
 * TODO(BE):
 *  - 실제 검색 로직(DB/서비스 호출)은 onSearch 콜백에서 처리
 *  - 예) searchBar.setOnSearch(q -> challengeView.setQuery(q));
 *  - (권장) 백엔드 연결 시에도 이 onSearch만 이어주면 됨
 */
public class MainSearchBar extends JPanel {

    private final JTextField field = new JTextField();
    private Consumer<String> onSearch = s -> {};

    // 입력 디바운스(너무 자주 조회되는 것 방지)
    private final Timer debounceTimer;
    private static final int DEBOUNCE_MS = 180;

    public MainSearchBar() {
        UITheme.ensureInit();
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));

        // ===== 입력 영역(아이콘 + 필드) =====
        JPanel inputWrap = new JPanel(new BorderLayout(10, 0));
        inputWrap.setBackground(Color.WHITE);
        inputWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 240), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel icon = new JLabel(makeSearchIconText());
        icon.setForeground(new Color(130, 130, 145));
        icon.setFont(getMaterialIconFontOrFallback(18f));

        field.setBorder(BorderFactory.createEmptyBorder());
        field.setFont(UITheme.BODY);
        field.setForeground(UITheme.TEXT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(UITheme.TEXT);

        // Enter 눌러도 그냥 즉시 검색
        field.addActionListener(e -> fireSearchNow());

        inputWrap.add(icon, BorderLayout.WEST);
        inputWrap.add(field, BorderLayout.CENTER);

        add(inputWrap, BorderLayout.CENTER);

        // ===== debounce timer =====
        debounceTimer = new Timer(DEBOUNCE_MS, e -> fireSearchNow());
        debounceTimer.setRepeats(false);

        // 타이핑하면 바로 조회 (버튼 없음)
        field.getDocument().addDocumentListener(new DocumentListener() {
            private void changed() {
                debounceTimer.restart();

                if (field.getText().trim().isEmpty()) {
                    debounceTimer.stop();
                    onSearch.accept("");
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
        });
    }

    /**
     * TODO(BE):
     * - 즉시 검색 트리거 구간
     * - 지금은 onSearch 콜백만 호출
     */
    private void fireSearchNow() {
        String q = field.getText().trim();
        onSearch.accept(q);
    }

    public void setOnSearch(Consumer<String> c) {
        this.onSearch = (c == null) ? (s -> {}) : c;
    }

    public void setQuery(String text) {
        field.setText(text == null ? "" : text);
    }

    public String getQuery() {
        return field.getText().trim();
    }

    public void clear() {
        setQuery("");
        onSearch.accept(""); 
    }

    // =========================
    // UI Helpers
    // =========================

    private String makeSearchIconText() {
        try {
            return new String(Character.toChars(0xE8B6));
        } catch (Exception e) {
            return "🔎";
        }
    }

    private Font getMaterialIconFontOrFallback(float size) {
        try {
            Font f = FontKit.materialIcon(size); // 프로젝트에 이미 있는 전제
            return (f != null) ? f : UITheme.BODY;
        } catch (Exception e) {
            return UITheme.BODY;
        }
    }
}
