import java.util.*;

public class WaveletTree {
    private WaveletTree left, right;
    private BitVector bitVector;
    private char low, high;

    public WaveletTree(String sequence, char low, char high, int blockSize) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be null or empty");
        }
        
        this.low = low;
        this.high = high;

        if (low == high) {
            return;
        }

        char mid = (char) ((low + high) / 2);
        StringBuilder leftSeq = new StringBuilder();
        StringBuilder rightSeq = new StringBuilder();
        bitVector = new BitVector(sequence.length(), blockSize);

        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c <= mid) {
                leftSeq.append(c);
                bitVector.set(i, true);
            } else {
                rightSeq.append(c);
                bitVector.set(i, false);
            }
        }

        bitVector.buildRankCache();

        if (leftSeq.length() > 0) {
            left = new WaveletTree(leftSeq.toString(), low, mid, blockSize);
        }
        if (rightSeq.length() > 0) {
            right = new WaveletTree(rightSeq.toString(), (char) (mid + 1), high, blockSize);
        }
    }

    public int rank(char c, int index) {
        if (index < 0) return 0;
        if (c < low || c > high) return 0;
        
        if (low == high) {
            return index + 1;
        }

        char mid = (char) ((low + high) / 2);
        if (c <= mid) {
            return left == null ? 0 : left.rank(c, bitVector.rank(index));
        } else {
            return right == null ? 0 : right.rank(c, index - bitVector.rank(index));
        }
    }

    public int select(char c, int occurrence) {
        if (occurrence <= 0 || c < low || c > high) {
            return -1;
        }

        if (low == high) {
            return occurrence - 1;
        }

        char mid = (char) ((low + high) / 2);

        if (c <= mid) {
            if (left == null) return -1;
            int posInLeft = left.select(c, occurrence);
            return posInLeft >= 0 ? bitVector.select(1, posInLeft + 1) : -1;
        } else {
            if (right == null) return -1;
            int posInRight = right.select(c, occurrence);
            return posInRight >= 0 ? bitVector.select(0, posInRight + 1) : -1;
        }
    }

    public static void testWaveletTree(WaveletTree wt, String bwtSuffix) {
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : bwtSuffix.toCharArray()) {
            uniqueChars.add(c);
        }

        for (char c : uniqueChars) {
            int totalOccurrences = 0;
            for (int i = 0; i < bwtSuffix.length(); i++) {
                if (bwtSuffix.charAt(i) == c) totalOccurrences++;
            }

            int rankAtEnd = wt.rank(c, bwtSuffix.length() - 1);
            System.out.println("Rank of '" + c + "' at end: " + rankAtEnd);
            assert rankAtEnd == totalOccurrences : "Rank mismatch for character '" + c + "'";

            for (int i = 1; i <= totalOccurrences; i++) {
                int selectPos = wt.select(c, i);
                assert bwtSuffix.charAt(selectPos) == c : "Select mismatch for character '" + c + "'";
                System.out.println("Position of " + i + "-th occurrence of '" + c + "': " + selectPos);
            }
        }
        System.out.println("Wavelet Tree tested successfully.");
    }
}