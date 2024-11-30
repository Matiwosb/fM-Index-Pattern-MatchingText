import java.util.*;


public class Burrows_Wheeler {
    //Converts input text into BWT
    public String transform(String text, int[] suffixArray) {
        int n = text.length();
        if (n <= 2) return text;
        StringBuilder bwt = new StringBuilder();
        for (int idx : suffixArray) {
            int charIndex = (idx - 1 + n) % n;
            bwt.append(text.charAt(charIndex));
        }
        return bwt.toString();
    }

    public WaveletTree buildWaveletTree(String bwt) {
        return new WaveletTree(bwt, 'A', 'Z');
    }

    // Converts BWT into original text
    public String untransform(String bwt) {
        // Create first and last column
        char[] last = bwt.toCharArray();
        String[] first = new String[last.length];
        for (int idx = 0; idx < last.length; idx++) {
            first[idx] = String.valueOf(last[idx]);
        }

        int i = 0;
        while (i++ < last.length - 1) {
            sortAndAdd(first, last);
        }
        //find fully rotated value in array
        for (var item : first) {
            if (item.charAt(item.length() - 1) == '$') return item;
        }
        System.out.println(Arrays.toString(first));
        return "";
    }
    //helper function to untransform bwt string
    private void sortAndAdd(String[] first, char[] last) {
        Arrays.sort(first);
        for (int idx = 0; idx < last.length; idx++) {
            first[idx] = last[idx] + first[idx];
        }
    }
}
