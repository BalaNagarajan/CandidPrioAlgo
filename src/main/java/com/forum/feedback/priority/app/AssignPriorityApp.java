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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.forum.feedback.priority.model.Reviewer;




/**
 * 
 * 
 * 
 * 
 * Input Format
 * 
 * 	John	Zane
   	John	Bhooshan
	John	Keith
	Philip	Biswyn
	Philip	Frank
	Philip	Madele
	Philip	Samanth
	Philip	Jason
	Mason	Louis
	Mason	Krushan
 * 
 *
 */
public class AssignPriorityApp {
	// Input file name
	public static final String CANDIDATE_FB_LIST_FILE_PATH = "./candidates-fb-list.xlsx";

	public static void main(String[] args) throws IOException, InvalidFormatException {
		// Initializing the workbook
		Workbook workbook = WorkbookFactory.create(new File(CANDIDATE_FB_LIST_FILE_PATH));
		// Getting the first sheet
		Sheet sheet = workbook.getSheetAt(0);
		// Initializing the row Iterator object
		Iterator<Row> rowIterator = sheet.rowIterator();
		// Intializing the Map to associate the candidate key with the reviewer
		// list
		Map<String, List<String>> candidatePrefMap = new HashMap<String, List<String>>();
		// Initializing the reviewer list
		List<String> personReviewerList = new ArrayList<String>();
		String candidateKey = null;
		String tempCandidKey = null;
		// Iterating the row of the sheet one by one
		while (rowIterator.hasNext()) {
			// Getting each row one by one
			Row row = rowIterator.next();
			// Iterating the cell iterator('column' iterator)
			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating column one by one
			while (cellIterator.hasNext()) {
				// Getting the column object one by one
				Cell cell = cellIterator.next();
				// Checking if its a first column
				if (cell.getColumnIndex() == 0) {
					// Getting the candiate key (name or email or empid)
					candidateKey = cell.getStringCellValue();
					if (row.getRowNum() == 0) {
						// Assigning the candidate key to the temporary
						// variable. We need to use this temporary key to check
						// whether the next row starts with different candidate
						tempCandidKey = candidateKey;
					}
				}
				// Checking the next column - where the reviewer key should
				// present
				if (cell.getColumnIndex() == 1) {
					/*
					 * Checking whether the temporary key matches with the next
					 * row candidate key.. If the candidate key matches then
					 * reviewer should be added to the existing list otherwise
					 * the reviewer list is finalized for that candidate and
					 * should be written to the map. And the new list is
					 * initialized for the next candidate , and the first
					 * reviewer is added to the list
					 * 
					 * In nut shell - logic to group the reviewer list for each
					 * candidate
					 */
					if (!candidateKey.equalsIgnoreCase(tempCandidKey)) {
						candidatePrefMap.put(tempCandidKey.trim(), personReviewerList);
						personReviewerList = new ArrayList<String>();
						personReviewerList.add(cell.getStringCellValue());
						tempCandidKey = candidateKey;
					} else {
						// If it matches then the reviewer is added to the list
						personReviewerList.add(cell.getStringCellValue());
					}
				}

			}
			// Finally adding the last candidate to the map
			candidatePrefMap.put(candidateKey.trim(), personReviewerList);
		}

		/**
		 * This logic assigns the priority to each reviewer based on the input
		 * order. The priority will be max to min based on the ascending order
		 * of each reviewer.
		 */
		int priority = 0;
		// This value needs to be modified based on the max value computation
		int maxLimit = 100;
		int tempTotalPriority = 0;
		List<Integer> countList = null;
		List<Reviewer> reviewerList = null;
		List<Integer> candidList = null;
		List<Integer> priorityList = null;
		// Initializing the GeneratePriority object to write down the computed
		// priority to the new spreadsheet
		GeneratePriority genPriorityObj = new GeneratePriority();
		Map<String, List<Reviewer>> updatedCandidateMap = new HashMap<String, List<Reviewer>>();
		Reviewer reviewerObj = null;
		// Validating the populated map
		if (candidatePrefMap != null && !candidatePrefMap.isEmpty()) {
			Set<String> keySet = candidatePrefMap.keySet();
			// Iterating the keys one by one - candidate keys
			for (String keyVal : keySet) {
				// Getting the associated reviewer list for each candidate
				List<String> reviewerNameList = candidatePrefMap.get(keyVal);
				// Checking the reviewer size limit
				if (reviewerNameList != null && reviewerNameList.size() > 0) {
					tempTotalPriority = 0;
					reviewerList = new ArrayList<Reviewer>();
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
						if	(randNumber==0) {
								randNumber = rand.nextInt(101);
							}
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
							priorityList.add(0, adjVal);
						} else if (compVal < maxLimit) {
							int comp = maxLimit - compVal;
							adjVal = priorityList.get(0);
							priorityList.remove(0);
							adjVal = adjVal + comp;
							priorityList.add(0, adjVal);
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

}
