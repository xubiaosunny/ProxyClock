package xubiao.proxyclock.utils;

/**
 * Created by xubiao on 2017/7/1.
 */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import android.webkit.*;
import android.content.Context;
import xubiao.proxyclock.MainActivity;

public class NetUtil {

    /**
     * 使用POST访问去访问网络
     * @param spec
     * @param strJson
     * @return
     */
    public static String doPost(String spec, String strJson,Map<String,String> headers) {
        System.out.println(spec);
        System.out.println(strJson);
        try {
            byte[] content = strJson.getBytes("utf-8");
            // 根据地址创建URL对象
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            conn.setRequestMethod("POST");
            // 设置请求的超时时间
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            // 设置请求的头
            Set<Entry<String, String>> sets = headers.entrySet();
            for(Entry<String, String> entry : sets) {
                System.out.print(entry.getKey() + ", ");
                System.out.println(entry.getValue());
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.setDoOutput(true); // 发送POST请求必须设置允许输出
            conn.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = conn.getOutputStream();
            os.write(content);
            os.flush();
            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());

                return result;

            } else {
                System.out.println(conn.getResponseCode());
                return "链接失败.........";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            return e.toString();
        }
    }

    public static String doGet(String spec, Map<String,String> headers) {
        System.out.println(spec);
        try {
            // 根据地址创建URL对象
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            conn.setRequestMethod("GET");
            // 设置请求的超时时间
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            // 设置请求的头
            Set<Entry<String, String>> sets = headers.entrySet();
            for(Entry<String, String> entry : sets) {
                System.out.print(entry.getKey() + ", ");
                System.out.println(entry.getValue());
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());

                return result;

            } else {
                System.out.println(conn.getResponseCode());
                return "链接失败.........";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            return e.toString();
        }
    }
}
