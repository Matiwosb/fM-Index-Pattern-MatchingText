import java.util.List;

public class PatternMatching {
    public static void search(WaveletTree wt, String bwt, List<String> patterns) {
        for (String pattern : patterns) {
            int count = wt.rank(pattern.charAt(pattern.length() - 1), bwt.length() - 1);
            for (int i = pattern.length() - 2; i >= 0; i--) {
                count = wt.rank(pattern.charAt(i), count - 1);
                if (count == 0)
                    break;
            }
            System.out.println("Pattern '" + pattern + "' occurred " + count + " times in the text.");
        }
    }
}
