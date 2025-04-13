package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ByteSize implements Token {
  private final long bytes;

  public ByteSize(String value) {
    String val = value.trim().toUpperCase();
    if (val.endsWith("KB")) {
      bytes = (long) (Double.parseDouble(val.replace("KB", "")) * 1024);
    } else if (val.endsWith("MB")) {
      bytes = (long) (Double.parseDouble(val.replace("MB", "")) * 1024 * 1024);
    } else if (val.endsWith("GB")) {
      bytes = (long) (Double.parseDouble(val.replace("GB", "")) * 1024 * 1024 * 1024);
    } else if (val.endsWith("B")) {
      bytes = Long.parseLong(val.replace("B", ""));
    } else {
      throw new IllegalArgumentException("Unknown or missing byte unit in: " + value);
    }
  }

  public long getBytes() {
    return bytes;
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE; // Make sure this exists in your enum
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(bytes);
  }

  @Override
  public String toString() {
    return bytes + " bytes";
  }
}
