package code123.games.crystal.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import code123.games.crystal.AssetManager;

public class GameDialogs {
    private final Stage stage;
    private final Skin skin;
    private NinePatchDrawable dialogBackground;

    public GameDialogs(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
        
        // 从UI图集加载对话框背景
        TextureAtlas.AtlasRegion region = AssetManager.getInstance().getAtlas("ui").findRegion("dialog_background");
        NinePatch patch = new NinePatch(region, 24, 24, 24, 24);  // 左、右、上、下边框的大小
        dialogBackground = new NinePatchDrawable(patch);
        
        // 修改对话框的默认样式
        WindowStyle windowStyle = skin.get(WindowStyle.class);
        windowStyle.background.setMinWidth(300);  // 设置最小宽度
        windowStyle.background.setMinHeight(200); // 设置最小高度
    }

    public void showVictoryDialog() {
        Dialog dialog = new Dialog("Victory!", skin) {
            {
                setBackground(dialogBackground);
            }
            
            @Override
            protected void result(Object obj) {
                if (obj instanceof Boolean && (Boolean)obj) {
                    // 这里可以添加点击确定后的逻辑，比如重新开始或返回主菜单
                }
            }
        };
        
        dialog.text("Congratulations!\nYou have successfully defended your crystal!")
            .padTop(20).padBottom(20);
        dialog.getContentTable().pad(20);
        dialog.button("OK", true)
            .padBottom(20);
        dialog.setSize(400, 250);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );
        dialog.show(stage);
    }

    public void showGameOverDialog() {
        Dialog dialog = new Dialog("Game Over", skin) {
            {
                setBackground(skin.newDrawable("window", new Color(0.8f, 0.8f, 0.8f, 0.9f)));
            }
            
            @Override
            protected void result(Object obj) {
                if (obj instanceof Boolean && (Boolean)obj) {
                    // 这里可以添加点击确定后的逻辑
                }
            }
        };
        
        dialog.text("Your crystal has been destroyed!\nTry again?")
            .padTop(20).padBottom(20);
        dialog.getContentTable().pad(20);
        dialog.button("OK", true)
            .padBottom(20);
        dialog.setSize(400, 250); // 设置固定大小
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );
        dialog.show(stage);
    }

    public void showDialog(String title, String message) {
        Dialog dialog = new Dialog(title, skin) {
            @Override
            protected void result(Object obj) {
                if (obj instanceof Boolean && (Boolean)obj) {
                    // 确定按钮的处理逻辑
                }
            }
        };
        
        dialog.text(message);
        dialog.button("确定", true);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );
        dialog.show(stage);
    }
} 