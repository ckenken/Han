package com.ckenken.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.naming.spi.DirStateFactory.Result;

import lab.adsl.object.Point;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ckenken.Main.Main;
import com.ckenken.storage.Category;
import com.ckenken.storage.NewPoint;

public class Same {
	public static ArrayList<Category> categories = new ArrayList<Category>(); 
	
	public static void main(String [] args) throws IOException, SQLException, ParseException, JSONException 
	{
//		File f = new File("same20-5.txt");
//		
//		FileInputStream FIS = new FileInputStream(f);
//		
//		InputStreamReader ISR = new InputStreamReader(FIS);
//		
//		BufferedReader BR = new BufferedReader(ISR);
//		
//		while(BR.ready())
//		{
//			String line = BR.readLine();
//			
//			line = line.replace("output:", "");
//			
//			String [] SP = line.split(",");
//			// SP[0] = id, SP[1] = clusterid (sameid)
//			
//			JDBC jdbc = new JDBC();
//			
//			String sql = "update raw2 set same = " + SP[1] + " where id = " + SP[0];
//			
//			jdbc.insertQuery(sql);
//			
//			System.out.println(line);
//		}
//		BR.close();
		
		
//		File f2 = new File("same_all0_20-5.txt");
//		
//		FileWriter FW = new FileWriter(f2);
//		
//		String sql2 = "select * from raw2 where same = 0";
//		
//		JDBC jdbc2 = new JDBC();
//		
//		ResultSet rs = jdbc2.query(sql2);
//		
//		try {
//			while(rs.next())
//			{
//				String lat = rs.getString("lat");
//				String lng = rs.getString("lng");
//				System.out.println(lat + "," + lng);
//				FW.append(lat + "," + lng + "\n");
//			}
//			FW.close();
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	
		insertSames();
	
	}
	
	public static ArrayList<NewPoint> getSameById(int id) throws SQLException, ParseException {
		
		JDBC jdbc = new JDBC();
		
		String sql = "select * from raw2 where same = " + id;
		
		ArrayList<NewPoint> result = new ArrayList<NewPoint>();
		
		ResultSet rs = jdbc.query(sql);
		
		while(rs.next())
		{
			double lat = rs.getDouble("lat");
			double lng = rs.getDouble("lng");
			
			String dateStr = rs.getString("date");
			
			Date d = Main.sdFormat.parse(dateStr);
			
			NewPoint np = new NewPoint(d, lat, lng);
			
			result.add(np);
		}		
		return result;
	}
	
	public static void insertSames() throws SQLException, ParseException, JSONException
	{
		JDBC jdbc = new JDBC();
		
		String sql = "select Count(distinct same) from raw2";
		
		ResultSet rs = jdbc.query(sql);
		
		while(rs.next()) {
			int n = rs.getInt("Count(distinct same)");
			System.out.println("n = " + n);
			for(int i = 0; i<n-1; i++) {
				ArrayList<NewPoint> p1 = getSameById(i);
				
				double latSum = 0.0;
				double lngSum = 0.0;
				
				if(p1.size() == 0) {
					System.out.println(i + ", error");
					continue;
				}
				
				Date d = p1.get(0).getTime();
				
				for(int j = 0; j<p1.size(); j++) {
					NewPoint p = p1.get(j);
					
					latSum += p.getLat();
					lngSum += p.getLng();
				}
				
				double centerLat = latSum/(double)p1.size();
				double centerLng = lngSum/(double)p1.size();
				
				NewPoint c = new NewPoint(d, centerLat, centerLng);
				insertSameSQL(i, c);
			}
		}
			
		
	}
	
	public static void insertSameSQL(int id, NewPoint p) throws JSONException, SQLException {
		
		@SuppressWarnings({ "resource", "deprecation" })
		DefaultHttpClient client = new DefaultHttpClient();
		
		System.out.println(p.getLat() + "," + p.getLng());
		
		String url = "https://api.foursquare.com/v2/venues/search?limit=30&ll=" + p.getLat() + "," + p.getLng() + "&client_id=BXTCY4HGTLWINDPRLFXCOWRUEDAJC12ZHEGDTGX4A5DX413K&client_secret=X20DAZW4CXKKC2V1O4QXYYHEQ1T5BMIBHUYD5ZJOVUKGFD3V&v=20140728";

		HttpGet request = new HttpGet(url);
		HttpResponse response = null;
		
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String responseString = null;
		
		try {
			responseString = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(responseString);
		
		JSONObject json = new JSONObject(responseString);
		
		JSONArray venues = json.getJSONObject("response").getJSONArray("venues");
		
		for(int i = 0; i<venues.length(); i++) {
			Category c = new Category();
			
			if(venues.getJSONObject(i).getJSONArray("categories").length() <= 0)
				continue;
			
			c.category = venues.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name");
			c.distance = venues.getJSONObject(i).getJSONObject("location").getInt("distance");
			
			JDBC jdbc = new JDBC();
			
		//	System.out.println(c.category);
			
			String sql = "select * from categories where name = '" + c.category + "'";
			
			if(c.category.contains("'"))
				continue;
			
			System.out.println(sql);
			
			ResultSet rs = jdbc.query(sql);
			
			while(rs.next()){
				c.parent = rs.getString("parent");		
			}
			categories.add(c);
		}	
		
		Collections.sort(categories, new CateComparable());
		
//		for(int i = 0; i<categories.size(); i++) {
//			System.out.println("cate = " + categories.get(i).category + ", parent = " + categories.get(i).parent +", distance = " + categories.get(i).distance);
//		}
		
		JDBC jdbc = new JDBC();
		
		String sql = "insert into same values(" + id + "," + p.getLat() + "," + p.getLng() + "," + 0 + ",'" + categories.get(0).category + "," + categories.get(1).category + "')";
		
		int OK = jdbc.insertQuery(sql);
		
	}
}
