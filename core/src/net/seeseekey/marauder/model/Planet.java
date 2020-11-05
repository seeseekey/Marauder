package net.seeseekey.marauder.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Planet extends Entity {

    public Planet(int x, int y, PlanetType type) {
        super(x, y, type.getVertices());
    }

    public static List<Planet> getPlanets(int mapWidth, int mapHeight, int borderDistance, int maxPlanets) {

        List<Planet> planets = new ArrayList<>();

        Vector2 startPoint = new Vector2(mapWidth / 2, mapHeight / 2);

        for (int i = 0; i < maxPlanets; i++) {

            boolean freeSpace;

            do {
                freeSpace = true;

                int x = ThreadLocalRandom.current().nextInt(borderDistance, mapWidth - borderDistance);
                int y = ThreadLocalRandom.current().nextInt(borderDistance, mapHeight - borderDistance);

                // TODO Never set a planet on start position

                Vector2 newPlanet = new Vector2(x, y);

                for (Planet planet : planets) {

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
                    planets.add(new Planet(x, y, PlanetType.PLANET_1));
                }

            } while (freeSpace == false);

        }

        return planets;
    }
}