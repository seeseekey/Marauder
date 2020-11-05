package net.seeseekey.marauder.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import net.seeseekey.marauder.utils.Tuple2;

import java.util.List;

public class Collider {

    public void collide(List<Entity> entitiesFromQuadtree, float delta) {

        for (Entity entityA : entitiesFromQuadtree) {

            for (Entity entityB : entitiesFromQuadtree) {

                if (entityA.equals(entityB)) {
                    continue; // Same object, continue
                }

                if (Intersector.overlaps(entityA.getPolygon().getBoundingRectangle(), entityB.getPolygon().getBoundingRectangle())) {
                    if (Intersector.overlapConvexPolygons(entityA.getPolygon(), entityB.getPolygon())) {
                        Gdx.app.debug("COLLISION", "Collision detected: " + entityA.toString() + " / " + entityB.toString());
                        handleCollision(entityA, entityB, delta);
                    }
                }
            }
        }
    }

    enum CollisionType {
        ShipToShip,
        ShipToPlanet,
        //        ShipToBullet,
        BulletToBullet,
        BulletToShip,
        BulletToPlanet,
        // Planet to planet should not be
        Unknown
    }

    private CollisionType getCollisionType(Entity a, Entity b) {

        if (a instanceof Ship && b instanceof Ship) {
            return CollisionType.ShipToShip;
        }

        if ((a instanceof Ship && b instanceof Planet) || (a instanceof Planet && b instanceof Ship)) {
            return CollisionType.ShipToPlanet;
        }

        if ((a instanceof Bullet && b instanceof Planet) || (a instanceof Planet && b instanceof Bullet)) {
            return CollisionType.BulletToPlanet;
        }

        if ((a instanceof Bullet && b instanceof Ship) || (a instanceof Ship && b instanceof Bullet)) {
            return CollisionType.BulletToShip;
        }

        return CollisionType.Unknown;
    }

    private void handleCollision(Entity a, Entity b, float delta) {

        CollisionType collisionType = getCollisionType(a, b);

        switch (collisionType) {
            case ShipToShip: {

                handleHardShipCollision(a, b, delta);
                break;
            }
            case ShipToPlanet: {

                if (a instanceof Planet) {
                    handleShipPlanetCollision(b, a, delta); // Another thing is the ship
                }

                if (b instanceof Planet) {
                    handleShipPlanetCollision(a, b, delta);
                }

                break;
            }
            case BulletToShip: {

                if (a instanceof Bullet) {

                    Bullet bullet = (Bullet) a;
                    bullet.destroy();

                    Ship ship = (Ship) b;
                    ship.setShipHullHealth(ship.getShipHullHealth() - bullet.getEnergy());

                    if (ship instanceof ControlledShip) {
                        ((ControlledShip) ship).underAttack();
                    }
                }

                if (b instanceof Bullet) {
                    Bullet bullet = (Bullet) b;
                    bullet.destroy();

                    Ship ship = (Ship) a;
                    ship.setShipHullHealth(ship.getShipHullHealth() - bullet.getEnergy());

                    if (ship instanceof ControlledShip) {
                        ((ControlledShip) ship).underAttack();
                    }
                }

                break;
            }
            case BulletToPlanet: {

                if (a instanceof Bullet) {
                    Bullet bullet = (Bullet) a;
                    bullet.destroy();
                }

                if (b instanceof Bullet) {
                    Bullet bullet = (Bullet) b;
                    bullet.destroy();
                }

                break;
            }
            case Unknown: {
                // Don't handle unknown collisions
                break;
            }
        }
    }

    private Tuple2<Vector2, Vector2> getCollisionLineForObject(Entity object, Entity collidingObject) {

        float currentDistanceLineToPoint = Float.MAX_VALUE;

        float x1BestLine = 0;
        float y1BestLine = 0;
        float x2BestLine = 0;
        float y2BestLine = 0;

        for (int i = 0; i < object.getPolygon().getTransformedVertices().length - 4; i += 2) {

            float x1 = object.getPolygon().getTransformedVertices()[i];
            float y1 = object.getPolygon().getTransformedVertices()[i + 1];
            float x2 = object.getPolygon().getTransformedVertices()[i + 2];
            float y2 = object.getPolygon().getTransformedVertices()[i + 3];

            for (int j = 0; j < collidingObject.getPolygon().getTransformedVertices().length; j += 2) {

                float x1Ship = collidingObject.getPolygon().getTransformedVertices()[j];
                float y1Ship = collidingObject.getPolygon().getTransformedVertices()[j + 1];

                float distanceLineToPoint = Intersector.distanceLinePoint(x1, y1, x2, y2, x1Ship, y1Ship);

                if (distanceLineToPoint < currentDistanceLineToPoint) {
                    currentDistanceLineToPoint = distanceLineToPoint;

                    x1BestLine = x1;
                    y1BestLine = y1;
                    x2BestLine = x2;
                    y2BestLine = y2;
                }
            }
        }

        Vector2 linePointA = new Vector2(x1BestLine, y1BestLine);
        Vector2 linePointB = new Vector2(x2BestLine, y2BestLine);

        return new Tuple2<>(linePointA, linePointB);
    }

