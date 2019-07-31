package com.accumulation.bshanyey.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * UTF-8工具类
 */
public class UTF8Utils {


    /**
     * UTF-8 BOM 转 UTF-8
     * 保留原文件，新文件为UTF-8无BOM编码
     *
     * @param fromPath
     * @param toPath
     */
    public static void conversion(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            return;
        }
        if (fromFile.isFile()) {
            if (!toFile.exists()) {
                toFile.getParentFile().mkdirs();
            }
            conversionFile2File(fromFile, toFile);
            return;
        }
        if (fromFile.isDirectory()) {
            if (!toFile.exists()) {
                toFile.mkdirs();
            }
            for (File file : fromFile.listFiles()) {
                conversion(file.getPath(), toPath + "/" + file.getName());
            }
        }
    }

    /**
     * 将utf-8 bom编码的文件 复制为一个utf-8无bom编码的文件 内容不变
     *
     * @param fromFile
     * @param toFile
     */
    private static void conversionFile2File(File fromFile, File toFile) {

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(fromFile));
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(toFile));

            byte[] content0 = new byte[1024];
            boolean flag = true;
            while (true) {
                int len = in.read(content0);
                if (len == -1) {
                    break;
                }
                String con;
                // 去bom
                if (flag && content0[0] == -17 && content0[1] == -69 && content0[2] == -65) {
                    flag = false;
                    con = new String(content0, 3, len - 3);
                } else {
                    con = new String(content0, 0, len);
                }

                // 写出
                out.write(new String(con));
                out.flush();
            }


            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * UTF-8 BOM 转 UTF-8
     * 替换原文件 超大文件不适用
     *
     * @param fromPath
     */
    public static void conversion(String fromPath) {
        File fromFile = new File(fromPath);
        if (!fromFile.exists()) {
            return;
        }
        if (fromFile.isFile()) {
            conversionFile2File(fromFile);
            return;
        }
        if (fromFile.isDirectory()) {
            for (File file : fromFile.listFiles()) {
                conversion(file.getPath());
            }
        }
    }

    /**
     * 将utf-8 bom编码的文件 复制为一个utf-8无bom编码的文件 内容不变
     *      超大文件不适用
     * @param fromFile
     */
    private static void conversionFile2File(File fromFile) {

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(fromFile));

            int len = in.available();
            byte[] content0 = new byte[len];
            in.read(content0);
            in.close();
            // 去bom
            if (len > 3 && content0[0] == -17 && content0[1] == -69 && content0[2] == -65) {
                String con = new String(content0, 3, len - 3);

                // 写出
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fromFile));
                out.write(new String(con));
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
