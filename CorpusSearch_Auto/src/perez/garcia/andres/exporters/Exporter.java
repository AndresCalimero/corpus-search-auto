package perez.garcia.andres.exporters;

import java.io.File;
import java.util.Collection;
import java.util.Date;

import perez.garcia.andres.models.ShowOnly;
import perez.garcia.andres.variables.Variable;

public interface Exporter {
	void export(Collection<String> genres, Collection<Variable> variables, String[][] outputTable, ShowOnly showOnly, Date dateOfSearch, File outFile) throws Exception;
}
