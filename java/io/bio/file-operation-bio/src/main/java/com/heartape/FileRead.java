package com.heartape;

import java.io.*;
import java.util.List;

public class FileRead {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("D:\\file.rdb");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
        List<Integer> list = (List)objectInputStream.readObject();
        System.out.println(list);
    }

}
