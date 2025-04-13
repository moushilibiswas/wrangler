package io.cdap.wrangler.parser;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testTotalAggregationWithUnitConversion() throws Exception {
    List<Row> rows = Arrays.asList(
      new Row("data_transfer_size", "1024KB").add("response_time", "1000ms"),
      new Row("data_transfer_size", "1MB").add("response_time", "1s"),
      new Row("data_transfer_size", "2MB").add("response_time", "2000ms")
    );

    String[] recipe = new String[]{
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec",
      "set-column :byte-unit MB",
      "set-column :time-unit seconds"
    };

    List<Row> results = TestingRig.execute(recipe, rows);

    Assert.assertEquals(1, results.size());

    Row result = results.get(0);

    // 1024KB = 1MB, total = 1MB + 1MB + 2MB = 4MB
    Assert.assertEquals(4.0, Double.parseDouble(result.getValue("total_size_mb").toString()), 0.001);

    // 1000ms = 1s, 1s = 1s, 2000ms = 2s, total = 4s
    Assert.assertEquals(4.0, Double.parseDouble(result.getValue("total_time_sec").toString()), 0.001);
  }

  @Test
  public void testAverageAggregation() throws Exception {
    List<Row> rows = Arrays.asList(
      new Row("data_transfer_size", "1MB").add("response_time", "1000ms"),
      new Row("data_transfer_size", "2MB").add("response_time", "2000ms")
    );

    String[] recipe = new String[]{
      "aggregate-stats :data_transfer_size :response_time avg_size_mb avg_time_sec",
      "set-column :byte-unit MB",
      "set-column :time-unit seconds",
      "set-column :aggregation-type average"
    };

    List<Row> results = TestingRig.execute(recipe, rows);

    Assert.assertEquals(1, results.size());

    Row result = results.get(0);

    // Average size: (1MB + 2MB) / 2 = 1.5MB
    Assert.assertEquals(1.5, Double.parseDouble(result.getValue("avg_size_mb").toString()), 0.001);

    // Average time: (1000ms + 2000ms) / 2 = 1500ms = 1.5s
    Assert.assertEquals(1.5, Double.parseDouble(result.getValue("avg_time_sec").toString()), 0.001);
  }

  // Add more tests for edge cases (empty rows, malformed input, etc.) if needed.
}
