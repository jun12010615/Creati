package creati;

import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;

import creati.ui.auth.AuthFrame;
import creati.util.FontKit;
import creati.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1. Look & Feel
            FlatArcIJTheme.setup();

            // 2. 폰트 로드
            FontKit.init();

            // 3. UI 테마 초기화
            UITheme.ensureInit();

            // 4. 전체 기본 폰트 적용
            setUIFont(FontKit.regular(14f));

            // 5. 둥근 UI 옵션
            UIManager.put("Component.arc", 18);
            UIManager.put("Button.arc", 18);
            UIManager.put("TextComponent.arc", 14);

            new AuthFrame().setVisible(true);
        });
    }

    private static void setUIFont(Font font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}
