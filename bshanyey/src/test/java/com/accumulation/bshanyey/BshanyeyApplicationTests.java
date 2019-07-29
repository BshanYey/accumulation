package com.accumulation.bshanyey;

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
    }

    @Test
    public void testFile(){

        // 将utf-8 bom编码的文件 复制为一个utf-8无bom编码的文件 内容不变
        try {
            InputStream in0 = new BufferedInputStream(new FileInputStream("C:\\Users\\coder\\Desktop\\temp\\t1.java"));
            InputStream in1 = new BufferedInputStream(new FileInputStream("C:\\Users\\coder\\Desktop\\temp\\t2.java"));
            OutputStream out = new BufferedOutputStream(new FileOutputStream("C:\\Users\\coder\\Desktop\\temp\\t3.java"));
            OutputStreamWriter o1 = new OutputStreamWriter(new FileOutputStream("C:\\Users\\coder\\Desktop\\temp\\t3.java"));

            byte[] content0 = new byte[1024];
            boolean flag = true;
            while (true){
                int len = in0.read(content0);
                if(len == -1){
                    break;
                }
                String con;
                // 去bom
                if(flag && content0[0] == -17 && content0[1] == -69 && content0[2] == -65){
                    flag = false;
                    con = new String(content0, 3, len - 3);
                } else {
                    con = new String(content0, 0, len);
                }

                // 写出
//                out.write(con);
                o1.write(new String(con));
                o1.flush();
                System.out.println();
            }


//            OutputStreamWriter ow = new OutputStreamWriter(out, "UTF-8");
//            ow.write(new String());
//            ow.flush();
            System.out.println();
            in0.close();
            in1.close();
//            ow.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
