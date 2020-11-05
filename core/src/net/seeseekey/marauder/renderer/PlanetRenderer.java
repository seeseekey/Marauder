package net.seeseekey.marauder.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.seeseekey.marauder.model.Planet;
import net.seeseekey.marauder.model.PlanetType;

public class PlanetRenderer {
    private final Texture planetTexture;

    public PlanetRenderer() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        planetTexture = new Texture(Gdx.files.internal(PlanetType.PLANET_1.getFilename()));
    }

    public void render(Planet planet, SpriteBatch batch) {

        TextureRegion textureRegion = new TextureRegion(planetTexture);

        batch.draw(
                textureRegion,
                planet.getX(),
                planet.getY(),
                planet.getOriginX(),
                planet.getOriginY(),
                planet.width,
                planet.height,
                planet.getScaleX(),
                planet.getScaleY(),
                0);
    }
}