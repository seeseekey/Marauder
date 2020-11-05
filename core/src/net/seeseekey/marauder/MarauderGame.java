package net.seeseekey.marauder;

import com.badlogic.gdx.Game;
import net.seeseekey.marauder.screens.GameScreen;

public class MarauderGame extends Game {

    private int viewportWidth = 800;
    private int viewportHeight = 480;

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    @Override
    public void create() {

        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
    }
}
