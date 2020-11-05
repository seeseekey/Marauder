package net.seeseekey.marauder.model;

public enum ShipType {

    // Player ship
    PLAYER("player-ship.png",
            6, // Speed
            16, // Top
            0, 8,
            27, 9,
            36, 0,
            62, 0,
            70, 8,
            97, 8,
            97, 15,
            53, 74,
            48, 74,
            44, 74,
            0, 15);

    // Private variables
    private String filename;

    /**
     * Index on bulletOpening
     */
    private int bulletOpening;

    /**
     * Speed of ship
     */
    private float speed;

    private float[] vertices;

    // Constructor
    ShipType(String filename, float speed, int bulletOpening, float... vertices) {

        this.filename = filename;
        this.bulletOpening = bulletOpening;
        this.speed = speed;
        this.vertices = vertices;
    }

    // Getter
    public String getFilename() {
        return filename;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float getSpeed() {
        return speed;
    }

    public int getBulletOpening() {
        return bulletOpening;
    }
}