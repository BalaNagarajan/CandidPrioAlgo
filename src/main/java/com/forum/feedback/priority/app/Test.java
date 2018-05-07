package com.forum.feedback.priority.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Test {

	public static void main(String args[]) {

		Random rand = new Random();
		int first = rand.nextInt(101);
		int upperBound = 100;
		int second = rand.nextInt(101);
		int third = rand.nextInt(101);
		int tot = first + second + third;

		int result1 = Math.round((int) (first * upperBound) / tot);
		int result2 = Math.round((int) (second * upperBound) / tot);
		int result3 = Math.round((int) (third * upperBound) / tot);
		int net = result1 + result2 + result3;
		if (net > upperBound) {
			int comp = net - upperBound;
			result1 = result1 - comp;
		} else if (net < upperBound) {
			int comp = upperBound - net;
			result1 = result1 + comp;
		}
		net = result1 + result2 + result3;
		List<Integer> priorityList = new ArrayList<Integer>();
		priorityList.add(result1);
		priorityList.add(result2);
		priorityList.add(result3);
		Collections.sort(priorityList, Collections.reverseOrder());

		for (int i : priorityList) {
			System.out.println("----------Priority Values----" + i);
		}

		System.out.println("---------After Net-----" + net);

	}
}
