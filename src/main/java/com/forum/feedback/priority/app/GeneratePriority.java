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
		Workbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		Sheet sheet = workbook.createSheet("Candidates");
		try {
			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream("candidate-prio-generated-file.xlsx");

			if (updatedCandidateMap != null && !updatedCandidateMap.isEmpty()) {
				GeneratePriority generatePriority = new GeneratePriority();
				Set<String> nameSet = updatedCandidateMap.keySet();
				int rowIndex = 0;
				for (String nameVal : nameSet) {
					List<Reviewer> reviewerFinalList = updatedCandidateMap.get(nameVal);
					if (reviewerFinalList != null && reviewerFinalList.size() > 0) {
						for (Reviewer reviewerObject : reviewerFinalList) {
							Row row = sheet.createRow(rowIndex);
							row.createCell(0).setCellValue(nameVal);
							row.createCell(1).setCellValue(reviewerObject.getReviewerName());
							row.createCell(2).setCellValue(reviewerObject.getReviewerPriority());
							rowIndex++;
						}
					}
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
