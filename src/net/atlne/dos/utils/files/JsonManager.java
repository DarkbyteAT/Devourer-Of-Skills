package net.atlne.dos.utils.files;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public class JsonManager {
	
	/**Stores all loaded JSON files, mapped to their file name.*/
	private ConcurrentHashMap<String, Object> jsonObjects = new ConcurrentHashMap<>();
	/**Stores the JSON handler for loading and saving JSON objects.*/
	private JsonHandler handler = new JsonHandler();

	/**Empties the HashMap.*/
	public void dispose() {
		jsonObjects.clear();
	}
	
	/**Loads a JSON object by file name.*/
	@SuppressWarnings("unchecked")
	public <T> T get(String fileName, Type type) {
		if(!jsonObjects.containsKey(fileName))
			jsonObjects.put(fileName, handler.load(fileName, type));
		return (T) jsonObjects.get(fileName);
	}
	
	/**Sets a JSON object by file name.*/
	public void set(String fileName, Object obj) {
		jsonObjects.put(fileName, obj);
		handler.save(fileName, obj);
	}
}