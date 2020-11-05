package net.seeseekey.marauder.model;

import com.badlogic.gdx.math.Polygon;
import net.seeseekey.marauder.utils.Tuple2;

public class Entity {

    /**
     * Method to get the width and height of a polygon based on the vertices.
     */
    public static Tuple2<Float, Float> getWidthAndHeightFromPolygonVertices(float[] transformedVertices) {

        // Get width and height before rotation
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;

        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (int i = 0; i < transformedVertices.length; i += 2) {

            float xVertice = transformedVertices[i];
            float yVertice = transformedVertices[i + 1];

            minX = Math.min(xVertice, minX);
            maxX = Math.max(xVertice, maxX);

            minY = Math.min(yVertice, minY);
            maxY = Math.max(yVertice, maxY);
        }

        float width = maxX - minX;
        float height = maxY - minY;

        return new Tuple2<>(width, height);
    }

    public final float width;
    public final float height;
    protected Polygon polygon;

    private boolean destroyed;

    public Entity(int x, int y, float... vertices) {

        polygon = new Polygon();
        polygon.setVertices(vertices);

        // Get width and height before rotation
        Tuple2<Float, Float> widthAndHeightFromPolygonVertices = Entity.getWidthAndHeightFromPolygonVertices(polygon.getTransformedVertices());

        width = widthAndHeightFromPolygonVertices.x;
        height = widthAndHeightFromPolygonVertices.y;

        polygon.setOrigin(width / 2, height / 2);
        polygon.setPosition(x, y);

        polygon.setScale(0.5f, 0.5f);

        destroyed = false;
    }

    public float getX() {
        return polygon.getX();
    }

    public float getY() {
        return polygon.getY();
    }

    public float getOriginX() {
        return polygon.getOriginX();
    }

    public float getOriginY() {
        return polygon.getOriginY();
    }

    public float getScaleX() {
        return polygon.getScaleX();
    }

    public float getScaleY() {
        return polygon.getScaleY();
    }

    public float getRotation() {
        return 360 - polygon.getRotation(); // correct counter clock wise rotation
    }

    public void setRotation(float rotation) {
        polygon.setRotation(360 - rotation); // correct clock wise rotation to counter clock wise
    }

    public void destroy() {
        destroyed = true;
    }

    /**
     * Only for debugging
     */
    public Polygon getPolygon() {

        return polygon;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
