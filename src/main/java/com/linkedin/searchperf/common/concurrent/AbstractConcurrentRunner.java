package com.linkedin.searchperf.common.concurrent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.TimerMetric;

public abstract class AbstractConcurrentRunner <T> {
    protected static Log LOG = LogFactory.getLog(AbstractConcurrentRunner.class);
    private static Log QUERY_LOG = LogFactory.getLog("QueryStats");
    public final TimerMetric responseTimeMetric = Metrics.newTimer(getClass(), "responseTimeMtric", TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS);
    private BlockingQueue<T> queue;
    private CountDownLatch startSignal = new CountDownLatch(1);
    private ExecutorService executors;
    private volatile  AtomicBoolean stop = new AtomicBoolean(false);
    private Timer timer = new Timer();
    private long testingTime;
    private int numThreads;

    public void init(int numThreads, long testingTime) {
      this.numThreads = numThreads;
      this.testingTime = testingTime;
      executors = Executors.newFixedThreadPool(numThreads);
      int capacity = numThreads * 5;
      queue = new ArrayBlockingQueue<T>(capacity);
    }

    public  void run() {
      if (executors == null) {
        throw new IllegalStateException("The concurrent runner is not initialized");
      }
      warmUp();
      for (int i = 0; i < numThreads; i++) {
        executors.execute(new SearchJob());
      }
      for (int i = 0; i < numThreads * 5 - 1; i++) {
        queue.offer(createRequest());
      }
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          stop.set(true);
        }
      }, testingTime);
      long now = System.currentTimeMillis();
      startSignal.countDown();
      while (!stop.get()) {
        //LOG.error("QueueSize = " + queue.size());
        try {
          queue.offer(createRequest(), 500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      //System.out.println("!!!" + (System.currentTimeMillis() - now));
    }

    private void warmUp() {

        sendRequest(createRequest(), QUERY_LOG, responseTimeMetric, stop);


    }

    protected abstract T createRequest();
    protected abstract void sendRequest(T request, Log QueryLog, TimerMetric responseTimeMetric, AtomicBoolean stop);
    private class SearchJob implements Runnable {

      @Override
      public void run() {
        try {
          startSignal.await();
          while(!stop.get()) {
            T request = createRequest();
            sendRequest(request, QUERY_LOG, responseTimeMetric, stop);
          }
        } catch (Exception ex) {
          LOG.error(ex.getMessage(), ex);
        }
      }
    }
    public ExecutorService getExecutors() {
      return executors;
    }

}
