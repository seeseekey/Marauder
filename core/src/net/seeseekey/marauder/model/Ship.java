package net.seeseekey.marauder.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ship extends Entity {

    public Vector2 direction;

    private int bulletOpening;

    private float speed;

    private int bulletCooldown = 200;

    private long lastBulletcooldown = 0;

    private int shipHullHealth = 500;

    private int money = 1000;

    private int cargo = 0; // cargo in %

    private int shield;

    /**
     * Set position
     *
     * @param x
     * @param y
     */
    public Ship(int x, int y, ShipType type) {

        super(x, y, type.getVertices());

        bulletOpening = type.getBulletOpening();

        speed = type.getSpeed();

        direction = new Vector2(0, 0); // no direction
    }

    /**
     * Normalite through modulu throug 360
     *
     * @param value
     */
    public void turnRight(float value) {
        float rotationRaw = ((polygon.getRotation() - value) % 360);

        if (rotationRaw < 0) {
            rotationRaw = 360 - rotationRaw;
        }

        polygon.setRotation(rotationRaw);
    }

    public void turnLeft(float value) {
        polygon.setRotation((polygon.getRotation() + value) % 360);
    }

    // normal direction to infinity move the ship (no friction in space)
    public void addDirection(float deltaTime) {

        Vector2 positionShip = new Vector2(polygon.getX(), polygon.getY());
        positionShip = positionShip.add(direction);
        polygon.setPosition(positionShip.x, positionShip.y);

    }

    public Bullet getBullet() {

        // Cooldown handling
        long currentTimeMillis = System.currentTimeMillis();
        long diff = currentTimeMillis - lastBulletcooldown;

        if (diff < bulletCooldown) {
            return null;
        }

        lastBulletcooldown = currentTimeMillis;

        // Get view direction from ship
        double directionX = MathUtils.sinDeg(getRotation());
        double directionY = MathUtils.cosDeg(getRotation()); // convert value to rad

        Vector2 directionThrustBullet = new Vector2((float) directionX * speed, (float) directionY * speed); // * 2 is for add speed to the bullet

        // Add ship movement to bullet
        directionThrustBullet = directionThrustBullet.add(direction);

        float x = polygon.getTransformedVertices()[bulletOpening];
        float y = polygon.getTransformedVertices()[bulletOpening + 1];

        Vector2 vector2 = new Vector2(x, y);
        vector2.add(new Vector2((float) directionX * speed, (float) directionY * speed));

        Gdx.app.debug("BULLET", x + "/" + y + "/" + directionThrustBullet.toString() + " | Ship X: " + getX() + " / Y: " + getY() + "Origin X:" + getOriginX() + " Y: " + getOriginY());

        // Debug setting to set bullet speed to zero
        //directionThrustBullet = new Vector2(0, 0);

        Bullet bullet = Bullet.create((int) vector2.x, (int) vector2.y, directionThrustBullet, getRotation(), BulletType.LASER_1);

        return bullet;
    }

    // activate the booster
    public void addDirectionThrust(float deltaTime) {

        // Get view direction from ship
        double directionX = MathUtils.sinDeg(getRotation());
        double directionY = MathUtils.cosDeg(getRotation()); // convert value to rad

        Vector2 positionShip = new Vector2(polygon.getX(), polygon.getY());
        Vector2 directionThrust = new Vector2((float) directionX * deltaTime * speed, (float) directionY * deltaTime * speed);

        direction.add(directionThrust);
        positionShip = positionShip.add(direction);

        polygon.setPosition(positionShip.x, positionShip.y);
    }

    public int getShipHullHealth() {
        return shipHullHealth;
    }

    public void setShipHullHealth(int health) {

        shipHullHealth = health;

        if (shipHullHealth < 0) {
            destroy();
        }
    }
}
