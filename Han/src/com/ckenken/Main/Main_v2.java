package com.ckenken.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lab.adsl.object.Point;

import com.ckenken.algo.Coarse;
import com.ckenken.io.JDBC;
import com.ckenken.storage.Coarse_pattern;
import com.ckenken.storage.NewPoint;
import com.ckenken.storage.Sequence;

public class Main_v2 {
	public static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public static void main(String [] args) throws SQLException, ParseException
	{
//		Sequence all = new Sequence();
//		
//		JDBC jdbc = new JDBC();
//		
//		String sql = "select * from raw2 where same != -1";
//		
//		ResultSet rs = jdbc.query(sql);
//		
//		rs.next();
//		double lat = rs.getDouble("lat");
//		double lng = rs.getDouble("lng");
//		String time = rs.getString("date");
//		
//		Date d = parseDate(time);
//		NewPoint np = new NewPoint(d, lat, lng);
//		Point startPoint = new Point(0, np);
//		startPoint.same = Integer.parseInt(rs.getString("same"));
//		
//		
//		int idCounter = 0;
//		
//		System.out.println(sdFormat.format(startPoint.ckTime));
//		
//		while(rs.next()) {
//			lat = Double.parseDouble(rs.getString("lat"));
//			lng = Double.parseDouble(rs.getString("lng"));
//			time = rs.getString("date");
//			d = parseDate(time);
//			np = new NewPoint(d, lat, lng);
//			Point p = new Point(0, np);
//			p.same = Integer.parseInt(rs.getString("same"));		
//			
//			
//			if(p.same != startPoint.same) { // 不同 same 的交界點，要抓取該點(e.g. p1)的中心點，time = startPoint.time，並放入 Sequence
//			
//				NewPoint newP = Coarse.getSameCenterById(p.same);
//				newP.setTime(startPoint.ckTime);
//				Point temp = new Point(idCounter, newP);
//				all.points.add(temp);
//				idCounter++;
//				startPoint = p;
//			}
//			else // 不是交界點 
//			{
//			
//			}
//		}
//		
//		
//		for(int i = 0; i<all.points.size(); i++) {
//			Point p = all.points.get(i);
//			System.out.println(p.id + ": " + p.lat + "," + p.lng + " " + sdFormat.format(p.ckTime));
//			sql = "select * from same where lat = " + p.lat + " and lng = " + p.lng;
//			rs = jdbc.query(sql);
//			rs.next();
//			
//			sql = "insert into sequence30 values(" + p.id + "," + rs.getInt("sameid") + "," + p.lat + "," + p.lng + ",'" + rs.getString("G") + "','" +  rs.getString("cate") + "','" + sdFormat.format(p.ckTime) + "')";
//			jdbc.insertQuery(sql);
//			
//		}
//		
	
		ArrayList<Sequence> origin = cut();
	
//		for(int i = 0; i<origin.size(); i++) {
//			System.out.println("o" + origin.get(i).objectID + ":");
//			for(int j = 0; j<origin.get(i).points.size(); j++) {
//				origin.get(i).points.get(j).show();
//			}
//		}

		Coarse.max_g_num = 75;
	
		int counter = 0;
		for(int i = 0; i<=Coarse.max_g_num; i++) {
			for(int j = 0; j<=Coarse.max_g_num; j++) {
				if(i == j)
					continue;
				
			//	System.out.println(i + "," + j + ":");
				
				ArrayList<Sequence> extended = Coarse.extend(origin, i);
				
			//	System.out.println("extended!");
				
				Coarse_pattern cp = Coarse.testCoarse(extended, i, j, 3600000, 3);
				if(cp != null) {
					cp.show();
					counter++;
				}	
			}
		}
		
		System.out.println(counter);
	}
	
	
	public static Date parseDate(String time) throws ParseException
	{
		sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		Date d = sdFormat.parse(time);
		
		return d;
	}
	
	@SuppressWarnings({ "deprecation" })
	public static ArrayList<Sequence> cut() throws SQLException, ParseException 
	{
		ArrayList<Sequence> origin = new ArrayList<Sequence>();
		
		JDBC jdbc = new JDBC();
		String sql = "select * from sequence30";
		
		ResultSet rs = jdbc.query(sql);
		
		ArrayList<Point> temp = new ArrayList<Point>();
		
		rs.next();
		
		NewPoint np = new NewPoint(Main.parseDate(rs.getString("time")), rs.getDouble("lat"), rs.getDouble("lng"), rs.getString("cate"));
		Point p = new Point((long)rs.getInt("seqid"), np);
		p.Gid = rs.getInt("G");
		temp.add(p);
		
		//System.out.println(p.ckTime.getDay());
		
		int previous = p.ckTime.getDay();
		int objectCounter = 0;
		
		while(rs.next()) {
			np = new NewPoint(Main.parseDate(rs.getString("time")), rs.getDouble("lat"), rs.getDouble("lng"), rs.getString("cate"));
			p = new Point((long)rs.getInt("sameid"), np);
			p.Gid = rs.getInt("G");
			
	//		p.show();
			
			if(previous == p.ckTime.getDay()) {   // same day
				temp.add(p);
			}
			else // move day 
			{
				Sequence seq = new Sequence(temp);
				seq.objectID = objectCounter;
				origin.add(seq);
				objectCounter++;
								
//				System.out.println(objectCounter + ":");
//				for(int i = 0; i<temp.size(); i++)	
//					temp.get(i).show();
				
				temp.clear();
				temp = new ArrayList<Point>();
				temp.add(p);
			
//				System.out.println(objectCounter + ":");
//				for(int i = 0; i<temp.size(); i++)	
//					temp.get(i).show();
//				System.out.println("");				
			}
			previous = p.ckTime.getDay();
		}
		
		return origin;
	}
}
