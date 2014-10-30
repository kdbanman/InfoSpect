package infospect;

import java.util.Arrays;

/* 
 * @author kdbanman
 * @see https://github.com/kdbanman/InfoSpect
 */
public class InformationSpectrum {
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
     * for the passed array (See README.txt at for explanation and examples).
     * 
     * @param source Array of integers of  length > 2 for toroidal spectral 
     * analysis.
     */
    public InformationSpectrum(int[] source) {
        sourceArray = source;
        blockSizeFrequencies = new int[source.length];
        
        maxBlockSize = source.length - 1;
        minBlockSize = 2;
        performAnalysis();
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
     * Shorthand wrapper for getBlockSizeFrequency()
     * 
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
     *  Returns a well-formatted table of the spectral analysis data.
     */
    @Override
    public String toString() {
        String stringRep = "Source Array: {";
        for (int i : sourceArray) {
            stringRep += Integer.toString(i) + ",";
        }
        stringRep += stringRep.substring(0, stringRep.length() - 1) + "}\n";
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
