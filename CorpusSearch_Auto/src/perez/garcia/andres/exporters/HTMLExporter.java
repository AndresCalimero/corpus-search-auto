package perez.garcia.andres.exporters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import perez.garcia.andres.models.ShowOnly;
import perez.garcia.andres.variables.Variable;

public class HTMLExporter implements Exporter {

	@Override
	public void export(Collection<String> genres, Collection<Variable> variables, String[][] outputTable, ShowOnly showOnly, Date dateOfSearch, File outFile) throws Exception {
		List<String> genresList = new ArrayList<>(genres);
		List<Variable> variablesList = new ArrayList<>(variables);
		
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<html>\n");
		htmlContent.append("<head>\n");
		htmlContent.append("<title>CorpusSearchAuto</title>\n");
		htmlContent.append("</head>\n");
		htmlContent.append("<body>\n");
		htmlContent.append("<h3>CorpusSearchAuto output table " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateOfSearch) + " (" + showOnly.toString() + ")</h3>\n");

		htmlContent.append("<blockquote>\n");
		htmlContent.append("<table border cellpadding=\"5\">\n");
		htmlContent.append("<tbody>\n");
		htmlContent.append("<tr>\n");
		htmlContent.append("<th></th>\n");
		for (Variable variable : variablesList) {
			htmlContent.append("<th>" + variable.getName() + "</th>\n");
		}
		htmlContent.append("</tr>\n");
		for (int i = 0; i < outputTable.length; i++) {
			htmlContent.append("<tr>\n");
			if (i != outputTable.length -1) {
				htmlContent.append("<th align=\"left\">" + genresList.get(i).substring(0, 1).toUpperCase() + genresList.get(i).substring(1).toLowerCase() + "</th>\n");
			} else {
				htmlContent.append("<th align=\"left\">TOTAL</th>\n");
			}
			for (int j = 0; j < variablesList.size(); j++) {
				if (i != outputTable.length -1) {
					htmlContent.append("<td align=\"right\">" + outputTable[i][j] + "</td>\n");
				} else {
					htmlContent.append("<td align=\"right\"><b>" + outputTable[i][j] + "</b></td>\n");
				}
			}
			htmlContent.append("</tr>\n");
		}
		
		htmlContent.append("</tbody>\n");
		htmlContent.append("</table>\n");
		htmlContent.append("</blockquote>\n");
		htmlContent.append("</body>\n");
		htmlContent.append("</html>\n");

		if (!outFile.getParentFile().isDirectory()) {
			outFile.getParentFile().mkdirs();
		}
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("UTF-8")))) {
			bw.write(htmlContent.toString());
		}

		System.out.println("HTML output file saved in " + outFile.getAbsolutePath());
	}
}
