package com.creati.util;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontKit {

    private static final Map<String, Font> base = new HashMap<>();
    private static boolean inited = false;

    public static void init() {
        if (inited) return;

        // assets/fonts에 넣은 Pretendard 9종 사용
        load("Thin", "/main/resources/fonts/Pretendard-Thin.ttf");
        load("ExtraLight", "/main/resources/fonts/Pretendard-ExtraLight.ttf");
        load("Light", "/main/resources/fonts/Pretendard-Light.ttf");
        load("Regular", "/main/resources/fonts/Pretendard-Regular.ttf");
        load("Medium", "/main/resources/fonts/Pretendard-Medium.ttf");
        load("SemiBold", "/main/resources/fonts/Pretendard-SemiBold.ttf");
        load("Bold", "/main/resources/fonts/Pretendard-Bold.ttf");
        load("ExtraBold", "/main/resources/fonts/Pretendard-ExtraBold.ttf");
        load("Black", "/main/resources/fonts/Pretendard-Black.ttf");
        
        // 로고용 esamanru
        load("EsamanruBold", "/main/resources/fonts/esamanru-Bold.ttf");

     // Material Icons
        load("MaterialIcons", "/main/resources/fonts/MaterialIcons-Regular.ttf");

        inited = true;
    }

    private static void load(String key, String path) {
        try (InputStream is = FontKit.class.getResourceAsStream(path)) {
            if (is == null) throw new IllegalStateException("폰트 파일을 찾을 수 없음: " + path);
            Font f = Font.createFont(Font.TRUETYPE_FONT, is);
            base.put(key, f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
        } catch (Exception e) {
            e.printStackTrace();
            // fallback
            base.put(key, new Font("SansSerif", Font.PLAIN, 12));
        }
    }

    private static Font get(String key, float size) {
        Font f = base.getOrDefault(key, new Font("SansSerif", Font.PLAIN, 12));
        return f.deriveFont(Font.PLAIN, size);
    }
    
    public static Font materialIcon(float size) {
        return get("MaterialIcons", size);
    }


    public static Font thin(float size) { return get("Thin", size); }
    public static Font extraLight(float size) { return get("ExtraLight", size); }
    public static Font light(float size) { return get("Light", size); }
    public static Font regular(float size) { return get("Regular", size); }
    public static Font medium(float size) { return get("Medium", size); }
    public static Font semiBold(float size) { return get("SemiBold", size); }
    public static Font bold(float size) { return get("Bold", size); }
    public static Font extraBold(float size) { return get("ExtraBold", size); }
    public static Font black(float size) { return get("Black", size); }
    public static Font esamanruBold(float size) { return get("EsamanruBold", size);
    }

}
