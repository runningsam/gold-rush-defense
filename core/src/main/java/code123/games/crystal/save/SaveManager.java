package code123.games.crystal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SaveManager {
    private static SaveManager instance;
    private static final String PREFS_NAME = "crystal_game_save";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_HIGHEST_LEVEL = "highest_level";
    private static final String KEY_TOTAL_GOLD = "total_gold";
    private static final String KEY_ACHIEVEMENTS = "achievements";
    private Preferences prefs;
    
    private SaveManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }
    
    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }
    
    public void saveProgress(String currentLevel, int totalGold) {
        Gdx.app.log("SaveManager", "Saving progress - Level: " + currentLevel + ", Gold: " + totalGold);
        prefs.putString(KEY_CURRENT_LEVEL, currentLevel);
        prefs.putInteger(KEY_TOTAL_GOLD, totalGold);
        
        // 更新最高关卡
        String highestLevel = prefs.getString(KEY_HIGHEST_LEVEL, "level1");
        if (shouldUpdateHighestLevel(currentLevel, highestLevel)) {
            prefs.putString(KEY_HIGHEST_LEVEL, currentLevel);
        }
        
        prefs.flush();
    }
    
    private boolean shouldUpdateHighestLevel(String currentLevel, String highestLevel) {
        // 简单的关卡比较逻辑，可以根据需要修改
        int current = extractLevelNumber(currentLevel);
        int highest = extractLevelNumber(highestLevel);
        return current > highest;
    }
    
    private int extractLevelNumber(String levelId) {
        return Integer.parseInt(levelId.replaceAll("\\D+", ""));
    }
    
    public String getCurrentLevel() {
        String level = prefs.getString(KEY_CURRENT_LEVEL, "level1");
        Gdx.app.log("SaveManager", "Loading saved level: " + level);
        return level;
    }
    
    public String getHighestLevel() {
        return prefs.getString(KEY_HIGHEST_LEVEL, "level1");
    }
    
    public int getTotalGold() {
        return prefs.getInteger(KEY_TOTAL_GOLD, 0);
    }
    
    public void resetProgress() {
        prefs.clear();
        prefs.flush();
    }
    
    public boolean hasSavedGame() {
        return prefs.contains(KEY_CURRENT_LEVEL);
    }
    
    public void clearProgress() {
        prefs.clear();
        prefs.flush();
    }
} 