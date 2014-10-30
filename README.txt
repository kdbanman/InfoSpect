Copyright 2014 Kirby Banman

https://github.com/kdbanman/InfoSpect

InfoSpect is short for Information Spectrum.  The purpose of the software is
to find repeated patterns in toroidal (circular) arrays of integers.

A toroidal array is one that can be rotated.  In essence, the InfoSpect library
treats looks at these two arrays in exactly the same way:

    {2,3,4,5,6,7,8}  and  {6,7,8,2,3,4,5}


Example 1
=========

Consider the length 9 array

    {1,1,0,2,2,2,1,0,2}

There are 5 unique blocks of length 2, and they occur in the array as shown:

    {l,1}:    {1,1,0,2,2,2,1,0,2}
               ^ ^
              --> not repeated

    {1,0}:    {1,1,0,2,2,2,1,0,2}
                 ^ ^       ^ ^
              --> repeated once

    {0,2}:    {1,1,0,2,2,2,1,0,2}
                   ^ ^       ^ ^
              --> repeated once

    {2,2}:    {1,1,0,2,2,2,1,0,2}
                     ^ ^
                       ^ ^
              --> repeated once

    {2,1}:    {1,1,0,2,2,2,1,0,2}
               ^         ^ ^   ^
              --> repeated once

    So there are 4 repetitions total for block size of 2.

There are 8 unique length 3 blocks:

    {1,1,0}:    {1,1,0,2,2,2,1,0,2}
                 ^ ^ ^
                --> not repeated

    {1,0,2}:    {1,1,0,2,2,2,1,0,2}
                   ^ ^ ^     ^ ^ ^
                --> repeated once

    {0,2,2}:    {1,1,0,2,2,2,1,0,2}
                     ^ ^ ^
                --> not repeated

    {2,2,2}:    {1,1,0,2,2,2,1,0,2}
                       ^ ^ ^
                --> not repeated

    {2,2,1}:    {1,1,0,2,2,2,1,0,2}
                         ^ ^ ^
                --> not repeated

    {2,1,0}:    {1,1,0,2,2,2,1,0,2}
                           ^ ^ ^
                --> not repeated

    {0,2,1}:    {1,1,0,2,2,2,1,0,2}
                 ^             ^ ^
                --> not repeated

    {2,1,1}:    {1,1,0,2,2,2,1,0,2}
                 ^ ^             ^
                --> not repeated
    
    So there is 1 repetition total for block size of 3.

There are no more repetitions for block sizes 4+, so the "information
spectrum" is summarized in this frequency table:

    ---------------------------------
    | Block Size | Repetition Freq. |
    ---------------------------------
    |      2     |         4        |
    |      3     |         1        |
    |      4     |         0        |
    |      5     |         0        |
    |      6     |         0        |
    |      7     |         0        |
    |      8     |         0        |
    ---------------------------------

To get this result in Java with the InformationSpectrum class:

```
    int[] arr = new int[] {1,1,0,2,2,2,1,0,2};
    InformationSpectrum spect = new InformationSpectrum(arr);

    int a = spect.getFrequency(2);  // a == 4
    int b = spect.getFrequency(3);  // b == 1
    int c = spect.getFrequency(7);  // c == 0

    int d = spect.getFrequency(9);  // ArrayIndexOutOfBoundsException
    int d = spect.getFrequency(0);  // ArrayIndexOutOfBoundsException
    int d = spect.getFrequency(1);  // ArrayIndexOutOfBoundsException
```

Notice that only the block sizes from the table are valid.  Blocks of size 1
are not considered patterns.  Blocks of size 9 (equal to array size) are not
repeatable.

In general, an array of size N may have patterns sized from 2 to N - 1.

This can be obtained in Java as well:

```
    int smallest = spect.getMinBlockSize();  // smallest == 2
    int largest  = spect.getMaxBlockSize();  // largest == 8
```


Example 2
=========

Consider another array:

    {2,1,0,2,2,2,1,0,2}

The repeated blocks are the following:

    ---------------------------
    |     Block   |  Repeated |
    ---------------------------
    |     2,1     |     1     |
    |     1,0     |     1     |
    |     0,2     |     1     |
    |     2,2     |     2     |   {2,2}:    {2,1,0,2,2,2,1,0,2}
    |    2,1,0    |     1     |              ^               ^
    |    1,0,2    |     1     |                    ^ ^
    |    0,2,2    |     1     |                      ^ ^
    |    2,2,1    |     1     |              --> repeated twice
    |   2,1,0,2   |     1     |
    |   1,0,2,2   |     1     |
    |   2,2,1,0   |     1     |
    |  2,1,0,2,2  |     1     |
    |  2,2,1,0,2  |     1     |
    | 2,2,1,0,2,2 |     1     |  {2,2,1,0,2,2}:    {2,1,0,2,2,2,1,0,2}
    ---------------------------                     ^ ^ ^ ^ ^       ^
                                                    ^       ^ ^ ^ ^ ^
                                                    --> repeated once

Summing the repetition frequencies for each block size yields the spectrum:

    ---------------------------------
    | Block Size | Repetition Freq. |
    ---------------------------------
    |      2     |         5        |
    |      3     |         4        |
    |      4     |         3        |
    |      5     |         2        |
    |      6     |         1        |
    |      7     |         0        |
    |      8     |         0        |
    ---------------------------------


================================================================================
================================================================================

Contiguous Mode
===============


For contiguous information analysis, blocks are only considered repetitive if
they are repeated in directly neighboring blocks.


Example 1
=========

Consider the array:

    {0,1,2,0,1,1}

the block {0,1} is not considered repetitive because the instances are not 
touching (contiguous).  See the following:

    -------------------------
    |  Block        | Freq. |
    -------------------------  {1}:    {0,1,2,0,1,1}
    |    0          |   0   |             ^     ^ ^
    |    1          |   1   |           --> repeated contiguously once
    |    2          |   0   |
    |   0,1         |   0   |  {0,1}:    {0,1,2,0,1,1}
    |   1,2         |   0   |             ^ ^   ^ ^
    |   2,0         |   0   |             --> not repeated contiguously
    |   1,1         |   0   |
    |  0,1,2        |   0   |
    |  1,2,0        |   0   |
    |  2,0,1        |   0   |
    |  0,1,1        |   0   |
    |  1,1,0        |   0   |
    |  1,0,1        |   0   |
    -------------------------

Which reduces to the spectrum:

    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      1     |         1        |
    |      2     |         0        |
    |      3     |         0        |
    ---------------------------------

Note that now blocks of size 1 are now meaningful, since they don't just
represent the frequency of each number.

Also note that the maximum block size is now the floor of half the array's
length, because a block that large repeated only once is already the length
of the array.  The example below demonstrates this.

Example 2
=========

    {1,1,0,1,1,0}

    ----------------------
    |  Block     | Freq. |
    ----------------------  {1}:    {1,1,0,1,1,0}
    |    1       |   2   |           ^ ^   ^ ^
    |    0       |   0   |           --> repeated contiguously twice
    |   1,1      |   0   |
    |   1,0      |   0   |  {0,1}:    {0,1,2,0,1,1}
    |   0,1      |   0   |             ^ ^   ^ ^
    |  1,1,0     |   1   |             --> not repeated contiguously
    |  1,0,1     |   1   |
    |  0,1,1     |   1   |
    ----------------------

###############################
#
#  TODO CONTIGUOUS MODE IS STUPID RIGHT NOW
#  frequency for 3 is overcounted in above example for obvious reasons
#
###########################3###

Finally, note that the same block may occur in more than one contiguous block.
Consider the array:

    {2,1,1,2,1,1,1}



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
