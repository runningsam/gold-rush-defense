package code123.games.crystal.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import code123.games.crystal.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Enemy {
    protected float health;
    protected float maxHealth;
    protected float speed;
    protected int reward;
    private Vector2 position;
    private Vector2 velocity;
    private final Sprite sprite;
    private Array<Vector2> pathPoints;
    private int currentPoint;
    private boolean isDead;
    private boolean hasReachedEnd;
    
    public Enemy(String type, Array<Vector2> pathPoints) {
        this.position = new Vector2(pathPoints.first());
        this.velocity = new Vector2();
        this.pathPoints = pathPoints;
        this.currentPoint = 1;
        this.isDead = false;
        this.hasReachedEnd = false;
        
        this.sprite = AssetManager.getInstance().createEnemySprite(type);
        if (this.sprite != null) {
            this.sprite.setOriginCenter();
            this.sprite.setPosition(position.x - sprite.getWidth()/2, position.y - sprite.getHeight()/2);
        }
        
        this.health = 100;
        this.maxHealth = this.health;
        
        System.out.println("Enemy created with health: " + health + "/" + maxHealth + " type: " + type + " sprite: " + sprite);
    }
    
    public void update(float delta) {
        if (isDead || hasReachedEnd) return;
        
        Vector2 target = pathPoints.get(currentPoint);
        Vector2 direction = target.cpy().sub(position).nor();
        velocity.set(direction).scl(speed);
        position.add(velocity.x * delta, velocity.y * delta);
        
        if (position.dst(target) < 2) {
            position.set(target);
            currentPoint++;
            if (currentPoint >= pathPoints.size) {
                hasReachedEnd = true;
                System.out.println("Enemy reached end point!");
            }
        }
        
        if (sprite != null) {
            sprite.setPosition(position.x - sprite.getWidth()/2, position.y - sprite.getHeight()/2);
            sprite.setRotation(velocity.angleDeg());
        }
    }
    
    public void takeDamage(float damage) {
        this.health -= damage;
        System.out.println("Enemy took " + damage + " damage. Health: " + health + "/" + maxHealth);
        if (health <= 0) {
            isDead = true;
            System.out.println("Enemy died!");
        }
    }
    
    public void render(SpriteBatch batch) {
        // 渲染敌人精灵
        if (sprite != null) {
            sprite.draw(batch);
            
            // 渲染生命值条
            float healthPercentage = health / maxHealth;
            float barWidth = 32;
            float barHeight = 4;
            float barY = position.y + sprite.getHeight() + 2;  // 使用sprite高度
            
            // 绘制血条背景（黑色）
            batch.setColor(0, 0, 0, 0.8f);
            batch.draw(AssetManager.getInstance().getPixel(), 
                position.x - barWidth/2,
                barY,
                barWidth,
                barHeight);
                
            // 绘制当前血量（红色）
            batch.setColor(1, 0, 0, 0.8f);
            batch.draw(AssetManager.getInstance().getPixel(),
                position.x - barWidth/2,
                barY,
                barWidth * healthPercentage,
                barHeight);
                
            // 重置颜色
            batch.setColor(1, 1, 1, 1);
        } else {
            // System.out.println("Enemy sprite is null " + health);
        }
    }
    
    // Getters and setters
    public void setHealth(float health) {
        this.health = health;
        this.maxHealth = health;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public void setReward(int reward) {
        this.reward = reward;
    }
    
    public boolean isDead() {
        return isDead;
    }
    
    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public int getReward() {
        return reward;
    }
    
    public Sprite getSprite() {
        return sprite;
    }
    
    public Vector2 getVelocity() {
        return velocity.cpy();
    }
}
