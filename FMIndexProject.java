import java.io.*;
import java.util.*;

public class FMIndexProject {
    private static final int CHUNK_SIZE = 50000; // Adjust chunk size based on memory capacity

    public static void main(String[] args) {
        String[] files = {
                "C:\\Users\\Roohana Karim\\Downloads\\algo_project_2024\\chimpanzee.txt",
                "C:\\Users\\Roohana Karim\\Downloads\\algo_project_2024\\dog.txt",
                "C:\\Users\\Roohana Karim\\Downloads\\algo_project_2024\\human.txt"
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
                int[] suffixArray = buildSuffixArray(chunk);

                // Print the suffix array and verify
                printSuffixArray(chunk, suffixArray);

                System.out.println("Suffix array built successfully for this chunk.");
            } catch (Exception e) {
                System.err.println("Error processing chunk: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

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
}
