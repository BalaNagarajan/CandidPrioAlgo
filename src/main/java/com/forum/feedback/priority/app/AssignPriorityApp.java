package com.forum.feedback.priority.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.forum.feedback.priority.model.Reviewer;

public class AssignPriorityApp {
	public static final String CANDIDATE_FB_LIST_FILE_PATH = "./candidates-fb-list.xlsx";

	public static void main(String[] args) throws IOException, InvalidFormatException {

		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory.create(new File(CANDIDATE_FB_LIST_FILE_PATH));

		// Retrieving the number of sheets in the Workbook
		System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

		/*
		 * =============================================================
		 * Iterating over all the sheets in the workbook (Multiple ways)
		 * =============================================================
		 */

		// 1. You can obtain a sheetIterator and iterate over it
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		System.out.println("Retrieving Sheets using Iterator");
		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			System.out.println("=> " + sheet.getSheetName());
		}

		/*
		 * ==================================================================
		 * Iterating over all the rows and columns in a Sheet (Multiple ways)
		 * ==================================================================
		 */

		// Getting the Sheet at index zero
		Sheet sheet = workbook.getSheetAt(0);

		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();

		// 1. You can obtain a rowIterator and columnIterator and iterate over
		// them
		System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
		Iterator<Row> rowIterator = sheet.rowIterator();
		Map<String, List<String>> candidatePrefMap = new HashMap<String, List<String>>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String candidateKey = null;
			List<String> reviewerList = new ArrayList<String>();
			// Now let's iterate over the columns of the current row
			Iterator<Cell> cellIterator = row.cellIterator();
			reviewerList = new ArrayList<String>();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cell != null && cell.getColumnIndex() == 0) {
					candidateKey = dataFormatter.formatCellValue(cell).trim();
				} else if (cell != null && cell.getColumnIndex() != 0) {
					if (dataFormatter.formatCellValue(cell) != null && !dataFormatter.formatCellValue(cell).isEmpty()) {
						reviewerList.add(dataFormatter.formatCellValue(cell));
					}
				}
			}
			candidatePrefMap.put(candidateKey.trim(), reviewerList);
		}

		// Planning to keep 100 as static value
		int priority = 0;
		int maxLimit = 100;
		int upperBound = 20;
		int tempTotalPriority = 0;
		List<Integer> countList = null;
		List<Reviewer> reviewerList = null;
		List<Integer> candidList = null;
		List<Integer> priorityList = null;
		GeneratePriority genPriorityObj = new GeneratePriority();
		Map<String, List<Reviewer>> updatedCandidateMap = new HashMap<String, List<Reviewer>>();
		Reviewer reviewerObj = null;
		if (candidatePrefMap != null && !candidatePrefMap.isEmpty()) {
			Set<String> keySet = candidatePrefMap.keySet();
			for (String keyVal : keySet) {
				List<String> reviewerNameList = candidatePrefMap.get(keyVal);
				if (reviewerNameList != null && reviewerNameList.size() > 0) {
					tempTotalPriority = 0;
					reviewerList =   new ArrayList<Reviewer>();
					if (reviewerNameList.size() == 2) {
						priority = maxLimit / 2;
						Reviewer reviewerObj1 = new Reviewer();
						reviewerObj1.setReviewerName(reviewerNameList.get(0));
						reviewerObj1.setReviewerPriority(priority);
						reviewerList.add(reviewerObj1);
						Reviewer reviewerObj2 = new Reviewer();
						reviewerObj2.setReviewerName(reviewerNameList.get(1));
						reviewerObj2.setReviewerPriority(priority);
						reviewerList.add(reviewerObj2);
					} else if (reviewerNameList.size() == 1) {
						priority = maxLimit;
						Reviewer reviewerObj1 = new Reviewer();
						reviewerObj1.setReviewerName(reviewerNameList.get(0));
						reviewerObj1.setReviewerPriority(priority);
						reviewerList.add(reviewerObj1);
					} else if (reviewerNameList.size() > 2) {
						candidList = new ArrayList<Integer>();
						priorityList = new ArrayList<Integer>();
						Random rand = new Random();
						int tot = 0;
						for (int i = 0; i < reviewerNameList.size(); i++) {
							int randNumber = rand.nextInt(101);
							candidList.add(randNumber);
							tot = tot + randNumber;
						}
						
						int compVal = 0;
						for (int i = 0; i < candidList.size(); i++) {
							int calcValue = Math.round((int) (candidList.get(i) * maxLimit) / tot);
							priorityList.add(calcValue);
             				compVal = compVal + calcValue;
						}
						
						
						int adjVal = 0;
						Collections.sort(priorityList, Collections.reverseOrder());
					if (compVal > maxLimit) {
							int comp = compVal - maxLimit;
							adjVal = priorityList.get(0);
							priorityList.remove(0);
							adjVal = adjVal - comp;
							priorityList.add(0,adjVal);
						} else if (compVal < maxLimit) {
							int comp = maxLimit - compVal;
							adjVal = priorityList.get(0);
							priorityList.remove(0);
							adjVal = adjVal + comp;
							priorityList.add(0,adjVal);
						}

						tot = 0;
						compVal = 0; 
						
						int index = 0;
						reviewerList = new ArrayList<Reviewer>();
						for (String reviewerName : reviewerNameList) {
							reviewerObj = new Reviewer();
							reviewerObj.setReviewerName(reviewerName);
							reviewerObj.setReviewerPriority(priorityList.get(index));
							reviewerList.add(reviewerObj);
							index++;
						}
					}

					updatedCandidateMap.put(keyVal, reviewerList);

				}
				
				
			}
		}

		genPriorityObj.generatePriorityHandBook(updatedCandidateMap);

		// Closing the workbook
		workbook.close();
	}

	private static void printCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			System.out.print(cell.getBooleanCellValue());
			break;
		case STRING:
			System.out.print(cell.getRichStringCellValue().getString());
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				System.out.print(cell.getDateCellValue());
			} else {
				System.out.print(cell.getNumericCellValue());
			}
			break;
		case FORMULA:
			System.out.print(cell.getCellFormula());
			break;
		case BLANK:
			System.out.print("");
			break;
		default:
			System.out.print("");
		}

		System.out.print("\t");
	}
}
