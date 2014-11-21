package com.ckenken.io;

import java.util.Comparator;

import com.ckenken.storage.Category;

public class CateComparable implements Comparator<Category>{

	@Override
	public int compare(Category a, Category b) {
		// TODO Auto-generated method stub
		
		if(a.distance > b.distance)
			return 1;
		else if (a.distance == b.distance)
			return 0;
		else 
			return -1;
		
	}

}
