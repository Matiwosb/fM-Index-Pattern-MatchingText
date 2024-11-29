import java.util.*;


class BitVector {
    private BitSet bitset;
    private int[] rankCache; // Optional cache for rank queries
    private int size;

    public BitVector(int size) {
        this.size = size;
        this.bitset = new BitSet(size);
        this.rankCache = new int[size + 1]; // Cache for faster rank queries
    }

    public void set(int index) {
        bitset.set(index);
    }

    public boolean get(int index) {
        return bitset.get(index);
    }

    public int rank(int index) {
        // Count 1s up to the index (inclusive)
        return rankCache[index];
    }

    public int select(int bitValue, int occurrence) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if ((bitValue == 1 && bitset.get(i)) || (bitValue == 0 && !bitset.get(i))) {
                count++;
                if (count == occurrence) {
                    return i;
                }
            }
        }
        return -1; // Return -1 if the occurrence is not found
    }    

    public void buildRankCache() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (bitset.get(i)) count++;
            rankCache[i + 1] = count;
        }
    }
}
