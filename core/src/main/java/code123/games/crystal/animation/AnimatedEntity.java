package code123.games.crystal.animation;

import code123.games.crystal.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

public class AnimatedEntity {
    private Skeleton skeleton;
    private AnimationState state;
    private float x, y;
    private float scale = 1.0f;

    public AnimatedEntity(SkeletonData skeletonData, AnimationStateData stateData) {
        skeleton = new Skeleton(skeletonData);
        state = new AnimationState(stateData);

        // 设置默认混合时间
        stateData.setDefaultMix(0.2f);
    }

    public void update(float delta) {
        state.update(delta);
        state.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        skeleton.setPosition(x, y);
    }

    public void setScale(float scale) {
        this.scale = scale;
        skeleton.getRootBone().setScale(scale);
    }

    public void playAnimation(String name, boolean loop) {
        state.setAnimation(0, name, loop);
    }

    public void addAnimation(String name, boolean loop, float delay) {
        state.addAnimation(0, name, loop, delay);
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public AnimationState getState() {
        return state;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public void render(SpriteBatch batch) {
        // 使用 SkeletonManager 的 renderer 来渲染
        AssetManager.getInstance().getSkeletonManager().render(batch, this);
    }

    public void dispose() {
        // 清理资源
        if (skeleton != null) {
            skeleton = null;
        }
        if (state != null) {
            state = null;
        }
    }
}
