package com.unaig.noway.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.unaig.noway.screens.GameScreen;

public class HPBar {

    public static final String TAG = HPBar.class.getName();

    private Rectangle outline;
    private Rectangle content;
    private int hp;
    private int maxHp;
    private Vector2 size;
    private Color color;

    public HPBar(int hp, int maxHp, Vector2 size, Color color) {
        this.hp = hp;
        this.maxHp = maxHp;
        this.size = size;
        this.color = color;
        content=new Rectangle();
    }

    public void render(ShapeRenderer shaper, float delta, Vector2 pos, int hp){
        update(delta, pos, hp);
        shaper.setColor(color);
        shaper.rect(content.x,content.y,content.width, content.height);
        shaper.setColor(Color.WHITE);
    }

    private void update(float delta, Vector2 pos, int hp){
        hp= MathUtils.clamp(hp,0,maxHp);
        this.hp=hp;
        Gdx.app.log(TAG,"size"+size);
        Gdx.app.log(TAG,"content"+content.x);

        content.set(pos.x,pos.y+size.y,hp*size.x/maxHp,size.y/3);
    }
}
