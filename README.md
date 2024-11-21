FM-Index Construction - Step 1 (Suffix Array)
Objective: Build the Suffix Array (SA) for large DNA sequences as the first step in constructing an FM-Index.

Input: Datasets from species like chimpanzees, dogs, and humans, containing large DNA sequences.

Challenges: Memory issues (Java heap space errors) required chunking the data for efficient processing.

Solution: Split datasets into chunks, process each chunk independently, and build suffix arrays for each.

Output: Successfully built suffix arrays for all chunks, covering the entire dataset.

Next Steps: Implement Burrows-Wheeler Transform (BWT) and complete FM-Index construction.
