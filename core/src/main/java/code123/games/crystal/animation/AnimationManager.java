package code123.games.crystal.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import code123.games.crystal.AssetManager;

public class AnimationManager {
    private static AnimationManager instance;
    private AnimatedEntity coyoteAnimation;
    private float coyoteX = 100;  // 距离左边100像素
    private float coyoteY = 100;  // 距离底部100像素

    private AnimationManager() {}

    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }

    public void createIntroAnimation() {
        coyoteAnimation = AssetManager.getInstance().getSkeletonManager().createAnimatedEntity("coyote");
        if (coyoteAnimation != null) {
//            coyoteAnimation.setScale(2f);
            coyoteAnimation.playAnimation("walk", true);
        }
    }

    public void update(float delta) {
        if (coyoteAnimation != null) {
            coyoteAnimation.setPosition(coyoteX, coyoteY);
            coyoteAnimation.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        if (coyoteAnimation != null) {
            coyoteAnimation.render(batch);
        }
    }

    public void dispose() {
        if (coyoteAnimation != null) {
            coyoteAnimation.dispose();
            coyoteAnimation = null;
        }
    }
}
