import java.io.*;
import java.util.*;

public class FMIndexProject {
    private static final int CHUNK_SIZE = 500; // Adjust chunk size based on memory capacity

    public static void main(String[] args) {
        String[] files = {
                "chimpanzee.txt",
                "dog.txt",
                "human.txt"
        };

        for (String file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                System.out.println("Processing file: " + file);
                StringBuilder sequenceBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(">")) { // Skip headers (e.g., FASTA format)
                        sequenceBuilder.append(line.trim());
                    }
                }

                String sequence = sequenceBuilder.toString();
                processChunks(sequence);
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }
    }

    private static void processChunks(String sequence) {
        int totalLength = sequence.length();
        for (int start = 0; start < totalLength; start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, totalLength);
            String chunk = sequence.substring(start, end);

            System.out.println("Processing chunk: " + chunk.substring(0, Math.min(50, chunk.length())) + "...");
            try {
                chunk += chunk.charAt(chunk.length() - 1) != '$' ? '$' : "";
                var chunk_temp = chunk.replace(' ', '(');
                int[] suffixArray = buildSuffixArray(chunk_temp);
                // Print the suffix array and verify
                printSuffixArray(chunk_temp, suffixArray);

                System.out.println("Suffix array built successfully for this chunk.");
                // Implementing Burrows-Wheeler Transformation
                Burrows_Wheeler bw = new Burrows_Wheeler();
                String bwt_suffix = bw.transform(chunk_temp, suffixArray);
                String bwt_original = bw.untransform(bwt_suffix).replace('(', ' ');

                if (bwt_original.equals(chunk)) System.out.println("Burrows-Wheeler transformation successful. ");
                else throw new Exception();

                WaveletTree wt = new WaveletTree(bwt_suffix, '$', 'z');
                System.out.println("Wavelet Tree built successfully for BWT.");

                WaveletTree.testWaveletTree(wt, bwt_suffix);
                
                                // // char c = bwt.replace(' ', '(').charAt(0);
                                // int rankResult = wt.rank(chunk_temp, bwt_suffix.length() - 1);
                                // System.out.println("Rank of '" + c + "': " + rankResult);
                
                            } catch (Exception e) {
                                System.err.println("Error processing chunk or during Burrows-Wheeler Transform. " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                
                    // private static void testWaveletTree(WaveletTree wt, String bwt_suffix) {
                    //     // TODO Auto-generated method stub
                    //     throw new UnsupportedOperationException("Unimplemented method 'testWaveletTree'");
                    // }
                
                    // Build suffix array (existing logic)

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

    // Print the suffix array for verification
    private static void printSuffixArray(String text, int[] suffixArray) {
        System.out.println("Suffix Array:");
        for (int index : suffixArray) {
            String suffix = text.substring(index);
            System.out.println(index + ": " + (suffix.length() > 50 ? suffix.substring(0, 50) + "..." : suffix));
        }
    }

    // private static void testWaveletTree(WaveletTree w, String bwtSuffix) {
    //     Set<Character> uniqueChars = new HashSet<>();
    //     for (char c : bwtSuffix.toCharArray()) {
    //         uniqueChars.add(c);
    //     }

    //     for (char c : uniqueChars) {
    //         int totalOccurrences = 0;
    //         for (int i = 0; i < bwtSuffix.length(); i++) {
    //             if (bwtSuffix.charAt(i) == c) totalOccurrences++;
    //         }

    //         // Test Rank
    //         int rankAtEnd = w.rank(c, bwtSuffix.length() - 1);
    //         System.out.println("Rank of '" + c + "' at end: " + rankAtEnd);
    //         assert rankAtEnd == totalOccurrences : "Rank mismatch for character '" + c + "'";

    //         // Test Select
    //         for (int i = 1; i <= totalOccurrences; i++) {
    //             int selectPos = wt.select(c, i);
    //             assert selectPos >= 0 && bwtSuffix.charAt(selectPos) == c : 
    //                 "Select mismatch for character '" + c + "', occurrence " + i;
    //             System.out.println("Position of " + i + "-th occurrence of '" + c + "': " + selectPos);
    //         }
            
    //     }
    //     System.out.println("Wavelet Tree tested successfully.");
    // }
}
