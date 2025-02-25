package code123.games.crystal.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import code123.games.crystal.AssetManager;
import code123.games.crystal.GameWorld;
import code123.games.crystal.wave.WaveManager;
import code123.games.crystal.EventManager;

import com.badlogic.gdx.Gdx;

public class GameHUD {
    private Stage stage;
    private Skin skin;
    private float gameTime;
    private GameWorld gameWorld;
    
    // UI elements
    private Label goldLabel;
    private Label livesLabel;
    private Label waveLabel;
    private Label timeLabel;
    private Label titleLabel;
    private Table topTable;
    
    public GameHUD(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        
        // 使用 FitViewport 替代 ScreenViewport
        // 使用与游戏世界相同的尺寸
        Viewport hudViewport = new FitViewport(800, 480);
        this.stage = new Stage(hudViewport);
        
        // Setup skin and UI components
        this.skin = AssetManager.getInstance().getUISkin();
        createUI();
        
        this.gameTime = 0;
        
        // 添加对关卡变化的监听
        EventManager.getInstance().on("levelChanged", () -> {
            updateLabels();
        });
    }
    
    private void createUI() {
        topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().pad(10);
        
        Table leftInfo = new Table();
        Image goldIcon = new Image(skin.getDrawable("gold_icon"));
        Label.LabelStyle hudLabelStyle = skin.get("hud-small", Label.LabelStyle.class);
        hudLabelStyle.font.getData().setScale(0.5f);
        
        goldLabel = new Label("0", hudLabelStyle);
        Image healthIcon = new Image(skin.getDrawable("health_icon"));
        livesLabel = new Label("0/0", hudLabelStyle);
        
        leftInfo.add(goldIcon).size(20).padRight(5);
        leftInfo.add(goldLabel).padRight(20);
        leftInfo.add(healthIcon).size(20).padRight(5);
        leftInfo.add(livesLabel);
        
        // Create right side info (wave and time)
        Table rightInfo = new Table();
        waveLabel = new Label("Wave: 1/1", hudLabelStyle);
        timeLabel = new Label("Time: 00:00", hudLabelStyle);
        
        rightInfo.add(waveLabel).padRight(20);
        rightInfo.add(timeLabel);
        
        // Add to top table
        topTable.add(leftInfo).expandX().left();
        
        String levelTitle = this.gameWorld.getLevelTitle();
        titleLabel = new Label(levelTitle, skin, "hud-small");
        topTable.add(titleLabel).expandX().center();

        topTable.add(rightInfo).expandX().right();
        
        stage.addActor(topTable);
    }
    
    public void update(float delta) {
        gameTime += delta;
        updateLabels();
        stage.act(delta);
    }
    
    private void updateLabels() {
        // Update gold and lives
        goldLabel.setText(String.valueOf(gameWorld.getGold()));
        livesLabel.setText(String.format("%d/%d", gameWorld.getLives(), gameWorld.getMaxLives()));
        
        // Update level title
        titleLabel.setText(gameWorld.getLevelTitle());
        
        // Update wave info
        WaveManager waveManager = gameWorld.getWaveManager();
        String waveText = String.format("Wave: %d/%d", waveManager.getCurrentWave(), waveManager.getTotalWaves());
        if (waveManager.isInBreak()) {
            waveText += String.format(" (Next: %.1fs)", waveManager.getBreakTimeRemaining());
        }
        waveLabel.setText(waveText);
        
        // Update time
        int minutes = (int)(gameTime / 60);
        int seconds = (int)(gameTime % 60);
        timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }
    
    public void render() {
        stage.draw();
    }
    
    public void resize(int width, int height) {
        // 更新视口并居中
        stage.getViewport().update(width, height, true);
    }
    
    public void dispose() {
        stage.dispose();
    }
} 