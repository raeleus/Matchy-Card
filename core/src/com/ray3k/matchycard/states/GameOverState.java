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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.matchycard.Core;
import com.ray3k.matchycard.State;

public class GameOverState extends State {
    private Stage stage;
    private Skin skin;
    private float score;
    private float highScore;

    public GameOverState(Core core) {
        super(core);
        highScore = 999.0f;
    }

    @Override
    public void start() {
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/Matchy Card.json", Skin.class);
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        refreshTable();
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void act(float delta) {
        stage.act(delta);
        
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            Core.stateManager.loadState("menu");
        }
    }

    @Override
    public void stop() {
        stage.dispose();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
        if (score < highScore) {
            highScore = score;
        }
    }
    
    private void refreshTable() {
        stage.clear();
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Label label = new Label("Game Over", skin);
        root.add(label).colspan(2);
        
        root.row();
        label = new Label("Time (seconds)\n\n" + (int) score, skin);
        label.setAlignment(Align.center);
        root.add(label).pad(20.0f);
        
        if (highScore < 900) {
            root.row();
            label = new Label("Best Time\n\n" + (int) highScore, skin);
            label.setAlignment(Align.center);
            root.add(label).pad(20.0f);
        }
        
        root.row();
        label = new Label("Press space\nto return to menu!", skin);
        label.setAlignment(Align.center);
        root.add(label);
    }
}
