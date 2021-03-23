package net.atlne.dos.utils.files;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler {
	
	/**Stores the JSON parser object.*/
	protected Gson json;

	/**Initialises the JSON parser object.*/
	public JsonHandler() {
		json = new GsonBuilder()
				.serializeNulls()
				.setPrettyPrinting()
				.create();
	}
	
	/**Loads an object from a file of the given class. <br>
	 * <b>DON'T USE</b>, instead use a {@link com.google.gson.reflect.TypeToken} to load. <br>
	 * Reference: {@link http://blog.xebia.com/acessing-generic-types-at-runtime-in-java/}*/
	@Deprecated
	public <T> T load(String path, Class<T> clazz) {
		T object = json.fromJson(new FileHandler(path).getContents(), clazz);
		return object;
	}
	
	/**Loads an object from a file of the given type. <br>
	 * Reference: {@link https://stackoverflow.com/a/54274159/5672547}*/
	public <T> T load(String path, Type type) {
		T object = json.fromJson(new FileHandler(path).getContents(), type);
		return object;
	}
	
	/**Saves an object to a given file.*/
	public void save(String path, Object obj) {
		new FileHandler(path).write(json.toJson(obj, obj.getClass()));
	}
}