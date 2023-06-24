package com.unaig.noway.entities.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

import static com.unaig.noway.util.Constants.TILE_SIZE;

public class Chest {

    private Animation<AtlasRegion> animation;
    private float stateTime;
    private boolean isOpen;
    private Rectangle rectangle;
    private TypingLabel label;
    private Image itemImage;


    public Chest(Array<AtlasRegion> animation, Rectangle rectangle) {
        this.animation = new Animation<>(.3f, animation, Animation.PlayMode.NORMAL);
        this.rectangle = rectangle;
        isOpen = false;
        stateTime = 0;
        itemImage = new Image(Assets.instance.objectsAtlas.findRegion("healthPotion"));
        label = new TypingLabel("{FAST}{SHRINK=1.0;1.0;true}[%50]Health Potion \n" +
                "x1 obtained[%][@regular]{ENDSHRINK}", Assets.instance.mainSkin, "regular");
    }

    public void draw(SpriteBatch batch, float delta) {
        stateTime += delta;
        if (!isOpen) {
            stateTime = 0;
            batch.draw(animation.getKeyFrame(stateTime), rectangle.x, rectangle.y, TILE_SIZE / 1.5f, TILE_SIZE / 1.5f);
        } else {
            batch.draw(animation.getKeyFrame(stateTime), rectangle.x, rectangle.y, TILE_SIZE / 1.5f, TILE_SIZE / 1.5f);
        }
    }

    public Animation<AtlasRegion> getAnimation() {
        return animation;
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

    public TypingLabel getLabel() {
        return label;
    }

    public void setLabel(TypingLabel label) {
        this.label = label;
    }

    public Image getItemImage() {
        return itemImage;
    }

    public void setItemImage(Image itemImage) {
        this.itemImage = itemImage;
    }
}
