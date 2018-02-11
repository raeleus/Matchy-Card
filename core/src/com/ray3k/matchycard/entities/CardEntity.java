/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
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

package com.ray3k.matchycard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.ray3k.matchycard.Core;
import com.ray3k.matchycard.Entity;
import com.ray3k.matchycard.SpineEntity;
import com.ray3k.matchycard.states.GameState;

public class CardEntity extends SpineEntity {
    private final static int REGION_RADIUS = 70;
    private String name;

    public CardEntity(TextureRegion region) {
        super(Core.DATA_PATH + "/spine/card.json", "back");
        RegionAttachment regionAttachment = new RegionAttachment("image");
        regionAttachment.setRegion(region);
        regionAttachment.setX(0.0f);
        regionAttachment.setY(0.0f);
        regionAttachment.setScaleX(1.0f);
        regionAttachment.setScaleY(1.0f);
        regionAttachment.getOffset()[0] = -REGION_RADIUS;
        regionAttachment.getOffset()[1] = REGION_RADIUS;
        regionAttachment.getOffset()[2] = REGION_RADIUS;
        regionAttachment.getOffset()[3] = REGION_RADIUS;
        regionAttachment.getOffset()[4] = REGION_RADIUS;
        regionAttachment.getOffset()[5] = -REGION_RADIUS;
        regionAttachment.getOffset()[6] = -REGION_RADIUS;
        regionAttachment.getOffset()[7] = -REGION_RADIUS;
        getSkeleton().findSlot("image").setAttachment(regionAttachment);
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("drop")) {
                    CardEntity.this.dispose();
                }
            }
            
        });
    }

    @Override
    public void actSub(float delta) {
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            if (getSkeletonBounds().aabbContainsPoint(Gdx.input.getX(), GameState.GAME_HEIGHT - Gdx.input.getY())) {
                if (getAnimationState().getCurrent(0).getAnimation().getName().equals("back")) {
                    if (GameState.flippedCards.size < 2) {
                        GameState.inst().playSound("flip", .5f);
                    }
                    
                    switch (GameState.flippedCards.size) {
                        case 0:
                            getAnimationState().setAnimation(0, "back-to-front", false);
                            getAnimationState().addAnimation(0, "front", false, 0.0f);
                            GameState.flippedCards.add(this);
                            break;
                        case 1:
                            getAnimationState().setAnimation(0, "back-to-front", false);
                            getAnimationState().addAnimation(0, "front", false, 0.0f);
                            GameState.flippedCards.add(this);
                            
                            GameState.inst().resetCards();
                            break;
                        default:
                            GameState.inst().resetCards(0.1f);
                    }
                }
            }
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
