package perez.garcia.andres.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import perez.garcia.andres.Main;
import perez.garcia.andres.exceptions.CorpusSearchAutoException;
import perez.garcia.andres.exporters.Exporter;
import perez.garcia.andres.exporters.ExporterFactory;
import perez.garcia.andres.models.CSCommand;
import perez.garcia.andres.models.Corpora;
import perez.garcia.andres.models.Corpus;
import perez.garcia.andres.models.CorpusType;
import perez.garcia.andres.models.Format;
import perez.garcia.andres.models.ShowOnly;
import perez.garcia.andres.utils.FileSystemUtils;
import perez.garcia.andres.utils.Utils;
import perez.garcia.andres.utils.XMLUtils;
import perez.garcia.andres.variables.BuiltInVariable;
import perez.garcia.andres.variables.QVariable;
import perez.garcia.andres.variables.Variable;
import perez.garcia.andres.variables.VariableFactory;

public class StatisticsByGenres implements Tool {

	private static final Namespace NAMESPACE = Namespace.getNamespace("http://corpus.search.auto");
	private static final URL SEARCH_SCHEMA_URL = Main.class.getResource("/perez/garcia/andres/schemas/search-schema.xsd");
	private static final Path OUTPUT_PATH = Main.ACTUAL_DIR.resolve("output");

	private final Date dateOfSearch = new Date();
	private final Exporter htmlExporter = ExporterFactory.getExporter(Format.HTML);
	private final Exporter excelExporter = ExporterFactory.getExporter(Format.EXCEL);

