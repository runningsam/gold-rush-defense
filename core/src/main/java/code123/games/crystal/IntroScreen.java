package code123.games.crystal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.Align;
import code123.games.crystal.story.StoryManager;
import com.badlogic.gdx.utils.Timer;
import code123.games.crystal.save.SaveManager;

public class IntroScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

    public IntroScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        
        // Load assets
        AssetManager.getInstance().loadAssets();
        this.skin = AssetManager.getInstance().getUISkin();
        
        createUI();
        
        // Set input processor
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        
        StoryManager storyManager = StoryManager.getInstance();
        String language = "en"; // 或从游戏设置中获取当前语言
        
        // 使用story.json中的标题
        Label titleLabel = new Label(
            storyManager.getGameIntroTitle(language), 
            skin, 
            "hud-large"
        );
        
        // 使用story.json中的故事文本
        String storyText = String.join("\n", storyManager.getGameIntroStory(language));
        Label storyLabel = new Label(storyText, skin, "hud-medium");
        storyLabel.setWrap(true);
        storyLabel.setAlignment(Align.center);
        
        // 创建一个容器表格来放置故事文本
        Table storyContainer = new Table();
        storyContainer.add(storyLabel).width(580).pad(10);  // 稍微减小宽度，为滚动条留出空间
        
        // 创建一个基础的 ScrollPaneStyle
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        ScrollPane scrollPane = new ScrollPane(storyContainer, scrollPaneStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, true); // 强制显示垂直滚动条
        scrollPane.setScrollBarPositions(false, true); // 设置滚动条位置
        
        // 添加继续游戏按钮
        boolean hasSave = SaveManager.getInstance().hasSavedGame();
        
        // 创建按钮容器
        Table buttonTable = new Table();
        buttonTable.defaults().pad(10).width(200).height(60);  // 设置默认的按钮样式
        
        TextButton continueButton = new TextButton("Continue Game", skin);
        continueButton.setDisabled(!hasSave);  // 如果没有存档则禁用按钮
        
        // 设置禁用状态下的颜色
        if (!hasSave) {
            continueButton.setColor(0.5f, 0.5f, 0.5f, 0.6f);  // 灰色半透明
        }
        
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!continueButton.isDisabled()) {
                    StoryManager.getInstance().loadSavedProgress();
                    game.setScreen(new GameScreen(game));
                }
            }
        });
        
        buttonTable.add(continueButton);
        
        // Start button
        TextButton startButton = new TextButton(hasSave ? "New Game" : "Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 新游戏时重置到第一关
                StoryManager.getInstance().setLevel("level1");
                // 清除存档
                SaveManager.getInstance().resetProgress();
                game.setScreen(new GameScreen(game));
            }
        });

        // Layout
        mainTable.add(titleLabel).pad(50).row();
        mainTable.add(scrollPane)
            .width(600)
            .height(250)  // 设置固定高度
            .pad(20)
            .row();
        buttonTable.add(startButton);
        mainTable.add(buttonTable).pad(40).row();
        
        stage.addActor(mainTable);
        
        // 添加自动滚动效果
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float scrollY = scrollPane.getScrollY();
                float maxScroll = scrollPane.getMaxY();
                
                // 如果还没有滚动到底部，继续滚动
                if (scrollY < maxScroll) {
                    scrollPane.setScrollY(Math.min(scrollY + 0.5f, maxScroll));
                }
            }
        }, 0, 0.016f);  // 约60fps的更新频率
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
} 