package net.seeseekey.marauder.model;

import com.badlogic.gdx.math.Vector2;
import net.seeseekey.marauder.utils.Tuple2;

public class Bullet extends Entity {

    public Vector2 direction;

    private int energy;

    public static Bullet create(final int x, final int y, Vector2 direction, float rotation, final BulletType type) {

        Tuple2<Float, Float> widthAndHeightFromPolygonVertices = Entity.getWidthAndHeightFromPolygonVertices(type.getVertices());

        // Center bullet on spawn point
        int correctedX = (int) (x - widthAndHeightFromPolygonVertices.x / 2);
        int correctedY = (int) (y - widthAndHeightFromPolygonVertices.y / 2);

        return new Bullet(correctedX, correctedY, direction, rotation, type);
    }

    private Bullet(final int x, final int y, Vector2 direction, float rotation, final BulletType type) {

        super(x, y, type.getVertices());

        this.direction = direction;
        this.polygon.setRotation(rotation);

        this.energy = type.getEnergy();
    }

    public void addDirection(float deltaTime) {

        Vector2 positionShip = new Vector2(polygon.getX(), polygon.getY());
        positionShip = positionShip.add(direction);
        polygon.setPosition(positionShip.x, positionShip.y);

    }

    public int getEnergy() {
        return energy;
    }
}