# FM-Index for Efficient Pattern Matching on Wikipedia Text

1. Problem Statement
Objective: To implement an FM-index that allows efficient pattern matching and compression on a large text dataset. By constructing the FM-index using the Burrows-Wheeler Transform, we aim to enable fast searching within a compressed text representation.

    ● Input: A large text file extracted from Wikipedia’s database dump (enwiki-20241020-pages-articles-multistream.xml.bz2).

    ● Output: Compressed text data and the positions of all occurrences of a specified pattern within the text.
 
3. Proposed Algorithmic Methodology
   
    ● Brute-force Algorithm: Implement a simple pattern-matching algorithm that checks each position in the text for a match to the pattern. This baseline approach will provide a direct comparison for evaluating the FM-index’s performance.
   
    ● Sophisticated Algorithm (KMP - Knuth-Morris-Pratt): Implement the KMP algorithm, which uses a failure function to skip unnecessary comparisons. The KMP algorithm will be used in combination with the FM-index to improve search efficiency by     
      reducing redundant checks.

4. Experimentation and Data Dataset:

    ● We will use Wikipedia’s database backup dump, specifically the file enwiki-20241020-pages-articles-multistream.xml.bz2, which is 22.5 GB in size.
    ● This file contains the main content of Wikipedia articles in XML format, which we will preprocess to extract plain text.
    ● This dataset size meets the project’s requirement of using a dataset above 50 MB and provides sufficient data for testing compression and pattern matching algorithms at scale.
    Dataset Link: https://dumps.wikimedia.org/enwiki/20241020/

 

Charts:

  ● Compression Ratio: A bar chart showing the compression ratio of the FM-indexed text vs. the original.
  
  ● Search Time Comparison: A line chart comparing search times for brute-force and KMP algorithms.
  
  ● Performance by Text Size: A line chart showing how each algorithm’s search time scales with the dataset size.
