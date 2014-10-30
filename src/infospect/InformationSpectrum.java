package infospect;

import java.util.Arrays;

/* 
 * @author kdbanman
 * @see https://github.com/kdbanman/InfoSpect
 */
public class InformationSpectrum {
    // whether or not the analysis is contiguous
    private final boolean isContiguous;
    // array with which the spectrum is initialized
    private final int[] sourceArray;
    // maximum possible size of block repeated within source array
    private final int maxBlockSize;
    // minimum possible size of block repeated within source array (it's always 2)
    private final int minBlockSize;
    // blockSizeFrequencies maps integer block sizes (ranging between 2 and
    // maxBlockSize) to integer frequencies of repetition of that block size.
    private final int[] blockSizeFrequencies;
    
    /**
     * Generate toroidal spectral analysis of information content for
     * for the array passed at initialization.
     * 
     * @param source Array of integers of  length > 2 for toroidal spectral 
     * analysis.
     */
    public InformationSpectrum(int[] source) {
        this(source, false);
    }
    
    /**
     * Generate *toroidal* spectral analysis of information content for
     * for the passed array (See README.txt at for explanation and examples).
     *
     * (Non-toroidal isn't useful to me right now, so it's not done.)
     * 
     * @param source Array of integers of  length > 2 for toroidal spectral 
     * analysis.
     * @param contiguous Whether or not to perform the analysis for contiguous
     * compressible blocks only.
     */
    public InformationSpectrum(int[] source, boolean contiguous) {
        isContiguous = contiguous;
        sourceArray = source;
        blockSizeFrequencies = new int[source.length];
        
        if (!contiguous) {
            maxBlockSize = source.length - 1;
            minBlockSize = 2;
            performAnalysis();
        } else {
            maxBlockSize = source.length / 2;
            minBlockSize = 1;
            performContiguousAnalysis();
        }
    }
    
    /**
     * Returns true if the spectral analysis was contiguous.
     */
    public boolean isContiguous() {
        return isContiguous;
    }
    
    /**
     * Returns a copy of the source array with which the InformationSpectrum was created.
     */
    public int[] getSourceArray() {
        return Arrays.copyOf(sourceArray, sourceArray.length);
    }
    
    /**
     * Returns the maximum size that a repeated block can be for the array with
     * which the InformationSpectrum was initialized.
     */
    public int getMaxBlockSize() {
        return maxBlockSize;
    }
    
    /**
     * Returns the minimum size that a repeated block can be for the array with which
     * the InformationSpectrum was initialized.
     */
    public int getMinBlockSize() {
        return minBlockSize;
    }
    
    /**
     * Returns the frequency with which blocks of a given size were repeated
     * within the initializing array (this corresponds to querying the Block
     * Size / Repetition Count tables in the README.txt).
     *
     * Valid block size parameters range from getMinBlockSize() to getMaxBlockSize().
     * Invalid queries return -1.
     */
    public int getFrequency(int blockSize) {
        return getBlockSizeFrequency(blockSize);
    }
    
    /**
     * Returns the frequency with which blocks of a given size were repeated
     * within the initializing array (this corresponds to querying the Block
     * Size / Repetition Count tables in the README.txt).
     *
     * Valid block size parameters range from getMinBlockSize() to getMaxBlockSize().
     * Invalid queries return -1.
     */
    public int getBlockSizeFrequency(int blockSize) {
        if (blockSize >= minBlockSize || blockSize <= maxBlockSize) {
            return blockSizeFrequencies[blockSize];
        } else {
            throw new ArrayIndexOutOfBoundsException("InformationSpectrum Error: Cannot request invalid block size frequency.");
        }
    }
    
