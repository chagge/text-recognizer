package root.core;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * 单词矫正（LCS算法）
 */
public class WordsCorrector {
    private static final int NEITHER     = 0;
    private static final int UP          = 1;
    private static final int LEFT        = 2;
    private static final int UP_AND_LEFT = 3;
    
    //最小匹配率：与字典中的单词最高匹配率 >= 该值，才将单词纠正
    private static final float MIN_MATCH_RATIO = 0.5f;
    
    //字典
    private static String[] dict = null;
    
    static {
        try {
            dict = IOUtils.toString(WordsCorrector.class.getClassLoader().getResourceAsStream("dict.txt")).replace("\r", "").split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static String correct(String input) {
        //先对输入串进行切割
        String[] words = input.split(" ");
        
        for (int i = 0; i < words.length; ++i) {
            
            int index = 0; //候选单词在字典中的位置
            int maxCl = Integer.MIN_VALUE;
        
            String word = words[i];
            for (int j = 0; j < dict.length; ++j) {
                String dictWord = dict[j];
                int cl = LCSAlgorithm(word, dictWord).length();
                if (cl > maxCl) {
                    maxCl = cl;
                    index = j;
                }
            }
    
            //计算匹配率（最大公共子串的长度除以当前单词的长度）
            float ratio = (float) maxCl / word.length();
            if (ratio > MIN_MATCH_RATIO) {
                //纠正
                words[i] = dict[index];
            }
        }
    
        StringBuilder sbOutput = new StringBuilder();
        for (String word : words) {
            sbOutput.append(word + " ");
        }
        
        return sbOutput.deleteCharAt(sbOutput.length() - 1).toString();
    }
    
    
    private static String LCSAlgorithm(String a, String b) {
        int n = a.length();
        int m = b.length();
        int S[][] = new int[n+1][m+1];
        int R[][] = new int[n+1][m+1];
        int ii, jj;
        
        // It is important to use <=, not <.  The next two for-loops are initialization
        for(ii = 0; ii <= n; ++ii) {
            S[ii][0] = 0;
            R[ii][0] = UP;
        }
        for(jj = 0; jj <= m; ++jj) {
            S[0][jj] = 0;
            R[0][jj] = LEFT;
        }
        
        // This is the main dynamic programming loop that computes the score and
        // backtracking arrays.
        for(ii = 1; ii <= n; ++ii) {
            for(jj = 1; jj <= m; ++jj) {
                
                if( a.charAt(ii-1) == b.charAt(jj-1) ) {
                    S[ii][jj] = S[ii-1][jj-1] + 1;
                    R[ii][jj] = UP_AND_LEFT;
                }
                
                else {
                    S[ii][jj] = S[ii-1][jj-1] + 0;
                    R[ii][jj] = NEITHER;
                }
                
                if( S[ii-1][jj] >= S[ii][jj] ) {
                    S[ii][jj] = S[ii-1][jj];
                    R[ii][jj] = UP;
                }
                
                if( S[ii][jj-1] >= S[ii][jj] ) {
                    S[ii][jj] = S[ii][jj-1];
                    R[ii][jj] = LEFT;
                }
            }
        }
        
        // The length of the longest substring is S[n][m]
        ii = n;
        jj = m;
        int pos = S[ii][jj] - 1;
        char lcs[] = new char[ pos+1 ];
        
        // Trace the backtracking matrix.
        while( ii > 0 || jj > 0 ) {
            if( R[ii][jj] == UP_AND_LEFT ) {
                ii--;
                jj--;
                lcs[pos--] = a.charAt(ii);
            }
            
            else if( R[ii][jj] == UP ) {
                ii--;
            }
            
            else if( R[ii][jj] == LEFT ) {
                jj--;
            }
        }
        
        return new String(lcs);
    }
}
