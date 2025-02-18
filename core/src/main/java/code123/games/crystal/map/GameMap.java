package code123.games.crystal.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.utils.Array;

public class GameMap {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private List<Vector2> pathPoints;
    private float tileSize = 32f;
    
    public GameMap(String mapPath) {
        try {
            // 加载TMX地图
            map = new TmxMapLoader().load(mapPath);
            
            // 初始化渲染器
            renderer = new OrthogonalTiledMapRenderer(map);
            pathPoints = new ArrayList<>();
            
            // 从对象层加载路径点
            loadPathPoints();
            
            // 打印调试信息
            TiledMapTileLayer backgroundLayer = (TiledMapTileLayer) map.getLayers().get("background");
            if (backgroundLayer != null) {
                System.out.println("Background layer loaded: " + backgroundLayer.getWidth() + "x" + backgroundLayer.getHeight());
            } else {
                System.out.println("Background layer not found!");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadPathPoints() {
        MapLayer objectLayer = map.getLayers().get("objects");
        if (objectLayer != null) {
            MapObjects objects = objectLayer.getObjects();
            Array<MapObject> sortedObjects = new Array<>();
            objects.forEach(sortedObjects::add);
            sortedObjects.sort((o1, o2) -> 
                Integer.compare(o1.getProperties().get("id", Integer.class),
                              o2.getProperties().get("id", Integer.class)));
            
            // 移除对路径点数量的限制，加载所有路径点
            for (MapObject obj : sortedObjects) {
                float x = obj.getProperties().get("x", Float.class) + tileSize/2;
                float y = obj.getProperties().get("y", Float.class) - tileSize/2;
                pathPoints.add(new Vector2(x, y));
            }
        }
    }
    
    public void render(OrthographicCamera camera) {
        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }
    }
    
    public boolean canBuildTowerAt(float x, float y) {
        // 1. 首先检查是否在路径层上
        TiledMapTileLayer pathLayer = (TiledMapTileLayer) map.getLayers().get("path");
        if (pathLayer != null) {
            int tileX = (int) (x / tileSize);
            int tileY = (int) (y / tileSize);
            TiledMapTileLayer.Cell pathCell = pathLayer.getCell(tileX, tileY);
            if (pathCell != null) {
                return false;  // 如果是路径，直接返回false
            }
        }
        
        // 2. 然后检查背景层是否可建造
        TiledMapTileLayer buildLayer = (TiledMapTileLayer) map.getLayers().get("background");
        if (buildLayer != null) {
            int tileX = (int) (x / tileSize);
            int tileY = (int) (y / tileSize);
            
            TiledMapTileLayer.Cell cell = buildLayer.getCell(tileX, tileY);
            if (cell != null) {
                return cell.getTile().getProperties().containsKey("buildable");
            }
        }
        return false;
    }
    
    public List<Vector2> getPathPoints() {
        return pathPoints;
    }
    
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
    }
} 