package com.linkedin.searchperf.mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.sensei.search.client.json.JsonSerializer;
import com.sensei.search.client.json.SenseiServiceProxy;
import com.sensei.search.client.json.req.Facet;
import com.sensei.search.client.json.req.Operator;
import com.sensei.search.client.json.req.Selection;
import com.sensei.search.client.json.req.SenseiClientRequest;
import com.sensei.search.client.json.req.Sort;
import com.sensei.search.client.json.req.query.Queries;
import com.sensei.search.client.json.res.SenseiResult;

public class PerfEval
{

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws Exception
  {
    // creat the table called car_record
    /*
          CREATE TABLE car_record(
              id INT not null AUTO_INCREMENT,
              category VARCHAR(100),
              city VARCHAR(100),
              mileage INT,
              tags VARCHAR(100),
              color VARCHAR(100),
              price FLOAT,
              groupid LONG,
              makemodel VARCHAR(100),
              year INT,
              contents TEXT,
              PRIMARY KEY (id)
          )
          
          CREATE TABLE car(
              id INT not null AUTO_INCREMENT,
              category VARCHAR(30),
              city VARCHAR(255),
              mileage INT,
              tags VARCHAR(255),
              color VARCHAR(15),
              price FLOAT,
              groupid LONG,
              makemodel VARCHAR(255),
              year INT,
              contents VARCHAR(255),
              PRIMARY KEY (id),
              fulltext (contents)
          )
     * 
     * */


//    insertData("data/cars3m.json");

    /*
     * 
     * CREATE INDEX index_color ON car (color);
     * 
     * CREATE INDEX index_price ON car (price);
     * 
     * */
    //done with inserting. Time spent:1469924
    // Sensei indexing time : sensei indexing time: 449413
    
  
    // MySQL query performance testing:
    MySQLQueryTest();
    SenseiQueryTest();
    
    
    
  }

  private static void SenseiQueryTest() throws JSONException
  {
    SenseiServiceProxy proxy = new SenseiServiceProxy("localhost", 8080);
    
    
    SenseiClientRequest senseiRequest = SenseiClientRequest.builder()
//        .addFacet("color", Facet.builder().minHit(1).expand(true).orderByHits().max(10).build())
        //.addFacet("price", Facet.builder().minHit(1).expand(false).orderByHits().max(10).build())
//        .addFacet("year", Facet.builder().minHit(1).expand(true).orderByVal().max(10).build())
//        .query(Queries.stringQuery("cool"))
//        .addSelection(Selection.terms("tags", Arrays.asList("cool", "hybrid"), Arrays.asList("favorite"), Operator.and))
        .addSelection(Selection.terms("color", Arrays.asList("red"), new ArrayList<String>(), Operator.or))
        .paging(10, 0)
        .fetchStored(false)
//        .addSort(Sort.desc("price"))
    .build();
    JSONObject serialized = (JSONObject) JsonSerializer.serialize(senseiRequest);
    System.out.println(serialized.toString(2));
    String req = serialized.toString();
    long start = System.currentTimeMillis();
    String senseiResult = proxy.sendPostRaw(proxy.getSearchUrl(),req);
    long end = System.currentTimeMillis();
    System.out.println("time used *********: "+ (end- start));
    
    System.out.println(senseiResult);
    
    
  }

  private static void MySQLQueryTest() throws SQLException
  {
    SQLEngine sql = new SQLEngine();
    
    long start = System.currentTimeMillis();
    ResultSet rs = sql.select("*", "car", " color='red'", null, "10");
    while(rs.next()){
      int id = rs.getInt("id");
      String sentence = rs.getString("contents");
    }
    rs.close();
    
    long end = System.currentTimeMillis();
    System.out.println("time used: "+ (end- start));
    
    sql.cleanUp();
  }

  private static void insertData(String filename) throws IOException, JSONException
  {
    long start = System.currentTimeMillis();
    File carfile = new File(filename);
    BufferedReader br = new BufferedReader(new FileReader(carfile));
    String line = br.readLine();
    SQLEngine sql = new SQLEngine();
    while(line != null){
      JSONObject json = new JSONObject(line.trim());
      InsertJSON(json, sql);
      line = br.readLine();
    }
    sql.cleanUp();
    br.close();
    long end = System.currentTimeMillis();
    System.out.println("done with inserting. Time spent:" + (end-start));
  }

  private static void InsertJSON(JSONObject json, SQLEngine sql) throws JSONException
  {
    String category = json.getString("category");
    String city = json.getString("city");
    int mileage = json.getInt("mileage");
    String tags = json.getString("tags");
    String color = json.getString("color");
    float price = (float)json.getDouble("price");
    long groupid = json.getLong("groupid");
    String makemodel = json.getString("makemodel");
    int year = json.getInt("year");
    String contents = json.getString("contents");
    
    
    String columns = " category, city, mileage, tags, color,  price,  groupid, makemodel, year, contents ";
    String values = "'"+category + "','" +  city + "'," + mileage + ", '" + tags +"','" + color + "',"+ price + ","+groupid + ",'"+makemodel +"',"+year+",'"+contents+"'";
//    * @param columns - Must be in format: String columns = "column1, column2, ..., columnN"
//    * @param values - Must be in format: String values = "'string_val_1', double_val_1, ..., value_n"
    
    sql.insert("car", columns, values);
  }

}
