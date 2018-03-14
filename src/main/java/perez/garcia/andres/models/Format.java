package perez.garcia.andres.models;

public enum Format {
	HTML, EXCEL, ALL;

	public static Format getFromName(String name) {
		return Format.valueOf(name.toUpperCase());
	}

	public String getExtension() {
		switch (this) {
		case EXCEL:
			return "xlsx";
		default:
			return this.name().toLowerCase();
		}
	}
}
