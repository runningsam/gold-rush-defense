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
import java.util.Map;
import code123.games.crystal.animation.SkeletonManager;

public class AssetManager implements Disposable {
    private static AssetManager instance;
    private HashMap<String, TextureAtlas> atlases;
    private Texture pixelTexture;
    private BitmapFont uiFont;
    private Skin uiSkin;
    private Map<String, Sprite> towerPreviewSprites = new HashMap<>();
    private Sprite glowSprite;
    private SkeletonManager skeletonManager;

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
        // 加载UI皮肤
        loadUISkin();

        // 加载发光效果
        glowSprite = createSprite("effects", "glow");
        
        // 预先创建所有塔的预览精灵
        towerPreviewSprites.put("arrow", createTowerSprite("arrow"));
        towerPreviewSprites.put("magic", createTowerSprite("magic"));
        
        // 初始化骨骼动画管理器
        skeletonManager = new SkeletonManager();
    }

    private void loadUISkin() {
        // 加载完整的皮肤文件
        uiSkin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

        // 加载UI图标
        Sprite goldIcon = createUISprite("gold_icon");
        Sprite healthIcon = createUISprite("health_icon");
        uiSkin.add("gold_icon", goldIcon);
        uiSkin.add("health_icon", healthIcon);

        uiFont = uiSkin.get("default", BitmapFont.class);
    }

    public Sprite createSprite(String atlasName, String spriteName) {
        TextureAtlas atlas = atlases.get(atlasName);
        if (atlas != null) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(spriteName);
            if (region != null) {
                return new Sprite(region);
            }
        }
        Gdx.app.log("AssetManager", "Atlas not found: " + atlasName);
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
        }
        Gdx.app.log("AssetManager", "Tower atlas not found " + type);
        return null;
    }

    public Sprite createEnemySprite(String type) {
        TextureAtlas atlas = atlases.get("enemies");
        if (atlas != null) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(type);
            if (region != null) {
                return new Sprite(region);
            }
        }
        Gdx.app.log("AssetManager", "Enemy atlas not found " + type);
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

    public Sprite getTowerPreviewSprite(String type) {
        return towerPreviewSprites.get(type);
    }

    public Sprite getGlowSprite() {
        return glowSprite;
    }

    public SkeletonManager getSkeletonManager() {
        return skeletonManager;
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
        if (skeletonManager != null) {
            skeletonManager.dispose();
        }
    }
}
