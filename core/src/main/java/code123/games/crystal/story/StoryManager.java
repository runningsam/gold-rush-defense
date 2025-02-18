package code123.games.crystal.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import code123.games.crystal.save.SaveManager;
import code123.games.crystal.GameWorld;

public class StoryManager {
    private static StoryManager instance;
    private JsonValue storyData;
    private String currentLanguage = "en";
    private String currentLevelId = "level1";
    private JsonValue currentLevel;
    
    private StoryManager() {
        loadStoryData();
    }
    
    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }
    
    private void loadStoryData() {
        try {
            String jsonString = Gdx.files.internal("story/story.json").readString();
            storyData = new JsonReader().parse(jsonString);
            setLevel(currentLevelId); // 设置默认关卡
        } catch (Exception e) {
            Gdx.app.error("StoryManager", "Error loading story data", e);
        }
    }
    
    public void setLevel(String levelId) {
        JsonValue levels = storyData.get("levels");
        for (JsonValue level : levels) {
            if (level.getString("id").equals(levelId)) {
                currentLevel = level;
                currentLevelId = levelId;
                return;
            }
        }
        Gdx.app.error("StoryManager", "Level " + levelId + " not found");
    }
    
    public String getCurrentMapPath() {
        return currentLevel.getString("map");
    }
    
    public int getInitialGold() {
        return currentLevel.getInt("initialGold");
    }
    
    public int getInitialLives() {
        return currentLevel.getInt("initialLives");
    }
    
    public void setLanguage(String language) {
        if (currentLevel.get("translations").has(language)) {
            currentLanguage = language;
        } else {
            Gdx.app.log("StoryManager", "Language " + language + " not found, using default");
        }
    }
    
    public String getTitle() {
        return currentLevel.get("translations").get(currentLanguage).getString("title");
    }
    
    public String getIntroTitle() {
        return currentLevel.get("translations").get(currentLanguage).get("intro").getString("title");
    }
    
    public String[] getIntroContent() {
        JsonValue content = currentLevel.get("translations").get(currentLanguage).get("intro").get("content");
        String[] lines = new String[content.size];
        for (int i = 0; i < content.size; i++) {
            lines[i] = content.getString(i);
        }
        return lines;
    }
    
    public String getVictoryTitle() {
        return currentLevel.get("translations").get(currentLanguage).get("victory").getString("title");
    }
    
    public String[] getVictoryContent() {
        JsonValue content = currentLevel.get("translations").get(currentLanguage).get("victory").get("content");
        String[] lines = new String[content.size];
        for (int i = 0; i < content.size; i++) {
            lines[i] = content.getString(i);
        }
        return lines;
    }
    
    public String getDefeatTitle() {
        return currentLevel.get("translations").get(currentLanguage).get("defeat").getString("title");
    }
    
    public String[] getDefeatContent() {
        JsonValue content = currentLevel.get("translations").get(currentLanguage).get("defeat").get("content");
        String[] lines = new String[content.size];
        for (int i = 0; i < content.size; i++) {
            lines[i] = content.getString(i);
        }
        return lines;
    }
    
    public JsonValue getWavesConfig() {
        return currentLevel.get("waves");
    }
    
    public JsonValue getEnemyConfig(String type) {
        JsonValue waves = getWavesConfig();
        for (JsonValue wave : waves) {
            for (JsonValue enemy : wave.get("enemies")) {
                if (enemy.getString("type").equals(type)) {
                    return enemy;
                }
            }
        }
        throw new RuntimeException("Enemy type not found: " + type);
    }
    
    public boolean hasNextLevel() {
        JsonValue levels = storyData.get("levels");
        int currentIndex = -1;
        
        // 找到当前关卡的索引
        for (int i = 0; i < levels.size; i++) {
            if (levels.get(i).getString("id").equals(currentLevelId)) {
                currentIndex = i;
                break;
            }
        }
        
        // 检查是否有下一关
        boolean hasNext = currentIndex != -1 && currentIndex < levels.size - 1;
        Gdx.app.log("StoryManager", "Current level: " + currentLevelId + 
                    ", Index: " + currentIndex + 
                    ", Has next: " + hasNext);
        return hasNext;
    }

    public void moveToNextLevel() {
        if (!hasNextLevel()) return;
        
        JsonValue levels = storyData.get("levels");
        boolean found = false;
        for (int i = 0; i < levels.size; i++) {
            if (levels.get(i).getString("id").equals(currentLevelId)) {
                String nextLevelId = levels.get(i + 1).getString("id");
                Gdx.app.log("StoryManager", "Moving from level " + currentLevelId + " to " + nextLevelId);
                setLevel(nextLevelId);
                found = true;
                break;
            }
        }
        if (!found) {
            Gdx.app.error("StoryManager", "Current level not found when trying to move to next level");
        }
    }

    public String getCurrentLevelId() {
        return currentLevelId;
    }

    public String getGameIntroTitle(String language) {
        return storyData.get("gameIntro").get(language).get("title").asString();
    }

    public String[] getGameIntroStory(String language) {
        JsonValue storyArray = storyData.get("gameIntro").get(language).get("story");
        String[] story = new String[storyArray.size];
        int i = 0;
        for (JsonValue line : storyArray) {
            story[i++] = line.asString();
        }
        return story;
    }

    public String getCurrentLevelTitle() {
        return currentLevel.get("translations").get(currentLanguage).get("title").asString();
    }

    public void loadSavedProgress() {
        SaveManager saveManager = SaveManager.getInstance();
        if (saveManager.hasSavedGame()) {
            String savedLevel = saveManager.getCurrentLevel();
            Gdx.app.log("StoryManager", "Loading saved level: " + savedLevel);
            setLevel(savedLevel);
        }
    }
    
    public void saveProgress() {
        String nextLevelId = getNextLevelId();
        if (nextLevelId != null) {
            // 还有下一关，保存下一关的信息
            SaveManager.getInstance().saveProgress(
                nextLevelId,  // 保存下一关的ID
                getInitialGold()  // 使用下一关的初始金币
            );
        } else {
            // 已经是最后一关，清除存档，这样玩家下次打开时从第一关开始
            SaveManager.getInstance().clearProgress();
            
            // 可以选择保存一些统计信息，比如通关记录
            // SaveManager.getInstance().saveGameCompletion();
        }
    }
    
    private String getNextLevelId() {
        JsonValue levels = storyData.get("levels");
        int currentIndex = -1;
        
        // 找到当前关卡的索引
        for (int i = 0; i < levels.size; i++) {
            if (levels.get(i).getString("id").equals(currentLevelId)) {
                currentIndex = i;
                break;
            }
        }
        
        // 如果找到当前关卡且不是最后一关
        if (currentIndex >= 0 && currentIndex < levels.size - 1) {
            return levels.get(currentIndex + 1).getString("id");
        }
        
        return null;
    }
    
    // 获取当前关卡的金币
    private int getCurrentLevelGold() {
        return currentLevel.getInt("initialGold", 0);
    }

    public float getWaveBreakDuration() {
        return currentLevel.getFloat("waveBreakDuration", 5.0f);  // 默认5秒
    }
} 