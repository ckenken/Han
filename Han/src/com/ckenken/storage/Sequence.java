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
		points = new ArrayList<Point>();
		
		for(int i = 0; i<input.size(); i++) {
			points.add(input.get(i));
		}
	}
	
	public void show()
	{
		for(int i = 0;i<points.size(); i++) {
			if(i != 0)
				System.out.print(points.get(i).id + "->");
			else 
				System.out.print(points.get(i).id);
		}
		System.out.println();
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
