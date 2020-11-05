package net.seeseekey.marauder.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.seeseekey.marauder.model.Planet;
import net.seeseekey.marauder.model.Ship;

import java.util.List;

public class DebugRenderer {

    private boolean playerShipBoundingBox;
    private boolean playerShipDirectionVector;
    private boolean playerShipPolygon;
    private boolean planetPolygons;
    private boolean planetBoundingBoxes;

    public DebugRenderer(final boolean playerShipBoundingBox, final boolean playerShipDirectionVector, final boolean playerShipPolygon, final boolean planetPolygons, final boolean planetBoundingBoxes) {
        this.playerShipBoundingBox = playerShipBoundingBox;
        this.playerShipDirectionVector = playerShipDirectionVector;
        this.playerShipPolygon = playerShipPolygon;
        this.planetPolygons = planetPolygons;
        this.planetBoundingBoxes = planetBoundingBoxes;
    }

    public void renderDebugInformation(SpriteBatch spriteBatch, BitmapFont font, Ship playerShip) {
        font.draw(spriteBatch, "Playership Position (" + playerShip.getX() + " / " + playerShip.getY() + ")", playerShip.getX() - 380, playerShip.getY());
        font.draw(spriteBatch, "Playership Origin (" + playerShip.getOriginX() + " / " + playerShip.getOriginY() + ")", playerShip.getX() - 380, playerShip.getY() + 20);
        font.draw(spriteBatch, "Playership Rotation (" + playerShip.getRotation() + ")", playerShip.getX() - 380, playerShip.getY() + 40);
        font.draw(spriteBatch, "Playership direction vector (x:" + playerShip.direction.x + " / y:" + playerShip.direction.y + ")", playerShip.getX() - 380, playerShip.getY() + 60);

        font.draw(spriteBatch, "Playership Scale x: " + playerShip.getPolygon().getScaleX() + " y: " + playerShip.getPolygon().getScaleY(), playerShip.getX() - 380, playerShip.getY() + 120);
        font.draw(spriteBatch, "Playership Ship health: " + playerShip.getShipHullHealth(), playerShip.getX() - 380, playerShip.getY() + 140);
    }

    public void renderDebugGeometry(ShapeRenderer shapeRenderer, List<Planet> planets, Ship playerShip) {

        // Player ship
        if (playerShipBoundingBox) {

            // Ship bounding box
            shapeRenderer.setColor(Color.FIREBRICK);
            Rectangle boundingRectangle = playerShip.getPolygon().getBoundingRectangle();
            shapeRenderer.rect(boundingRectangle.x, boundingRectangle.y, boundingRectangle.width, boundingRectangle.height);
        }

        if (playerShipDirectionVector) {

            // Direction vector for playership
            shapeRenderer.setColor(Color.VIOLET);
            Vector2 vector2 = new Vector2(playerShip.getX(), playerShip.getY());
            shapeRenderer.line(vector2, vector2.cpy().add(playerShip.direction.cpy().scl(20)));
        }

        if (playerShipPolygon) {
            // Ship bounding box
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.polygon(playerShip.getPolygon().getTransformedVertices());
        }

        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(playerShip.getX(), playerShip.getY(), playerShip.width, playerShip.height);

        // Planets
        if (planetBoundingBoxes) {

            shapeRenderer.setColor(Color.GREEN);
            for (Planet planet : planets) {
                shapeRenderer.rect(planet.getX(), planet.getY(), planet.width, planet.height);
            }
        }

        if (planetPolygons) {
            // Render planet polygons
            shapeRenderer.setColor(Color.GREEN);
            for (Planet planet : planets) {
                shapeRenderer.polygon(planet.getPolygon().getTransformedVertices());
            }
        }
    }
}
