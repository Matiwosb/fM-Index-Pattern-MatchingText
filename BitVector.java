import java.util.BitSet;

public class BitVector {
    private BitSet bitset;
    private int[] rankCache;
    private int size;
    private int blockSize;

    public BitVector(int size, int blockSize) {
        if (size <= 0 || blockSize <= 0) {
            throw new IllegalArgumentException("Size and block size must be positive.");
        }

        this.size = size;
        this.blockSize = blockSize;
        this.bitset = new BitSet(size);
        this.rankCache = new int[(size + blockSize - 1) / blockSize + 1];
    }

    public void set(int index, boolean value) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        bitset.set(index, value);
    }

    public boolean get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return bitset.get(index);
    }

    public int rank(int index) {
        if (index < 0) return 0;
        if (index >= size) return rank(size - 1);

        int blockIndex = index / blockSize;
        int rankInBlock = 0;
        int blockStart = blockIndex * blockSize;

        for (int i = blockStart; i <= index; i++) {
            if (bitset.get(i)) {
                rankInBlock++;
            }
        }

        return rankCache[blockIndex] + rankInBlock;
    }

    public void buildRankCache() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (bitset.get(i)) {
                count++;
            }

            if ((i + 1) % blockSize == 0 || i == size - 1) {
                rankCache[i / blockSize + 1] = count;
            }
        }
    }

    public int select(int bit, int occurrence) {
        if (occurrence <= 0) {
            return -1;
        }

        int count = 0;
        for (int i = 0; i < size; i++) {
            if (bitset.get(i) == (bit == 1)) {
                count++;
                if (count == occurrence) {
                    return i;
                }
            }
        }
        return -1; // Occurrence not found
    }

    public int size() {
        return size;
    }
}