    private void performAnalysis() {
        // for each block size
        for (int blockSize = minBlockSize; blockSize <= maxBlockSize; blockSize++) {
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
    
    private void performContiguousAnalysis() {
        // for each block size
        for (int blockSize = minBlockSize; blockSize <= getMaxBlockSize(); blockSize++) {
            // remember matched blocks for each size (including repetitions) by start index
            // so that they are not matched twice
            boolean[] patternFound = new boolean[sourceArray.length];
            // look for each block of length blockSize in the rest of the array
            for (int sourceBlockStart = 0; sourceBlockStart < getSourceArray().length; sourceBlockStart++) {
                // ignore blocks already matched
                if (patternFound[sourceBlockStart]) continue;
                // remember the right boundary of the contiguous match is needed so that it can be used when we look for left
                // for matches (we need to avoid overlap and it's a toroidal array)
                int rightBoundary = toroidalIndex(sourceBlockStart + blockSize - 1);
                // look for the current block in the next contiguous blocks of the same size.
                     // look for first match to the right of the current block
                for (int potentialMatchBlockStart = toroidalIndex(sourceBlockStart + blockSize);
                     // continue looking as long as the potential match block does not start or end inside the current block
                     toroidalIndex(potentialMatchBlockStart + blockSize) < sourceBlockStart
                       || potentialMatchBlockStart > toroidalIndex(sourceBlockStart + blockSize - 1);
                     // increment the potential match block index one block size at a time
                     potentialMatchBlockStart = toroidalIndex(potentialMatchBlockStart + blockSize)) {
                    // check for match
                    if (blocksMatch(sourceBlockStart, potentialMatchBlockStart, blockSize)) {
                        // mark the matching blocks as found
                        patternFound[sourceBlockStart] = true;
                        patternFound[potentialMatchBlockStart] = true;
                        // increment the block size frequency
                        setBlockSizeFrequency(blockSize, getBlockSizeFrequency(blockSize) + 1);
                        // set the right boundary
                        rightBoundary = toroidalIndex(potentialMatchBlockStart + blockSize - 1);
                    } else {
                        // stop looking matches if contiguity is broken
                        break;
                    }
                }
                
                int elementsUnchecked = rightBoundary > sourceBlockStart ?
                                           sourceArray.length - (rightBoundary - sourceBlockStart + 1) :
                                           sourceBlockStart - rightBoundary - 1;
                // look left of the source block for matches is long as there is a block
                // left to check between the source block start and the right boundary
                if (elementsUnchecked > blockSize) {
                    // look for the current block in the previous (to the left) contiguous blocks of the same size
                         // look for first match to the left of the current block
                    for (int potentialMatchBlockStart = toroidalIndex(sourceBlockStart - blockSize);
                         // continue looking as long as the potential match block does not start or end inside or on the right boundary
                         toroidalIndex(potentialMatchBlockStart + blockSize) < sourceBlockStart
                            || potentialMatchBlockStart > rightBoundary;
                         // decrement the potential match block index one block size at a time
                         potentialMatchBlockStart = toroidalIndex(potentialMatchBlockStart - blockSize)) {
                        // check for match
                        if (blocksMatch(sourceBlockStart, potentialMatchBlockStart, blockSize)) {
                            // mark the matching blocks as found
                            patternFound[sourceBlockStart] = true;
                            patternFound[potentialMatchBlockStart] = true;
                            // increment the block size frequency
                            setBlockSizeFrequency(blockSize, getBlockSizeFrequency(blockSize) + 1);
                        } else {
                            // stop looking matches if contiguity is broken
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Set the frequency of a particular block size.
     */
    private void setBlockSizeFrequency(int blockSize, int frequency) {
        if (blockSize >= minBlockSize || blockSize <= maxBlockSize) {
        blockSizeFrequencies[blockSize] = frequency;
        } else {
            throw new ArrayIndexOutOfBoundsException("InformationSpectrum Error: Cannot set invalid block size frequency.");
        }
    }
    
    /**
     * Returns whether or not the blocks of the specified size (starting at
     * either specified index) match exactly.  Does not care about overlap.
     * Checks toroidally.
     */
    private boolean blocksMatch(int startA, int startB, int blockSize) {
        for (int i = 0; i < blockSize; i++) {
            if (getSourceArray()[toroidalIndex(startA + i)] != getSourceArray()[toroidalIndex(startB + i)]) return false;
        }
        return true;
    }
    
    /**
     * Returns the toroidal array index for the source array of the spectrum. 
     */
    private int toroidalIndex(int i) {
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
    private int toroidalAccess(int[] source, int i) {
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
        String stringRep = "Source Array: {";
        for (int i : sourceArray) {
            stringRep += Integer.toString(i) + ",";
        }
        stringRep += stringRep.substring(0, stringRep.length() - 1) + "}\n";
        stringRep += isContiguous() ? "Contiguous Analysis:\n" : "Non-Contiguous Analysis:\n";
        stringRep += "Block Size - Repetition Count\n";
        for (int i = minBlockSize; i <= maxBlockSize; i++) {
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
