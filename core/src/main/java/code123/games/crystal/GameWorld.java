package code123.games.crystal;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import code123.games.crystal.entities.Tower;
import code123.games.crystal.entities.Enemy;
import code123.games.crystal.entities.towers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import code123.games.crystal.map.GameMap;
import code123.games.crystal.wave.WaveManager;
import code123.games.crystal.effects.Effect;

public class GameWorld {
    private static GameWorld instance;
    private static final float TILE_SIZE = 32f;  // 添加瓦片大小常量
    private static final int MAX_LIVES = 20;  // Add at top with other constants
    
    private WaveManager waveManager;
    private Array<Enemy> enemies;
    private Array<Tower> towers;
    private GameMap gameMap;
    private int gold;
    private int lives;
    private OrthographicCamera camera;
    private Tower selectedTower; // 当前选中的防御塔类型
    private Vector2 buildPosition; // 建造位置
    private static Array<Effect> effects = new Array<>();
    private GameState gameState;
    
    public static GameWorld getInstance() {
        return instance;
    }
    
    public GameWorld(OrthographicCamera camera) {
        instance = this;  // 在构造函数中设置实例
        this.camera = camera;
        this.gameMap = new GameMap("maps/level1.tmx");
        this.enemies = new Array<>();
        this.towers = new Array<>();
        
        // 从地图中获取路径点
        Array<Vector2> pathPoints = new Array<>(gameMap.getPathPoints().toArray(new Vector2[0]));
        this.waveManager = new WaveManager(pathPoints);
        
        this.gold = 500;  // 增加初始金币
        this.lives = 20;
        this.gameState = GameState.PLAYING;
    }
    
    public void update(float delta) {
        if (gameState != GameState.PLAYING) {
            return;
        }
        
        // 检查游戏失败条件
        if (lives <= 0) {
            gameState = GameState.GAME_OVER;
            return;
        }
        
        // 检查游戏胜利条件
        if (waveManager.isCompleted() && enemies.size == 0) {
            gameState = GameState.VICTORY;
            return;
        }
        
        // 更新波次和生成怪物
        Enemy newEnemy = waveManager.update(delta);
        if (newEnemy != null) {
            enemies.add(newEnemy);
        }
        
        // 更新所有防御塔
        for (Tower tower : towers) {
            tower.update(delta, enemies);
        }
        
        // 更新所有怪物
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta);
            
            if (enemy.hasReachedEnd()) {
                lives--;
                enemies.removeIndex(i);
            } else if (enemy.isDead()) {
                gold += enemy.getReward();
                enemies.removeIndex(i);
            }
        }
        
        // 更新效果
        for (int i = effects.size - 1; i >= 0; i--) {
            Effect effect = effects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                effects.removeIndex(i);
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        // 渲染地图
        gameMap.render(camera);
        
        // 渲染防御塔
        for (Tower tower : towers) {
            if (tower.getSprite() != null) {
                tower.getSprite().draw(batch);
            }
        }
        
        // 渲染敌人
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
        
        // 渲染效果
        for (Effect effect : effects) {
            effect.render(batch);
        }
    }

    public boolean canBuildTower(int cost) {
        return gold >= cost;
    }

    public void spendGold(int amount) {
        gold -= amount;
    }

    public boolean buildTower(String type, Vector2 position) {
        // 检查是否可以在该位置建造
        if (!gameMap.canBuildTowerAt(position.x, position.y)) {
            return false;
        }
        
        // 检查是否已经有塔在这个位置
        for (Tower tower : towers) {
            if (tower.getPosition().dst(position) < TILE_SIZE) {
                return false;
            }
        }
        
        // 创建新塔
        Tower tower = null;
        int cost = 0;
        switch (type) {
            case "arrow":
                cost = 100;
                if (gold >= cost) {
                    tower = new ArrowTower(position);
                }
                break;
            case "magic":
                cost = 150;
                if (gold >= cost) {
                    tower = new MagicTower(position);
                }
                break;
        }
        
        if (tower != null) {
            towers.add(tower);
            gold -= cost;
            System.out.println("Tower built! Remaining gold: " + gold);
            return true;
        }
        
        System.out.println("Not enough gold! Required: " + cost + ", Have: " + gold);
        return false;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public int getGold() {
        return gold;
    }

    public int getLives() {
        return lives;
    }

    public Array<Tower> getTowers() {
        return towers;
    }

    public void dispose() {
        gameMap.dispose();
    }

    public static void addEffect(Effect effect) {
        effects.add(effect);
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getMaxLives() {
        return MAX_LIVES;
    }

    public void setGameState(GameState finished) {
       gameState = finished;
    }
}
