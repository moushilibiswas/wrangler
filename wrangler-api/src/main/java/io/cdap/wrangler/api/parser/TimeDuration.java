package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * TimeDuration token implementation to parse values like 150ms, 2s, etc.
 */
public class TimeDuration implements Token {
  private final String original;
  private final long millis;

  public TimeDuration(String value) {
    this.original = value;
    this.millis = parseMilliseconds(value.toLowerCase());
  }

  private long parseMilliseconds(String value) {
    if (value.endsWith("ms")) {
      return (long) Double.parseDouble(value.replace("ms", ""));
    } else if (value.endsWith("s")) {
      return (long) (Double.parseDouble(value.replace("s", "")) * 1000);
    } else if (value.endsWith("m")) {
      return (long) (Double.parseDouble(value.replace("m", "")) * 60 * 1000);
    } else if (value.endsWith("h")) {
      return (long) (Double.parseDouble(value.replace("h", "")) * 60 * 60 * 1000);
    } else {
      return Long.parseLong(value); // Assume milliseconds
    }
  }

  public long getMilliseconds() {
    return this.millis;
  }

  @Override
  public Object value() {
    return original;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(original);
  }
}
