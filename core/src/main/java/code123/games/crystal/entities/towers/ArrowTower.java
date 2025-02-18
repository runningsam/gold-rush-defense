package code123.games.crystal.entities.towers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import code123.games.crystal.entities.Tower;
import code123.games.crystal.entities.Enemy;

public class ArrowTower extends Tower {
    public ArrowTower(Vector2 position) {
        super(position);
        this.range = 150f;
        this.damage = 20f;
        this.attackSpeed = 1.0f;
        this.cost = 100;
        initSprite("arrow");
    }

    @Override
    public void update(float delta, Array<Enemy> enemies) {
        if (attackTimer > 0) {
            attackTimer -= delta;
        }
        
        if (canAttack()) {
            for (Enemy enemy : enemies) {
                if (isInRange(enemy)) {
                    attack(enemy);
                    attackTimer = 1f / attackSpeed;
                    break;
                }
            }
        }
    }

    @Override
    protected void attack(Enemy enemy) {
        if (enemy != null) {
            createAttackEffect(enemy);
            enemy.takeDamage(damage);
        }
    }
}
