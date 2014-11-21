package com.ckenken.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class JDBC {
	public Connection con;
	
	public JDBC()
	{
		try {
			con = DriverManager.getConnection( "jdbc:mysql://localhost:8889/han", "root","root");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public ResultSet query(String sql) {
		
		ResultSet rs = null;
		Statement st = null;
		
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}

	public int insertQuery(String sql) {
		
		int OK = 0;
		Statement st = null;
		
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			OK = st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return OK;
	}

	
	public ResultSet select(PreparedStatement ps) {
		
		return null;
	}
	
	public static void main(String [] args) throws SQLException {
		
//		Connection con = null; 
	  //  con = DriverManager.getConnection( "jdbc:mysql://localhost:8889/han", "root","root"); 
		
	//    Statement stat = null; 
	    	    
//	    stat = con.createStatement();
//	    stat.execute("insert into raw values(0, 120, 50, 0, 1, '2014-11-14', 'category')");
//	    
//	    PreparedStatement pst = null;
//	    
//	    pst = con.prepareStatement("insert into raw values(1, ?, ?, 0, 1, ?, 'category')");
//	    
//	    pst.setString(1, "122");
//
//	    pst.setString(2, "67");
//
//	    pst.setString(3, "2014-11-12");
//	    		
//	    pst.executeUpdate();

	    JDBC jdbc = new JDBC();
	    
	    String sql = "select * from categories where name = 'Town'";
	    
	    System.out.println(sql);
	    
	    ResultSet rs = jdbc.query(sql);
	    
	    while(rs.next()) 
	    {
	    	System.out.println(rs.getString("parent"));
	    }
	    
	}	
	
}
