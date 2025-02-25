package code123.games.crystal;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import code123.games.crystal.ui.GameHUD;
import code123.games.crystal.story.StoryManager;
import code123.games.crystal.ui.BuildToolbar;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.InputMultiplexer;
import code123.games.crystal.ui.GameDialogs;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class GameScreen implements Screen {
    private final Main game;
    private GameWorld gameWorld;
    private OrthographicCamera camera;
    private Viewport viewport;
    private String selectedTowerType = null;
    
    private GameHUD hud;
    private BuildToolbar buildToolbar;
    private Vector2 hoverCell = new Vector2(-1, -1);
    
    private GameDialogs gameDialogs;
    
    public GameScreen(Main game) {
        this.game = game;
        
        // 创建相机和视口
        camera = new OrthographicCamera();
        // 使用地图大小 (25 * 32 = 800, 15 * 32 = 480)
        camera.setToOrtho(false, 800, 480);
        // 创建 FitViewport，使用固定的游戏世界大小
        viewport = new FitViewport(800, 480, camera);
        camera.update();
    
        // 确保资源已加载
        AssetManager.getInstance().loadAssets();

        StoryManager storyManager = StoryManager.getInstance();
        storyManager.setLanguage("en");
        this.gameWorld = new GameWorld(camera);

        this.hud = new GameHUD(gameWorld);
        this.buildToolbar = new BuildToolbar(gameWorld, towerType -> {
            Gdx.app.log("GameScreen", "Tower type selected: " + towerType);
            selectedTowerType = towerType;  // 通过回调更新选择的塔类型
        });

        EventManager eventManager = EventManager.getInstance(); 
        eventManager.on("levelTransitionStart", ()->{
            gameDialogs.showTransitionDialog(
                "Next Level",
                String.format("Level will start in %.0f seconds...", 
                gameWorld.getTransitionTimer())
            );
            Gdx.app.log("GameScreen", "Level transition started");
            Gdx.app.log("GameScreen", "Game state: " + gameWorld.getGameState());
        });
        eventManager.on("levelTransitionEnd", ()->{
            gameDialogs.hideTransitionDialog();
        });
        eventManager.on("gameFinished", () -> {
            gameDialogs.showGameFinishedDialog(() -> {
                Gdx.app.log("GameScreen", "Game finished dialog confirmed");
                game.setScreen(new IntroScreen(game));
            });
        });
        eventManager.on("gameReset", ()->{
            Gdx.app.log("GameScreen", "Game reset");
            gameDialogs.showStoryDialog(() -> {
                Gdx.app.log("GameScreen", "Story dialog confirmed");
                gameWorld.setGameState(GameState.PLAYING);
            });
        });
        eventManager.on("gameOver", ()->{
            Gdx.app.log("GameScreen", "Game over");
            gameDialogs.showGameOverDialog(() -> {
                Gdx.app.log("GameScreen", "Game over dialog confirmed");
                this.gameWorld.resetGame();
            });
        });
        eventManager.on("levelVictory", () -> {
            gameDialogs.showVictoryDialog(() -> {
                Gdx.app.log("GameScreen", "Victory dialog confirmed");
                if (gameWorld.hasNextLevel()) {
                    StoryManager.getInstance().moveToNextLevel();  // 移动到下一关
                    gameWorld.startLevelTransition();
                } else {
                    // 如果没有下一关，显示游戏完成对话框
                    gameWorld.handleGameFinished();
                }
            });
        });
    }

    @Override
    public void render(float delta) {
        // 清除屏幕
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        update(delta);
        draw();
    }

    private void update(float delta) {
        // 如果游戏暂停，不更新游戏逻辑
        if (gameWorld.getGameState() == GameState.PAUSED) {
            return;
        }
        
        // 游戏逻辑更新
        gameDialogs.update(delta);
        gameWorld.update(delta);

       if(gameWorld.getGameState() == GameState.PLAYING){
            buildToolbar.update(delta);
            hud.update(delta);
       }
        
        // 如果正在关卡切换中，更新过渡提示
        if (gameWorld.getGameState() == GameState.TRANSITIONING) {
            gameDialogs.updateTransitionDialog(
                String.format("Level will start in %.0f seconds...", 
                gameWorld.getTransitionTimer())
            );
        }

        // 更新悬浮位置
        if (selectedTowerType != null) {
            updateHoverCell();
        } else {
            hoverCell.x = -1;
            hoverCell.y = -1;
        }
    }

    private void updateHoverCell() {
        // 获取鼠标屏幕坐标
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        
        // 将屏幕坐标转换为世界坐标
        Vector3 worldCoords = viewport.unproject(new Vector3(screenX, screenY, 0));
        
        // 计算网格坐标
        hoverCell.x = (int)(worldCoords.x / 32) * 32;
        hoverCell.y = (int)(worldCoords.y / 32) * 32;
    }

    private void draw() {
        // 渲染游戏场景
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        
        // 根据不同状态渲染不同内容
        GameState state = gameWorld.getGameState();
        switch (state) {
            case LOADING:
                // 渲染加载画面
                renderLoadingScreen();
                break;
            case INTRO:
            case PLAYING:
                // 渲染游戏画面
                game.batch.begin();
                gameWorld.render(game.batch);
                renderHoverTower();
                game.batch.end();
                hud.render();
                buildToolbar.render();
                break;
            
            case VICTORY:
            case TRANSITIONING:
                // 只渲染游戏世界，不渲染UI
                game.batch.begin();
                gameWorld.render(game.batch);
                game.batch.end();
                break;
            case GAME_OVER:
                // 渲染游戏世界和HUD，但不渲染建造工具栏
                game.batch.begin();
                gameWorld.render(game.batch);
                game.batch.end();
                hud.render();
                break;
            case PAUSED:
                // 渲染游戏世界和HUD
                game.batch.begin();
                gameWorld.render(game.batch);
                game.batch.end();
                hud.render();
                break;
        }
        
        // 对话框和暂停菜单总是在最上层
        gameDialogs.render();
    }

    private void renderLoadingScreen() {
        game.batch.begin();
        // 渲染加载进度条或加载动画
        // 这里可以使用一个简单的文本显示 "Loading..."
        AssetManager.getInstance().getUIFont().draw(
            game.batch,
            "Loading...",
            viewport.getWorldWidth() / 2 - 50,
            viewport.getWorldHeight() / 2
        );
        game.batch.end();
    }

    private void renderHoverTower() {
        if (selectedTowerType != null && hoverCell.x >= 0 && hoverCell.y >= 0) {
            // 使用 AssetManager 中的发光精灵
            Sprite glowSprite = AssetManager.getInstance().getGlowSprite();
            if (glowSprite != null) {
                glowSprite.setPosition(hoverCell.x, hoverCell.y);
                if (gameWorld.canPlaceTowerAt(new Vector2(hoverCell.x, hoverCell.y))) {
                    glowSprite.setColor(0, 1, 0, 0.5f);
                } else {
                    glowSprite.setColor(1, 0, 0, 0.5f);
                }
                glowSprite.draw(game.batch);
            }

            // 使用 AssetManager 中的预览精灵
            Sprite towerSprite = AssetManager.getInstance().getTowerPreviewSprite(selectedTowerType);
            if (towerSprite != null) {
                towerSprite.setPosition(hoverCell.x, hoverCell.y);
                towerSprite.setAlpha(0.7f);
                towerSprite.draw(game.batch);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.setScreenX((width - viewport.getScreenWidth() )/ 2);
        viewport.setScreenY((height -viewport.getScreenHeight()) / 2);
        hud.resize(width, height);
        buildToolbar.resize(width, height);
        gameDialogs.resize(width, height);
    }

    @Override
    public void show() {
        // 初始化对话框管理器
        gameDialogs = new GameDialogs();
        
        // 设置输入处理器
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameDialogs.getStage());
        multiplexer.addProcessor(buildToolbar.getStage());
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                System.out.println("GameScreen touchDown: " + screenX + ", " + screenY);
                // 只处理塔的建造逻辑
                if (selectedTowerType != null) {
                    Vector3 worldCoords = viewport.unproject(new Vector3(screenX, screenY, 0));
                    float x = (float) Math.floor(worldCoords.x / 32) * 32;
                    float y = (float) Math.floor(worldCoords.y / 32) * 32;
                    Vector2 buildPosition = new Vector2(x, y);
                    Gdx.app.log("GameScreen", "buildPosition: " + buildPosition +" hoverCell: " + hoverCell);
                    if (gameWorld.canPlaceTowerAt(buildPosition)) {
                        if (gameWorld.buildTower(selectedTowerType, buildPosition)) {
                            selectedTowerType = null;
                        }
                    } else {
                        System.out.println("Cannot build here!");
                    }
                }
                return false;  // 让事件继续传递
            }
            
            @Override
            public boolean keyDown(int keycode) {
                switch(keycode) {
                    case Input.Keys.ESCAPE:
                        selectedTowerType = null;
                        // ESC键切换暂停状态
                        if (gameWorld.getGameState() == GameState.PLAYING) {
                            gameWorld.setGameState(GameState.PAUSED);
                            gameDialogs.showPauseDialog(
                                () -> {
                                    gameWorld.setGameState(GameState.PLAYING);
                                },
                                () -> {
                                    game.setScreen(new IntroScreen(game));
                                }
                            );
                        } else if (gameWorld.getGameState() == GameState.PAUSED) {
                            gameWorld.setGameState(GameState.PLAYING);
                            gameDialogs.hidePauseDialog();
                        }
                        break;
                }
                return true;
            }
        });
        
        Gdx.input.setInputProcessor(multiplexer);

        // 将游戏设置为加载状态
        gameWorld.setGameState(GameState.LOADING);
        
        // 模拟加载过程（实际项目中替换为真实的资源加载）
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                gameWorld.resetGame();
            }
        }, 1.0f); // 1秒后完成"加载"
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        gameDialogs.dispose();
        hud.dispose();
        buildToolbar.dispose();
    }
}
