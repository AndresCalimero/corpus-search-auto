package perez.garcia.andres.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import perez.garcia.andres.exceptions.CorpusSearchAutoException;
import perez.garcia.andres.models.Corpora;

public class GenreFinder implements Tool {

	private static final Namespace NAMESPACE = Namespace.getNamespace("corpus.search.auto");

	@Override
	public void execute(List<String> params, boolean interactive) throws Exception {
		Map<String, String> paramsMap = new HashMap<>();
		for (int i = 0; i + 1 < params.size(); i++) {
			if (params.get(i).matches("-(corpora|in|out)") && !params.get(i + 1).startsWith("-")) {
				paramsMap.put(params.get(i).replace("-", ""), params.get(++i));
			}
		}

		if (!paramsMap.containsKey("corpora")) {
			throw new IllegalArgumentException("The corpora is required.");
		}

		if (!paramsMap.containsKey("in")) {
			throw new IllegalArgumentException("The in file is required.");
		}

		File inFile = new File(paramsMap.get("in"));
		if (!inFile.isFile())
			throw new IllegalArgumentException("The input file does not exist.");
		File outFile;
		if (paramsMap.containsKey("out")) {
			outFile = new File(paramsMap.get("out"));
		} else {
			outFile = inFile.toPath().getParent().resolve("genres.xml").toFile();
		}
		if (outFile.isDirectory())
			throw new IllegalArgumentException("The output file is not valid.");

		System.out.println("Searching for genres...");
		String corporaName = paramsMap.get("corpora");
		switch (corporaName) {
		case "YCOE":
			YCOEGenreFinder(inFile, outFile);
			break;

		case "PPCEME":
		case "PPCMBE":
		case "PPCME2":
			PENNGenreFinder(Corpora.CORPORAS.get(corporaName), inFile, outFile);
			break;

		default:
			throw new CorpusSearchAutoException(paramsMap.get("corpora") + " not supported.");
		}

		System.out.println("Done.");
		System.out.println();
		System.out.println("List of genres saved in " + outFile.getAbsolutePath());
	}

	private void YCOEGenreFinder(File inFile, File outFile) throws IOException {
		Corpora corpora = Corpora.CORPORAS.get("YCOE");

		Element genresElement = generateRootElement(corpora);
		Document xmlDocument = new Document(genresElement);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), Charset.forName("UTF-8")))) {
			Map<String, Element> genresMap = new HashMap<>();
			StringBuilder genreSb = new StringBuilder();
			String filename = null;
			long wordCount = 0;
			boolean isFilename = false, isGenre = false, isWordCount = false;
			for (String line; (line = br.readLine()) != null;) {
				if (isFilename) {
					filename = line.substring(20, line.indexOf("</TD>")).trim();
					isFilename = false;
				} else if (isGenre) {
					if (!line.contains("</TD>")) {
						genreSb.append(line.substring((genreSb.length() == 0 ? 20 : 0), line.indexOf("<br>")));
						genreSb.append(" ");
					} else {
						genreSb.append(line.substring((genreSb.length() == 0 ? 20 : 0), line.indexOf("</TD>")));

						String genre = genreSb.toString().trim().toLowerCase();
						Element corpusElement = new Element("corpus", NAMESPACE).setText(filename);
						if (genresMap.containsKey(genre)) {
							genresMap.get(genre).addContent(corpusElement);
						} else {
							Element genreElement = new Element("genre", NAMESPACE).setAttribute("name", genre);
							genreElement.addContent(corpusElement);
							genresElement.addContent(genreElement);
							genresMap.put(genre, genreElement);
						}

						genreSb.setLength(0);
						isGenre = false;
					}
				} else if (isWordCount) {
					wordCount += Long.valueOf(line.substring(20, line.indexOf("</TD>")).trim().replace(",", ""));
					isWordCount = false;
				} else if (line.replace(" ", "").equalsIgnoreCase("<TRVALIGN=\"TOP\"><TD><STRONG>Filename</STRONG></TD>")) {
					isFilename = true;
				} else if (line.replace(" ", "").equalsIgnoreCase("<TRVALIGN=\"TOP\"><TD><STRONG>Genre</STRONG></TD>")) {
					isGenre = true;
				} else if (line.replace(" ", "").equalsIgnoreCase("<TRVALIGN=\"TOP\"><TD><STRONG>Wordcount</STRONG></TD>")) {
					isWordCount = true;
				}
			}
			System.out.println("Word count: " + wordCount);
		}

		saveXMLFile(outFile, xmlDocument);
	}

	private void PENNGenreFinder(Corpora corpora, File inFile, File outFile) throws IOException {
		Element genresElement = generateRootElement(corpora);
		Document xmlDocument = new Document(genresElement);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), Charset.forName("UTF-8")))) {
			Map<String, Element> genresMap = new HashMap<>();
			int linesToName = 0, linesToGenre = 0;
			String filename = null;
			boolean searching = false;
			for (String line; (line = br.readLine()) != null;) {
				if (searching) {
					if (line.equalsIgnoreCase("</table>")) {
						break;
					} else {
						linesToName--;
						linesToGenre--;
						if (linesToName == 0) {
							filename = line.substring(4).trim();
							linesToName = 5;
						} else if (linesToGenre == 0) {
							String genre = line.substring(4).trim().toLowerCase();
							Element corpusElement = new Element("corpus", NAMESPACE).setText(filename);
							if (genresMap.containsKey(genre)) {
								genresMap.get(genre).addContent(corpusElement);
							} else {
								Element genreElement = new Element("genre", NAMESPACE).setAttribute("name", genre);
								genreElement.addContent(corpusElement);
								genresElement.addContent(genreElement);
								genresMap.put(genre, genreElement);
							}
							linesToGenre = 5;
						}
					}
				} else if (line.equalsIgnoreCase("<th colspan=4>Table 3: Wordcount summary by individual text")) {
					linesToName = 7;
					linesToGenre = 9;
					searching = true;
				}
			}
		}

		saveXMLFile(outFile, xmlDocument);
	}

	private void saveXMLFile(File outFile, Document xmlDocument) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(xmlDocument, new FileWriter(outFile));
	}

	private Element generateRootElement(Corpora corpora) {
		Element genresElement = new Element("genres", NAMESPACE);
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		genresElement.addNamespaceDeclaration(xsi);
		//genresElement.setAttribute("schemaLocation", "corpus.search.auto genres-schema.xsd", xsi);
		genresElement.setAttribute("corpora", corpora.getName());
		return genresElement;
	}

	@Override
	public String usage() {
		return "CospusSearchAuto -tool genre-finder -corpora [CORPORA] -in [PATH_OF_INFO_FILE] (-out [PATH_OF_OUT_FILE])";
	}
}
