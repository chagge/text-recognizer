package root;

import root.core.WordsCorrector;

/**
 * 测试类
 */
public class Test {
    public static void main(String[] args) {
        String input = "hell0 w0rld !";
        System.out.println( WordsCorrector.correct(input) );
    }
}
