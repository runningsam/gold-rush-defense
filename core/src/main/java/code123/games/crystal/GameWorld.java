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
import code123.games.crystal.story.StoryManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

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
    private static Array<Effect> effects = new Array<>();
    private GameState gameState;
    private EventManager eventManager;
    private float transitionTimer = 0;
        private static final float TRANSITION_DELAY = 3f; // 3秒后切换关卡
        
        public static GameWorld getInstance() {
            return instance;
        }
        
        public GameWorld(OrthographicCamera camera) {
            instance = this;
            this.camera = camera;
            
            // 从 StoryManager 获取当前关卡的地图路径
            String mapPath = StoryManager.getInstance().getCurrentMapPath();
            this.gameMap = new GameMap(mapPath);
            
            this.enemies = new Array<>();
            this.towers = new Array<>();
            
            // 从地图中获取路径点
            Array<Vector2> pathPoints = new Array<>(gameMap.getPathPoints().toArray(new Vector2[0]));
            this.waveManager = new WaveManager(pathPoints);
            
            // 从 StoryManager 获取初始配置
            StoryManager storyManager = StoryManager.getInstance();
            this.gold = storyManager.getInitialGold();
            this.lives = storyManager.getInitialLives();
            this.gameState = GameState.INTRO;
            this.eventManager = EventManager.getInstance();
        }
        
        public void update(float delta) {
            // 先检查特殊状态
            if (gameState == GameState.INTRO || 
                gameState == GameState.GAME_OVER || 
                gameState == GameState.VICTORY ||    // 添加 VICTORY 状态检查
                gameState == GameState.FINISHED) {   // 添加 FINISHED 状态检查
                return;
            }

            // 处理关卡切换
            if (gameState == GameState.TRANSITIONING) {
                transitionTimer += delta;
                if (transitionTimer >= TRANSITION_DELAY) {
                    Gdx.app.log("GameWorld", "Transition complete, resetting game");
                    resetGame();
                    transitionTimer = 0;
                    eventManager.emit("levelTransitionEnd");
                }
                return;
            }

            // 检查游戏失败条件
            if (lives <= 0) {
                gameState = GameState.GAME_OVER;
                eventManager.emit("gameOver");
                return;
            }

            // 检查游戏胜利条件
            if (waveManager.isCompleted() && enemies.size == 0) {
                handleLevelVictory();
                return;
            }

            // 正常游戏逻辑更新...
            
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
            // 修改条件，允许在 INTRO、PLAYING、VICTORY 状态下渲染
            if (gameState != GameState.PLAYING && 
                gameState != GameState.INTRO && 
                gameState != GameState.VICTORY) {
                return;
            }

            // 渲染地图
            gameMap.render(camera);
            
            // 只在 PLAYING 状态下渲染其他游戏元素
            if (gameState == GameState.PLAYING) {
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
        }
    
        public boolean canBuildTower(int cost) {
            return gold >= cost;
        }
    
        public void spendGold(int amount) {
            gold -= amount;
        }
    
        /**
         * Checks whether a tower can be placed at the specified position.
         * This method first uses GameMap's canBuildTowerAt(x,y) — which returns true if the tile is buildable
         * (i.e. not a road) — and then verifies that no tower is already present in that cell.
         *
         * @param position the world coordinate for the tower placement.
         * @return true if the tower can be placed; false otherwise.
         */
        public boolean canPlaceTowerAt(Vector2 position) {
            // 1. Check that this cell is buildable (i.e. not a road) as defined by the map.
            if (!gameMap.canBuildTowerAt(position.x, position.y)) {
                return false;
            }
            
            // 2. Convert the world coordinate to tile indices.
            int tileX = (int)(position.x / TILE_SIZE);
            int tileY = (int)(position.y / TILE_SIZE);
            
            // 3. Ensure no other tower occupies this tile.
            for (Tower tower : towers) {
                int towerTileX = (int)(tower.getPosition().x / TILE_SIZE);
                int towerTileY = (int)(tower.getPosition().y / TILE_SIZE);
                if (towerTileX == tileX && towerTileY == tileY) {
                    return false;
                }
            }
            return true;
        }
    
        /**
         * Attempts to build a tower of the specified type at the given position.
         * It first verifies, using canPlaceTowerAt(), that the position is valid (i.e.
         * buildable and unoccupied). If so, it creates the tower (provided there is enough gold)
         * and deducts the cost.
         *
         * @param type the type of tower to build (e.g. "arrow", "magic").
         * @param position the world coordinate where the tower should be placed.
         * @return true if the tower was built; false otherwise.
         */
        public boolean buildTower(String type, Vector2 position) {
            // Use the unified check before attempting to build.
            if (!canPlaceTowerAt(position)) {
                System.out.println("Cannot build here: either not buildable or already occupied.");
                return false;
            }
            
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
                // Add additional tower types here if needed.
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

        public void resetGame() {
            Gdx.app.log("GameWorld", "Resetting game state");
            
            // 重置游戏状态
            StoryManager storyManager = StoryManager.getInstance();
            String mapPath = storyManager.getCurrentMapPath();
            Gdx.app.log("GameWorld", "Loading new map: " + mapPath);
            
            this.gameMap = new GameMap(mapPath);
            this.enemies.clear();
            this.towers.clear();
            effects.clear();
            
            Array<Vector2> pathPoints = new Array<>(gameMap.getPathPoints().toArray(new Vector2[0]));
            this.waveManager = new WaveManager(pathPoints);
            
            this.gold = storyManager.getInitialGold();
            this.lives = storyManager.getInitialLives();
            
            // 设置为介绍状态，而不是直接开始游戏
            this.gameState = GameState.INTRO;
            
            // 通知 GameScreen 显示介绍对话框和更新HUD
            eventManager.emit("gameReset");
            eventManager.emit("levelChanged");
        }
    
        public float getTransitionTimer() {
            return TRANSITION_DELAY - transitionTimer;
        }
    
        public boolean hasNextLevel() {
            return StoryManager.getInstance().hasNextLevel();
        }
    
        public void startLevelTransition() {
            Gdx.app.log("GameWorld", "Starting level transition");
            gameState = GameState.TRANSITIONING;
            transitionTimer = 0;
            eventManager.emit("levelTransitionStart");
        }

        public void finish() {
            Gdx.app.log("GameWorld", "Finishing game");
            gameState = GameState.FINISHED;
            eventManager.emit("gameFinished");
        }

        public String getCurrentLevelId() {
            return StoryManager.getInstance().getCurrentLevelId();
        }

        public void setGameState(GameState state) {
            Gdx.app.log("GameWorld", "Setting game state: " + state);
            this.gameState = state;
        }

        public String getLevelTitle() {
            return StoryManager.getInstance().getCurrentLevelTitle();
        }

        public void saveGame() {
            StoryManager.getInstance().saveProgress();
        }
        
        // 在关卡胜利时保存进度
        private void handleLevelVictory() {
            gameState = GameState.VICTORY;
            saveGame();  // 保存当前关卡的进度
            eventManager.emit("levelVictory");
        }

        // 添加新方法用于处理游戏完成
        public void handleGameFinished() {
            gameState = GameState.FINISHED;
            eventManager.emit("gameFinished");
        }
}
