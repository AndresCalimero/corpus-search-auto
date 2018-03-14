package perez.garcia.andres.models;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import perez.garcia.andres.Main;
import perez.garcia.andres.utils.FileSystemUtils;

public class Corpora {

	public static final Map<String, Corpora> CORPORAS = new HashMap<String, Corpora>();
	private static final Path CORPORAS_PATH = Main.ACTUAL_DIR.resolve("corporas");

	private final ListProperty<Corpus> corpusList = new SimpleListProperty<Corpus>(FXCollections.observableArrayList());
	private final StringProperty name = new SimpleStringProperty();

	static {
		File corporasFolder = CORPORAS_PATH.toFile();
		if (corporasFolder.isDirectory()) {
			for (File corporaFolder : corporasFolder.listFiles()) {
				if (corporaFolder.isDirectory()) {
					File psdFolder = corporaFolder.toPath().resolve("psd").toFile();
					File posFolder = corporaFolder.toPath().resolve("pos").toFile();
					if (psdFolder.isDirectory() && posFolder.isDirectory()) {
						Corpora corpora = new Corpora(corporaFolder.getName());
						for (File corpusFile : psdFolder.listFiles(new FileSystemUtils.ExtensionFilter("psd"))) {
							Corpus corpus = new Corpus(corpora, CorpusType.PSD, corpusFile);
							corpora.getCorpusList().add(corpus);
						}
						CORPORAS.put(corpora.getName(), corpora);

						for (File corpusFile : posFolder.listFiles(new FileSystemUtils.ExtensionFilter("pos"))) {
							Corpus corpus = new Corpus(corpora, CorpusType.POS, corpusFile);
							corpora.getCorpusList().add(corpus);
						}
						CORPORAS.put(corpora.getName(), corpora);
					}
				}
			}
		}
	}

	private Corpora(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return name.getValue();
	}

	public Path getPath() {
		return CORPORAS_PATH.resolve(getName());
	}

	public ListProperty<Corpus> corpusListProperty() {
		return this.corpusList;
	}

	public List<Corpus> getCorpusList() {
		return this.corpusListProperty().get();
	}
}
