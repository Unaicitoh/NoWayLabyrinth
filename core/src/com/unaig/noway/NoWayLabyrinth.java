package com.unaig.noway;

import com.badlogic.gdx.Game;
import com.unaig.noway.data.Assets;
import com.unaig.noway.screens.GameScreen;

public class NoWayLabyrinth extends Game {


    @Override
    public void create() {
        Assets.instance.load();
        setScreen(new GameScreen());

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        Assets.instance.dispose();
    }
}
