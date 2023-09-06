package cn.zhaokanglun.myrpc.fastjson2;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.Selector;
import java.time.LocalDateTime;

public class FastJson2Test {
    @Test
    public void dateTest() throws IOException {
        Selector selector = Selector.open();
        LocalDateTime now = LocalDateTime.now();
        System.out.println(JSON.toJSONString(now));
    }
}
