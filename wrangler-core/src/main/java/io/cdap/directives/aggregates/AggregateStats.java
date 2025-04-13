package io.cdap.directives.aggregates;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import java.util.*;

/**
 * A directive that aggregates byte size and time duration values from rows and outputs total or average values.
 */
public class AggregateStats implements Directive {
  private String byteCol;
  private String timeCol;
  private String outByteCol;
  private String outTimeCol;
  private String byteUnit = "b"; // default: bytes
  private String timeUnit = "ms"; // default: milliseconds
  private String type = "total"; // default aggregation type

  // Store to accumulate totals during execution
  private long totalBytes = 0;
  private long totalTimeMs = 0;
  private int count = 0;

  @Override
public UsageDefinition define() {
  UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
  builder.define("byte-col", TokenType.COLUMN_NAME);
  builder.define("time-col", TokenType.COLUMN_NAME);
  builder.define("out-byte-col", TokenType.COLUMN_NAME);
  builder.define("out-time-col", TokenType.COLUMN_NAME);
  builder.defineOptional("byte-unit", TokenType.TEXT);
  builder.defineOptional("time-unit", TokenType.TEXT);
  builder.defineOptional("aggregation-type", TokenType.TEXT);
  return builder.build();
}


  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    // Initialize the arguments
    this.byteCol = args.value("byte-col").value().toString();
    this.timeCol = args.value("time-col").value().toString();
    this.outByteCol = args.value("out-byte-col").value().toString();
    this.outTimeCol = args.value("out-time-col").value().toString();

    if (args.contains("byte-unit")) {
      this.byteUnit = args.value("byte-unit").value().toString().toLowerCase();
    }

    if (args.contains("time-unit")) {
      this.timeUnit = args.value("time-unit").value().toString().toLowerCase();
    }

    if (args.contains("aggregation-type")) {
      this.type = args.value("aggregation-type").value().toString().toLowerCase();
    }
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    for (Row row : rows) {
      try {
        String byteStr = row.getValue(byteCol).toString();
        String timeStr = row.getValue(timeCol).toString();

        ByteSize byteSize = new ByteSize(byteStr);
        TimeDuration duration = new TimeDuration(timeStr);

        // Add values to running totals
        totalBytes += byteSize.getBytes();
        totalTimeMs += duration.getMilliseconds();
        count++;

      } catch (Exception e) {
        throw new DirectiveExecutionException("Failed to parse row for aggregation: " + e.getMessage(), e);
      }
    }

    if (count == 0) {
      throw new DirectiveExecutionException("No valid rows to aggregate.");
    }

    // Finalize aggregation based on the type (total or average)
    if (type.equals("average")) {
      totalBytes /= count;
      totalTimeMs /= count;
    }

    // Convert units as required
    double convertedBytes = convertBytes(totalBytes, byteUnit);
    double convertedTime = convertTime(totalTimeMs, timeUnit);

    // Create the result Row
    List<Row> result = new ArrayList<>();
    Row output = new Row();
    output.add(outByteCol, convertedBytes + " " + byteUnit);
    output.add(outTimeCol, convertedTime + " " + timeUnit);
    result.add(output);
    return result;
  }

  private double convertBytes(long bytes, String unit) {
    switch (unit) {
      case "kb": return bytes / 1024.0;
      case "mb": return bytes / (1024.0 * 1024);
      case "gb": return bytes / (1024.0 * 1024 * 1024);
      case "b":
      default: return (double) bytes;
    }
  }

  private double convertTime(long millis, String unit) {
    switch (unit) {
      case "seconds": return millis / 1000.0;
      case "minutes": return millis / 60000.0;
      case "ms":
      default: return (double) millis;
    }
  }

  @Override
  public void destroy() {
    // Clean up resources if necessary
  }
}
