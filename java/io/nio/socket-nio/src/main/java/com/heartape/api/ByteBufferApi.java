package com.heartape.api;

import java.nio.ByteBuffer;

public class ByteBufferApi {
    public static void main(String[] args) {
        // 堆内缓冲区
        ByteBuffer allocate = ByteBuffer.allocate(10);
        ByteBuffer wrap = ByteBuffer.wrap("byte".getBytes());
        // 堆外缓冲区
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(10);

        // 源码注释描述了几个变量的关系 -> Invariants: mark <= position <= limit <= capacity
        // 设置缓冲区容量（最大值）
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        // 获取缓冲区容量（最大值）
        int capacity = byteBuffer.capacity();
        // 标记当前位置
        byteBuffer.mark();
        // mark索引跳转到上次标记的位置
        byteBuffer.reset();
        // 设置缓冲区索引
        byteBuffer.position(2);
        // 限制缓冲区索引
        byteBuffer.limit(8);
        // 缓冲区剩余空间 -> limit - position
        int remaining = byteBuffer.remaining();

        /*
         * 切换成读模式，将写状态时position范围内的数据用于读取
         * 切换之前的状态: limit == 8,position == 5
         * 切换之后的状态: limit == 5,position == 0
         * 注意，仅表示从ByteBuffer读取
         */
        byteBuffer.flip();
        // 读取
        byte b = byteBuffer.get();
        byte[] bytes = new byte[5];
        /*
         * 将数据转移到目标数组
         * if (length > limit() - position)
         * 源码表示会检查Buffer中是否有足够的字节，没有的话需要手动指定长度，否则抛出异常。
         */
        byteBuffer.get(bytes);
        byteBuffer.get(bytes, byteBuffer.position(), byteBuffer.limit());
        //将position重新设置为0，可以重复读
        allocate.rewind();
        //切换成写模式，position = 0，limit = capacity(数组总的容量)，并不会真正清理数据。
        allocate.clear();
    }
}
