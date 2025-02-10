package code123.games.crystal.wave;

import com.badlogic.gdx.utils.Array;

public class Wave {
    private Array<WaveUnit> units;
    private float spawnInterval;
    private int currentUnit;
    private float timeSinceLastSpawn;
    private boolean isCompleted;
    
    public Wave(float spawnInterval) {
        this.units = new Array<>();
        this.spawnInterval = spawnInterval;
        this.currentUnit = 0;
        this.timeSinceLastSpawn = 0;
        this.isCompleted = false;
    }
    
    public void addUnit(String enemyType, int count) {
        units.add(new WaveUnit(enemyType, count));
    }
    
    public boolean update(float delta) {
        if (isCompleted) return true;
        
        timeSinceLastSpawn += delta;
        
        if (timeSinceLastSpawn >= spawnInterval) {
            timeSinceLastSpawn = 0;
            return true;
        }
        
        return false;
    }
    
    public String getNextEnemyType() {
        if (currentUnit >= units.size) return null;
        
        WaveUnit unit = units.get(currentUnit);
        unit.count--;
        
        if (unit.count <= 0) {
            currentUnit++;
        }
        
        if (currentUnit >= units.size) {
            isCompleted = true;
        }
        
        return unit.enemyType;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    private static class WaveUnit {
        String enemyType;
        int count;
        
        WaveUnit(String enemyType, int count) {
            this.enemyType = enemyType;
            this.count = count;
        }
    }
} 