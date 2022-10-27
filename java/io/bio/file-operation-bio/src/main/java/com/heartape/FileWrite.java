package com.heartape;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileWrite {

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\file.rdb");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        objectOutputStream.writeObject(list);
        objectOutputStream.flush();
    }

}