	@Override
	public void execute(List<String> params, boolean interactive) throws Exception {
		// Global vars
		File inFile;
		String inFilename;
		Format format;
		Path outPath;
		ShowOnly showOnly;
		List<Corpora> corporas = new ArrayList<>();
		Map<Integer, Map<Integer, Variable>> variables = new HashMap<>();
		Map<Integer, Map<String, List<Corpus>>> corpusByGenre = new HashMap<>();
		Map<Integer, Map<Corpus, Boolean>> includedCorpus = new HashMap<>();
		Date lastStartDate;

		Map<String, String> paramsMap = new HashMap<String, String>();
		for (int i = 0; i + 1 < params.size(); i++) {
			if (params.get(i).matches("-(in|show-only|out-format|out)") && !params.get(i + 1).startsWith("-")) {
				paramsMap.put(params.get(i).substring(1), params.get(++i));
			}
		}

		if (!paramsMap.containsKey("in")) {
			throw new IllegalArgumentException("The in file is required.");
		}
		inFile = new File(paramsMap.get("in"));

		if (!inFile.isFile())
			throw new IllegalArgumentException("The input file does not exist.");
		if (paramsMap.containsKey("show-only")) {
			showOnly = ShowOnly.getFromName(paramsMap.get("show-only"));
		} else {
			showOnly = ShowOnly.ALL;
		}
		if (paramsMap.containsKey("out-format")) {
			format = Format.getFromName(paramsMap.get("out-format"));
		} else {
			format = Format.ALL;
		}
		inFilename = inFile.getName().replace(".xml", "");
		if (paramsMap.containsKey("out")) {
			outPath = Paths.get(paramsMap.get("out"));
		} else {
			outPath = inFile.toPath().getParent().resolve(inFile.getName() + " output");
		}

		System.out.println("Validating xml search file...");

		if (XMLUtils.validateXMLWithSchema(inFile, SEARCH_SCHEMA_URL)) {
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(inFile);
			List<Element> corporasElement = document.getRootElement().getChildren("corpora", NAMESPACE);
			for (Element corporaElement : corporasElement) {
				String corporaName = corporaElement.getAttributeValue("name");
				Corpora corpora = Corpora.CORPORAS.get(corporaName);
				if (corpora == null)
					throw new CorpusSearchAutoException("The corpora " + corporaName + " does not exist.");
				corporas.add(corpora);
			}

			System.out.println("The search file is valid.");
			System.out.println();

			// Creación y carga de la estructura de datos
			for (int iCorpora = 0; iCorpora < corporasElement.size(); iCorpora++) {
				variables.put(iCorpora, new TreeMap<>());
				corpusByGenre.put(iCorpora, new LinkedHashMap<>());
				includedCorpus.put(iCorpora, new LinkedHashMap<>());
				Corpora corpora = corporas.get(iCorpora);
				Element corporaElement = corporasElement.get(iCorpora);
				Element variablesElement = corporaElement.getChild("variables", NAMESPACE);

				List<Element> qVariablesElements = variablesElement.getChildren("variable", NAMESPACE);
				for (Element qVariableElement : qVariablesElements) {
					String qVariableName = qVariableElement.getTextNormalize();
					File variableQFile = new File(qVariableElement.getAttributeValue("q-file"));
					if (!variableQFile.isFile())
						throw new CorpusSearchAutoException("The q file of the variable " + qVariableName + " does not exist.");
					variables.get(iCorpora).put(XMLUtils.indexOfElement(qVariableElement), VariableFactory.getQVariable(qVariableName, variableQFile));
				}

				List<Element> builtInVariablesElements = variablesElement.getChildren("built-in-variable", NAMESPACE);
				for (Element builtInVariableElement : builtInVariablesElements) {
					String builtInVariableName = builtInVariableElement.getTextNormalize();
					String builtInVariableID = builtInVariableElement.getAttributeValue("name");
					BuiltInVariable builtInVariable = VariableFactory.getBuiltInVariable(builtInVariableID, builtInVariableName);
					if (builtInVariable == null)
						throw new CorpusSearchAutoException("The built-in variable " + builtInVariableID + " does not exist.");
					variables.get(iCorpora).put(XMLUtils.indexOfElement(builtInVariableElement), builtInVariable);
				}

				for (Corpus corpusInCorpora : corpora.getCorpusList()) {
					includedCorpus.get(iCorpora).put(corpusInCorpora, false);
				}

				List<Element> genresElements = corporaElement.getChild("genres", NAMESPACE).getChildren("genre", NAMESPACE);
				for (Element genreElement : genresElements) {
					String genreName = genreElement.getAttributeValue("name").trim();
					if (!corpusByGenre.get(iCorpora).containsKey(genreName)) {
						corpusByGenre.get(iCorpora).put(genreName, new ArrayList<Corpus>());
					}
					List<Element> corpusElements = genreElement.getChildren("corpus", NAMESPACE);

					for (Element corpusElement : corpusElements) {
						String corpusName = corpusElement.getTextNormalize();
						Corpus corpus = null;

						for (Corpus corpusInCorpora : corpora.getCorpusList()) {
							if (corpusInCorpora.getName().equals(corpusName)) {
								includedCorpus.get(iCorpora).replace(corpusInCorpora, true);
								corpusByGenre.get(iCorpora).get(genreName).add(corpusInCorpora);
								corpus = corpusInCorpora;
							}
						}
						if (corpus == null)
							throw new CorpusSearchAutoException("The corpus " + corpusName + " does not belong to the corpora " + corpora.getName());
					}
				}

				for (Entry<Corpus, Boolean> entryPos : includedCorpus.get(iCorpora).entrySet()) {
					Corpus posCorpus = entryPos.getKey();
					boolean isIncludedPos = entryPos.getValue();

					if (isIncludedPos && posCorpus.getType() == CorpusType.POS) {
						boolean posHasPsd = false;

						for (Entry<Corpus, Boolean> entryPsd : includedCorpus.get(iCorpora).entrySet()) {
							Corpus psdCorpus = entryPsd.getKey();
							boolean isIncludedPsd = entryPsd.getValue();

							if (isIncludedPsd && psdCorpus.getType() == CorpusType.PSD && posCorpus.getName().equals(psdCorpus.getName())) {
								posHasPsd = true;
								break;
							}
						}

						if (!posHasPsd)
							throw new CorpusSearchAutoException("The \"pos\" file " + posCorpus.getName() + " does not have an asociated \"psd\" file in the corpora " + corpora.getName());
					}
				}

				System.out.print("Corpus files of " + corpora.getName() + " NOT included in the search:");
				StringBuilder corpusNotIncluded = new StringBuilder();
				includedCorpus.get(iCorpora).forEach((corpus, isIncluded) -> {
					if (!isIncluded) {
						corpusNotIncluded.append("\t[" + corpus.getType() + "] " + corpus.getName() + "\n");
					} else {

					}
				});
				if (corpusNotIncluded.length() == 0) {
					System.out.println(" none.");
				} else {
					System.out.println();
					System.out.println(corpusNotIncluded.toString());
				}
			}

			System.out.println();
			if (interactive) {
				if (!Utils.askUserToContinue("Do you want to continue with the search? [Y/N]: ")) {
					System.out.println("Search aborted.");
					return;
				}
			}

			// Procesado de corporas (blocking I/O)
			Date dateOfStart = new Date();
			for (int iCorpora = 0; iCorpora < corporasElement.size(); iCorpora++) {
				// [genero][variable]
				List<String> genres = new ArrayList<>(corpusByGenre.get(iCorpora).keySet());
				String[][] outputTable = new String[genres.size() + 1][variables.get(iCorpora).size()];
				Corpora corpora = corporas.get(iCorpora);
				lastStartDate = new Date();
				int builtInVariables = countBuildInVariables(variables.get(iCorpora).values());
				int qVariables = countQVariables(variables.get(iCorpora).values());
				boolean canContinue = true;

				System.out.println();
				System.out.println("------------------- (PROCESING CORPORA " + corpora.getName() + ") -------------------");
				System.out.println();

				if (qVariables != 0) {
					System.out.println("Creating temporary directory structure...");
					File tempFolder = corpora.getPath().resolve("temp" + System.currentTimeMillis()).toFile();
					Map<String, File> folderForGenre = createTempDirStructure(tempFolder, corpusByGenre.get(iCorpora));
					System.out.println("Done (" + folderForGenre.size() + " folders created).");

					System.out.println("Generating CS commands...");
					File outputDirectory = OUTPUT_PATH.resolve(new SimpleDateFormat("dd-MM-yyyy HH.mm.ss.SSS").format(dateOfSearch) + " " + corpora.getName()).toFile();
					List<CSCommand> commands = generateCSCommands(variables.get(iCorpora).values(), folderForGenre, outputDirectory);
					System.out.println("Done (" + commands.size() + " commands generated).");

					System.out.println("Executing commands...");
					int threadsNumber;
					switch (Runtime.getRuntime().availableProcessors()) {
					case 1: 
					case 2:
						threadsNumber = Runtime.getRuntime().availableProcessors();
						break;
						
					default:
						threadsNumber = Runtime.getRuntime().availableProcessors() - 1;
					}
					Semaphore semaphore = new Semaphore(threadsNumber);
					int actualCommand = 1;
					for (CSCommand command : commands) {
						semaphore.acquire();
						System.out.println("[" + actualCommand++ + "/" + commands.size() + "] Searching " + command.getVariable().getName().toLowerCase() + " in " + command.getGenre().toLowerCase() + "...");
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Process csProcess = Runtime.getRuntime().exec(command.getCommand());
									InputStream errorStream = csProcess.getErrorStream();
									csProcess.getInputStream().close();
									int exitVal = csProcess.waitFor();
									if (exitVal != 0) {
										System.out.println("\t" + Utils.convertStreamToString(errorStream).trim());
									}
									errorStream.close();
									semaphore.release();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
					semaphore.acquire(threadsNumber);

					if (actualCommand == commands.size() + 1) {
						for (int i = 0; i < variables.get(iCorpora).size(); i++) {
							long hits = 0, tokens = 0, total = 0;
							for (int j = 0; j < genres.size(); j++) {
								CSCommand csCommand = getCSCommand(commands, genres.get(j), variables.get(iCorpora).get(i));
								if (csCommand != null) {
									String result = getResultOfOutFile(csCommand.getOutFilePath().toFile());
									String[] values = result.split("/");
									if (values.length == 3) {
										switch (showOnly) {
										case ALL:
												hits += Long.valueOf(values[0]);
												tokens += Long.valueOf(values[1]);
												total += Long.valueOf(values[2]);
												outputTable[j][i] = result;
											break;
										case HITS:
												hits += Long.valueOf(values[0]);
												outputTable[j][i] = values[0];
											break;
										case TOKENS:
												tokens += Long.valueOf(values[1]);
												outputTable[j][i] = values[1];
											break;
										case TOTAL:
												total += Long.valueOf(values[2]);
												outputTable[j][i] = values[2];
											break;
										}
									}
								}
							}
							switch (showOnly) {
							case ALL:
								outputTable[genres.size()][i] = hits + "/" + tokens + "/" + total;
								break;
							case HITS:
								outputTable[genres.size()][i] = String.valueOf(hits);
								break;
							case TOKENS:
								outputTable[genres.size()][i] = String.valueOf(tokens);
								break;
							case TOTAL:
								outputTable[genres.size()][i] = String.valueOf(total);
								break;
							}
						}
					} else {
						canContinue = false;
					}

					cleanTempFolders(tempFolder);
				}

				if (canContinue && builtInVariables != 0) {
					int actualVariable = 1;
					System.out.println();
					System.out.println("Executing built-in variables...");
					for (int i = 0; i < variables.get(iCorpora).values().size(); i++) {
						Variable variable = variables.get(iCorpora).get(i);
						if (variable instanceof BuiltInVariable) {
							BuiltInVariable builtInVariable = (BuiltInVariable) variable;
							System.out.println("[" + actualVariable++ + "/" + builtInVariables + "] Searching " + builtInVariable.getName().toLowerCase() + "...");
							String[] output = builtInVariable.process(corpusByGenre.get(iCorpora));
							for (int j = 0; j < outputTable.length; j++) {
								outputTable[j][i] = output[j];
							}
						}
					}
					System.out.println("Done.");
					System.out.println();
				}
				
				switch (format) {
				case HTML:
					htmlExporter.export(genres, variables.get(iCorpora).values(), outputTable, showOnly, dateOfSearch, getOutFile(outPath, corpora, inFilename, Format.HTML));
					break;
				case EXCEL:
					excelExporter.export(genres, variables.get(iCorpora).values(), outputTable, showOnly, dateOfSearch, getOutFile(outPath, corpora, inFilename, Format.EXCEL));
					break;
				case ALL:
					htmlExporter.export(genres, variables.get(iCorpora).values(), outputTable, showOnly, dateOfSearch, getOutFile(outPath, corpora, inFilename, Format.HTML));
					excelExporter.export(genres, variables.get(iCorpora).values(), outputTable, showOnly, dateOfSearch, getOutFile(outPath, corpora, inFilename, Format.EXCEL));
					break;
				}

				Utils.successfulMessage(corpora.getName(), lastStartDate, new Date());
			}

			System.out.println();
			System.out.println("---------------------------");
			System.out.println();
			Utils.successfulMessage(inFile.getName(), dateOfStart, new Date());
		}
	}

	private int countBuildInVariables(Collection<Variable> vars) {
		int builtInVariables = 0;
		for (Variable variable : vars) {
			if (variable instanceof BuiltInVariable) {
				builtInVariables++;
			}
		}
		return builtInVariables;
	}

	private int countQVariables(Collection<Variable> vars) {
		int qVariable = 0;
		for (Variable variable : vars) {
			if (variable instanceof QVariable) {
				qVariable++;
			}
		}
		return qVariable;
	}

	private File getOutFile(Path outPath, Corpora corpora, String inFilename, Format format) {
		return outPath.resolve(corpora.getName() + "-" + inFilename + "-output." + format.getExtension()).toFile();
	}

	private String getResultOfOutFile(File outFile) throws Exception {
		if (!outFile.isFile())
			return "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(outFile), Charset.forName("UTF-8")))) {
			boolean isResult = false;
			for (String line; (line = br.readLine()) != null;) {
				if (isResult) {
					return line.trim();
				} else if (line.trim().equalsIgnoreCase("whole search, hits/tokens/total")) {
					isResult = true;
				}
			}
		}
		return "NO RESULT FOUND";
	}

