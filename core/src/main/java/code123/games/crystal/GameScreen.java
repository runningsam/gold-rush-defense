package code123.games.crystal;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import code123.games.crystal.ui.GameHUD;
import code123.games.crystal.ui.BuildToolbar;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.InputMultiplexer;
import code123.games.crystal.ui.GameDialogs;

public class GameScreen implements Screen {
    private final Main game;
    private GameWorld gameWorld;
    private OrthographicCamera camera;
    private Viewport viewport;
    private String selectedTowerType = null;
    
    private static final float MIN_WORLD_WIDTH = 800;   // 最小游戏世界宽度
    private static final float MIN_WORLD_HEIGHT = 480;  // 最小游戏世界高度
    
    private GameHUD hud;
    private BuildToolbar buildToolbar;
    private Vector2 hoverCell = new Vector2(-1, -1);
    
    private Stage uiStage;
    private Skin skin;
    private GameDialogs gameDialogs;
    
    public GameScreen(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        
        // 设置初始相机位置
        camera.position.set(MIN_WORLD_WIDTH / 2, MIN_WORLD_HEIGHT / 2, 0);
        camera.update();
        
        // 确保资源已加载
        AssetManager.getInstance().loadAssets();
        
        this.gameWorld = new GameWorld(camera);
        
        this.hud = new GameHUD(viewport, gameWorld);
        this.buildToolbar = new BuildToolbar(gameWorld, towerType -> {
            selectedTowerType = towerType;  // 通过回调更新选择的塔类型
        });
    }

    @Override
    public void render(float delta) {
        // 清屏
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // 更新相机和游戏逻辑
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        gameWorld.update(delta);
        hud.update(delta);
        
        // 更新悬浮位置
        if (selectedTowerType != null) {
            Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            hoverCell.x = (float) Math.floor(mousePos.x / 32) * 32;
            hoverCell.y = (float) Math.floor(mousePos.y / 32) * 32;
        } else {
            hoverCell.x = -1;
            hoverCell.y = -1;
        }
        
        // 渲染游戏世界
        game.batch.begin();
        gameWorld.render(game.batch);
        
        // 渲染悬浮格子
        if (selectedTowerType != null && hoverCell.x >= 0 && hoverCell.y >= 0) {
            Sprite towerSprite = AssetManager.getInstance().createTowerSprite(selectedTowerType);
            if (towerSprite != null) {
                towerSprite.setPosition(hoverCell.x, hoverCell.y);
                towerSprite.setAlpha(0.5f);
                towerSprite.draw(game.batch);
            }
        }
        
        // 检查游戏状态并显示对话框
        if (gameWorld.getGameState() == GameState.VICTORY) {
            gameDialogs.showVictoryDialog();
            gameWorld.setGameState(GameState.FINISHED); // 防止重复显示对话框
        } else if (gameWorld.getGameState() == GameState.GAME_OVER) {
            gameDialogs.showGameOverDialog();
            gameWorld.setGameState(GameState.FINISHED); // 防止重复显示对话框
        }
        
        game.batch.end();
        hud.render(delta);
        
        // 渲染UI
        buildToolbar.render(delta);
        // 渲染UI
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(MIN_WORLD_WIDTH / 2, MIN_WORLD_HEIGHT / 2, 0);
        camera.update();
        hud.resize(width, height);
        buildToolbar.resize(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        // 初始化UI系统
        uiStage = new Stage(new ExtendViewport(800, 480));
        
        // 加载默认UI皮肤
        skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        
        // 初始化对话框管理器
        gameDialogs = new GameDialogs(uiStage, skin);
        
        // 为调试添加：直接显示对话框
        gameDialogs.showVictoryDialog();
        
        // 添加输入处理器，注意顺序很重要
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(buildToolbar.getStage());  // 添加工具栏的stage
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                System.out.println("GameScreen touchDown: " + screenX + ", " + screenY);
                // 只处理塔的建造逻辑
                if (selectedTowerType != null) {
                    Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                    float x = (float) Math.floor(worldCoords.x / 32) * 32;
                    float y = (float) Math.floor(worldCoords.y / 32) * 32;
                    
                    if (gameWorld.buildTower(selectedTowerType, new Vector2(x, y))) {
                        selectedTowerType = null;
                    }
                }
                return false;  // 让事件继续传递
            }
            
            @Override
            public boolean keyDown(int keycode) {
                switch(keycode) {
                    case Input.Keys.ESCAPE:
                        selectedTowerType = null;
                        break;
                }
                return true;
            }
        });
        
        System.out.println("Input processor set up with " + multiplexer.size() + " processors");
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        uiStage.dispose();
        skin.dispose();
    }
}
