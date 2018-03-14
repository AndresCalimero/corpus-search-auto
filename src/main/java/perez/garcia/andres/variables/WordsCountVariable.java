package perez.garcia.andres.variables;

import perez.garcia.andres.models.Corpus;
import perez.garcia.andres.models.CorpusType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WordsCountVariable extends BuiltInVariable {

	public static final String ID = "words_count";

	private static final List<String> IGNORED_NODES = Arrays.asList("ID", "CODE", ".", ",");

	WordsCountVariable(String name) {
		super(ID, name);
	}

	@Override
	public String[] process(Map<String, List<Corpus>> corpusByGenre) throws Exception {
		List<String> genres = new ArrayList<>(corpusByGenre.keySet());
		String[] output = new String[genres.size() + 1];

		long totalWords = 0;
		for (int i = 0; i < genres.size(); i++) {
			long wordsGenre = 0;
			List<Corpus> corpusList = corpusByGenre.get(genres.get(i));
			for (int j = 0; j < corpusList.size(); j++) {
				Corpus corpus = corpusList.get(j);
				if (corpus.getType() == CorpusType.POS) {
					try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(corpus.getFile()), Charset.forName("UTF-8")))) {
						for (String line; (line = br.readLine()) != null;) {
							line = line.trim().replaceAll("[_/]", "//");
							String[] words = line.split(" ");
							for (int k = 0; k < words.length; k++) {
								int lastIndexOfSeparator = words[k].lastIndexOf("//");
								if (lastIndexOfSeparator != -1) {
									String node = words[k].substring(lastIndexOfSeparator + 2);
									if (!IGNORED_NODES.contains(node)) {
										wordsGenre++;
										totalWords++;
									}
								}
							}
						}
					}
				}
			}
			if (wordsGenre != 0) {
				output[i] = String.valueOf(wordsGenre);
			} else {
				output[i] = "0";
			}
		}

		if (totalWords != 0) {
			output[output.length - 1] = String.valueOf(totalWords);
		} else {
			output[output.length - 1] = "0";
		}
		return output;
	}
}
