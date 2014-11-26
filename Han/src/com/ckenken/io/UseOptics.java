package com.ckenken.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import lab.adsl.object.Point;
import lab.adsl.optics.OPTICS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.ckenken.storage.KML;
import com.ckenken.storage.NewPoint;

public class UseOptics {
	public static void main(String [] args) throws IOException
	{
		KML kml = new KML();
		
		File f = new File("history-10-13-2014.kml");
		
		FileInputStream FIS = new FileInputStream(f);
		
		@SuppressWarnings("resource")
		InputStreamReader ISR = new InputStreamReader(FIS);
		
		StringBuilder SB = new StringBuilder();
		
		while(ISR.ready()) 
		{
			char a = (char)ISR.read();
			SB.append(a);
		}
		
		kml.raw = SB.toString();
		
		//System.out.print(SB.toString());
		
		Document doc = Jsoup.parse(SB.toString());
		
		Elements whens = doc.getElementsByTag("when");
		Elements coords = doc.getElementsByTag("gx:coord");
		System.out.println(whens.get(0).text());
		//System.out.println(coords.get(0).text());
		
		Map<Long, Point> result = new HashMap<Long, Point>();
		
		for(int i = 0; i<whens.size(); i++) { //whens.size();
			
			String [] SP = coords.get(i).text().split(" ");
			
			String lat = SP[0];
			String lng = SP[1];
			
			NewPoint newtemp = new NewPoint(whens.get(i).text(), lat, lng);
			//System.out.println(newtemp.getLat());
			Point temp = new Point((long)i, newtemp);
			
			kml.points.add(temp);
			result.put((long)i, temp);
		}
		
		System.out.println(whens.size());
		
		//System.out.println(result.get((long)0).lat);
		
		for(int i = 0; i<10; i++) {
			System.out.println(result.get((long)i).lat + ", " + result.get((long)i).lng);
			
		}
		
//		Map<Long, Point> result = new HashMap<Long, Point>();
//		Point p0 = new Point(0, 53.7456517542, -0.4875719547);
//		result.put(0L, p0);
//		
		OPTICS k = new OPTICS();
		
		k.setParameter(0, 15, 3);
			
		k.pts = result;
		
	//	k.pts = k.getSyntheticData();
	
		k.runOptics();
		
		k.extractCluster();
		
		k.displayCluster();
	//	System.out.println(k.clusterOrder.get(0).ckTime);
		
		for(int i = 0;i<k.clusterOrder.size(); i++) {
			kml.points.get((int)k.clusterOrder.get(i).id).clusterId = k.clusterOrder.get(i).clusterId;
		}		
		
	}
}
