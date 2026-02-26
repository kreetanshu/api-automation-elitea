package com.openapistore.utils;

public final class RandomDataUtil {

  private RandomDataUtil() {}

  public static String repeat(String value, int times) {
    return value.repeat(Math.max(0, times));
  }
}
