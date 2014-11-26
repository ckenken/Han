package com.ckenken.storage;

import java.util.Date;

public class NewPoint {
	private Date time;
	private double lat;
	private double lng;
	private String cate;
	
	@SuppressWarnings("deprecation")
	public NewPoint(String inputTime, String inputLat, String inputLng)
	{
		// inputTime = 2014-10-13T07:01:34.748-07:00
		String date = inputTime.substring(0, 19);
		String [] SP = date.split("T");
		// SP[0] = date, SP[1] = time
		
		String [] SP2 = SP[0].split("-");
		// SP2[0] = year, SP2[1] = month, SP2[2] = day
		
		String [] SP3 = SP[1].split(":");
		//SP3[0] = hour, SP3[1] = min, SP3[2] = sec
		
	//	System.out.println(SP[1]);
		
		// Date(int year, int month, int date, int hrs, int min, int sec)
		int year = Integer.parseInt(SP2[0]);
		int month = Integer.parseInt(SP2[1]);
		int day = Integer.parseInt(SP2[2]);
		int hour = Integer.parseInt(SP3[0]);
		int min = Integer.parseInt(SP3[1]);
		int sec = Integer.parseInt(SP3[2]);
		
		time = new Date(year, month, day, hour, min, sec);
		lat = Double.parseDouble(inputLat);
		lng = Double.parseDouble(inputLng);
	}
	
	public NewPoint(Date d, double inputLat, double inputLng)
	{
		time = d;
		lat = inputLat;
		lng = inputLng;
	}
	
	public NewPoint(Date d, double inputLat, double inputLng, String inputCate)
	{
		time = d;
		lat = inputLat;
		lng = inputLng;
		cate = inputCate;
	}
	
	public Date getTime()
	{
		return time;
	}
	public double getLat()
	{
		return lat;
	}
	public double getLng()
	{
		return lng;
	}
	public String getCate()
	{
		return cate;
	}
	
	public void setTime(Date d)
	{
		time = d;
	}
}
