package com.linkedin.searchperf.common.query;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.linkedin.searchperf.sensei.query.SchemaMetadata;
import com.linkedin.searchperf.sensei.query.SchemaParser;

public class SchemaParserTest extends Assert {
  private SchemaParser schemaParser;
  private InputStream schemaContent;
  @Before
  public void before() throws Exception {
    schemaParser = new SchemaParser();
    schemaContent = getClass().getClassLoader().getResourceAsStream("sensei/schema.xml");
  }
  @Test
  public void test1Parse() throws Exception {
    SchemaMetadata queryMetadata = schemaParser.parse(schemaContent);
    assertEquals(4, queryMetadata.getFacetsPerType().size());
    assertEquals(3, queryMetadata.getRangeFacetsWithRanges().size());
  }

}
