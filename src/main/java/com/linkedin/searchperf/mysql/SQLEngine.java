package com.linkedin.searchperf.mysql;

import java.sql.*;
import java.util.HashMap;


public class SQLEngine  {

	public static final String CURRENT_DATABASE = "jdbc:mysql://localhost/cars";
	
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "root";
	public static final String PASSWORD = "12345";
	
	
	private Connection conn = null;
	private Statement readStmt = null;
	private Statement writeStmt = null;
	
	public static void main(String[] args) {
		SQLEngine test = new SQLEngine();
	}
	
	public SQLEngine(String db) {
		try {
			//System.out.println("Connecting to database...");
			conn = this.openConnection(db, USER, PASSWORD);
			readStmt = conn.createStatement(); 
			writeStmt = conn.createStatement(); 
		} 
		catch (Exception e) { 
			System.out.println("Connection failed"); 
		}
	}
	
	// By default, loads the case study 1 database
	public SQLEngine() {
		try {
			//System.out.println("Connecting to database...");
			conn = this.openConnection(SQLEngine.CURRENT_DATABASE, USER, PASSWORD);
			readStmt = conn.createStatement(); 
			writeStmt = conn.createStatement(); 
		} 
		catch (Exception e) { 
			System.out.println("Connection failed"); 
		}
	}
	
	public Connection getConn(){
		return conn;
	}
	
	public Connection openConnection(String db, String user, 
			String password) {
		try {
			Class.forName(DRIVER);
			Connection conn = DriverManager.getConnection(db, 
					user, password);
			conn.setAutoCommit(false);
			return conn;
		} 
		catch (Exception e) { 
			cleanUp();
			e.printStackTrace();
			System.out.println("Connection failed"); 
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {}
			return openConnection(db, user, password);
		}
	}
	
	public ResultSet getGeneratedKeys() throws SQLException {
		return writeStmt.getGeneratedKeys();
	}
	
	/**
	 * 
	 * @param table
	 * @param columns - Must be in format: String columns = "column1, column2, ..., columnN"
	 * @param values - Must be in format: String values = "'string_val_1', double_val_1, ..., value_n"
	 */
	public boolean insert(String table, String columns, String values) {
		String insert = "";
		try {
			insert = "insert into " + table + " (" + columns + ") values (" + values + ")"; 
			
			//System.out.println(insert);
			writeStmt.executeUpdate(insert);
			return true;
		}
		catch (Exception e) {
//			System.out.println("Skipping duplicate: " + values);
//			System.out.println(insert);
			e.printStackTrace();
			System.out.print("*");
			return false;
		}
	}
	
	/**
	 * 
	 * @param table
	 * @param columns - Must be in format: String columns = "column1, column2, ..., columnN"
	 * @param values - Must be in format: String values = "'string_val_1', double_val_1, ..., value_n"
	 */
	public void replace(String table, String columns, String values, String dummyCol) {
		String insert = "";
		try {
			insert = "insert into " + table + " (" + columns + ") values (" + values + ") " +
					"on duplicate key update " + dummyCol + " = " + dummyCol; 
			
			//System.out.println(insert);
			writeStmt.executeUpdate(insert);
		}
		catch (Exception e) {
			System.out.println(insert);
			e.printStackTrace();
		}
	}
	
	public String[] getSingleTerms(HashMap idfs) {
		int size = 96218;
		String[] terms = new String[size];
		String term = "";
		int numInserted = 0;
		try {
			ResultSet rs = this.select("term, idf", "cutoff_6_terms", "");
			while (rs.next()) {
				term = rs.getString("term");
				terms[numInserted++] = term;
				
				idfs.put(term, new Double(rs.getDouble("idf")));
			}
			System.out.println(numInserted);
		} catch (Exception e) { 
			System.out.println(term + ", numInserted = " + numInserted);
			e.printStackTrace(); 
		}
		
		return terms;
	}
	
	public String[] getEnumeratedTerms(HashMap idfs) {
		int size = 15020801;
		String[] terms = new String[size];
		int numInserted = 0;
		String term = "";
		int tf = 0;
		try {
			ResultSet rs = this.select("term, tf, idf", "cutoff_6_terms", "");
			while (rs.next()) {
				term = rs.getString("term");
				tf = rs.getInt("tf");
				for(int i = 0; i < tf; i++)
					terms[i + numInserted] = term;
				numInserted += tf;
				
				idfs.put(term, new Double(rs.getDouble("idf")));
			}
			System.out.println(numInserted);
		} catch (Exception e) { 
			System.out.println(term + ", tf = " + tf + ", numInserted = " + numInserted);
			e.printStackTrace(); 
		}
		
		return terms;
	}
	
	public String getWeightedIdfRandomTerm(HashMap termIndex) {
		int index = (int)(5334861.0 * Math.random()) + 1;
		if (termIndex.containsKey(new Integer(index))) {
			return (String)termIndex.get(new Integer(index));
		}
		
		ResultSet rs = select("term", "cutoff_6_term_instances", "idx = " + index);
		String term = "";
		try {
			if (rs.next())
				term = rs.getString("term");
		} catch (Exception e) { e.printStackTrace(); }
		
		termIndex.put(new Integer(index), term);
		return term;
	}

