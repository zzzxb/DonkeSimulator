package cn.tofucat.donkey.utils;

import cn.tofucat.donkey.config.JsonConfig;
import cn.tofucat.donkey.config.JsonConfigLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class FontManager {

    private static final ObjectMap<String, BitmapFont> fontCache = new ObjectMap<>();
    private static boolean initialized = false;

    public static void init() {
        init("fonts/fonts.json");
    }

    public static void init(String configPath) {
        if (initialized) {
            return;
        }

        JsonConfig config = JsonConfigLoader.load(configPath);
        if (config == null) {
            Log.error("FontManager", "加载字体配置失败: {}", configPath);
            return;
        }

        Array<JsonConfig> fontConfigs = config.getArray("fonts");
        if (fontConfigs == null || fontConfigs.size == 0) {
            Log.warn("FontManager", "没有找到字体配置");
            return;
        }

        int loadedCount = 0;
        for (JsonConfig fc : fontConfigs) {
            if (!fc.getBoolean("enabled", true)) {
                continue;
            }

            String file = fc.getString("file");
            String alias = fc.getString("alias", file.replace(".ttf", "").replace(".otf", ""));
            Array<Integer> sizes = fc.getIntArray("sizes");

            String colorStr = fc.getString("color", "WHITE");
            int borderWidth = fc.getInt("borderWidth", 0);
            String borderColorStr = fc.getString("borderColor", "BLACK");

            Color color = parseColor(colorStr);
            Color borderColor = parseColor(borderColorStr);

            if (sizes == null || sizes.size == 0) {
                Log.warn("FontManager", "字体 {} 没有配置字号，跳过", file);
                continue;
            }

            for (int i = 0; i < sizes.size; i++) {
                int size = sizes.get(i);
                String key = alias + "_" + size;
                BitmapFont font = generateFont("fonts/" + file, size, color, borderWidth, borderColor);
                if (font != null) {
                    fontCache.put(key, font);
                    Log.info("FontManager", "加载字体: {} ({}px) -> {}", file, size, key);
                    loadedCount++;
                }
            }
        }

        initialized = true;
        Log.debug("FontManager", "字体初始化完成，共加载 {} 个字体", loadedCount);
    }

    private static BitmapFont generateFont(String fontPath, int size, Color color,
                                           int borderWidth, Color borderColor) {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        if (!fontFile.exists()) {
            Log.error("FontManager", "字体文件不存在: {}", fontPath);
            return null;
        }

        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

            parameter.size = size;
            parameter.color = color;
            parameter.borderWidth = borderWidth;
            parameter.borderColor = borderColor;
            parameter.characters = loadChineseCharacters();

            // ✅ 根据字号自动优化
            if (size <= 20) {
                parameter.gamma = 1.2f;
                parameter.renderCount = 2;
                parameter.borderWidth = 0;
            } else if (size <= 28) {
                // 中字号
                parameter.gamma = 0.85f;
                parameter.renderCount = 1;
            } else {
                // 大字号：更平滑
                parameter.gamma = 0.8f;
                parameter.renderCount = 1;
            }

            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;

            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();

            font.setUseIntegerPositions(true);

            return font;

        } catch (Exception e) {
            Log.error("FontManager", "生成字体失败: {} - {}", fontPath, e.getMessage());
            return null;
        }
    }

    private static String loadChineseCharacters() {
        try {
            FileHandle file = Gdx.files.internal("fonts/common.txt");
            if (!file.exists()) {
                Log.warn("FontManager", "fonts/common.txt 不存在");
                return getDefaultCharacters();
            }
            String content = file.readString();
            StringBuilder result = new StringBuilder();
            for (char c : content.toCharArray()) {
                if (result.indexOf(String.valueOf(c)) == -1) {
                    result.append(c);
                }
            }
            return result.toString();
        } catch (Exception e) {
            Log.error("FontManager", "加载 common.txt 失败: {}", e.getMessage());
            return getDefaultCharacters();
        }
    }

    private static String getDefaultCharacters() {
        return "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严龙飞" +
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "，。、；：！？\"\"''（）【】《》……—·～　" +
            "·「」『』◆◇○●□■△▲※☆★";
    }

    private static Color parseColor(String name) {
        if (name == null) return Color.WHITE;
        switch (name.toUpperCase()) {
            case "WHITE": return Color.WHITE;
            case "BLACK": return Color.BLACK;
            case "GOLD": return Color.GOLD;
            case "RED": return Color.RED;
            case "GREEN": return Color.GREEN;
            case "BLUE": return Color.BLUE;
            case "GRAY": return Color.GRAY;
            case "YELLOW": return Color.YELLOW;
            default: return Color.WHITE;
        }
    }

    public static BitmapFont getFont(String key) {
        if (!initialized) {
            Log.warn("FontManager", "未初始化，无法获取字体: {}", key);
            return null;
        }

        BitmapFont font = fontCache.get(key);
        if (font == null) {
            Log.warn("FontManager", "字体不存在: {}，返回 null", key);
            return null;
        }
        return font;
    }

    public static BitmapFont getFont(String alias, int size) {
        return getFont(alias + "_" + size);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void dispose() {
        for (BitmapFont font : fontCache.values()) {
            font.dispose();
        }
        fontCache.clear();
        initialized = false;
        Log.debug("FontManager", "字体资源已释放");
    }
}
