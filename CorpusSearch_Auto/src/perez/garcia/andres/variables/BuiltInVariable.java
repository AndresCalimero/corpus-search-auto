package perez.garcia.andres.variables;

import java.util.List;
import java.util.Map;

import perez.garcia.andres.models.Corpus;

public abstract class BuiltInVariable extends Variable {
	
	private String ID;

	protected BuiltInVariable(String id) {
		this(id, "");
	}
	
	protected BuiltInVariable(String id, String name) {
		super(name);
		ID = id;
	}
	
	public String getID() {
		return ID;
	}
	
	public abstract String[] process(Map<String, List<Corpus>> corpusByGenre) throws Exception;
}
