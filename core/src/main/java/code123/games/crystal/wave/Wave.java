package code123.games.crystal.wave;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;

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
        if (isCompleted) {
            return false;
        }
        
        timeSinceLastSpawn += delta;
        
        if (timeSinceLastSpawn >= spawnInterval) {
            timeSinceLastSpawn = 0;
            Gdx.app.log("Wave", "Ready to spawn next enemy, interval: " + spawnInterval);
            return true;
        }
        
        return false;
    }
    
    public String getNextEnemyType() {
        if (isCompleted || currentUnit >= units.size) {
            Gdx.app.log("Wave", "No more enemies to spawn, completed: " + isCompleted + 
                       ", currentUnit: " + currentUnit + "/" + units.size);
            return null;
        }
        
        WaveUnit unit = units.get(currentUnit);
        unit.count--;
        
        String enemyType = unit.enemyType;
        
        if (unit.count <= 0) {
            currentUnit++;
            if (currentUnit >= units.size) {
                isCompleted = true;
                Gdx.app.log("Wave", "Wave completed, all units spawned");
            }
        }
        
        return enemyType;
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