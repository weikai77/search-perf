package com.linkedin.searchperf.common.concurrent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkedin.searchperf.common.query.QueryProducer;
import com.sensei.search.client.json.JsonSerializer;
import com.sensei.search.client.json.SenseiServiceProxy;
import com.sensei.search.client.json.req.SenseiClientRequest;
import com.sensei.search.client.json.res.SenseiResult;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.core.TimerMetric;

public class SenseiConcurrentRunner {
    private static Log LOG = LogFactory.getLog(SenseiConcurrentRunner.class);
    private static Log QueryLog = LogFactory.getLog("QueryStats");
    public final TimerMetric responseTimeMetric = Metrics.newTimer(SenseiConcurrentRunner.class, "responseTimeMtric", TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS);
    private BlockingQueue<SenseiClientRequest> queue;
    private CountDownLatch startSignal = new CountDownLatch(1);
    private ExecutorService executors;
    private volatile boolean stop = false;
    private SenseiServiceProxy senseiServiceProxy;
    private Timer timer = new Timer();

    public  void run(SenseiRunnerConfig config, final SenseiServiceProxy senseiServiceProxy) {
      this.senseiServiceProxy = senseiServiceProxy;
      int capacity = config.getNumThreads() * 5;
      queue = new LinkedBlockingQueue<SenseiClientRequest>(capacity);
      executors = Executors.newFixedThreadPool(config.getNumThreads());
      QueryProducer queryProducer = new QueryProducer();
      queryProducer.init(SenseiRunnerConfig.getResource(config.getSchemaPath()), SenseiRunnerConfig.getResource(config.getDataFilePath()));
      for (int i = 0; i < config.getNumThreads(); i++) {
        executors.submit(new SearchJob());
      }
      for (int i = 0; i < capacity - 1; i++) {
        queue.offer(queryProducer.createQuery(config.isIncludeFacets(), config.getSimpleSelections(), config.getRangeSelections(), config.getPathSelections()));
      }
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          stop = true;
        }
      }, config.getTestingTime());
      startSignal.countDown();
      while (!stop) {
        queue.offer(queryProducer.createQuery(config.isIncludeFacets(), config.getSimpleSelections(), config.getRangeSelections(), config.getPathSelections()));
      }
    }

    private class SearchJob implements Runnable {

      @Override
      public void run() {
        try {
          startSignal.await();
          while(!stop) {
            SenseiClientRequest request = queue.poll();
            long now = System.currentTimeMillis();
            TimerContext time = responseTimeMetric.time();
            SenseiResult senseiResult = senseiServiceProxy.sendSearchRequest(request);
            time.stop();
            if (QueryLog.isInfoEnabled()) {
              QueryLog.info("time=[" + (System.currentTimeMillis() - now) + "], hits=[" + senseiResult.getNumhits() + "], query=" + JsonSerializer.serialize(request).toString() + "]");
            }

          }
        } catch (Exception ex) {
          LOG.error(ex.getMessage(), ex);
        }
      }
    }
}
