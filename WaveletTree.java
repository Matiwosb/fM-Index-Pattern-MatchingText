import java.util.*;

import java.util.HashSet;

class WaveletTree {
    private WaveletTree left, right;
    private BitVector bitVector;
    private char low, high;

    public WaveletTree(String sequence, char low, char high) {
        this.low = low;
        this.high = high;

        if (low == high) {
            // Leaf node
            return;
        }

        char mid = (char) ((low + high) / 2);
        StringBuilder leftSeq = new StringBuilder();
        StringBuilder rightSeq = new StringBuilder();
        bitVector = new BitVector(sequence.length());

        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c <= mid) {
                leftSeq.append(c);
                bitVector.set(i);
            } else {
                rightSeq.append(c);
            }
        }

        bitVector.buildRankCache();

        if (leftSeq.length() > 0) {
            left = new WaveletTree(leftSeq.toString(), low, mid);
        }
        if (rightSeq.length() > 0) {
            right = new WaveletTree(rightSeq.toString(), (char) (mid + 1), high);
        }
    }

    public int rank(char c, int index) {
        if (low == high) return index + 1;

        char mid = (char) ((low + high) / 2);
        if (c <= mid) {
            return left == null ? 0 : left.rank(c, bitVector.rank(index));
        } else {
            return right == null ? 0 : right.rank(c, index - bitVector.rank(index));
        }
    }

    public int select(char c, int occurrence) {
        if (low == high) {
            // Leaf node: Find the position of the occurrence in the original sequence
            return occurrence - 1;
        }
    
        char mid = (char) ((low + high) / 2);
    
        if (c <= mid) {
            // The character is in the left child
            int posInLeft = left.select(c, occurrence);
            return bitVector.select(1, posInLeft + 1); // Map to the bit vector of the parent
        } else {
            // The character is in the right child
            int posInRight = right.select(c, occurrence);
            return bitVector.select(0, posInRight + 1); // Map to the bit vector of the parent
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

            // Test Rank
            int rankAtEnd = wt.rank(c, bwtSuffix.length() - 1);
            System.out.println("Rank of '" + c + "' at end: " + rankAtEnd);
            assert rankAtEnd == totalOccurrences : "Rank mismatch for character '" + c + "'";

            // Test Select
            for (int i = 1; i <= totalOccurrences; i++) {
                int selectPos = wt.select(c, i);
                assert bwtSuffix.charAt(selectPos) == c : "Select mismatch for character '" + c + "'";
                System.out.println("Position of " + i + "-th occurrence of '" + c + "': " + selectPos);
            }
        }
        System.out.println("Wavelet Tree tested successfully.");
    }
    
}
