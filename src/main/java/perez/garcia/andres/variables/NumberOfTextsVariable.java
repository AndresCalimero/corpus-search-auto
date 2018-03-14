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

public class NumberOfTextsVariable extends BuiltInVariable {

	public static final String ID = "number_of_texts";

	NumberOfTextsVariable(String name) {
		super(ID, name);
	}

	@Override
	public String[] process(Map<String, List<Corpus>> corpusByGenre) throws Exception {
		List<String> genres = new ArrayList<>(corpusByGenre.keySet());
		String[] output = new String[genres.size() + 1];

		long total = 0;
		for (int i = 0; i < genres.size(); i++) {
            long numberOfTexts = 0;
		    for (Corpus corpus : corpusByGenre.get(genres.get(i))) {
		        if (corpus.getType() == CorpusType.PSD) numberOfTexts++;
            }
		    output[i] = String.valueOf(numberOfTexts);
		    total += numberOfTexts;
		}

		output[output.length - 1] = String.valueOf(total);
		return output;
	}
}
