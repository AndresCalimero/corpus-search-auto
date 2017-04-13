package perez.garcia.andres.utils;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom2.Element;
import org.xml.sax.SAXException;

import perez.garcia.andres.exceptions.CorpusSearchAutoException;

public class XMLUtils {

	public static boolean validateXMLWithSchema(File xmlFile, URL schemaURL) throws Exception {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(schemaURL);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlFile));
		} catch (SAXException e) {
			throw new CorpusSearchAutoException("The input file is not valid (" + e.getMessage() + ")");
		}
		return true;
	}

	public static int indexOfElement(Element element) {
		if (!element.isRootElement()) {
			Element parent = element.getParentElement();
			List<Element> children = parent.getChildren();
			int i;
			for (i = 0; i < children.size() && !children.get(i).equals(element); i++);
			if (i != children.size()) {
				return i;
			}
		}
		return -1;
	}
}
