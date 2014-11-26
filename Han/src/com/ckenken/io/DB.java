package com.ckenken.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lab.adsl.object.Point;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.ckenken.storage.KML;
import com.ckenken.storage.NewPoint;
import com.ckenken.storage.Category;

@SuppressWarnings("deprecation")
public class DB {
	
	public static ArrayList<Category> categories = new ArrayList<Category>(); 
	
	public static void main(String [] args) throws IOException, NumberFormatException, JSONException, SQLException
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
		System.out.println(whens.get(0).text().substring(0, 19));
		//System.out.println(coords.get(0).text());
		
		Map<Long, Point> result = new HashMap<Long, Point>();
		
		System.out.println(whens.size());
		
		for(int i = 16822; i<whens.size(); i++) { //whens.size();
			
			String [] SP = coords.get(i).text().split(" ");
			
			String lng = SP[0];
			String lat = SP[1];
	
			NewPoint newtemp = new NewPoint(whens.get(i).text().substring(0,19), lat, lng);
			//System.out.println(newtemp.getLat());
			Point temp = new Point((long)i, newtemp);
			
			insertSQL(temp, whens.get(i).text().substring(0,19));
			
//			break;
			
			//kml.points.add(temp);
			//result.put((long)i, temp);
		}
		
		//System.out.println(whens.size());
		
	}
	
	public static void insertSQL(Point p, String time) throws JSONException, SQLException {
		
		@SuppressWarnings({ "resource" })
		DefaultHttpClient client = new DefaultHttpClient();
		
		System.out.println(p.lat + "," + p.lng);
		
		String url = "https://api.foursquare.com/v2/venues/search?limit=30&ll=" + p.lat + "," + p.lng + "&client_id=BXTCY4HGTLWINDPRLFXCOWRUEDAJC12ZHEGDTGX4A5DX413K&client_secret=X20DAZW4CXKKC2V1O4QXYYHEQ1T5BMIBHUYD5ZJOVUKGFD3V&v=20140728";

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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		String sql = "insert into raw2 values(" + (int)p.id + "," + p.lat + "," + p.lng + "," + 0 + "," + 0 + ",'" + time +"','" + categories.get(0).category + "')";
		
		int OK = jdbc.insertQuery(sql);
		
	}
}
