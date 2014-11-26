package com.ckenken.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.naming.spi.DirStateFactory.Result;

import lab.adsl.object.Point;
import lab.adsl.optics.OPTICS;

import com.ckenken.storage.NewPoint;

public class ClusterG {
	
	public static void main(String [] args) throws SQLException, IOException
	{
//		JDBC jdbc = new JDBC();
//		
//		String sql = "select distinct cate from same";
//		
//		ResultSet rs = jdbc.query(sql);
//		
//		while(rs.next()) {
//			String cate = rs.getString("cate");
//			
//		//	System.out.println("=================");
//			
//			runClusterG(cate);
//			
//			System.out.println("=================");
//		}
		parseSameG();
	}
	
	public static void runClusterG(String category) throws SQLException, IOException
	{
		JDBC jdbc = new JDBC();
		
		String sql = "select * from same where cate = '" + category + "'";
		
		ResultSet rs = jdbc.query(sql);
		
		//ArrayList<Point> newPoints = new ArrayList<Point>();
		
		Map<Long, Point> result = new HashMap<Long, Point>();
		
		while(rs.next())
		{
			long sameId = (long)rs.getInt("sameid");
			double lat = rs.getDouble("lat");
			double lng = rs.getDouble("lng");
			String cate = rs.getString("cate");
			
			NewPoint np = new NewPoint(new Date(), lat, lng, cate);
			
			result.put(sameId, new Point((int)sameId, np));	
		}
		
		for(Object key: result.keySet()) {
	//		System.out.println(result.get(key).lat + ", " + result.get(key).lng + " " + result.get(key).cate);			
		}
		
		
//		for(int i = 0; i<result.size(); i++) {
//			Set set = result.
//			
//			System.out.println(result.get(id).lat + ", " + result.get(id).lng + " " + result.get(id).cate);	
//		}
//		
		OPTICS k = new OPTICS();
		
		k.setParameter(0, 200, 1);
			
		k.pts = result;
		
	//	k.pts = k.getSyntheticData();
	
		k.runOptics();
		
		k.extractCluster();
		
		k.displayCluster();

	}
	
	
	public static void parseSameG() throws FileNotFoundException
	{
		JDBC jdbc = new JDBC();
		String sql = "";
		
		File f = new File("sameG_new.txt");
		FileInputStream FIS = new FileInputStream(f);
		
		Scanner scanner = new Scanner(FIS);
		
		int idCounter = 0;
		int previous = -1;
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			
			line = line.replace("output:", "");
			String [] SP = line.split(",");
			//System.out.println(line);
			
			if(line.charAt(0) == '=') {
				previous = -1;
			}
			else if (SP[1].equals("-1")) {
				sql = "update same set G=" + idCounter + " where sameid=" + Integer.parseInt(SP[0]);
				jdbc.insertQuery(sql);	
				idCounter++;
				previous = -1;
			}
			else if (Integer.parseInt(SP[1]) == previous) {
				sql = "update same set G=" + idCounter + " where sameid=" + Integer.parseInt(SP[0]);
				jdbc.insertQuery(sql);
			}
			else if ((Integer.parseInt(SP[1]) != previous)) {
				idCounter++;
				sql = "update same set G=" + idCounter + " where sameid=" + Integer.parseInt(SP[0]);
				jdbc.insertQuery(sql);	
				previous = Integer.parseInt(SP[1]);
			}
		}
	}
	
}
