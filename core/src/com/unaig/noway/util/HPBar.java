package com.unaig.noway.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.Color.*;

public class HPBar {

    public static final String TAG = HPBar.class.getName();

    private final Rectangle outline;
    private final Rectangle content;
    private float visualHp;
    private final int maxHp;
    private final Vector2 size;
    private Color color;

    public HPBar(int maxHp, Vector2 size) {
        this.maxHp = maxHp;
        this.size = size;
        visualHp = maxHp;
        color = GREEN;
        content = new Rectangle();
        outline = new Rectangle();
    }

    public void render(ShapeDrawer shaper, float delta, Vector2 pos, float hp) {
        update(delta, pos, hp);
        renderHPBackground(shaper);
        if (hp <= maxHp / 4f) {
            this.color = FIREBRICK;
        } else if (hp <= maxHp / 2f) {
            this.color = YELLOW;
        } else {
            this.color = GREEN;
        }
        shaper.setColor(color);
        shaper.filledRectangle(content);

        float x = (this.visualHp - hp);
        shaper.setColor(RED);
        shaper.filledRectangle(content.x + hp * size.x / maxHp, content.y, x * size.x / maxHp, content.height);
        shaper.setColor(WHITE);
    }

    private void renderHPBackground(ShapeDrawer shaper) {
        float hpOutline = .5f;
        shaper.setColor(BLACK);
        shaper.filledRectangle(content.x - hpOutline, content.y - hpOutline, size.x + hpOutline * 2, content.height + hpOutline * 2);

        shaper.setColor(GRAY);
        shaper.filledRectangle(content.x, content.y, size.x, content.height);
    }

    private void update(float delta, Vector2 pos, float hp) {
        hp = MathUtils.clamp(hp, 0, maxHp);
//        Gdx.app.log(TAG,""+hp);
//        Gdx.app.log(TAG,""+visualHp);
//        Gdx.app.log(TAG,"rendering");
        if (visualHp > hp) {
            visualHp = Math.max(hp, visualHp - delta * 35);

        } else if (visualHp < hp) {
            visualHp = Math.min(hp, visualHp + delta * 35);
        }

        content.set(pos.x, pos.y + size.y, visualHp * size.x / maxHp, size.y / 9);
        outline.set(content);
    }

    public float getVisualHp() {
        return visualHp;
    }
}
