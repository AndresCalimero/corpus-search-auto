package perez.garcia.andres.models;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import perez.garcia.andres.utils.Utils;

public class Corpus {

	private final ObjectProperty<Corpora> corpora = new SimpleObjectProperty<>();
	private final ObjectProperty<CorpusType> type = new SimpleObjectProperty<>();
	private final ObjectProperty<File> file = new SimpleObjectProperty<>();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty genre = new SimpleStringProperty();

	public Corpus(Corpora corpora, CorpusType type, File file) {
		this.corpora.set(corpora);
		this.type.set(type);
		this.file.set(file);
		this.name.set(Utils.removeExtension(file.getName()));
	}

	public ObjectProperty<Corpora> corporaProperty() {
		return this.corpora;
	}

	public Corpora getCorpora() {
		return this.corporaProperty().get();
	}
	
	public ObjectProperty<CorpusType> typeProperty() {
		return this.type;
	}
	
	public CorpusType getType() {
		return this.typeProperty().get();
	}

	public ObjectProperty<File> fileProperty() {
		return this.file;
	}

	public File getFile() {
		return this.fileProperty().get();
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().get();
	}

	public StringProperty genreProperty() {
		return this.genre;
	}

	public String getGenre() {
		return this.genreProperty().get();
	}

	public void setGenre(final String genre) {
		this.genreProperty().set(genre);
	}
}
