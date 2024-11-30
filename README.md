
# CSC 7300
## Algorithm Design and Analysis


### Teamates :
    Roohana Karim
    Patrcick Adeosun
    Matiwos Birbo
    Kaushani Samarawickrama 
## Table of Content
## Description
## Problem
## Application
## Input
## Output
## Algorithms
    Description
### FM-Index Construction - Step 1 (Suffix Array)
#### Objective: Build the Suffix Array (SA) for large DNA sequences as the first step in constructing an FM-Index.

Input: Datasets from species like chimpanzees, dogs, and humans, containing large DNA sequences.

Challenges: Memory issues (Java heap space errors) required chunking the data for efficient processing.

Solution: Split datasets into chunks, process each chunk independently and build suffix arrays for each.

Output: Successfully built suffix arrays for all chunks, covering the entire dataset.

Burrows-Wheeler Transform - Step 2
Objective: Transform the processed text using the BWT method, inversely transform the resultant string, and then compare the two results.

Input: Suffix Array from step 1 and the processed text.

Output: Successfully transformed text permutation. This output is then passed as input into the inverse BWT function, and then compared.

### Wavalet Tree and Bit Vector