	private CSCommand getCSCommand(List<CSCommand> commands, String genre, Variable variable) {
		if (variable instanceof QVariable) {
			for (CSCommand command : commands) {
				if (command.getGenre().equals(genre) && command.getVariable().equals(variable)) {
					return command;
				}
			}
		}
		return null;
	}

	private void cleanTempFolders(File tempFolder) {
		System.out.println("Deleting temporary directory structure...");
		if (tempFolder.isDirectory()) {
			FileSystemUtils.deleteDirectory(tempFolder);
		}
		System.out.println("Done.");
	}

	private List<CSCommand> generateCSCommands(Collection<Variable> variables, Map<String, File> folderForGenre, File outputDirectory) throws CorpusSearchAutoException {
		FileSystemUtils.createDirectoryIfNotExist(OUTPUT_PATH.toFile());
		FileSystemUtils.forceCreateDirectory(outputDirectory);

		List<CSCommand> commands = new ArrayList<>();
		Set<String> genres = folderForGenre.keySet();
		for (Variable variable : variables) {
			if (variable instanceof QVariable) {
				QVariable qVariable = (QVariable) variable;
				File variableFolder = outputDirectory.toPath().resolve(Utils.normalizeString(qVariable.getName())).toFile();
				FileSystemUtils.createDirectoryIfNotExist(variableFolder);
				for (String genre : genres) {
					File genreFolder = variableFolder.toPath().resolve(Utils.normalizeString(genre)).toFile();
					FileSystemUtils.createDirectoryIfNotExist(genreFolder);
					String corpusToSearch = folderForGenre.get(genre).toString() + File.separator + "*.psd";
					CSCommand command = new CSCommand(qVariable, corpusToSearch, genreFolder.toPath().resolve("result.out"));
					command.setGenre(genre);
					commands.add(command);
				}
			}
		}
		return commands;
	}

