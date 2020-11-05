package net.seeseekey.marauder.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ControlledShip extends Ship {

    public static List<ControlledShip> getControlledShips(int mapWidth, int mapHeight, int borderDistance, int maxShips) {

        List<ControlledShip> controlledShips = new ArrayList<>();

        Vector2 startPoint = new Vector2(mapWidth / 2, mapHeight / 2);

        for (int i = 0; i < maxShips; i++) {

            boolean freeSpace;

            do {
                freeSpace = true;

                int x = ThreadLocalRandom.current().nextInt(borderDistance, mapWidth - borderDistance);
                int y = ThreadLocalRandom.current().nextInt(borderDistance, mapHeight - borderDistance);

                // TODO Never set a planet on start position

                Vector2 newPlanet = new Vector2(x, y);

                for (ControlledShip planet : controlledShips) {

                    Vector2 existingPlanet = new Vector2(planet.getX(), planet.getY());

                    // Check minimum distance
                    if (newPlanet.dst(startPoint) < 500) {
                        freeSpace = false;
                        break;
                    }

                    // Check minimum distance
                    if (newPlanet.dst(existingPlanet) < 500) {
                        freeSpace = false;
                        break;
                    }
                }

                if (freeSpace) {
                    controlledShips.add(new ControlledShip(x, y, ShipType.PLAYER));
                }

            } while (freeSpace == false);

        }

        return controlledShips;
    }

    ControlledShipMode controlledShipMode;

    Planet lastVisitedPlanet;

    /**
     * Set position
     *
     * @param x
     * @param y
     * @param type
     */
    public ControlledShip(final int x, final int y, final ShipType type) {
        super(x, y, type);

        controlledShipMode = ControlledShipMode.TradeBetweenPlanets;
        lastVisitedPlanet = null;
    }

    public void underAttack() {
        controlledShipMode = ControlledShipMode.AttackOtherShips;
    }

    private Planet getNearestPlanet(List<Planet> planets, final float x, final float y, Planet skipPlanet) {

        Vector2 shipPosition = new Vector2(x, y);

        Planet currentNearestPlanet = null;

        for (Planet planet : planets) {

            if (currentNearestPlanet == null) {
                currentNearestPlanet = planet;
            }

            Vector2 planetPosition = new Vector2(planet.getX(), planet.getY());

            float distancePlanetToShip = shipPosition.dst(planetPosition);
            float distanceCurrentNearestPlanetToShip = shipPosition.dst(new Vector2(currentNearestPlanet.getX(), currentNearestPlanet.getY()));

            if (distancePlanetToShip < distanceCurrentNearestPlanetToShip) {

                if (skipPlanet != planet) {
                    currentNearestPlanet = planet;
                }
            }
        }

        return currentNearestPlanet;
    }

    private Ship getNearestShip(List<Ship> ships, final float x, final float y) {

        Vector2 shipPosition = new Vector2(x, y);

        Ship currentNearestShip = null;

        for (Ship ship : ships) {

            if (currentNearestShip == null) {
                currentNearestShip = ship;
            }

            Vector2 planetPosition = new Vector2(ship.getX(), ship.getY());

            float distancePlanetToShip = shipPosition.dst(planetPosition);
            float distanceCurrentNearestPlanetToShip = shipPosition.dst(new Vector2(currentNearestShip.getX(), currentNearestShip.getY()));

            if (distancePlanetToShip < distanceCurrentNearestPlanetToShip) {

                if (ship != this) { // ignore the own ship

                    currentNearestShip = ship;
                }
            }
        }

        return currentNearestShip;
    }

    public void computeActions(final List<Planet> planets, final List<Ship> ships, List<Bullet> bullets, float delta) {

        switch (controlledShipMode) {
            case TradeBetweenPlanets: {

                Planet nearestPlanet = getNearestPlanet(planets, getX(), getY(), lastVisitedPlanet);

                Vector2 shipPosition = new Vector2(getX(), getY());
                float distancePlanetToShip = shipPosition.dst(nearestPlanet.getX(), nearestPlanet.getY());

                if (distancePlanetToShip < 100) {
                    lastVisitedPlanet = nearestPlanet;
                    nearestPlanet = getNearestPlanet(planets, getX(), getY(), lastVisitedPlanet);
                }

                Vector2 target = new Vector2(nearestPlanet.getX(), nearestPlanet.getY());

                turnToTarget(target, delta * 100);
                addDirectionThrust(delta / 10);

                break;
            }
            case AttackOtherShips: {

                Ship nearestPlanet = getNearestShip(ships, getX(), getY());

                Vector2 target = new Vector2(nearestPlanet.getX(), nearestPlanet.getY());

                turnToTarget(target, delta * 100);
                addDirectionThrust(delta / 10);

                Bullet bullet = getBullet();

                if (bullet != null) {
                    bullets.add(bullet);
                }

                break;
            }
        }
    }

    // Enemy Ship AI
    private void lookAt(Vector2 target) {
        getPolygon().setRotation(getLookAtAngle(target));
    }

    private float getLookAtAngle(Vector2 target) {

        float angle = target.sub(getX(), getY()).angle();

        angle = angle - 90;

        if (angle < 0) {
            angle = 360 - Math.abs(angle);
        }

        return 360 - angle;
    }

    private void turnToTarget(Vector2 target, float deltaTime) {

        float angle = getLookAtAngle(target);
        float touchAngle = Math.abs(getRotation());

        if (angle < touchAngle) {
            if (Math.abs(angle - touchAngle) < 180) {
                turnLeft(deltaTime);
            } else {
                turnRight(deltaTime);
            }
        } else {
            if (Math.abs(angle - touchAngle) < 180) {
                turnRight(deltaTime);
            } else {
                turnLeft(deltaTime);
            }
        }
    }
}
