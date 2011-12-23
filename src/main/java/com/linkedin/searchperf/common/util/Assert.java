package com.linkedin.searchperf.common.util;

public class Assert {
  public static void notNull(Object obj) {
    notNull("The object should be not null");

  }
  public static void notNull(Object obj, String message) {
    if (obj == null) {
      throw new IllegalArgumentException(message);
    }
   }
  public static void state(boolean condition) {
    state(condition, "The condition should not be false");
   }
  public static void state(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
   }
}
