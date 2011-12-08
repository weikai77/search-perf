package com.linkedin.searchperf.es;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.linkedin.searchperf.common.IndexLoader;

public class ElasticSearchIndexLoader implements IndexLoader {

  private final File _dataFile;
  private final String _esHost;
  private final int _esPort;
  ElasticSearchIndexLoader(File dataFile,String esHost,int esPort){
    _dataFile = dataFile;
    _esHost = esHost;
    _esPort = esPort;
  }
  
  @Override
  public void loadData() throws Exception {
    BlockingQueue<JSONObject> queue = new LinkedBlockingQueue<JSONObject>(2000000);
    List<Worker> workers = new ArrayList<Worker>();
    for (int i=0; i<3; i++)
    {
      Worker worker = new Worker(queue);
      worker.start();
      workers.add(worker);
    }
    
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_dataFile),UTF8));
    try
    {
      String line = null;
      while ((line = br.readLine()) != null)
      {
        JSONObject json = (JSONObject) JSONValue.parse(line);
        queue.put(json);
      }
    }
    finally
    {
      br.close();
    }
    
    for (Worker w : workers)
      w.join();
  }

  private class Worker extends Thread
  {
    final BlockingQueue<JSONObject> _queue;
    
    public Worker(BlockingQueue<JSONObject> queue)
    {
      this._queue = queue;
    }
    
    @Override
    public void run()
    {
      Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(ElasticSearchIndexLoader.this._esHost, ElasticSearchIndexLoader.this._esPort));
      try
      {
        JSONObject json = null;
        while ((json = _queue.take()) != null)
        {
          IndexRequestBuilder req = client.prepareIndex("perf", "views");
          XContentBuilder xcb = jsonBuilder().startObject();
  
          for (Entry<String,Object> entry : (Set<Entry<String,Object>>) json.entrySet())
          {
            if (entry.getKey().equals("job_functions"))
            {
              JSONArray functions = new JSONArray();
              functions.addAll(Arrays.asList(((String)entry.getValue()).split(",")));
              xcb.field("job_function", functions);
            }
            else
            {
              xcb.field(entry.getKey(), entry.getValue());
            }
          }
          
          xcb.endObject();
          req.setSource(xcb).execute();
        }
      }
      catch (InterruptedException ex)
      {
        System.out.println("Interrupted, exiting...");
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      finally
      {
        client.close();
      }
    }
  }
}
