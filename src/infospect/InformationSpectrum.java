package infospect;

/* 
 * The following is repeated in the README.txt at
 * https://github.com/kdbanman/InfoSpect
 * 
 * All examples use integers 0, 1, and 2, but this is just for readability.
 * Any integers may be used by the class.
 *
 * Data structure representing the information content (patterns and
 * repetition) of an array.
 * 
 * Any length N array can have repeated blocks (subarrays) ranging in length
 * from length 2 to length N-1.  (Length 1 blocks are ignored for reasons that
 * are explained later.)  For instance, the length 9 array
 *     {1,1,0,2,2,1,1,0,2}
 * has three length 2 blocks that occur twice, {1.1}, {1,0}, and {0,2}, so 3
 * total length 2 block repetitions. It also has two length 3 blocks that occur
 * twice, {1,1,0} and {1,0,2}, and it has one length 4 block that repeats twice,
 * {1,1,0,2}.  The information spectrum is the following table:
 *     ---------------------------------
 *     | Block Size | Repetition Count |
 *     ---------------------------------
 *     |      2     |         3        |
 *     |      3     |         2        |
 *     |      4     |         1        |
 *     ---------------------------------
 * 
 * If the an array is treated toroidally, where the first and last members of
 * the array are neighbors of each other, then the spectrum may change. First,
 * repeated blocks can now be up to length N-1.  Length N blocks are ignored
 * because a length N toroidal array is defined as a repetition of length N. 
 * Consider the length 9 toroidal array
 *     {2,1,0,2,2,2,1,0,2}
 * Repetition analysis (assuming I have made no errors) yields:
 *     ---------------------------
 *     | Repeating block | Count |
 *     ---------------------------
 *     |     2,1         |   2   |
 *     |     1,0         |   2   |
 *     |     0,2         |   2   |
 *     |     2,2         |   3   |
 *     |    2,1,0        |   2   |
 *     |    1,0,2        |   2   |
 *     |    0,2,2        |   2   |
 *     |    2,2,1        |   2   |
 *     |   2,1,0,2       |   2   |
 *     |   1,0,2,2       |   2   |
 *     |   2,2,1,0       |   2   |
 *     |  2,1,0,2,2      |   2   |
 *     |  2,2,1,0,2      |   2   |
 *     | 2,2,1,0,2,2     |   2   |
 *     ---------------------------
 * This reduces to an information spectrum of:
 *     ---------------------------------
 *     | Block Size | Repetition Count |
 *     ---------------------------------
 *     |      2     |         5        |
 *     |      3     |         4        |
 *     |      4     |         3        |
 *     |      5     |         2        |
 *     |      6     |         1        |
 *     |      7     |         0        |
 *     |      8     |         0        |
 *     ---------------------------------
 * 
 * It is worth noting that greater quantities in an information spectrum
 * correspond to less information in the source array, because more repetition
 * means more compressibility.  For instance, see the length 5 toroidal array
 *   {1,1,1,1,1}
 * which has the spectrum
 *     ---------------------------------
 *     | Block Size | Repetition Count |
 *     ---------------------------------
 *     |      2     |         4        |
 *     |      3     |         4        |
 *     |      4     |         4        |
 *     ---------------------------------
 * The maximal repetition corresponds to minimal information content.
 * For contrast, consider the array
 *     {2,1,0,1,2,0}
 * It has no repeating blocks.  Length 1 blocks are repeated, but they are not
 * compressible blocks because they are already minimally represented.
 * 
 * @author kdbanman
 */
public class InformationSpectrum{
    private final int[] sourceArray;
    // maximum possible size of block repeated within source array
    private final int maxBlockSize;
    // minimum possible size of block repeated within source array (it's always 2)
    private final int minBlockSize;
    // blockSizeFrequencies maps integer block sizes (ranging between 2 and
    // maxBlockSize) to integer frequencies of repetition of that block size.
    private final int[] blockSizeFrequencies;
    
    /**
     * Generate *Toroidal* spectral analysis of information content for
     * for the array passed at initialization (See README.txt at
     * https://github.com/kdbanman/InfoSpect for explanation and examples).
     *
     * (Non-toroidal isn't useful to me right now, so it's not done.)
     * 
     * @param source Array of integers of  length > 2 for toroidal spectral 
     * analysis.
     */
    public InformationSpectrum(int[] source) {
        sourceArray = source;
        maxBlockSize = source.length - 1;
        minBlockSize = 2;
        blockSizeFrequencies = new int[source.length];
        
        analyzeArray();
    }
    
