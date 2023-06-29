package com.unaig.noway;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.tommyettinger.colorful.Shaders;
import com.unaig.noway.data.Assets;
import com.unaig.noway.screens.BlankScreen;
import com.unaig.noway.screens.GameScreen;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.ManagedScreen;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;

public class NoWayLabyrinth extends ManagedGame<ManagedScreen, ScreenTransition> {

    //TODO cofres dont show correctly, keep status and invetory when going up, visible sprint, center bugs, 4th stair not showing
    //TODO cursor disable actions, reset map center and to easy, and pathfinding
    private SpriteBatch batch;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch(1000, Shaders.makeRGBAShader());
        Assets.instance.load();
        screenManager.addScreen("Game", new GameScreen(this));
        screenManager.addScreen("Reset", new BlankScreen());

        BlendingTransition b = new BlendingTransition(batch, 1f);
        screenManager.addScreenTransition("blend", b);
        screenManager.pushScreen("Game", "blend");
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

    public SpriteBatch getBatch() {
        return batch;
    }
}
