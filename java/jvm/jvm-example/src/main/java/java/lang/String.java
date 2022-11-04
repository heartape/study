package java.lang;

/**
 * 由于双亲委派模型，java.lang等少数的包是由Bootstrap ClassLoader进行加载，
 * 这些包下的类的加载会一步步传递到Bootstrap ClassLoader。
 */
public class String {

    static {
        System.out.println("装载自定义String");
    }

    /**
     * java8运行效果
     * <pre>
     * 错误: 在类 java.lang.String 中找不到 main 方法, 请将 main 方法定义为:
     *    public static void main(String[] args)
     * 否则 JavaFX 应用程序类必须扩展javafx.application.Application
     * </pre>
     * java17无法通过编译
     */
    public static void main(String[] args) {
        String s = new String();
        System.out.println(s);
    }
}
