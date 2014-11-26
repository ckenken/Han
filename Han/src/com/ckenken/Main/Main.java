package com.ckenken.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lab.adsl.object.Point;

import com.ckenken.io.JDBC;
import com.ckenken.storage.NewPoint;
import com.ckenken.storage.Sequence;

public class Main {
	public static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public static void main(String [] args) throws SQLException, ParseException
	{
		Sequence all = new Sequence();
		
		JDBC jdbc = new JDBC();
		
		String sql = "select * from raw2 where same != -1";
		
		ResultSet rs = jdbc.query(sql);
		
		rs.next();
		double lat = rs.getDouble("lat");
		double lng = rs.getDouble("lng");
		String time = rs.getString("date");
		
		Date d = parseDate(time);
		NewPoint np = new NewPoint(d, lat, lng);
		Point startPoint = new Point(0, np);
		startPoint.same = Integer.parseInt(rs.getString("same"));
		
		double latSum = lat;
		double lngSum = lng;
		int counter = 1;
		
		int idCounter = 0;
		
		System.out.println(sdFormat.format(startPoint.ckTime));
		
		while(rs.next()) {
			lat = Double.parseDouble(rs.getString("lat"));
			lng = Double.parseDouble(rs.getString("lng"));
			time = rs.getString("date");
			d = parseDate(time);
			np = new NewPoint(d, lat, lng);
			Point p = new Point(0, np);
			p.same = Integer.parseInt(rs.getString("same"));		
			
			if(latSum == 0.0)
			{
				latSum += lat;
				lngSum += lng; 
			}
			
			if(p.same != startPoint.same) { // 不同 same 的交界點，要抓取該點(e.g. p1)的中心點，time = startPoint.time，並放入 Sequence
				double centerLat = latSum/(double)counter;
				double centerLng = lngSum/(double)counter;
				Date t = startPoint.ckTime;
				Point temp = new Point(idCounter, new NewPoint(t, centerLat, centerLng));
				temp.same = startPoint.same;
				all.points.add(temp);
				latSum = lat;
				lngSum = lng;
				idCounter++;
				counter = 1;
				startPoint = p;
			}
			else // 不是交界點 
			{
				latSum += lat;
				lngSum += lng;
				counter++;
			}
		}
		
		
		for(int i = 0; i<all.points.size(); i++) {
			Point p = all.points.get(i);
			System.out.println(p.id + ": " + p.lat + "," + p.lng + " " + sdFormat.format(p.ckTime));
			
		}
		
	}
	
	
	
	
	public static Date parseDate(String time) throws ParseException
	{
		sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		Date d = sdFormat.parse(time);
		
		return d;
	}
	
}
