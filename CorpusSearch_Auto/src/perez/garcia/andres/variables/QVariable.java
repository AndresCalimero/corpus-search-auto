package perez.garcia.andres.variables;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class QVariable extends Variable {

	private final ObjectProperty<File> qfile = new SimpleObjectProperty<>();
	
	QVariable(String name, File qFile) {
		super(name);
		this.qfile.set(qFile);
	}
	
	QVariable(File qFile) {
		this("", qFile);
	}

	public ObjectProperty<File> qfileProperty() {
		return this.qfile;
	}

	public File getQfile() {
		return this.qfileProperty().get();
	}

	public void setQfile(final File qfile) {
		this.qfileProperty().set(qfile);
	}
}
