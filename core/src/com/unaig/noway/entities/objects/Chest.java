package com.unaig.noway.entities.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

import static com.unaig.noway.util.Constants.TILE_SIZE;

public class Chest extends Object {

    private Animation<AtlasRegion> animation;
    private float stateTime;
    private boolean isOpen;
    private Image itemImage;
    private Item item;


    public Chest(Array<AtlasRegion> animation, Rectangle rectangle) {
        this.animation = new Animation<>(.3f, animation, Animation.PlayMode.NORMAL);
        this.rectangle = rectangle;
        isOpen = false;
        stateTime = 0;
        int rnd = MathUtils.random(4);
        switch (rnd) {
            case 0:
                item = new HealthPotion();
                break;
            case 1:
                item = new ManaPotion();
                break;
            case 2:
                item = new ArmorPotion();
                break;
            case 3:
                item = new LabyKey();
                break;
        }

        if (item == null) {
            label = new TypingLabel("{FAST}{SHRINK=1.0;1.0;true}[%50]Ups... \n" + "Empty chest, good luck in \nthe next one.[%]{ENDSHRINK}", Assets.instance.mainSkin, "regular");
        } else {
            setLabel(item.getLabel());
            setItemImage(item.getItemImage());
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        stateTime += delta;
        if (!isOpen) {
            stateTime = 0;
            batch.draw(animation.getKeyFrame(stateTime), rectangle.x - TILE_SIZE / 3f, rectangle.y - TILE_SIZE / 3f, TILE_SIZE / 1.5f, TILE_SIZE / 1.5f);
        } else {
            batch.draw(animation.getKeyFrame(stateTime), rectangle.x - TILE_SIZE / 3f, rectangle.y - TILE_SIZE / 3f, TILE_SIZE / 1.5f, TILE_SIZE / 1.5f);
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

    public Image getItemImage() {
        return itemImage;
    }

    public void setItemImage(Image itemImage) {
        this.itemImage = itemImage;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
