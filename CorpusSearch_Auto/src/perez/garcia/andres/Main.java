package perez.garcia.andres;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import perez.garcia.andres.models.Corpora;
import perez.garcia.andres.tools.Tool;
import perez.garcia.andres.tools.Tools;

public class Main {
	
	/*
	java -jar CorpusSearchAuto.jar -tool genre-finder -corpora YCOE -in "data\corporas\YCOE\info\YcoeTextInfo.htm"
	java -jar CorpusSearchAuto.jar -tool genre-finder -corpora PPCEME -in "data\corporas\PPCEME\info\description.html"
	java -jar CorpusSearchAuto.jar -tool genre-finder -corpora PPCMBE -in "data\corporas\PPCMBE\info\description.html"
	java -jar CorpusSearchAuto.jar -tool genre-finder -corpora PPCME2 -in "data\corporas\PPCME2\info\description.html"

	java -jar CorpusSearchAuto.jar -tool statistics-by-genres -in "data\searches\search.xml" -out-format all
	*/
	
	public static final Path ACTUAL_DIR = Paths.get("").toAbsolutePath().resolve("data");

	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else if (args.length < 2) {
			printHelp();
		} else if (args[0].equals("-tool")) {
			Tool tool = Tools.getToolByName(args[1]);
			if (tool != null) {
				List<String> params = new ArrayList<>();
				for (int i = 2; i < args.length; i++) {
					params.add(args[i]);
				}
				try {
					tool.execute(params, true);
				} catch (Exception e) {
					//e.printStackTrace();
					System.err.println("[ERROR] " + e.toString());
					System.err.println();
					if (e instanceof IllegalArgumentException) {
						printHelp();
					}
				}
			} else {
				printHelp();
			}
		} else {
			printHelp();
		}
		System.exit(0);
	}

	private static void printHelp() {
		System.out.println("Uses:");
		System.out.println("	CospusSearchAuto");
		System.out.println("	CospusSearchAuto -tool [tool] [parameters]");
		System.out.println();
		System.out.println("Tools:");
		Tools.TOOLS.forEach((name, tool) -> {
			System.out.println("\t" + name + ":");
			System.out.println("\t\t" + tool.usage());
		});
		System.out.println();
		System.out.println("Corporas:");
		for (Corpora corpora : Corpora.CORPORAS.values()) {
			System.out.println("\t" + corpora.getName() + " (" + corpora.getPath().toString() + ")");
		}
		System.out.println();
	}
}
