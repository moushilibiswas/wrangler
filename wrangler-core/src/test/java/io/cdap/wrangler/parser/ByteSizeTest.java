package io.cdap.wrangler.parser;

import org.junit.Assert;
import org.junit.Test;

import io.cdap.wrangler.api.parser.ByteSize;

public class ByteSizeTest {

    @Test
    public void testByteSizeParsing() {
        ByteSize byteSize1 = new ByteSize("10KB");
        Assert.assertEquals(10 * 1024, byteSize1.getBytes());

        ByteSize byteSize2 = new ByteSize("1MB");
        Assert.assertEquals(1 * 1024 * 1024, byteSize2.getBytes());

        ByteSize byteSize3 = new ByteSize("1GB");
        Assert.assertEquals(1L * 1024 * 1024 * 1024, byteSize3.getBytes());

        ByteSize byteSize4 = new ByteSize("500B");
        Assert.assertEquals(500L, byteSize4.getBytes());
    }
}
