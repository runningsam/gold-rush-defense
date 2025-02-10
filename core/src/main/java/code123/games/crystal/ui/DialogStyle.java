package code123.games.crystal.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import code123.games.crystal.AssetManager;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DialogStyle {
    public static void applyStyle(Dialog dialog) {
        // 设置背景颜色
        dialog.setBackground(new TextureRegionDrawable(
            new TextureRegion(AssetManager.getInstance().getPixel()))
        );
        dialog.getBackground().setMinWidth(300);
        dialog.getBackground().setMinHeight(200);
        
        // 设置标题样式
        Label.LabelStyle titleStyle = new Label.LabelStyle(
            AssetManager.getInstance().getUIFont(), 
            Color.WHITE
        );
        dialog.getTitleLabel().setStyle(titleStyle);
        
        // 设置内容样式
        dialog.getContentTable().pad(20);
        
        // 设置按钮样式
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = AssetManager.getInstance().getUIFont();
        buttonStyle.up = new TextureRegionDrawable(
            new TextureRegion(AssetManager.getInstance().getPixel())
        );
        dialog.getButtonTable().pad(10);
    }
} 