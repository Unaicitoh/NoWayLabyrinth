package com.unaig.noway.entities.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

public class Dialog {


    private TypingLabel label;
    private Rectangle rect;

    public Dialog(String text, Rectangle rectangle) {
        label = new TypingLabel(text, Assets.instance.mainSkin);
        rect = rectangle;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public TypingLabel getLabel() {
        return label;
    }

    public void setLabel(TypingLabel label) {
        this.label = label;
    }
}
