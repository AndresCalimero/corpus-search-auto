package perez.garcia.andres.tools;

import java.util.HashMap;
import java.util.Map;

public class Tools {
	
	public final static Map<String, Tool> TOOLS = new HashMap<String, Tool>();
	
	static {
		TOOLS.put("genre-finder", new GenreFinder());
		TOOLS.put("statistics-by-genres", new StatisticsByGenres());
	}
	
	public static Tool getToolByName(String name) {
		return TOOLS.get(name);
	}
}
