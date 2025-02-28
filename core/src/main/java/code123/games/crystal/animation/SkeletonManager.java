package code123.games.crystal.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.badlogic.gdx.files.FileHandle;

public class SkeletonManager {
    private TextureAtlas atlas;
    private SkeletonRenderer renderer;
    private SkeletonRendererDebug debugRenderer;

    public SkeletonManager() {
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(false);
        debugRenderer = new SkeletonRendererDebug();
    }

    public AnimatedEntity createAnimatedEntity(String name) {
        try {
            // 移除 "assets/" 前缀
            String atlasPath = "animations/" + name + "/" + name + ".atlas";
            String skeletonPath = "animations/" + name + "/" + name + ".json";
            
            FileHandle atlasFile = Gdx.files.internal(atlasPath);
            FileHandle jsonFile = Gdx.files.internal(skeletonPath);
            
            if (!atlasFile.exists() || !jsonFile.exists()) {
                Gdx.app.error("SkeletonManager", "Animation files not found: " + atlasPath);
                return null;
            }
            
            Gdx.app.debug("SkeletonManager", "Loading animation files from: " + atlasPath);
            TextureAtlas atlas = new TextureAtlas(atlasFile);
            SkeletonJson json = new SkeletonJson(atlas);
            SkeletonData skeletonData = json.readSkeletonData(jsonFile);
            
            // 创建动画状态
            AnimationStateData stateData = new AnimationStateData(skeletonData);
            
            return new AnimatedEntity(skeletonData, stateData);
        } catch (Exception e) {
            Gdx.app.error("SkeletonManager", "Error creating animated entity: " + e.getMessage(), e);
            return null;
        }
    }

    public void render(SpriteBatch batch, AnimatedEntity entity) {
        renderer.draw(batch, entity.getSkeleton());
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }
}
