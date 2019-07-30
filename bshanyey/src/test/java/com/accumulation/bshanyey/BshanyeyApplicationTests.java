package com.accumulation.bshanyey;

import com.accumulation.bshanyey.utils.UTF8Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BshanyeyApplicationTests {

    @Test
    public void contextLoads() {
        UTF8Utils.conversion("C:\\Users\\coder\\Desktop\\temp\\test1", "C:\\Users\\coder\\Desktop\\temp\\test2");
    }

}
