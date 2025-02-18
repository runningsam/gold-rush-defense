package code123.games.crystal.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import code123.games.crystal.AssetManager;
import code123.games.crystal.GameWorld;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.function.Consumer;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class BuildToolbar {
    private Sprite arrowTowerIcon;
    private Sprite magicTowerIcon;
    private Stage toolbarStage;
    private GameWorld gameWorld;
    private Consumer<String> onTowerSelected;  // 添加回调接口
    
    private static final float ICON_SIZE = 48;
    private static final float ICON_PADDING = 16;
    private static final int ARROW_TOWER_COST = 100;
    private static final int MAGIC_TOWER_COST = 150;
    
    public BuildToolbar(GameWorld gameWorld, Consumer<String> onTowerSelected) {
        this.gameWorld = gameWorld;
        this.onTowerSelected = onTowerSelected;
        this.toolbarStage = new Stage(new FitViewport(800, 480));
        createToolbar();
    }
    
    private void createToolbar() {
        Skin skin = AssetManager.getInstance().getUISkin();
        
        // 先加载图标
        arrowTowerIcon = AssetManager.getInstance().createTowerSprite("arrow");
        magicTowerIcon = AssetManager.getInstance().createTowerSprite("magic");
        
        if (arrowTowerIcon == null || magicTowerIcon == null) {
            System.err.println("Failed to load tower icons!");
            return;
        }
        
        // 直接使用 Table 作为根容器
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.pad(10);
        // buttonTable.setDebug(true);
        buttonTable.bottom();  // 确保按钮在底部
        
        // 创建箭塔按钮
        ImageButton arrowButton = new ImageButton(new TextureRegionDrawable(arrowTowerIcon));
        Label.LabelStyle labelStyle = skin.get("default", Label.LabelStyle.class);
        labelStyle.font.getData().setScale(0.3f); // Scale the font to 30%
        
        Label arrowCostLabel = new Label(ARROW_TOWER_COST + "Gold", labelStyle);
        arrowCostLabel.setAlignment(Align.top);
        arrowCostLabel.setTouchable(Touchable.disabled);  // 禁用标签的输入事件
        Stack arrowStack = new Stack();
        // arrowStack.setDebug(true);
        arrowStack.add(arrowButton);
        arrowStack.add(arrowCostLabel);
        buttonTable.add(arrowStack).size(ICON_SIZE, ICON_SIZE).pad(ICON_PADDING);
        
        // 创建魔法塔按钮
        ImageButton magicButton = new ImageButton(new TextureRegionDrawable(magicTowerIcon));
        Label magicCostLabel = new Label(MAGIC_TOWER_COST + "Gold", labelStyle);
        magicCostLabel.setAlignment(Align.top);
        magicCostLabel.setTouchable(Touchable.disabled);  // 禁用标签的输入事件
        Stack magicStack = new Stack();
        // magicStack.setDebug(true);
        magicStack.add(magicButton);
        magicStack.add(magicCostLabel);
        buttonTable.add(magicStack).size(ICON_SIZE, ICON_SIZE).pad(ICON_PADDING);
        
        // 确保表格在底部
        buttonTable.bottom();  // 确保按钮在底部
        
        // 添加按钮点击事件
        arrowButton.addListener(new ClickListener() {
           
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameWorld.getGold() >= ARROW_TOWER_COST) {
                    onTowerSelected.accept("arrow");
                }
            }
            
        });
        
        magicButton.addListener(new ClickListener() {
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameWorld.getGold() >= MAGIC_TOWER_COST) {
                    onTowerSelected.accept("magic");
                }
            }
        });
        
        // 将表格添加到舞台
        toolbarStage.addActor(buttonTable);
    }
    public void update(float delta) {
        toolbarStage.act(delta);
    }

    public void render() {
        toolbarStage.draw();
    }
    
    public void resize(int width, int height) {
        toolbarStage.getViewport().update(width, height, true);
    }
    
    public Stage getStage() {
        return toolbarStage;
    }

    public void dispose() {
        toolbarStage.dispose();
    }
} 