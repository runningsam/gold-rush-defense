package code123.games.crystal.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Effect {
    protected Vector2 position;
    protected float duration;
    protected float timer;
    protected boolean isFinished;

    public Effect(Vector2 position, float duration) {
        this.position = position;
        this.duration = duration;
        this.timer = duration;
        this.isFinished = false;
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
    
    public boolean isFinished() {
        return isFinished;
    }
} 