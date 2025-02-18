package code123.games.crystal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public AssetManager assets;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = AssetManager.getInstance();
        
        // Make sure assets are loaded
        assets.loadAssets();
        
        setScreen(new IntroScreen(this));
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
