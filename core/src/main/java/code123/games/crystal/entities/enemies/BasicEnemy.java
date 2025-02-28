package code123.games.crystal.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import code123.games.crystal.entities.Enemy;

public class BasicEnemy extends Enemy {
    public BasicEnemy(String type, Array<Vector2> path) {
        super(type, path);
        this.health = 100;
        this.maxHealth = 100;
        this.speed = 100f;
        this.reward = 10;
    }
}
