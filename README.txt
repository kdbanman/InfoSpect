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


Aside
=====

It is worth noting that greater quantities in an information spectrum
correspond to less information in the source array, because more repetition
means more compressibility.  For instance, see the length 5 toroidal array

    {1,1,1,1,1}

which has the spectrum

    ---------------------------------
    | Block Size | Repetition Count |
    ---------------------------------
    |      2     |         4        |
    |      3     |         4        |
    |      4     |         4        |
    ---------------------------------

The maximized repetition corresponds to minimal information content.

For contrast, consider the array

    {2,1,0,1,2,0}

It has no repeating blocks, which corresponds to maximized information content.
