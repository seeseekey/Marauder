package net.seeseekey.marauder.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import net.seeseekey.marauder.MarauderGame;
import net.seeseekey.marauder.model.Bullet;
import net.seeseekey.marauder.model.Collider;
import net.seeseekey.marauder.model.ControlledShip;
import net.seeseekey.marauder.model.Entity;
import net.seeseekey.marauder.model.Planet;
import net.seeseekey.marauder.model.Ship;
import net.seeseekey.marauder.model.ShipType;
import net.seeseekey.marauder.model.Starfield;
import net.seeseekey.marauder.renderer.BulletRenderer;
import net.seeseekey.marauder.renderer.DebugRenderer;
import net.seeseekey.marauder.renderer.PlanetRenderer;
import net.seeseekey.marauder.renderer.ShipRenderer;
import org.danilopianini.util.FlexibleQuadTree;
import org.danilopianini.util.SpatialIndex;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private final MarauderGame marauder;
    private boolean debugMode;

    // Settings
    private final int mapWidth = 25000; // Width of map
    private final int mapHeight = 25000; // Height of map
    private final int planetBorderDistance = 1000; // Distance to map border, in which no planets should be created
    private final int maxPlanets = 250; // Maximum number of planet
    private final int starsBorderDistance = 200; // Distance to map border, in which no stars should be created
    private final int maxStars = 12500; // Maximum number of stars
    private final int mapTransition = 100; // Area in which should map transition working

    // Game data
    private Starfield starfield;
    private List<Planet> planets;
    private List<ControlledShip> enemyShips;
    private Ship playerShip;
    private List<Bullet> bullets;
    private OrthographicCamera camera;

    // Game logic
    private Collider collider;

    // Renderer (high level)
    private PlanetRenderer planetRenderer;
    private ShipRenderer shipRenderer;
    private BulletRenderer bulletRenderer;
    private DebugRenderer debugRenderer;

    // Renderer (low level)
    BitmapFont bitmapFont;
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;

    public GameScreen(MarauderGame marauder) {

        // Create
        this.marauder = marauder;
        this.debugMode = false;

        // Create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, marauder.getViewportWidth(), marauder.getViewportHeight());

        // Game data
        starfield = new Starfield(mapWidth, mapHeight, starsBorderDistance, maxStars);

        enemyShips = new ArrayList<>();
        enemyShips.add(new ControlledShip(mapWidth / 2, mapHeight / 2 + 150, ShipType.PLAYER));

        //enemyShips.addAll(ControlledShip.getControlledShips(mapWidth, mapHeight, planetBorderDistance, 100));

        planets = Planet.getPlanets(mapWidth, mapHeight, planetBorderDistance, maxPlanets);
        playerShip = new Ship(mapWidth / 2, mapHeight / 2, ShipType.PLAYER);

        bullets = new ArrayList<Bullet>();

        // Game logic
        collider = new Collider();

        // Renderer (high level)
        planetRenderer = new PlanetRenderer();
        shipRenderer = new ShipRenderer();
        bulletRenderer = new BulletRenderer();
        debugRenderer = new DebugRenderer(true, true, true, true, true);

        // Renderer (low level)
        bitmapFont = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        // Debug
        List<Entity> entities = new ArrayList<>();
        entities.add(playerShip);
        entities.addAll(planets);
        entities.addAll(enemyShips);

        final SpatialIndex<Entity> qt = new FlexibleQuadTree<>();

        for (Entity entityA : entities) {
            qt.insert(entityA, entityA.getX(), entityA.getY());
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(final float delta) {

        // OpenGL setup
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // https://stackoverflow.com/questions/35969253/libgdx-antialiasing
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        // Input handling
        if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerShip.turnLeft(100 * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerShip.turnRight(100 * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerShip.direction = new Vector2(0, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {

            Bullet bullet = playerShip.getBullet();

            if (bullet != null) {
                bullets.add(bullet);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            // Add thrust vector
            playerShip.addDirectionThrust(delta); // Optimkze delta time via game screen
        } else {
            // use unchanged vector
            playerShip.addDirection(delta);
        }

        // Check Map winding ( fix 100 and 0 problem ekhard?)
        if (playerShip.getX() < 0) {
            playerShip.getPolygon().setPosition(mapWidth - mapTransition, playerShip.getY());
        }

        if (playerShip.getX() > mapWidth) {
            playerShip.getPolygon().setPosition(mapTransition, playerShip.getY());
        }

        if (playerShip.getY() < 0) {
            playerShip.getPolygon().setPosition(playerShip.getX(), mapHeight - mapTransition);
        }

        if (playerShip.getY() > mapHeight) {
            playerShip.getPolygon().setPosition(playerShip.getX(), mapTransition);
        }

        // Calc Bullets positions
        for (Bullet bullet : bullets) {
            bullet.addDirection(delta);
        }

        // Ship AI
        List<Ship> allShips = new ArrayList<>();
        allShips.add(playerShip);
        allShips.addAll(enemyShips);

        for (ControlledShip enemyShip : enemyShips) {

            enemyShip.computeActions(planets, allShips, bullets, delta);
        }

        // Check collision
        final SpatialIndex<Entity> quadTree = new FlexibleQuadTree<>();

        quadTree.insert(playerShip, playerShip.getX(), playerShip.getY());

        for (Entity entityA : planets) {
            quadTree.insert(entityA, entityA.getX(), entityA.getY());
        }

        for (Entity entityA : enemyShips) {
            quadTree.insert(entityA, entityA.getX(), entityA.getY());
        }

        for (Entity entityA : bullets) {
            quadTree.insert(entityA, entityA.getX(), entityA.getY());
        }

        // todo query with width from viewport
        List<Entity> entitiesFromQuadtree = quadTree.query(
                new double[]{playerShip.getX() - 500, playerShip.getY() - 500},
                new double[]{playerShip.getX() + 500, playerShip.getY() + 500});

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();

        collider.collide(entitiesFromQuadtree, delta);

        shapeRenderer.end();

        // Check destroyed
        if (playerShip.isDestroyed()) {
            // Game over
        }

        for (int i = 0; i < bullets.size(); i++) {

            Bullet bullet = bullets.get(i);

            if (bullet.isDestroyed()) {
                bullets.remove(i);
            }
        }

        for (int i = 0; i < enemyShips.size(); i++) {

            Ship ship = enemyShips.get(i);

            if (ship.isDestroyed()) {
                enemyShips.remove(i);
            }
        }

        // Set camera to space ship position (right place)
        camera.position.set(playerShip.getX(), playerShip.getY(), 0);
        camera.update();

        // Render data

        // renderDebugGeometry star background
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Vector2 star : starfield.getStars()) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.circle(star.x, star.y, 0.80f);
        }

        shapeRenderer.end();

        // Planates, ships and bullets
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // Planets
        for (Planet planet : planets) {
            planetRenderer.render(planet, spriteBatch);
        }

        // Player ship
        shipRenderer.render(playerShip, spriteBatch);

        // Enemy ships
        for (Ship enemyShip : enemyShips) {
            shipRenderer.render(enemyShip, spriteBatch);
        }

        // Bullets ships
        for (Bullet bullet : bullets) {
            bulletRenderer.render(bullet, spriteBatch);
        }

        if (debugMode) {
            debugRenderer.renderDebugInformation(spriteBatch, bitmapFont, playerShip);
        }

        spriteBatch.end();

        if (debugMode) {

            // Debug geometry
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin();

            // Debug rendering
            debugRenderer.renderDebugGeometry(shapeRenderer, planets, playerShip);
        }

        shapeRenderer.end();
    }

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
