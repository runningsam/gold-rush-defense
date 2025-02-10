package code123.games.crystal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class AssetManager implements Disposable {
    private static AssetManager instance;
    private HashMap<String, TextureAtlas> atlases;
    private Texture pixelTexture;
    private BitmapFont uiFont;
    private Skin uiSkin;
    
    private AssetManager() {
        atlases = new HashMap<>();
    }
    
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    public void loadAssets() {
        // 加载打包后的图集
        atlases.put("towers", new TextureAtlas(Gdx.files.internal("packed/towers.atlas")));
        atlases.put("enemies", new TextureAtlas(Gdx.files.internal("packed/enemies.atlas")));
        atlases.put("effects", new TextureAtlas(Gdx.files.internal("packed/effects.atlas")));
        atlases.put("ui", new TextureAtlas(Gdx.files.internal("packed/ui.atlas")));
        
        // 创建1x1像素纹理
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        pixelTexture = new Texture(pixmap);
        pixmap.dispose();
        
        // 加载UI字体
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.2f);
        
        // 加载UI资源
        atlases.put("uiskin", new TextureAtlas(Gdx.files.internal("uiskin/uiskin.atlas")));
        
        // 加载UI皮肤
        loadUISkin();
    }
    
    private void loadUISkin() {
        // 加载字体
        BitmapFont uiFont = new BitmapFont();  // 或者从文件加载特定字体
        
        // 创建皮肤
        uiSkin = new Skin();
        
        // 将字体添加到皮肤中
        uiSkin.add("default", uiFont);
        
        // 创建默认的标签样式
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = uiFont;
        uiSkin.add("default", labelStyle);
        

        // 加载UI图标
        Sprite goldIcon = createUISprite("gold_icon");
        Sprite healthIcon = createUISprite("health_icon");
        uiSkin.add("gold_icon", goldIcon);
        uiSkin.add("health_icon", healthIcon);
        
        // 加载其他 UI 资源...
    }
    
    public Sprite createSprite(String atlasName, String spriteName) {
        TextureAtlas atlas = atlases.get(atlasName);
        if (atlas != null) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(spriteName);
            if (region != null) {
                return new Sprite(region);
            }
            System.out.println("Sprite not found: " + spriteName + " in atlas: " + atlasName);
        }
        System.out.println("Atlas not found: " + atlasName);
        return null;
    }
    
    // 便捷方法，用于获取特定类型的精灵
    public Sprite createTowerSprite(String type) {
        TextureAtlas atlas = atlases.get("towers");
        if (atlas != null) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(type);
            if (region != null) {
                return new Sprite(region);
            }
            System.out.println("Tower sprite not found: " + type);
        }
        System.out.println("Tower atlas not found");
        return null;
    }
    
    public Sprite createEnemySprite(String type) {
        TextureAtlas atlas = atlases.get("enemies");
        if (atlas != null) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(type);
            if (region != null) {
                System.out.println("Enemy sprite found: " + type);
                return new Sprite(region);
            }
            System.out.println("Enemy sprite not found: " + type);
        }
        System.out.println("Enemy atlas not found");
        return null;
    }
    
    public Sprite createUISprite(String uiElement) {
        return createSprite("ui", uiElement);
    }
    
    public Texture getPixel() {
        return pixelTexture;
    }
    
    public BitmapFont getUIFont() {
        return uiFont;
    }
    
    public Skin getUISkin() {
        return uiSkin;
    }
    
    public TextureAtlas getAtlas(String name) {
        return atlases.get(name);
    }
    
    @Override
    public void dispose() {
        for (TextureAtlas atlas : atlases.values()) {
            atlas.dispose();
        }
        atlases.clear();
        if (pixelTexture != null) {
            pixelTexture.dispose();
        }
        if (uiFont != null) {
            uiFont.dispose();
        }
        if (uiSkin != null) {
            uiSkin.dispose();
        }
    }
}