	/**
	 * 
	 * @param table
	 * @param set - Should be in format: String set = "colName='stringVal', colName2=intVal, ..."
	 * @param where
	 */
	public void update(String table, String set, String where) {
		String update = "";
		try {
			update = "update " + table + " set " + set;
			if (where != null && where.length() > 0)
				 update += " where " + where;
			
			writeStmt.executeUpdate(update);
		}
		catch (Exception e) {
			System.out.println(update);
			e.printStackTrace();
		}
	}
	
	public static String escape(String input) {
//		if (input.length() < 1)
//			return "";
//		String result = "";
//		String[] split = input.split("'");
//		try {
//			result = split[0];
//		}
//		catch (Exception e) {
//			System.out.println("Trying to split: <<" + input + ">>");
//			return "\\'";
//		} 
//		for(int i = 1; i < split.length; i++)
//			result += "\\'" + split[i];
//		return result;
		return input.replaceAll("'", "''");
	}
	
	public void insert(String table, String[] columns, String[] values) {
		try {
			String insert = "insert into " + table;
			if (columns.length > 0) {
				insert += "(";
				for(int i = 0; i < columns.length; i++)
					insert += columns[i] + ", ";
				if (insert.length() >= 2)
					insert = insert.substring(0, insert.length() - 2);
				insert += ")";
			}
			insert += " values ("; 
			if (values.length > 0) {
				for(int i = 0; i < values.length; i++) {
					String val = values[i];
					boolean isNumber = true;
					try {
						Double.parseDouble(val);
					}
					catch (NumberFormatException nfe) { 
						isNumber = false;
					}
					if (isNumber)
						insert += val + ", ";
					else
						insert += "'" + val + "', ";
				}
				if (insert.length() >= 2)
					insert = insert.substring(0, insert.length() - 2);
				insert += ")";
			}
			System.out.println(insert);
			writeStmt.executeUpdate(insert);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getDate(Timestamp d) {
		return d.getDay() + "-" + d.getMonth() + "-" + (1900 + d.getYear());
	}
	
	public void printResultSet(ResultSet rs) {
		try {
			int count = 1;
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 1; i <= rsmd.getColumnCount(); i++)
				System.out.print(rsmd.getColumnName(i) + "\t");
			
			System.out.println();
			while(rs.next()) {
				for(int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object obj = rs.getObject(i);
//					if (obj.getClass().getCanonicalName().equals(
//							"java.sql.Timestamp"))
//						System.out.print(getDate(((Timestamp)obj)) + "\t");
//					else
						System.out.print(obj + "\t");
				}
				System.out.println();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet select(String columns, String table, String condition) {
		try {
			String select = "select " + columns + " from " + table;
			
			if (condition != null && condition.length() > 0)
				select += " where " + condition;
//			System.out.println(select);
			return readStmt.executeQuery(select);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ResultSet selectLimit(String columns, String table, String condition, int start, int step) {
		try {
			String select = "select " + columns + " from " + table;
			
			if (condition != null && condition.length() > 0)
				select += " where " + condition;
			
			select += " limit " + start + " , " + step;
//			System.out.println(select);
			return readStmt.executeQuery(select);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ResultSet select(String query) {
		try {
		
			if (query != null && query.length() > 0)
				return readStmt.executeQuery(query);
			else
				return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void delete(String table, String condition) {
		try {
			String delete = "delete from " + table;
			
			if (condition != null && condition.length() > 0)
				delete += " where " + condition;
			
			writeStmt.executeUpdate(delete);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet select(String columns, String table, String condition, String orderBy, String limit) {
		try {
			String select = "select " + columns + " from " + table;
			
			if (condition != null && condition.length() > 0)
				select += " where " + condition;
			
			if (orderBy != null && orderBy.length() > 0)
				select += " order by " + orderBy;
			
			if (limit != null && limit.length() > 0)
				select += " limit " + limit;
			
			return readStmt.executeQuery(select);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ResultSet select(String columns, String table, String condition, String orderBy) {
		String select = "";
		try {
			select = "select " + columns + " from " + table;
			
			if (condition != null && condition.length() > 0)
				select += " where " + condition;
//			System.out.println(select);
			select += " order by " + orderBy;
			
			return readStmt.executeQuery(select);
		}
		catch (Exception e) {
			System.out.println(select);
			e.printStackTrace();
			return null;
		}
	}
	
	public void commit() {
		try {
			if (conn != null)
				conn.commit();
		} 
		catch (Exception e) { 
			System.out.println("Could not commit"); 
		}
	}
	
	public void closeStatements() {
		try {
			if (readStmt != null)
				readStmt.close();
			
			readStmt = null;
			if (writeStmt != null)
				writeStmt.close();
			
			writeStmt = null;
		} 
		catch (Exception e) { 
			System.out.println("Could not close statement"); 
		}
	}
	
	public void cleanUp() {
		try {
			closeStatements();
			if (conn != null)
				conn.close();
		} 
		catch (Exception e) { 
			System.out.println("Could not close connection"); 
		}
	}
}
