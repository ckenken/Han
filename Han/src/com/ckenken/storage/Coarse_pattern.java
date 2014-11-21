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
	
}
