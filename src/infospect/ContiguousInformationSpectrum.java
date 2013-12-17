package infospect;

/**
 *
 * @author kdbanman
 */
public class ContiguousInformationSpectrum extends InformationSpectrum {
    public ContiguousInformationSpectrum(int[] source) {
        super(source);
    }
    
    @Override
    protected void analyzeArray() {
        // for each block size
        for (int blockSize = 2; blockSize <= getMaxBlockSize(); blockSize++) {
            // if a block is repeated at an index, then a pattern has been found
            // and the repeated block shouldn't be used to find other repetition
            boolean[] patternFound = new boolean[getSourceArray().length];
            // look for each block of length blockSize in the rest of the array
            for (int matchSourceBlockStart = 0; matchSourceBlockStart < getSourceArray().length; matchSourceBlockStart++) {
                // ignore the patterns of this blockSize that are already found
                if (patternFound[matchSourceBlockStart]) continue;
                // look for the current block in the rest of the array.  don't look for yourself within yourself. that's silly.
                for (int potentialMatchBlockStart = matchSourceBlockStart + 1; potentialMatchBlockStart <= toroidalIndex(matchSourceBlockStart - 1); potentialMatchBlockStart++) {
                    // if the first entries of the match source block and the block currently under
                    // inspection match, then continue inspection
                    boolean potentialMatch = getSourceArray()[matchSourceBlockStart] == getSourceArray()[potentialMatchBlockStart];
                    if (potentialMatch) {
                        for (int i = 1; i < blockSize; i++) {
                            potentialMatch = toroidalAccess(getSourceArray(), matchSourceBlockStart + i) == toroidalAccess(getSourceArray(), potentialMatchBlockStart + i);
                            if (!potentialMatch) break;
                        }
                        if (potentialMatch) {
                            patternFound[potentialMatchBlockStart] = true;
                            setBlockSizeFrequency(blockSize, getBlockSizeFrequency(blockSize) + 1);
                        }
                    }
                }
            }
        }
    }
}
