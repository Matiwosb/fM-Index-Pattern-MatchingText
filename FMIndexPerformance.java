import java.io.*;
import java.util.*;

public class FMIndexPerformance {
    private static final int CHUNK_SIZE = 500; // Adjust chunk size based on memory capacity

    public static void main(String[] args) {
        String[] files = {
                "chimpanzee.txt",
                "dog.txt",
                "human.txt"
        };

        for (String file : files) {
            System.out.println("-------------------------------------------------");
            System.out.println("Starting processing for file: " + file);
            System.out.println("-------------------------------------------------");

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sequenceBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(">")) { // Skip headers (e.g., FASTA format)
                        sequenceBuilder.append(line.trim());
                    }
                }

                String sequence = sequenceBuilder.toString();
                if (sequence.isEmpty()) {
                    System.out.println("File " + file + " is empty or invalid. Skipping.");
                    continue;
                }

                analyzePerformance(sequence, file);

            } catch (IOException e) {
                System.err.println("Error reading file: " + file + " - " + e.getMessage());
            }

            System.out.println("-------------------------------------------------");
            System.out.println("Finished processing for file: " + file);
            System.out.println("-------------------------------------------------\n");
        }
    }

    private static void analyzePerformance(String sequence, String fileName) {
        long initialMemory = getUsedMemory();
        long startTime = System.currentTimeMillis();

        int totalLength = sequence.length();
        for (int start = 0; start < totalLength; start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, totalLength);
            String chunk = sequence.substring(start, end);

            System.out.println("Processing chunk: " + chunk.substring(0, Math.min(50, chunk.length())) + "...");

            try {
                // Ensure the sequence ends with '$'
                if (!chunk.endsWith("$")) {
                    chunk += "$";
                }
                String chunkTemp = chunk.replace(' ', '(');

                // Build the suffix array
                int[] suffixArray = buildSuffixArray(chunkTemp);

                // Burrows-Wheeler Transformation
                Burrows_Wheeler bw = new Burrows_Wheeler();
                String bwtSuffix = bw.transform(chunkTemp, suffixArray);

                // Build Wavelet Tree
                char minChar = (char) bwtSuffix.chars().min().orElse('$');
                char maxChar = (char) bwtSuffix.chars().max().orElse('z');
                WaveletTree wt = new WaveletTree(bwtSuffix, minChar, maxChar);

                // Perform pattern matching queries
                String[] patterns = { "ACG", "TGCA", "GATTACA", "TTAGGC" }; // Example patterns
                for (String pattern : patterns) {
                    long queryStart = System.nanoTime();
                    int[] positions = backwardSearch(pattern, wt, suffixArray);
                    long queryEnd = System.nanoTime();
                    System.out.println("Pattern '" + pattern + "' found at positions: " +
                            Arrays.toString(positions) + " (Query Time: " + (queryEnd - queryStart) + " ns)");
                }

            } catch (Exception e) {
                System.err.println("Error processing chunk: " + e.getMessage());
                e.printStackTrace();
            }
        }

        long finalMemory = getUsedMemory();
        long endTime = System.currentTimeMillis();

        System.out.println("File: " + fileName);
        System.out.println("Initial Memory: " + initialMemory + " bytes");
        System.out.println("Final Memory: " + finalMemory + " bytes");
        System.out.println("Memory Used: " + Math.abs(finalMemory - initialMemory) + " bytes");
        System.out.println("Total Time Taken: " + (endTime - startTime) + " ms");
    }

    private static int[] buildSuffixArray(String text) {
        int n = text.length();
        Integer[] suffixes = new Integer[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = i;
        }

        Arrays.sort(suffixes, (a, b) -> text.substring(a).compareTo(text.substring(b)));

        int[] suffixArray = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = suffixes[i];
        }

        return suffixArray;
    }

    private static int[] backwardSearch(String pattern, WaveletTree waveletTree, int[] suffixArray) {
        int start = 0;
        int end = suffixArray.length - 1;

        for (int i = pattern.length() - 1; i >= 0; i--) {
            char c = pattern.charAt(i);

            int rankStart = (start > 0) ? waveletTree.rank(c, start - 1) : 0;
            int rankEnd = waveletTree.rank(c, end);

            if (rankStart >= rankEnd) {
                return new int[0];
            }

            start = rankStart;
            end = rankEnd - 1;
        }

        int[] positions = new int[end - start + 1];
        for (int i = start; i <= end; i++) {
            positions[i - start] = suffixArray[i];
        }

        return positions;
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Trigger garbage collection
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
