package code123.games.crystal.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import code123.games.crystal.AssetManager;
import code123.games.crystal.effects.ProjectileEffect;
import code123.games.crystal.GameWorld;
import code123.games.crystal.entities.towers.ArrowTower;
import code123.games.crystal.entities.towers.MagicTower;

public abstract class Tower {
    protected Vector2 position;
    protected float range;
    protected float damage;
    protected float attackSpeed;
    protected float attackTimer;
    protected int cost;
    protected Sprite sprite;

    public Tower(Vector2 position) {
        this.position = position;
        this.attackTimer = 0;
    }

    public abstract void update(float delta, Array<Enemy> enemies);
    
    protected boolean isInRange(Enemy enemy) {
        return position.dst(enemy.getPosition()) <= range;
    }

    protected abstract void attack(Enemy enemy);

    protected boolean canAttack() {
        return attackTimer <= 0;
    }

    public int getCost() {
        return cost;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return sprite;
    }

    protected void initSprite(String textureKey) {
        sprite = AssetManager.getInstance().createTowerSprite(textureKey);
        if (sprite != null) {
            sprite.setPosition(position.x, position.y);
        }
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        if (sprite != null) {
            sprite.setPosition(position.x, position.y);
        }
    }

    protected void createAttackEffect(Enemy target) {
        String effectType = getClass().getSimpleName().toLowerCase();
        if (effectType.startsWith("arrow")) {
            effectType = "arrow";
        } else if (effectType.startsWith("magic")) {
            effectType = "magic";
        }
        
        Vector2 towerTop = new Vector2(
            position.x + 16,
            position.y + 32
        );
        
        ProjectileEffect effect = new ProjectileEffect(
            towerTop,
            target,
            effectType,
            damage  // 传递塔的伤害值
        );
        GameWorld.addEffect(effect);
    }
}
