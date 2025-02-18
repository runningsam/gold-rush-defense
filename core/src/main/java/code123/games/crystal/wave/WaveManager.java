package code123.games.crystal.wave;

import com.badlogic.gdx.utils.Array;
import code123.games.crystal.entities.Enemy;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import code123.games.crystal.story.StoryManager;
import com.badlogic.gdx.Gdx;

public class WaveManager {
    private Array<Wave> waves;
    private int currentWave;
    private Wave activeWave;
    private Array<Vector2> pathPoints;
    private boolean isCompleted;
    private float waveBreakTimer;
    private float waveBreakDuration;  // 从配置中读取的波次间隔时间
    private boolean isBreak;
    
    public WaveManager(Array<Vector2> pathPoints) {
        this.waves = new Array<>();
        this.currentWave = 0;
        this.pathPoints = pathPoints;
        this.isCompleted = false;
        this.isBreak = false;
        this.waveBreakTimer = 0;
        
        // 从 StoryManager 获取波次间隔时间
        this.waveBreakDuration = StoryManager.getInstance().getWaveBreakDuration();
        
        // 初始化波次
        initializeWaves();
        activeWave = waves.get(0);
    }
    
    private void initializeWaves() {
        JsonValue wavesConfig = StoryManager.getInstance().getWavesConfig();
        
        for (JsonValue waveData : wavesConfig) {
            Wave wave = new Wave(waveData.getFloat("spawnInterval"));
            
            for (JsonValue enemyData : waveData.get("enemies")) {
                wave.addUnit(
                    enemyData.getString("type"),
                    enemyData.getInt("count")
                );
            }
            waves.add(wave);
        }
    }
    
    public Enemy update(float delta) {
        if (isCompleted) return null;
        
        if (isBreak) {
            waveBreakTimer -= delta;
            if (waveBreakTimer <= 0) {
                isBreak = false;
                if (currentWave + 1 < waves.size) {
                    currentWave++;
                    activeWave = waves.get(currentWave);
                    Gdx.app.log("WaveManager", "Switching to next wave: " + (currentWave + 1) + 
                               "/" + waves.size);
                } else {
                    isCompleted = true;
                    Gdx.app.log("WaveManager", "All waves completed");
                }
            }
            return null;
        }
        
        if (activeWave.update(delta)) {
            String enemyType = activeWave.getNextEnemyType();
            if (enemyType != null) {
                Gdx.app.log("WaveManager", "Spawning enemy: " + enemyType + 
                           " in wave " + (currentWave + 1) + "/" + waves.size);
                return createEnemy(enemyType);
            }
        }
        if (activeWave.isCompleted()) {
            Gdx.app.log("WaveManager", "Current wave " + (currentWave + 1) + 
                       " completed, hasNextWave: " + (currentWave + 1 < waves.size));
            if (currentWave + 1 < waves.size) {
                isBreak = true;
                waveBreakTimer = waveBreakDuration;
                Gdx.app.log("WaveManager", "Starting break timer: " + waveBreakDuration + 
                           ", current wave: " + (currentWave + 1));
            } else {
                isCompleted = true;
                Gdx.app.log("WaveManager", "All waves completed");
            }
            return null;
        }

        return null;
    }
    
    private Enemy createEnemy(String type) {
        JsonValue enemyConfig = StoryManager.getInstance().getEnemyConfig(type);
        
        Enemy enemy = new Enemy(type, pathPoints);
        enemy.setHealth(enemyConfig.getFloat("health"));
        enemy.setSpeed(enemyConfig.getFloat("speed"));
        enemy.setReward(enemyConfig.getInt("reward"));
        
        return enemy;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public float getBreakTimeRemaining() {
        return isBreak ? waveBreakTimer : 0;
    }
    
    public boolean isInBreak() {
        return isBreak;
    }
    
    public int getCurrentWave() {
        // 如果已完成，返回最后一波的索引
        if (isCompleted) {
            return waves.size;
        }
        return Math.min(currentWave + 1, waves.size);  // 确保不会超过总波数
    }
    
    public int getTotalWaves() {
        return waves.size;
    }
} 