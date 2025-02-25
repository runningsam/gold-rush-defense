package code123.games.crystal.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import code123.games.crystal.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import code123.games.crystal.story.StoryManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import code123.games.crystal.GameWorld;
import code123.games.crystal.GameState;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameDialogs {
    private Stage stage;
    private Skin skin;
    private Drawable dialogBackground;
    private Dialog pauseDialog;

    public GameDialogs() {
        this.stage = new Stage(new FitViewport(800, 480));
        this.skin = AssetManager.getInstance().getUISkin();

        // 从UI图集加载对话框背景
        TextureAtlas.AtlasRegion region = AssetManager.getInstance().getAtlas("ui").findRegion("dialog_background");
        NinePatch patch = new NinePatch(region, 24, 24, 24, 24); // 左、右、上、下边框的大小
        dialogBackground = new NinePatchDrawable(patch);

        // 修改对话框的默认样式
        WindowStyle windowStyle = skin.get(WindowStyle.class);
        windowStyle.background.setMinWidth(300); // 设置最小宽度
        windowStyle.background.setMinHeight(200); // 设置最小高度
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void showVictoryDialog(Runnable onConfirm) {
        StoryManager storyManager = StoryManager.getInstance();

        Dialog dialog = new Dialog("", skin) {
            {
                setBackground(dialogBackground);

                Label titleLabel = new Label(storyManager.getVictoryTitle(), skin);
                titleLabel.setAlignment(Align.center);

                getTitleTable().clear();
                getTitleTable().pad(20);
                getTitleTable().add(titleLabel).expandX().fillX().padTop(20);
            }

            @Override
            protected void result(Object obj) {
                if (obj instanceof Boolean && (Boolean) obj && onConfirm != null) {
                    remove();
                    onConfirm.run();
                }
            }
        };

        // 构建胜利文本
        StringBuilder messageText = new StringBuilder();
        for (String line : storyManager.getVictoryContent()) {
            messageText.append(line).append("\n");
        }

        Label messageLabel = new Label(messageText.toString(), skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        Table contentTable = new Table();
        contentTable.defaults().pad(20);
        contentTable.add(messageLabel).width(500f).expandX();

        dialog.getContentTable().clear();
        dialog.getContentTable().add(contentTable).expand().fill();

        TextButton button = new TextButton("OK", skin);
        dialog.button(button, true).padBottom(20);

        dialog.setSize(600, 400);
        dialog.setPosition(
                (stage.getWidth() - dialog.getWidth()) / 2,
                (stage.getHeight() - dialog.getHeight()) / 2);

        dialog.show(stage);
    }

    public void showGameOverDialog(Runnable onRetry) {
        StoryManager storyManager = StoryManager.getInstance();

        Dialog dialog = new Dialog("", skin) {
            {
                setBackground(dialogBackground);

                Label titleLabel = new Label(storyManager.getDefeatTitle(), skin);
                titleLabel.setAlignment(Align.center);

                getTitleTable().clear();
                getTitleTable().pad(20);
                getTitleTable().add(titleLabel).expandX().fillX().padTop(20);
            }

            @Override
            protected void result(Object obj) {
                if (obj instanceof Boolean) {
                    if ((Boolean) obj) { // Retry button
                        if (onRetry != null) {
                            onRetry.run();
                        }
                    }
                    remove();
                }
            }
        };

        // 构建失败文本
        StringBuilder messageText = new StringBuilder();
        for (String line : storyManager.getDefeatContent()) {
            messageText.append(line).append("\n");
        }

        Label messageLabel = new Label(messageText.toString(), skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        Table contentTable = new Table();
        contentTable.defaults().pad(20);
        contentTable.add(messageLabel).width(500f).expandX();

        dialog.getContentTable().clear();
        dialog.getContentTable().add(contentTable).expand().fill();

        // 添加重试和退出按钮
        TextButton retryButton = new TextButton("Retry", skin);
        TextButton quitButton = new TextButton("Quit", skin);
        dialog.button(retryButton, true).padBottom(20);
        dialog.button(quitButton, false).padBottom(20);

        dialog.setSize(600, 400);
        dialog.setPosition(
                (stage.getWidth() - dialog.getWidth()) / 2,
                (stage.getHeight() - dialog.getHeight()) / 2);

        dialog.show(stage);
    }

    public void showStoryDialog(Runnable onConfirm) {
        StoryManager storyManager = StoryManager.getInstance();

        Dialog dialog = new Dialog(storyManager.getCurrentLevelTitle(), skin) {
            {
                setBackground(dialogBackground);

                Label titleLabel = new Label(storyManager.getTitle(), skin);
                titleLabel.setAlignment(Align.center);

                getTitleTable().clear();
                getTitleTable().pad(20);
                getTitleTable().add(titleLabel).expandX().fillX().padTop(20);
            }

            @Override
            protected void result(Object obj) {
                if (obj instanceof Boolean && (Boolean) obj && onConfirm != null) {
                    remove(); // 确保对话框从stage中移除
                    // 设置游戏状态为 PLAYING
                    GameWorld.getInstance().setGameState(GameState.PLAYING);
                    onConfirm.run();
                }
            }
        };

        // 构建介绍文本
        StringBuilder messageText = new StringBuilder();
        messageText.append("\n");
        messageText.append(storyManager.getIntroTitle()).append("\n");
        for (String line : storyManager.getIntroContent()) {
            messageText.append(line).append("\n");
        }

        Label messageLabel = new Label(messageText.toString(), skin, "hud-medium");
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        // 创建内容表格并设置布局
        Table contentTable = new Table();
        contentTable.defaults().pad(20);
        contentTable.add(messageLabel).width(500f).expandX(); // 增加文本宽度

        // 将内容表格添加到对话框
        dialog.getContentTable().clear();
        dialog.getContentTable().add(contentTable).expand().fill();

        // 添加按钮
        TextButton button = new TextButton("OK", skin);
        dialog.button(button, true).padBottom(20);

        // 设置对话框大小和位置
        dialog.setSize(600, 400); // 调整对话框大小
        dialog.setPosition(
                (stage.getWidth() - dialog.getWidth()) / 2,
                (stage.getHeight() - dialog.getHeight()) / 2);

        dialog.show(stage);
    }

    public Dialog showTransitionDialog(String title, String message) {
        Gdx.app.log("GameDialogs", "Showing transition dialog");
        Dialog dialog = new Dialog("", skin) {
            {
                setBackground(dialogBackground);
                getTitleTable().add(new Label(title, skin)).pad(20).center();
            }
        };

        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        dialog.getContentTable().add(messageLabel).width(400f).pad(20);
        dialog.setSize(500, 200);
        dialog.setPosition(
                (stage.getWidth() - dialog.getWidth()) / 2,
                (stage.getHeight() - dialog.getHeight()) / 2);

        dialog.show(stage);
        return dialog;
    }

    public void updateTransitionDialog(String message) {
        // Find and update existing transition dialog if it exists
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Dialog) {
                Dialog dialog = (Dialog) actor;
                Table contentTable = dialog.getContentTable();
                if (contentTable.getChildren().size > 0) {
                    Actor firstChild = contentTable.getChildren().first();
                    if (firstChild instanceof Label) {
                        ((Label) firstChild).setText(message);
                    }
                }
            }
        }
    }

    public void showGameFinishedDialog(Runnable callback) {
        Dialog dialog = new Dialog("", skin) {
            {
                setBackground(dialogBackground);
                getTitleTable().add(new Label("Congratulations!", skin)).pad(20);
            }
        };

        Label messageLabel = new Label("You've completed all levels!\nThank you for playing!", skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        dialog.getContentTable().add(messageLabel).width(400f).pad(20);
        dialog.button("OK", true).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.run();
            }
        });

        dialog.show(stage);
    }

    public void hideTransitionDialog() {
        Gdx.app.log("GameDialogs", "Hiding transition dialog");
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Dialog) {
                Dialog dialog = (Dialog) actor;
                dialog.remove();
            }
        }
    }

    public void showPauseDialog(Runnable onResume, Runnable onMainMenu) {
        if (pauseDialog != null)
            pauseDialog.remove();

        pauseDialog = new Dialog("", skin) {
            {
                setBackground(dialogBackground);

                Label titleLabel = new Label("PAUSED", skin, "hud-large");
                titleLabel.setAlignment(Align.center);

                getTitleTable().clear();
                getTitleTable().add(titleLabel).expandX().fillX().pad(20);

                TextButton resumeButton = new TextButton("Resume", skin);
                TextButton mainMenuButton = new TextButton("Main Menu", skin);

                getButtonTable().add(resumeButton).width(200).height(60).pad(10).row();
                getButtonTable().add(mainMenuButton).width(200).height(60).pad(10);

                resumeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        hide();
                        if (onResume != null)
                            onResume.run();
                    }
                });

                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        hide();
                        if (onMainMenu != null)
                            onMainMenu.run();
                    }
                });
            }
        };

        pauseDialog.show(stage);
    }

    public void hidePauseDialog() {
        if (pauseDialog != null) {
            pauseDialog.hide();
        }
    }

    public Stage getStage() {
        return stage;
    }
}
