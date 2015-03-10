package com.onplan.benchmark.service;

public class StrategyPoolBenchmark {
//  @Benchmark
  public void testProcessPriceTick() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("End of benchmark!");
  }
}
