package cn.tofucat.donkey.manager;

import cn.tofucat.donkey.condition.ConditionManager;
import cn.tofucat.donkey.config.JsonConfig;
import cn.tofucat.donkey.config.JsonConfigLoader;
import cn.tofucat.donkey.ui.StoryLogDialog;
import cn.tofucat.donkey.ui.Toast;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

/**
 * 故事管理器
 * 负责加载故事、检查解锁、显示弹窗
 */
public class StoryManager {
    private static StoryManager instance;
    private final Array<StoryLogEntry> allStories = new Array<>();
    private int unlockedIndex = 0;
    private StoryLogDialog currentDialog;
    private TextureAtlas textureAtlas;
    private final static Array<String> tips = new Array<>();

    private StoryManager() {
        loadStories();
    }

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    public void setTextureAtlas(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public StoryLogDialog getCurrentDialog() {
        return currentDialog;
    }

    /**
     * 从 JSON 加载所有故事
     */
    private void loadStories() {
        JsonConfig config = JsonConfigLoader.load("config/story_logs.json");
        if (config == null) {
            Log.error(StoryManager.class, "加载故事配置失败");
            return;
        }

        Array<JsonConfig> logs = config.getArray("logs");
        for (JsonConfig log : logs) {
            String id = log.getString("id");
            String title = log.getString("title");
            String content = log.getString("content");
            int chapter = log.getInt("chapter");

            StoryLogEntry entry = new StoryLogEntry(id, title, content, chapter);
            allStories.add(entry);
        }

        // 注册故事解锁条件
        ConditionManager.getInstance().registerFromJson(logs);

        Log.debug(StoryManager.class, "加载故事: {} 条", allStories.size);
    }

    /**
     * 检查并获取新解锁的故事
     */
    public Array<StoryLogEntry> checkAndGetNewStories() {
        Array<StoryLogEntry> newStories = new Array<>();

        for (int i = unlockedIndex; i < allStories.size; i++) {
            StoryLogEntry entry = allStories.get(i);
            if (ConditionManager.getInstance().isUnlocked(entry.getId())) {
                newStories.add(entry);
                unlockedIndex = i + 1;
                Log.debug(StoryManager.class, "故事解锁: {} ({}/{})",
                    entry.getTitle(), i + 1, allStories.size);
            } else {
                break;
            }
        }

        return newStories;
    }

    /**
     * 显示故事弹窗
     */
    public void showStories(Stage stage, Array<StoryLogEntry> stories) {
        if (stories == null || stories.size == 0) {
            return;
        }

        if (currentDialog != null && !currentDialog.isFinished()) {
            currentDialog.close();
        }

        currentDialog = new StoryLogDialog(textureAtlas, stage, stories);
        currentDialog.show();
    }

    public void putTips(String tip) {
        tips.add(tip);
    }

    /**
     * 检查并显示新故事
     */
    public void checkAndShow(Stage stage) {
        Array<StoryLogEntry> newStories = checkAndGetNewStories();
        if (newStories.size > 0) {
            showStories(stage, newStories);
        }
    }

    /**
     * 获取所有故事（用于调试）
     */
    public Array<StoryLogEntry> getAllStories() {
        return allStories;
    }

    /**
     * 故事条目
     */
    public static class StoryLogEntry {
        private final String id;
        private final String title;
        private final String content;
        private final int chapter;

        public StoryLogEntry(String id, String title, String content, int chapter) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.chapter = chapter;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public int getChapter() {
            return chapter;
        }
    }
}
