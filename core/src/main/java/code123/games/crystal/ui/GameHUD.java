package code123.games.crystal.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import code123.games.crystal.AssetManager;
import code123.games.crystal.GameWorld;
import code123.games.crystal.wave.WaveManager;

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
    private Table topTable;
    
    public GameHUD(Viewport gameViewport, GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        
        // Create stage with its own viewport
        Viewport hudViewport = new ScreenViewport();
        this.stage = new Stage(hudViewport);
        
        // Setup skin and UI components
        this.skin = AssetManager.getInstance().getUISkin();
        createUI();
        
        this.gameTime = 0;
    }
    
    private void createUI() {
        topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().pad(10);
        
        // Create left side info (gold and lives)
        Table leftInfo = new Table();
        Image goldIcon = new Image(skin.getDrawable("gold_icon"));
        goldLabel = new Label("0", skin);
        Image healthIcon = new Image(skin.getDrawable("health_icon"));
        livesLabel = new Label("0/0", skin);
        
        leftInfo.add(goldIcon).size(20).padRight(5);
        leftInfo.add(goldLabel).padRight(20);
        leftInfo.add(healthIcon).size(20).padRight(5);
        leftInfo.add(livesLabel);
        
        // Create right side info (wave and time)
        Table rightInfo = new Table();
        waveLabel = new Label("Wave: 1/1", skin);
        timeLabel = new Label("Time: 00:00", skin);
        
        rightInfo.add(waveLabel).padRight(20);
        rightInfo.add(timeLabel);
        
        // Add to top table
        topTable.add(leftInfo).expandX().left();
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
    
    public void render(float delta) {
        stage.draw();
    }
    
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    public void dispose() {
        stage.dispose();
    }
} 