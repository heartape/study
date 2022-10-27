package com.heartape;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class HttpRequest {
    public static void main(String[] args) throws IOException {
        Socket socket = SSLSocketFactory.getDefault().createSocket("gitee.com", 443);
        OutputStream outputStream = socket.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        // 根路径需要有"/"
        bufferedWriter.write("GET /heartape/io HTTP/1.1\r\n");
        bufferedWriter.write("Host: gitee.com\r\n\r\n");
        bufferedWriter.flush();

        String s;
        // 如果正文很长且为一行，会很慢，所以不推荐readLine
        while ((s = bufferedReader.readLine()) != null) {
            System.out.println(s);
        }
    }
}
