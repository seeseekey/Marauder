package net.seeseekey.marauder.model;

public enum PlanetType {

    // Player ship
    PLANET_1("planet-1.png",
            34, 9,
            104, 0,
            173, 20,
            209, 65,
            213, 134,
            171, 190,
            111, 209,
            37, 185,
            1, 138,
            4, 77,
            34, 7);

    // Private variables
    private String filename;

    private float[] vertices;

    // Constructor
    PlanetType(String filename, float... vertices) {

        this.filename = filename;
        this.vertices = vertices;
    }

    // Getter
    public String getFilename() {
        return filename;
    }

    public float[] getVertices() {
        return vertices;
    }
}
