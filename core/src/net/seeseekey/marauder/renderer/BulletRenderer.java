package net.seeseekey.marauder.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.seeseekey.marauder.model.Bullet;
import net.seeseekey.marauder.model.BulletType;

public class BulletRenderer {

    private final Texture bulletTexture;

    public BulletRenderer() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        bulletTexture = new Texture(Gdx.files.internal(BulletType.LASER_1.getFilename()));
    }

    public void render(Bullet bullet, SpriteBatch batch) {

        TextureRegion textureRegion = new TextureRegion(bulletTexture);

        batch.draw(
                textureRegion,
                bullet.getX(),
                bullet.getY(),
                bullet.getOriginX(),
                bullet.getOriginY(),
                bullet.width,
                bullet.height,
                bullet.getScaleX(),
                bullet.getScaleY(),
                -bullet.getRotation());
    }
}