/*
 * The MIT License
 *
 * Copyright 2017 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.matchycard.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.matchycard.Core;
import com.ray3k.matchycard.Entity;
import com.ray3k.matchycard.EntityManager;
import com.ray3k.matchycard.InputManager;
import com.ray3k.matchycard.State;
import com.ray3k.matchycard.entities.CardEntity;
import com.ray3k.matchycard.entities.GameOverTimerEntity;

public class GameState extends State {
    private static GameState instance;
    private float score;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private InputManager inputManager;
    private Skin skin;
    private Stage stage;
    private Table table;
    private Label scoreLabel;
    public static EntityManager entityManager;
    public static final float GAME_WIDTH = 800.0f;
    public static final float GAME_HEIGHT = 600.0f;
    public static Array<CardEntity> flippedCards;
    private float resetCardsTimer;
    
    public static GameState inst() {
        return instance;
    }
    
    public GameState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        instance = this;
        
        score = 0;
        
        inputManager = new InputManager();
        
        gameCamera = new OrthographicCamera();
        gameViewport = new StretchViewport(GameState.GAME_WIDTH, GameState.GAME_HEIGHT, gameCamera);
        gameViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), true);
        gameViewport.apply();
        
        gameCamera.position.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/Matchy Card.json", Skin.class);
        stage = new Stage(new StretchViewport(GameState.GAME_WIDTH, GameState.GAME_HEIGHT));
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputManager);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        entityManager = new EntityManager();
        
//        createStageElements();
        
        flippedCards = new Array<CardEntity>();

        Array<String> textures = new Array<String>();
        for (String string : Core.imagePacks.get(Core.DATA_PATH + "/cards")) {
            textures.add(string);
        }
        
        textures.shuffle();
        textures.setSize(10);
        
        textures.addAll(textures);

        final int columns = 5;
        final int rows = 20 / columns;
        final float sideBorder = 100.0f;
        final float topBorder = 100.0f;
        for (int j = 0; j < 20 / columns; j++) {
            for (int i = 0; i < columns; i++) {
                String name = textures.random();
                textures.removeValue(name, false);
                TextureRegion textureRegion = Core.generatedAtlas.findRegion(name);
                
                CardEntity card = new CardEntity(textureRegion);
                card.setName(name);
                card.getSkeleton().findBone("root").setScale(.5f);
                card.setX(sideBorder + (GAME_WIDTH - sideBorder * 2) / (columns - 1) * i);
                card.setY(topBorder + (GAME_HEIGHT - topBorder * 2) / (rows - 1) * j);
                entityManager.addEntity(card);
            }
        }
    }
    
    public void resetCards() {
        if (flippedCards.size == 2 && flippedCards.get(0).getName().equals(flippedCards.get(1).getName())) {
            resetCardsTimer = 1.0f;
        } else {
            resetCardsTimer = 2.0f;
        }
    }
    
    public void resetCards(float time) {
        resetCardsTimer = time;
    }
    
    public boolean checkForWin() {
        boolean win = true;
        
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof CardEntity) {
                if (!((CardEntity) entity).getAnimationState().getCurrent(0).getAnimation().getName().equals("drop")) {
                    win = false;
                    break;
                }
            }
        }
        
        return win;
    }
    
    private void createStageElements() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        scoreLabel = new Label("0", skin);
        root.add(scoreLabel).expandY().padTop(25.0f).top().expandX();
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        gameCamera.update();
        spriteBatch.setProjectionMatrix(gameCamera.combined);
        spriteBatch.begin();
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        entityManager.draw(spriteBatch, delta);
        spriteBatch.end();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        stage.draw();
    }

    @Override
    public void act(float delta) {
        score += delta;
        
        if (resetCardsTimer > 0) {
            resetCardsTimer -= delta;
            if (resetCardsTimer <= 0) {
                boolean dropCards = flippedCards.size == 2 && flippedCards.get(0).getName().equals(flippedCards.get(1).getName());
                
                if (dropCards) {
                    playSound("beep", .5f);
                } else {
//                    playSound("wrong", .5f);
                }
                
                for (Entity entity : entityManager.getEntities()) {
                    if (entity instanceof CardEntity) {
                        CardEntity card = (CardEntity) entity;
                        if (!card.getAnimationState().getCurrent(0).getAnimation().getName().equals("back")) {
                            if (!dropCards) {
                                card.getAnimationState().setAnimation(0, "front-to-back", false);
                                card.getAnimationState().addAnimation(0, "back", false, 0.0f);
                            } else {
                                card.getAnimationState().setAnimation(0, "drop", false);
                                card.setDepth(-10);
                            }
                        }
                    }
                }
                flippedCards.clear();
                if (checkForWin()) {
                    entityManager.addEntity(new GameOverTimerEntity(2.0f));
                }
            }
        }
        
        entityManager.act(delta);
        
        stage.act(delta);
        
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            playSound("win", .5f);
            Core.stateManager.loadState("menu");
        }
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    public float getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText(Integer.toString(score));
    }
    
    public void addScore(float score) {
        this.score += score;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public void setGameCamera(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void playSound(String name) {
        playSound(name, 1.0f, 1.0f);
    }
    
    public void playSound (String name, float volume) {
        playSound(name, volume, 1.0f);
    }
    
    /**
     * 
     * @param name
     * @param volume
     * @param pitch .5 to 2. 1 is default
     */
    public void playSound(String name, float volume, float pitch) {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/" + name + ".wav", Sound.class).play(volume, pitch, 0.0f);
    }
}