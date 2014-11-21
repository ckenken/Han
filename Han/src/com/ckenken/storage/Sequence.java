package com.ckenken.storage;

import java.util.ArrayList;

import lab.adsl.object.Point;

public class Sequence {
	public ArrayList<Point> points;
	public int objectID;
	
	public Sequence() 
	{
		points = new ArrayList<Point>();
	}
	
	public Sequence(ArrayList<Point> input) 
	{
		points = input;
	}
	
	public ArrayList<Point> getSubSequence(int index) 
	{
		ArrayList<Point> temp = new ArrayList<Point>();
		
		for(int i = index; i<points.size(); i++) {
			temp.add(points.get(i));
		}
		
		return temp;
	}
}