	private Map<String, File> createTempDirStructure(File tempFolder, Map<String, List<Corpus>> corpusByGenre) throws Exception {
		Map<String, File> folderForGenre = new HashMap<>();
		for (File file : tempFolder.getParentFile().listFiles()) {
			if (file.getName().startsWith("temp")) {
				FileSystemUtils.deleteDirectory(file);
			}
		}
		FileSystemUtils.forceCreateDirectory(tempFolder);
		Set<String> genres = corpusByGenre.keySet();
		for (String genre : genres) {
			String folderName = Utils.normalizeString(genre);
			File genreFolder = tempFolder.toPath().resolve(folderName).toFile();
			folderForGenre.put(genre, genreFolder);
			if (!genreFolder.mkdir())
				throw new CorpusSearchAutoException("Unable to create " + folderName + " folder (do you have permissions?).");
			for (Corpus corpus : corpusByGenre.get(genre)) {
				if (corpus.getType() == CorpusType.PSD) {
					Files.copy(corpus.getFile().toPath(), genreFolder.toPath().resolve(corpus.getName() + ".psd"), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}

		return folderForGenre;
	}

	@Override
	public String usage() {
		return "CospusSearchAuto -tool statistics-by-genres -in [PATH_OF_SEARCH_FILE] (-show-only [all|hits|tokens|total] -out-format [all|html|excel] -out [PATH_OF_OUT_FOLDER])";
	}
}
