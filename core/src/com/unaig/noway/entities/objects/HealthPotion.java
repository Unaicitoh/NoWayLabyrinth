package com.unaig.noway.entities.objects;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.data.Assets;

public class HealthPotion extends Item {

    public HealthPotion() {
        itemImage = new Image(Assets.instance.objectsAtlas.findRegion("healthPotion"));
        label = new TypingLabel("{FASTER}{SHRINK=1.0;1.0;true}[%50]Health Potion \nRestores 50 HP\n" +
                "x1 obta ined[%][@regular]{ENDSHRINK}", Assets.instance.mainSkin, "regular");
        emptyLabel = new TypingLabel("{FASTER}{SHRINK=1.0;1.0;true}[%50]Max. quantity\n" +
                "of potions\nreached.[%][@regular]{ENDSHRINK}", Assets.instance.mainSkin, "regular");
    }


}
