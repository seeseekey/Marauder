package net.seeseekey.marauder.model;

public enum BulletType {

    // Player ship
    LASER_1("laser-1.png",
            4, // Speed
            50, // Energy
            0, 0,
            0, 8,
            8, 8,
            8, 0);

    // Private variables
    private String filename;

    /**
     * Speed of ship
     */
    private float speed;

    private int energy;

    private float[] vertices;

    // Constructor
    BulletType(String filename, float speed, int energy, float... vertices) {

        this.filename = filename;
        this.speed = speed;
        this.energy = energy;
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

    public int getEnergy() {
        return energy;
    }
}
