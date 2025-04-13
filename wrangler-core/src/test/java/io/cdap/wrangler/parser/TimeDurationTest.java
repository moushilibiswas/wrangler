package io.cdap.wrangler.parser;

import org.junit.Test;

import io.cdap.wrangler.api.parser.TimeDuration;

import static org.junit.Assert.*;

public class TimeDurationTest {

    @Test
    public void testParseTimeDuration() {
        TimeDuration durationMs = new TimeDuration("500ms");
        assertEquals(500, durationMs.getMilliseconds());  // 500ms = 500 milliseconds
        
        TimeDuration durationSec = new TimeDuration("2.5s");
        assertEquals(2.5 * 1000, durationSec.getMilliseconds(), 0.001);  // 2.5s = 2500 milliseconds
        
        TimeDuration durationMin = new TimeDuration("3m");
        assertEquals(3 * 60 * 1000, durationMin.getMilliseconds());  // 3 minutes = 180000 milliseconds

        TimeDuration durationHours = new TimeDuration("1h");
        assertEquals(1 * 60 * 60 * 1000, durationHours.getMilliseconds());  // 1 hour = 3600000 milliseconds
    }
}
