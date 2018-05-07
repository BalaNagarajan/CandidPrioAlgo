package com.forum.feedback.priority.app;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.forum.feedback.priority.model.Reviewer;

public class GeneratePriority {

	public void generatePriorityHandBook(Map<String, List<Reviewer>> updatedCandidateMap) {

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook();

		/*
		 * CreationHelper helps us create instances for various things like
		 * DataFormat, Hyperlink, RichTextString etc in a format (HSSF, XSSF)
		 * independent way
		 */
		CreationHelper createHelper = workbook.getCreationHelper();

		// Create a Sheet
		Sheet sheet = workbook.createSheet("Candidates");

		// Create a Font for styling header cells
		/*
		 * Font headerFont = workbook.createFont(); headerFont.setBold(true);
		 * headerFont.setFontHeightInPoints((short) 14);
		 * headerFont.setColor(IndexedColors.RED.getIndex());
		 */

		// Create a CellStyle with the font
		/*
		 * CellStyle headerCellStyle = workbook.createCellStyle();
		 * headerCellStyle.setFont(headerFont);
		 */

		// Create a Row
		// Row headerRow = sheet.createRow(0);

		// Creating cells
		/*
		 * Cell cell1 = headerRow.createCell(0);
		 * cell1.setCellValue("Candiate Name");
		 * cell1.setCellStyle(headerCellStyle); Cell cell2 =
		 * headerRow.createCell(1); cell2.setCellValue("ReviewerName");
		 * cell2.setCellStyle(headerCellStyle);
		 */
		try {
			
			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream("candidate-prio-generated-file.xlsx");
		

			if (updatedCandidateMap != null && !updatedCandidateMap.isEmpty()) {
				GeneratePriority generatePriority = new GeneratePriority();
				Set<String> nameSet = updatedCandidateMap.keySet();
				int rowIndex = 0;
				for (String nameVal : nameSet) {
					List<Reviewer> reviewerFinalList = updatedCandidateMap.get(nameVal);
					System.out.println("-----------------CANDIDATE NAME---------" + nameVal);
					System.out.println("-----------------REVIEWERS Starts---------");
					if (reviewerFinalList != null && reviewerFinalList.size() > 0) {
						for (Reviewer reviewerObject : reviewerFinalList) {
							Row row = sheet.createRow(rowIndex);
							row.createCell(0).setCellValue(nameVal);
							row.createCell(1).setCellValue(reviewerObject.getReviewerName());
							row.createCell(2).setCellValue(reviewerObject.getReviewerPriority());
							rowIndex++;
						}
					}
					System.out.println("-----------------REVIEWERS Ends---------");
				}

			}
			workbook.write(fileOut);

			fileOut.close();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
