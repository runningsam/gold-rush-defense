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
            
            for (MapObject obj : sortedObjects) {
                if (obj.getProperties().get("id", Integer.class) <= 8) {
                    float x = obj.getProperties().get("x", Float.class) + tileSize/2;
                    float y = obj.getProperties().get("y", Float.class) - tileSize/2;  // 只需要减去半个瓦片大小
                    pathPoints.add(new Vector2(x, y));
                }
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
        TiledMapTileLayer buildLayer = (TiledMapTileLayer) map.getLayers().get("background");
        if (buildLayer != null) {
            int tileX = (int) (x / tileSize);
            int tileY = (int) (y / tileSize);
            
            TiledMapTileLayer.Cell cell = buildLayer.getCell(tileX, tileY);
            return cell != null && cell.getTile().getProperties().containsKey("buildable");
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