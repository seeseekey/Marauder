package net.seeseekey.marauder.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Starfield {

    private List<Vector2> stars;

    public Starfield(int mapWidth, int mapHeight, int borderDistance, int maxStars) {

        stars = new ArrayList<Vector2>();

        for (int i = 0; i < maxStars; i++) {

            int x = ThreadLocalRandom.current().nextInt(borderDistance, mapWidth - borderDistance);
            int y = ThreadLocalRandom.current().nextInt(borderDistance, mapHeight - borderDistance);

            stars.add(new Vector2(x, y));
        }
    }

    public List<Vector2> getStars() {
        return stars;
    }
}
