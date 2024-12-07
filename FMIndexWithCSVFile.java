import java.io.*;
import java.util.*;

public class FMIndexWithCSVFile {
    public static void main(String[] args) {
        String[] files = {
            "./chimpanzee.txt",
            "./dog.txt",
            "./human.txt"
        };

        System.out.println("Starting program execution");
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        for (String file : files) {
            String outputFile = file.substring(0, file.lastIndexOf('.')) + "_output.txt";
            String csvFile = file.substring(0, file.lastIndexOf('.')) + "_performance.csv";

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
                 PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile))) {
                
                // Enhanced CSV header with more metrics
                csvWriter.println("BlockingFactor,SequenceLength,PatternLength,MemoryUsage,BuildTime,QueryTime,MatchesFound");
                processFile(file, writer, csvWriter);
            } catch (IOException e) {
                System.err.println("Error creating output files: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void processFile(String inputFile, PrintWriter writer, PrintWriter csvWriter) {
        File f = new File(inputFile);
        System.out.println("\nChecking file: " + inputFile);
        System.out.println("Absolute path: " + f.getAbsolutePath());
        System.out.println("File exists: " + f.exists());
        System.out.println("File can read: " + f.canRead());
        System.out.println("File length: " + (f.exists() ? f.length() : "N/A"));

        if (!f.exists()) {
            System.err.println("ERROR: File does not exist: " + inputFile);
            return;
        }

        if (!f.canRead()) {
            System.err.println("ERROR: Cannot read file: " + inputFile);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder sequenceBuilder = new StringBuilder();
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (!line.startsWith(">")) {
                    line = line.replaceAll("[^ACGT]", "").trim();
                    if (!line.isEmpty()) {
                        sequenceBuilder.append(line);
                    }
                }

                if (lineCount % 1000 == 0) {
                    System.out.println("Read " + lineCount + " lines...");
                }
            }

            String sequence = sequenceBuilder.toString();
            System.out.println("File read complete. Total lines: " + lineCount);
            System.out.println("Sequence length: " + sequence.length());

            if (sequence.isEmpty()) {
                System.out.println("WARNING: Empty sequence");
                return;
            }

            // Test with different blocking factors
            int[] blockingFactors = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
            
            for (int blockSize : blockingFactors) {
                System.out.println("\nTesting with blocking factor: " + blockSize);
                analyzePerformance(sequence, inputFile, writer, csvWriter, blockSize);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + inputFile);
            e.printStackTrace();
        }
    }

    private static void analyzePerformance(String sequence, String fileName, 
                                         PrintWriter writer, PrintWriter csvWriter, int blockSize) {
        long buildStartTime = System.currentTimeMillis();
        long initialMemory = getUsedMemory();

        for (int start = 0; start < sequence.length(); start += blockSize) {
            int end = Math.min(start + blockSize, sequence.length());
            String chunk = sequence.substring(start, end) + "$";

            try {
                // Build data structures
                int[] suffixArray = buildSuffixArray(chunk);
                Burrows_Wheeler bw = new Burrows_Wheeler();
                String bwtSuffix = bw.transform(chunk, suffixArray);

                char minChar = (char) bwtSuffix.chars().min().orElse('$');
                char maxChar = (char) bwtSuffix.chars().max().orElse('z');
                WaveletTree wt = new WaveletTree(bwtSuffix, minChar, maxChar, blockSize);

                long buildTime = System.currentTimeMillis() - buildStartTime;

                String[] patterns = {"ACG", "TGCA", "GATTACA", "TTAGGC"};
                for (String pattern : patterns) {
                    // Verify pattern exists using direct search
                    boolean exists = false;
                    for (int i = 0; i <= chunk.length() - pattern.length(); i++) {
                        if (chunk.substring(i, i + pattern.length()).equals(pattern)) {
                            exists = true;
                            writer.println("Pattern found at position " + i + " using direct search");
                        }
                    }

                    long queryStart = System.nanoTime();
                    int[] positions = backwardSearch(pattern, wt, suffixArray);
                    long queryTime = System.nanoTime() - queryStart;

                    // Write results to both output files
                    String resultStr = String.format("Pattern '%s' found at positions: %s (Query Time: %d ns)",
                            pattern, Arrays.toString(positions), queryTime);
                    System.out.println(resultStr);
                    writer.println(resultStr);

                    // Write CSV metrics
                    csvWriter.println(String.format("%d,%d,%d,%d,%d,%d,%d",
                        blockSize,
                        sequence.length(),
                        pattern.length(),
                        getUsedMemory() - initialMemory,
                        buildTime,
                        queryTime,
                        positions.length
                    ));
                }

            } catch (Exception e) {
                System.err.println("Error processing block " + blockSize + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        long finalMemory = getUsedMemory();
        long endTime = System.currentTimeMillis();

        // Write summary statistics
        String[] summaryStats = {
            "File: " + fileName,
            "Blocking Factor: " + blockSize,
            "Initial Memory: " + initialMemory + " bytes",
            "Final Memory: " + finalMemory + " bytes",
            "Memory Used: " + Math.abs(finalMemory - initialMemory) + " bytes",
            "Total Time Taken: " + (endTime - buildStartTime) + " ms"
        };

        for (String stat : summaryStats) {
            System.out.println(stat);
            writer.println(stat);
        }
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
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }

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

        if (end < start) {
            return new int[0];
        }

        int[] positions = new int[end - start + 1];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = suffixArray[start + i];
        }
        Arrays.sort(positions);

        return positions;
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Trigger garbage collection
        return runtime.totalMemory() - runtime.freeMemory();
    }
}