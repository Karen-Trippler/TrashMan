package com.trashman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapController extends TiledMap implements InputProcessor {
    private final int xSize;
    private final int ySize;
    private final int xGrid;
    private final int yGrid;
    // add the player
    private Player player;
    private Position playerposition;
    private Bin bin;
    private Trash banana;
    private Trash paper;
    private Trash bottle;
    private Trash chemical;
    HashMap<Trash, TiledMapTileLayer.Cell> trash_map = new HashMap<>();


    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager manager = Assets.manager;

    private TiledMapTileLayer.Cell grass = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bush = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell players = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bananas = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell papers = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bottles = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell chemicals = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bins = new TiledMapTileLayer.Cell();

    public MapController(int xGrid, int yGrid) {
        super();
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.xSize = 32*xGrid;
        this.ySize = 32*yGrid;

        //initialize player
        this.playerposition = new Position(0,4);
        this.player = new Player(playerposition);
        //initialize trash
        this.paper = new Trash(new Position(8,7));
        this.banana = new Trash(new Position(10,6));
        this.bottle = new Trash(new Position(55,16));
        this.chemical = new Trash(new Position(12,26));
        trash_map.put(banana,bananas);
        trash_map.put(paper,papers);
        trash_map.put(bottle,bottles);
        trash_map.put(chemical,chemicals);

        //initialize bin
        this.bin = new Bin(new Position(20,22));


        grass.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/grass.png", Texture.class))));
        bush.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bush.png", Texture.class))));
        players.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/goodman_L.png", Texture.class))));

        //initialize all the trashes
        papers.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/paper.png", Texture.class))));
        bananas.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/banana.png", Texture.class))));
        bottles.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bottle.png", Texture.class))));
        chemicals.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/chemical_waste.png", Texture.class))));

        createMap();
    }
    private void createMap() {
        MapLayers layers = getLayers();

        baseLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);
        objectLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);

        Set<Position> entrances = new HashSet<>();
        entrances.add(new Position(0, 2));
        entrances.add(new Position(17, 0));
        entrances.add(new Position(xGrid - 1, 11));
        entrances.add(new Position(5, yGrid - 1));

        Map<Position, Boolean> walls = MapGenerator.generate(xGrid, yGrid, entrances);

        for (int row = 0; row < xGrid; row++) {
            for (int col = 0; col < yGrid; col++) {
                baseLayer.setCell(col, row, grass);
                if (walls.getOrDefault(new Position(col, row), false)) {
                    objectLayer.setCell(col, row, bush);
                }
            }
        }

        // add the player
        objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), players);

        layers.add(baseLayer);
        layers.add(objectLayer);
        Gdx.input.setInputProcessor(this);

        // add the trash
        for(Map.Entry<Trash, TiledMapTileLayer.Cell> entry : trash_map.entrySet()){
            objectLayer.setCell(entry.getKey().getPosition().getX(),entry.getKey().getPosition().getY(),entry.getValue());
        }

        // add the bin
        objectLayer.setCell(10,11,bins);

    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DPAD_RIGHT) {
            if (objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.moveright();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_LEFT) {
            if (objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.moveleft();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_UP) {
            if (objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.moveup();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_DOWN) {
            if (objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.movedown();
            } else {
                return false;
            }
        }
        objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), players);

        if (keycode == Input.Keys.SPACE) {
            for(Map.Entry<Trash, TiledMapTileLayer.Cell> entry : trash_map.entrySet()){
                if ((objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == entry.getValue() ||
                        objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == entry.getValue() ||
                        objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == entry.getValue() ||
                        objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == entry.getValue()) &&
                        player.checkbag()) {
                    objectLayer.setCell(entry.getKey().getPosition().getX(), entry.getKey().getPosition().getY(), null);
                    player.pickup(entry.getKey());
                } else {
                    return false;
                }
            }

        }
        if (keycode == Input.Keys.ENTER) {
            if ((objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == bins ||
                    objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == bins ||
                    objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == bins ||
                    objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == bins) &&
                    !player.checkbag()) {
                objectLayer.setCell(bin.getPosition().getX(), bin.getPosition().getY(), null);
                player.putdown();
            } else {
                return false;
            }
        }
            return false;
        }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