    private Vector2 getNormalOutsideForLine(Vector2 linePointA, Vector2 linePointB) {

        // Calc normal
        float dx = linePointB.x - linePointA.x;
        float dy = linePointB.y - linePointA.y;

        // Get normal thats shows out and normalize
        Vector2 normalOutside = new Vector2(dy, -dx);
        normalOutside.nor();

        return normalOutside;
    }

    private Vector2 getNormalInsideForLine(Vector2 linePointA, Vector2 linePointB) {

        // Calc normal
        float dx = linePointB.x - linePointA.x;
        float dy = linePointB.y - linePointA.y;

        // Get normal thats shows out and normalize
        Vector2 normaleInside = new Vector2(-dy, dx);
        normaleInside.nor();

        return normaleInside;
    }

    private void handleShipPlanetCollision(Entity shipEntity, Entity planet, float delta) {

        Ship ship = (Ship) shipEntity;

        Tuple2<Vector2, Vector2> collidingLineForPlanet = getCollisionLineForObject(planet, ship);
        Vector2 normalOutside = getNormalOutsideForLine(collidingLineForPlanet.x, collidingLineForPlanet.y);

        // Add normal to ship to avoid stucking in planet
        do {
            // Use normal of colliding line of planet to remove ship from collision area
            ship.direction = new Vector2(ship.direction.x * -1, ship.direction.y * -1); // Invert vector
            ship.direction.add(normalOutside);
            ship.addDirection(delta);

        } while (Intersector.overlapConvexPolygons(shipEntity.getPolygon(), planet.getPolygon()));

        // Slow down the ship, after planet collision
        ship.direction = ship.direction.scl(0.25f);

        ship.setShipHullHealth(ship.getShipHullHealth() - 100);
    }

    private void handleHardShipCollision(Entity shipEntityA, Entity shipEntityB, float delta) {

        Ship shipA = (Ship) shipEntityA;
        Ship shipB = (Ship) shipEntityB;

        // Energy handling
        // Add the half of impulse to other ship and vice versa

        // Ship B
        Tuple2<Vector2, Vector2> collidingLineOnShipB = getCollisionLineForObject(shipB, shipA);

        Vector2 normalOutsideFromShipB = getNormalOutsideForLine(collidingLineOnShipB.x, collidingLineOnShipB.y);

        // Ship A
        Tuple2<Vector2, Vector2> collidingLineOnShipA = getCollisionLineForObject(shipA, shipB);

        Vector2 normalOutsideFromShipA = getNormalOutsideForLine(collidingLineOnShipA.x, collidingLineOnShipA.y);

        // Add normal to ship to avoid stucking in ship
        do {
            // Use normal of colliding line of other ship to remove ship from collision area
            shipA.direction = new Vector2(shipA.direction.x * -1, shipA.direction.y * -1); // Invert vector
            shipA.direction.add(normalOutsideFromShipB);
            shipA.addDirection(delta);

            // Use normal of colliding line of other ship to remove ship from collision area
            shipB.direction = new Vector2(shipB.direction.x * -1, shipB.direction.y * -1); // Invert vector
            shipB.direction.add(normalOutsideFromShipA);
            shipB.addDirection(delta);

        }
        while (Intersector.overlapConvexPolygons(shipA.getPolygon(), shipB.getPolygon())); // Ensure that ships never overlap

        // Slow down the ship, after ship collision
        shipA.direction = shipA.direction.scl(0.25f);

        // Slow down the ship, after ship collision
        shipB.direction = shipB.direction.scl(0.25f);

        shipA.setShipHullHealth(shipA.getShipHullHealth() - 100);
        shipB.setShipHullHealth(shipB.getShipHullHealth() - 100);
    }
}