Copyright 2013 Kirby Banman
https://github.com/kdbanman/InfoSpect

--------------------------------------------------------------------------------

Javadoc and .jar file are in the dist/ directory.

Run correctness and optional performance tests with `java -jar InfoSpect.jar`

Conceptual description is below.

--------------------------------------------------------------------------------

All examples use integers 0, 1, and 2, but this is just for readability.
Any integers may be used by the class.

Data structure representing the information content (patterns and
repetition) of an array.

Any length N array can have repeated blocks (subarrays) ranging in length
from length 2 to length N-1.  (Length 1 blocks are ignored for reasons that
are explained later.)  For instance, the length 9 array
    {1,1,0,2,2,1,1,0,2}
has three length 2 blocks that occur twice, {1.1}, {1,0}, and {0,2}, so 3
total length 2 block repetitions. It also has two length 3 blocks that occur
twice, {1,1,0} and {1,0,2}, and it has one length 4 block that repeats twice,
{1,1,0,2}.  The information spectrum is the following table:
    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      2     |         3        |
    |      3     |         2        |
    |      4     |         1        |
    ---------------------------------

If the an array is treated toroidally, where the first and last members of
the array are neighbors of each other, then the spectrum may change. First,
repeated blocks can now be up to length N-1.  Length N blocks are ignored
because a length N toroidal array is defined as a repetition of length N. 
Consider the length 9 toroidal array
    {2,1,0,2,2,2,1,0,2}
Repetition analysis (assuming I have made no errors) yields:
    ---------------------------
    | Repeating block | Count |
    ---------------------------
    |     2,1         |   2   |
    |     1,0         |   2   |
    |     0,2         |   2   |
    |     2,2         |   3   |
    |    2,1,0        |   2   |
    |    1,0,2        |   2   |
    |    0,2,2        |   2   |
    |    2,2,1        |   2   |
    |   2,1,0,2       |   2   |
    |   1,0,2,2       |   2   |
    |   2,2,1,0       |   2   |
    |  2,1,0,2,2      |   2   |
    |  2,2,1,0,2      |   2   |
    | 2,2,1,0,2,2     |   2   |
    ---------------------------
This reduces to an information spectrum of:
    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      2     |         5        |
    |      3     |         4        |
    |      4     |         3        |
    |      5     |         2        |
    |      6     |         1        |
    |      7     |         0        |
    |      8     |         0        |
    ---------------------------------

For contiguous information analysis, blocks are only considered repetitive if
they are repeated in directly neighboring blocks.  For instance, in the array
    {0,1,2,0,1,1}
the block {0,1} is not considered repetitive because there is a 2 separating
both instances.  The contiguous repetition analysis for the array is
    ---------------------------
    | Repeating block | Count |
    ---------------------------
    |      0          |   0   |
    |      1          |   0   |
    |      2          |   0   |
    |      0          |   0   |
    |      1          |   1   |
    |     0,1         |   0   |
    |     1,2         |   0   |
    |     2,0         |   0   |
    |     0,1         |   0   |
    |     1,1         |   0   |
    |     1,0         |   0   |
    |    0,1,2        |   0   |
    |    1,2,0        |   0   |
    |    2,0,1        |   0   |
    |    0,1,1        |   0   |
    |    1,1,0        |   0   |
    |    1,0,1        |   0   |
    ---------------------------
Note that now blocks of size 1 are now meaningful, since they don't just
represent the counts of each number.  Also note that the maximum block size
is now the floor of half the array's length, because a block that size
repeated only once is already the length of the array (or the length of the
array less one if the array is of odd number length).  Finally, note that
blocks may repeat, since a there may be contiguous patterns of the same block
in multiple locations.  The spectrum for the above repetition analysis is:
    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      1     |         1        |
    |      2     |         0        |
    |      3     |         0        |
    ---------------------------------

Alternatively, the array
    {0,1,0,1,1,1,0,1}
has the repetition analysis:
    ---------------------------
    | Repeating block | Count |
    ---------------------------
    |      0          |   0   |
    |      1          |   0   |
    |      0          |   0   |
    |      1          |   2   |
    |      0          |   0   |
    |      1          |   0   |
    |     0,1         |   2   |
    |     1,0         |   0   |
    |     1,1         |   0   |
    |     1,0         |   2   |
    |    0,1,0        |   0   |
    |    1,0,1        |   0   |
    |    0,1,1        |   0   |
    |    1,1,1        |   0   |
    |    1,1,0        |   0   |
    |    1,0,1        |   0   |
    |    0,1,0        |   0   |
    |    1,0,1        |   0   |
    |   0,1,0,1       |   0   |
    |   1,0,1,1       |   0   |
    |   1,1,1,0       |   0   |
    |   1,1,0,1       |   0   |
    |   1,0,1,0       |   0   |
    |   0,1,0,1       |   0   |
    |   1,0,1,0       |   0   |
    ---------------------------
which reduces to the spectrum:
    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      1     |         2        |
    |      2     |         4        |
    |      3     |         0        |
    |      4     |         0        |
    ---------------------------------

It is worth noting that greater quantities in an information spectrum
correspond to less information in the source array, because more repetition
means more compressibility.  For instance, see the length 5 toroidal array
    {1,1,1,1,1}
which has the non-contiguous spectrum
    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      2     |         4        |
    |      3     |         4        |
    |      4     |         4        |
    ---------------------------------
and the contiguous spectrum
    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      1     |         4        |
    |      2     |         3        |  *three once-repeated {1,1} blocks with
    ---------------------------------   different starting indices
The maximal repetition corresponds to minimal information content.

For contrast, consider the array
    {2,1,0,1,2,0}
It has no repeating blocks, contiguous or not.
