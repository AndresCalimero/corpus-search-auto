package perez.garcia.andres.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import perez.garcia.andres.models.ShowOnly;
import perez.garcia.andres.variables.Variable;

public class ExcelExporter implements Exporter {

	@Override
	public void export(Collection<String> genres, Collection<Variable> variables, String[][] outputTable, ShowOnly showOnly, Date dateOfSearch, File outFile) throws Exception {
		List<String> genresList = new ArrayList<>(genres);
		List<Variable> variablesList = new ArrayList<>(variables);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		POIXMLProperties xmlProps = workbook.getProperties();    
		POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
		coreProps.setCreator("CorpusSearchAuto");
		XSSFSheet sheet = workbook.createSheet("Result");
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		Row headerRow = sheet.createRow(0);
		for (int i = 0; i < variablesList.size(); i++) {
			Cell cell = headerRow.createCell(i + 1);
			cell.setCellValue(variablesList.get(i).getName());
			cell.setCellStyle(headerStyle);
		}

		int rowNumber = 0;
		for (rowNumber = 0; rowNumber < outputTable.length; rowNumber++) {
			Row resultRow = sheet.createRow(rowNumber + 1);
			Cell genreNameCell = resultRow.createCell(0);
			if (rowNumber != outputTable.length - 1) {
				genreNameCell.setCellValue(genresList.get(rowNumber).substring(0, 1).toUpperCase() + genresList.get(rowNumber).substring(1).toLowerCase());
			} else {
				genreNameCell.setCellValue("TOTAL");
			}
			genreNameCell.setCellStyle(headerStyle);
			for (int j = 0; j < variablesList.size(); j++) {
				Cell resultCell = resultRow.createCell(j + 1);
				String result = outputTable[rowNumber][j];
				if (result == null) {
					resultCell.setCellValue("null");
				} else if (result.matches("\\d+")) {
					resultCell.setCellValue(Long.parseLong(result));
				} else if(result.replace(",", ".").matches("\\d+\\.\\d+")) {
					resultCell.setCellValue(Double.parseDouble(result.replace(",", ".")));
				} else {
					resultCell.setCellValue(result);
				}
				if (rowNumber == outputTable.length - 1) {
					resultCell.setCellStyle(headerStyle);
				}
			}
		}
		
		for (int i = 0; i < variables.size() + 1; i++) {
			sheet.autoSizeColumn(i);
		}

		if (!outFile.getParentFile().isDirectory()) {
			outFile.getParentFile().mkdirs();
		}
		FileOutputStream out = new FileOutputStream(outFile);
		workbook.write(out);
		workbook.close();
		out.close();
		System.out.println("Excel output file saved in " + outFile.getAbsolutePath());
	}
}
