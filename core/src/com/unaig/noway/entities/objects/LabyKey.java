package com.unaig.noway.entities.objects;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

public class LabyKey extends Item {

    public LabyKey() {
        itemImage = new Image(Assets.instance.objectsAtlas.findRegion("keyIcon"));
        itemImage.scaleBy(.5f);
        label = new TypingLabel("{FASTER}{SHRINK=1.0;1.0;true}[%50]LABYRINTH KEY !! \n" +
                "x1 obtained[%][@regular]{ENDSHRINK}", Assets.instance.mainSkin, "regular");
    }
}
