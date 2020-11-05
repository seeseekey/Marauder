package net.seeseekey.marauder.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.seeseekey.marauder.model.Ship;
import net.seeseekey.marauder.model.ShipType;

public class ShipRenderer {

    private final Texture playerShip;

    public ShipRenderer() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        playerShip = new Texture(Gdx.files.internal(ShipType.PLAYER.getFilename()));
    }

    public void render(Ship ship, SpriteBatch batch) {

        TextureRegion textureRegion = new TextureRegion(playerShip);

        batch.draw(
                textureRegion,
                ship.getX(),
                ship.getY(),
                ship.getOriginX(),
                ship.getOriginY(),
                ship.width,
                ship.height,
                ship.getScaleX(),
                ship.getScaleY(),
                -ship.getRotation());
    }

    // TODO Dispose
}
