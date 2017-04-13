package perez.garcia.andres.exporters;

import java.util.HashMap;
import java.util.Map;

import perez.garcia.andres.models.Format;

public class ExporterFactory {
	
	private static Map<Format, Exporter> EXPORTERS = new HashMap<>();
	
	static {
		EXPORTERS.put(Format.EXCEL, new ExcelExporter());
		EXPORTERS.put(Format.HTML, new HTMLExporter());
	}
	
	public static Exporter getExporter(Format format) {
		return EXPORTERS.get(format);
	}
}
