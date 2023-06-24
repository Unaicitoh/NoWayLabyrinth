package com.unaig.noway.entities.objects;

import com.badlogic.gdx.math.Rectangle;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

public class Dialog {


    private TypingLabel label;
    private Rectangle rectangle;

    public Dialog(String text, Rectangle rectangle) {
        label = new TypingLabel(text, Assets.instance.mainSkin);
        this.rectangle = rectangle;
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
}
