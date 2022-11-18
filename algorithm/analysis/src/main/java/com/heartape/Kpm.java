package com.heartape;

/**
 * KMP算法是一种改进的字符串匹配算法，由D.E.Knuth，V.R.Pratt和J.H.Morris提出，由三人名字命名。
 */
public class Kpm {

    /**
     * 目标子串
     */
    private final String target = "a b a b a c a b d";

    private final boolean start = false;
    private final int num = 0;


    /**
     * 回溯数组，整个kmp算法的核心。
     * 在进行匹配之前，提前将目标子串进行分析，将{@link Kpm#target}逐步拆分前缀和后缀，并缓存至当前数组。
     * <pre>
     * target    a   b   a   b   a   c   a   b   d
     * back      0   0   0   1   2   0   0   1   0
     *
     * 数组内的数字代表当前位置匹配失败时，下次匹配的索引位置。
     * 每当{@link Kpm#target}内出现和index==0位置一样的字符时，便开始计数。
     *
     *         p0↓     p1↓
     * target    a   b   a   b   a   c   a   b   d
     * back      0   0   0
     * ---p1遇到与p0相同字符时便更改{@link Kpm#start}，表示开始计数。
     *
     *             p0↓     p1↓
     * target    a   b   a   b   a   c   a   b   d
     * back      0   0   0   1
     * ---下一个字符依旧与前缀匹配，{@link Kpm#num} + 1，
     * 表示在后续使用中，如果字符匹配失败时，便跳转到index=1的位置继续匹配，因为前一个字符一定与index==0的字符相同。
     *
     * 以此类推。。。。。。
     * </pre>
     */
    private final byte[] back = new byte[target.length()];


}
