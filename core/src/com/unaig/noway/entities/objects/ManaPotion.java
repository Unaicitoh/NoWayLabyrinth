package com.unaig.noway.entities.objects;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

public class ManaPotion extends Item {
    public ManaPotion() {
        itemImage = new Image(Assets.instance.objectsAtlas.findRegion("manaPotion"));
        label = new TypingLabel("{FAST}{SHRINK=1.0;1.0;true}[%50]Mana Potion \n" +
                "x1 obtained[%][@regular]{ENDSHRINK}", Assets.instance.mainSkin, "regular");
    }
}
