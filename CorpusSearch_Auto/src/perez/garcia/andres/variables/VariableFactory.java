package perez.garcia.andres.variables;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VariableFactory {
	
	private static final Map<String, Class<? extends BuiltInVariable>> BUILT_IN_VARIABLES = new HashMap<>();
	
	static {
		BUILT_IN_VARIABLES.put(AverageWordlengthVariable.ID, AverageWordlengthVariable.class);
	}
	
	public static QVariable getQVariable(String name, File qFile) {
		return new QVariable(name, qFile);
	}
	
	public static BuiltInVariable getBuiltInVariable(String id, String name) {
		try {
			return BUILT_IN_VARIABLES.get(id.toLowerCase().replaceAll(" ", "_")).getDeclaredConstructor(String.class).newInstance(name);
		} catch (Exception e) {
			return null;
		}
	}
}
