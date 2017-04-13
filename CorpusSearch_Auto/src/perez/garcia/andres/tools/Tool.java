package perez.garcia.andres.tools;

import java.util.List;

public interface Tool {
	void execute(List<String> params, boolean interactive) throws Exception;
	String usage();
}
