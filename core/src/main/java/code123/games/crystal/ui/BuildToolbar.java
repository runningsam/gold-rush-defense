package code123.games.crystal.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import code123.games.crystal.AssetManager;
import code123.games.crystal.GameWorld;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.function.Consumer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

public class BuildToolbar {
    private Sprite arrowTowerIcon;
    private Sprite magicTowerIcon;
    private Stage toolbarStage;
    private GameWorld gameWorld;
    private Consumer<String> onTowerSelected;

    private static final float ICON_SIZE = 48;
    private static final float ICON_PADDING = 16;
    private static final int ARROW_TOWER_COST = 100;
    private static final int MAGIC_TOWER_COST = 150;

    public BuildToolbar(GameWorld gameWorld, Consumer<String> onTowerSelected) {
        this.gameWorld = gameWorld;
        this.onTowerSelected = onTowerSelected;
        this.toolbarStage = new Stage(new FitViewport(800, 480));
        
        // 初始化图标
        this.arrowTowerIcon = AssetManager.getInstance().createTowerSprite("arrow");
        this.magicTowerIcon = AssetManager.getInstance().createTowerSprite("magic");
        
        createToolbar();
    }

    private void createToolbar() {
        // 从纹理图集中获取9patch区域
        TextureAtlas atlas = AssetManager.getInstance().getAtlas("ui");
        TextureAtlas.AtlasRegion region = atlas.findRegion("toolbar_9patch");
        
        // 打印调试信息
        Gdx.app.log("BuildToolbar", "Region size: " + region.getRegionWidth() + "x" + region.getRegionHeight());
        Gdx.app.log("BuildToolbar", "Region coordinates: " + region.getRegionX() + "," + region.getRegionY());
        
        // 创建9patch，注意边缘区域的设置
        NinePatch backgroundPatch = new NinePatch(region, 
            8, 8,     // 左右边距
            8, 8      // 上下边距
        );
        
        // 创建背景drawable
        NinePatchDrawable background = new NinePatchDrawable(backgroundPatch);
         // 创建主容器
         Table mainTable = new Table();
         mainTable.pad(0);
         mainTable.setFillParent(true);
         mainTable.pad(0).bottom();

        // 创建主容器
        Table toolbarTable = new Table();
        toolbarTable.setBackground(background);
        mainTable.add(toolbarTable).pad(0).expandX().center();
        
        // 添加塔按钮
        float buttonSize = 32;  // 按钮尺寸
        float padding = 16;    // 按钮间距
        
        // 获取按钮背景
        TextureAtlas.AtlasRegion buttonRegion = atlas.findRegion("button_background");  // 暂时复用这个背景
        NinePatch buttonPatch = new NinePatch(buttonRegion, 4, 4, 4, 4);
        NinePatchDrawable buttonBackground = new NinePatchDrawable(buttonPatch);
        
        // 箭塔按钮
        ImageButton arrowButton = new ImageButton(new TextureRegionDrawable(arrowTowerIcon));
        // 箭塔按钮
        Table arrowTable = new Table();
        arrowTable.setBackground(buttonBackground);
        arrowTable.add(arrowButton).size(buttonSize).pad(0).center();
        toolbarTable.add(arrowTable).padRight(4);
        
        // 魔法塔按钮
        ImageButton magicButton = new ImageButton(new TextureRegionDrawable(magicTowerIcon));
        Table magicTable = new Table();
        magicTable.setBackground(buttonBackground);
        magicTable.add(magicButton).size(buttonSize).pad(0).center();
        toolbarTable.add(magicTable);
        
        // 设置位置并调整大小
        mainTable.pack();
        
        // 添加到舞台
        toolbarStage.addActor(mainTable);

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
