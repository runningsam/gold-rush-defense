package code123.games.crystal.wave;

import com.badlogic.gdx.utils.Array;
import code123.games.crystal.entities.Enemy;
import com.badlogic.gdx.math.Vector2;

public class WaveManager {
    private Array<Wave> waves;
    private int currentWave;
    private Wave activeWave;
    private Array<Vector2> pathPoints;
    private boolean isCompleted;
    private float waveBreakTimer;  // 波次间隔计时器
    private static final float WAVE_BREAK_DURATION = 10f;  // 波次间隔时间（秒）
    private boolean isBreak;  // 是否在休息阶段
    
    public WaveManager(Array<Vector2> pathPoints) {
        this.waves = new Array<>();
        this.currentWave = 0;
        this.pathPoints = pathPoints;
        this.isCompleted = false;
        this.isBreak = false;
        this.waveBreakTimer = 0;
        
        // 初始化波次
        initializeWaves();
        activeWave = waves.get(0);
    }
    
    private void initializeWaves() {
        // 第一波：5个普通怪物
        Wave wave1 = new Wave(2.0f);
        wave1.addUnit("normal", 5);
        waves.add(wave1);
        
        // 第二波：8个普通怪物
        Wave wave2 = new Wave(1.5f);
        wave2.addUnit("normal", 8);
        waves.add(wave2);
        
        // 第三波：10个普通怪物和2个精英怪物
        Wave wave3 = new Wave(1.0f);
        wave3.addUnit("normal", 10);
        wave3.addUnit("elite", 2);
        waves.add(wave3);
    }
    
    public Enemy update(float delta) {
        if (isCompleted) return null;
        
        if (isBreak) {
            waveBreakTimer -= delta;
            if (waveBreakTimer <= 0) {
                isBreak = false;
                currentWave++;
                if (currentWave < waves.size) {
                    activeWave = waves.get(currentWave);
                } else {
                    isCompleted = true;
                }
            }
            return null;
        }
        
        if (activeWave.update(delta)) {
            String enemyType = activeWave.getNextEnemyType();
            if (enemyType != null) {
                return createEnemy(enemyType);
            } else if (activeWave.isCompleted()) {
                isBreak = true;
                waveBreakTimer = WAVE_BREAK_DURATION;
            }
        }
        
        return null;
    }
    
    private Enemy createEnemy(String type) {
        String textureKey = "basic";
        if (type.equals("elite")) {
            textureKey = "elite";
        }
        
        Enemy enemy = new Enemy(textureKey, pathPoints);
        switch (type) {
            case "normal":
                enemy.setHealth(100f);
                enemy.setSpeed(100f);
                enemy.setReward(10);
                break;
            case "elite":
                enemy.setHealth(200f);
                enemy.setSpeed(80f);
                enemy.setReward(20);
                break;
        }
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
        return currentWave + 1;  // 转换为1-based索引
    }
    
    public int getTotalWaves() {
        return waves.size;
    }
} 