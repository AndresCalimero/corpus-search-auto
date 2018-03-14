package perez.garcia.andres.variables;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Variable {
	
	private final StringProperty name = new SimpleStringProperty();
	
	protected Variable() {
		this("");
	}
	
	protected Variable(String name) {
		setName(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(final String name) {
		this.nameProperty().set(name);
	}
}
