package com.ckenken.storage;

import java.util.ArrayList;

public class Snippet {
	public Sequence s;
	public ArrayList<Integer> object_ids;
	public int weight;
	
	public Snippet(Sequence input_s, ArrayList<Integer> objs)
	{
		s = input_s;
		object_ids = objs;
		weight = objs.size();
	}
}
