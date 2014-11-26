package com.ckenken.storage;

import java.util.ArrayList;

public class Coarse_pattern {
	public static int max_pattern_id = 0;
	public int pattern_id;
	public ArrayList<Snippet> snippet_sets;
	public ArrayList<Integer> G_sequence_ids;
	
	public Coarse_pattern()
	{
		snippet_sets = new ArrayList<Snippet>();
		G_sequence_ids = new ArrayList<Integer>();
	}
	
	public void show()
	{
		
		System.out.println(G_sequence_ids.get(0) + "->" + G_sequence_ids.get(1) + " ");
		
//		for(int i =0; i<snippet_sets.size(); i++) {
//			snippet_sets.get(i).s.show();
//		}
	}
}