    protected void analyzeArray() {
        // for each block size
        for (int blockSize = 2; blockSize <= maxBlockSize; blockSize++) {
            // if a block is repeated at an index, then a pattern has been found
            // and the repeated block shouldn't be used to find other repetition
            boolean[] patternFound = new boolean[sourceArray.length];
            // look for each block of length blockSize in the rest of the array
            for (int matchSourceBlockStart = 0; matchSourceBlockStart < sourceArray.length; matchSourceBlockStart++) {
                // ignore the patterns of this blockSize that are already found
                if (patternFound[matchSourceBlockStart]) continue;
                // look for the current block in the rest of the array
                for (int potentialMatchBlockStart = 0; potentialMatchBlockStart < sourceArray.length; potentialMatchBlockStart++) {
                    // don't look for yourself within yourself. that's silly.
                    if (potentialMatchBlockStart == matchSourceBlockStart) continue;
                    // if the first entries of the match source block and the block currently under
                    // inspection match, then continue inspection
                    boolean potentialMatch = sourceArray[matchSourceBlockStart] == sourceArray[potentialMatchBlockStart];
                    if (potentialMatch) {
                        for (int i = 1; i < blockSize; i++) {
                            potentialMatch = toroidalAccess(sourceArray, matchSourceBlockStart + i) == toroidalAccess(sourceArray, potentialMatchBlockStart + i);
                            if (!potentialMatch) break;
                        }
                        if (potentialMatch) {
                            patternFound[potentialMatchBlockStart] = true;
                            blockSizeFrequencies[blockSize] = blockSizeFrequencies[blockSize] + 1;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns the source array with which the InformationSpectrum was created.
     */
    public int[] getSourceArray() {
        return sourceArray;
    }
    
    /**
     * Returns the maximum size that a repeated block can be for the array with
     * which the InformationSpectrum was initialized.  (The minimum is 2.)
     */
    public int getMaxBlockSize() {
        return maxBlockSize;
    }
    
    /**
     * Returns the minimum size that a repeated block can be for the array with which
     * the InformationSpectrum was initialized.  It's always 2.
     */
    public int getMinBlockSize() {
        return 2;
    }
    
    /**
     * Returns the frequency with which blocks of a given size were repeated
     * within the initializing array (this corresponds to quering the Block
     * Size / Repetition Count tables in the README.txt at 
     * https://github.com/kdbanman/InfoSpect).
     *
     * Valid block size parameters range from 2 to N-1 (where N is the length
     * of the initializing array).  Invalid queries return -1.
     *
     */
    public int getBlockSizeFrequency(int blockSize) {
        if (blockSize >= 2 || blockSize <= maxBlockSize) {
            return blockSizeFrequencies[blockSize];
        } else {
            throw new ArrayIndexOutOfBoundsException("InformationSpectrum Error: Cannot request negative block size frequency.");
        }
    }
    
    /**
     * Set the frequency of a particular block size.
     */
    protected void setBlockSizeFrequency(int blockSize, int frequency) {
        if (blockSize >= 2 || blockSize <= maxBlockSize) {
        blockSizeFrequencies[blockSize] = frequency;
        } else {
            throw new ArrayIndexOutOfBoundsException("InformationSpectrum Error: Cannot request negative block size frequency.");
        }
    }
    
    protected int toroidalIndex(int i) {
        i = i % getSourceArray().length;
        if (i < 0) {
            return getSourceArray().length + i;
        } else {
            return i;
        }
    }
    
    /**
     * toroidal array accessor
     */
    protected int toroidalAccess(int[] source, int i) {
        return getSourceArray()[toroidalIndex(i)];
    }
    
    /**
     * (deeply) copy a subarray as if it were toroidal.  both start and finish
     * indices are included in the array.  for instance, a copy from index 1 to
     * index 3 of {0,1,2,3,4,5,6,7} returns {1,2,3}.
     */
    private int[] toroidalCopy(int[] source, int start, int finish) {
        int[] copy = new int[finish - start + 1];
        for (int i = 0; i < finish - start + 1; i++) {
            copy[i] = toroidalAccess(source, i + start);
        }
        return copy;
    }
    
    /**
     *  Returns a well-formatted table of the spectral analysis data.
     */
    @Override
    public String toString() {
        String stringRep = "Block Size - Repetition Count\n";
        for (int i = 2; i <= maxBlockSize; i++) {
            String blockSize = Integer.toString(i);
            String freq = Integer.toString(getBlockSizeFrequency(i));
            stringRep += blockSize + "           - ".substring(blockSize.length()) + freq + "\n";
        }
        return stringRep;
        
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null) {return false;}
        if (this.getClass() == o.getClass()) {
            return getSourceArray().equals(((InformationSpectrum)o).getSourceArray());
        } 
        return false;
    }
    
    @Override
    public int hashCode() {
        return getSourceArray().hashCode();
    }
}
