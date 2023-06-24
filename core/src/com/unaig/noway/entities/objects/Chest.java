package com.unaig.noway.entities.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.TILE_SIZE;

public class Chest {


    public Animation<AtlasRegion> getAnimation() {
        return animation;
    }
    private Animation<AtlasRegion> animation;
    private float stateTime;
    private boolean isOpen;
    private Rectangle rectangle;

    public Chest(Array<AtlasRegion> animation, Rectangle rectangle) {
        this.animation = new Animation<>(.25f, animation, Animation.PlayMode.NORMAL);
        this.rectangle = rectangle;
        isOpen = false;
        stateTime=0;
    }

    public void draw(SpriteBatch batch) {
        if(!isOpen){
            stateTime=0;
            batch.draw(animation.getKeyFrame(stateTime),rectangle.x,rectangle.y,TILE_SIZE/1.5f,TILE_SIZE/1.5f);
        }
    }

    public void setAnimation(Animation<AtlasRegion> animation) {
        this.animation = animation;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

}
