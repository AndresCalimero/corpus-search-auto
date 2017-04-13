package perez.garcia.andres.models;

import java.nio.file.Path;

import perez.garcia.andres.Main;
import perez.garcia.andres.exceptions.CorpusSearchAutoException;
import perez.garcia.andres.variables.QVariable;

public class CSCommand {

	private static final Path CS_PATH = Main.ACTUAL_DIR.resolve("CS.jar");
	private static final String CS_COMMAND = "java -classpath \"$1\" csearch/CorpusSearch \"$2\" \"$3\" -out \"$4\"";

	private Path outFilePath;
	private String corpusToSearch;
	private QVariable variable;
	private String genre;

	public CSCommand(QVariable variable, String corpusToSearch, Path outFilePath) {
		this.outFilePath = outFilePath;
		this.corpusToSearch = corpusToSearch;
		this.variable = variable;
	}
	
	public Path getOutFilePath() {
		return outFilePath;
	}

	public void setOutFilePath(Path outFilePath) {
		this.outFilePath = outFilePath;
	}

	public String getCorpusToSearch() {
		return corpusToSearch;
	}

	public void setCorpusToSearch(String corpusToSearch) {
		this.corpusToSearch = corpusToSearch;
	}

	public QVariable getVariable() {
		return variable;
	}

	public void setVariable(QVariable variable) {
		this.variable = variable;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getCommand() throws CorpusSearchAutoException {
		if (!CS_PATH.toFile().isFile()) throw new CorpusSearchAutoException("CS.jar file not found in " + CS_PATH.toString());
		return CS_COMMAND.replace("$1", CS_PATH.toString()).replace("$2", variable.getQfile().toString())
				.replace("$3", corpusToSearch).replace("$4", outFilePath.toString());
	}
}
