package com.linkedin.searchperf.common.query;

import java.io.BufferedInputStream;
import java.io.InputStream;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.linkedin.searchperf.sensei.query.SenseiQueryProducer;
import com.sensei.search.client.json.JsonSerializer;
import com.sensei.search.client.json.req.Selection.Path;
import com.sensei.search.client.json.req.Selection.Range;
import com.sensei.search.client.json.req.SenseiClientRequest;
import com.sensei.search.client.json.req.Terms;

public class QueryProducerTest extends Assert {
  private static InputStream dataInput;
  private static InputStream schemaContent;
  private static SenseiQueryProducer queryProducer;

  @BeforeClass
  public static void before() throws Exception {

    dataInput = new BufferedInputStream(QueryProducerTest.class.getClassLoader().getResourceAsStream("cars.json"));
    schemaContent = QueryProducerTest.class.getClassLoader().getResourceAsStream("sensei/schema.xml");
    queryProducer = new SenseiQueryProducer();
    queryProducer.init(schemaContent, dataInput);
  }
  @Test
  public void test1QueryGeneration() throws Exception {
      SenseiClientRequest query = queryProducer.createQuery(false, 1, 1, 1);
      System.out.println(((JSONObject)JsonSerializer.serialize(query)).toString(2));
      assertEquals(3, query.getSelections().size());
      assertTrue(query.getSelections().get(0) instanceof Terms);
      assertTrue(query.getSelections().get(1) instanceof Range);
      assertTrue(query.getSelections().get(2) instanceof Path);
  }
  @Test
  public void test2QueryGenerationTerms() throws Exception {
      SenseiClientRequest query = queryProducer.createQuery(false, 3, 0, 0);
      //System.out.println(((JSONObject)JsonSerializer.serialize(query)).toString(2));
      assertEquals(3, query.getSelections().size());
      assertTrue(query.getSelections().get(0) instanceof Terms);
      assertTrue(query.getSelections().get(1) instanceof Terms);
      assertTrue(query.getSelections().get(2) instanceof Terms);
      query = queryProducer.createQuery(false, 0, 3, 0);
      //System.out.println(((JSONObject)JsonSerializer.serialize(query)).toString(2));
      assertEquals(3, query.getSelections().size());
      assertTrue(query.getSelections().get(0) instanceof Range);
      assertTrue(query.getSelections().get(1) instanceof Range);
      assertTrue(query.getSelections().get(2) instanceof Range);
      query = queryProducer.createQuery(false, 0, 0, 3);
      //System.out.println(((JSONObject)JsonSerializer.serialize(query)).toString(2));
      assertEquals(2, query.getSelections().size());
      assertTrue(query.getSelections().get(0) instanceof Path);
      assertTrue(query.getSelections().get(1) instanceof Path);
  }
  @Test
  public void test3QueryGenerationFacets() throws Exception {
      SenseiClientRequest query = queryProducer.createQuery(true, 3, 1, 1);
      assertEquals(5, query.getFacets().size());
  }
}
