package code123.games.crystal;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class EventManager {
    private static EventManager instance;
    private ObjectMap<String, Array<Runnable>> listeners;
    
    private EventManager() {
        listeners = new ObjectMap<>();
    }
    
    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }
    
    public void on(String event, Runnable callback) {
        Gdx.app.log("EventManager", "Subscribing to event: " + event);
        if (!listeners.containsKey(event)) {
            listeners.put(event, new Array<>());
        }
        listeners.get(event).add(callback);
    }
    
    public void emit(String event) {
        Gdx.app.log("EventManager", "Emitting event: " + event);
        if (listeners.containsKey(event)) {
            for (Runnable callback : listeners.get(event)) {
                callback.run();
            }
        }
    }
} 