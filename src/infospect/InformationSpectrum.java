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
public class InformationSpectrum {
    // maximum possible size of block repeated within source array
    private final int maxBlockSize;
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
        maxBlockSize = source.length - 1;
        blockSizeFrequencies = new int[source.length];
        
        // for each block size
        for (int blockSize = 2; blockSize <= maxBlockSize; blockSize++) {
            // if a block is repeated at an index, then a pattern has been found
            // and the repeated block shouldn't be used to find other repetition
            boolean[] patternFound = new boolean[source.length];
            // look for each block of length blockSize in the rest of the array
            for (int matchSourceBlockStart = 0; matchSourceBlockStart < source.length; matchSourceBlockStart++) {
                // ignore the patterns of this blockSize that are already found
                if (patternFound[matchSourceBlockStart]) continue;
                // look for the current block in the rest of the array
                for (int potentialMatchBlockStart = 0; potentialMatchBlockStart < source.length; potentialMatchBlockStart++) {
                    // don't look for yourself within yourself. that's silly.
                    if (potentialMatchBlockStart == matchSourceBlockStart) continue;
                    // if the first entries of the match source block and the block currently under
                    // inspection match, then continue inspection
                    boolean potentialMatch = source[matchSourceBlockStart] == source[potentialMatchBlockStart];
                    if (potentialMatch) {
                        for (int i = 1; i < blockSize; i++) {
                            potentialMatch = toroidalAccess(source, matchSourceBlockStart + i) == toroidalAccess(source, potentialMatchBlockStart + i);
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
     * Returns the maximum size that a repeated block can be for the array with
     * which the InformationSpectrum was initialized.  (The minimum is 2.)
     */
    public int getMaxBlockSize() {
        return maxBlockSize;
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
    public int getBlockSizeRepetitionCount(int blockSize) {
        if (blockSize >= 2 || blockSize <= maxBlockSize) {
            return blockSizeFrequencies[blockSize];
        } else {
            System.out.println("InformationSpectrum Error: Cannot request negative block size frequency.");
            return -1;
        }
    }
    
    /**
     * toroidal array accessor
     */
    private int toroidalAccess(int[] source, int i) {
        i = i % source.length;
        if (i < 0) {
            return source[source.length + i];
        } else {
            return source[i];
        }
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
            String freq = Integer.toString(getBlockSizeRepetitionCount(i));
            stringRep += blockSize + "           - ".substring(blockSize.length()) + freq + "\n";
        }
        return stringRep;
        
    }
    
    /**
     * Static wrapper of constructor so that library may be used statically, 
     * returns the InformationSpectrum corresponding to the passed source array.
     */
    public static InformationSpectrum analyzeArray(int[] source) {
        return new InformationSpectrum(source);
    }
    
    /**
     *  Test passed array with command line output for feedback.
     */
    private static void testArray(int[] test) {
        String testStr = "{";
        for (int i : test) {
            testStr += Integer.toString(i) + ",";
        }
        testStr = testStr.substring(0, testStr.length() - 1) + "}";
        System.out.println("Analyzing " + testStr + "...");
        InformationSpectrum trivial = analyzeArray(test);
        System.out.println("Result:");
        System.out.println(trivial);
    }
    
    private static void runsPerSecond(int numberOfArrays, int arrayLength) {
        
        System.out.println("Creating " + numberOfArrays + " arrays of length " + arrayLength + "...");
        // make a number of random test arrays of specified length
        int[][] tests = new int[numberOfArrays][arrayLength];
        for (int i = 0; i < numberOfArrays; i++) {
            tests[i] = new int[arrayLength];
            for (int j = 0; j < arrayLength; j++) {
                tests[i][j] = (int)(Math.random() * 3);
            }
        }
        
        System.out.println("Processing arrays...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfArrays; i++) {
            InformationSpectrum test = new InformationSpectrum(tests[i]);
        }
        long finishTime = System.currentTimeMillis();
        
        long arraysPerSec = numberOfArrays * 1000 / (finishTime - startTime);
        System.out.println("  " + (int) arraysPerSec + " arrays processed per second\n");
    }
    
    private static void runTests() {
        testArray(new int[]{1,1,1,1,1});
        testArray(new int[]{1,1,0,1,1,0,1,1,0,2});
        testArray(new int[]{0,1,1,0,1,1,0,1,1});
        testArray(new int[]{0,1,2,2,1,0,0,1,2,2,1,1});
        testArray(new int[]{2,1,0,2,2,2,1,0,2});
        testArray(new int[]{22,11,0,22,22,22,11,0,22});
        testArray(new int[]{2,1,0,1,2,0});
        testArray(new int[]{9,8,7,8,9,7});
        
        runsPerSecond(10000, 10);
        runsPerSecond(10000, 20);
        runsPerSecond(5000, 50);
        runsPerSecond(1000, 100);
        runsPerSecond(200, 150);
    }
    
    /**
     * Convenience method to run hard-coded tests, including performance tests
     * for your machine.
     */
    public static void main(String[] args) {
        runTests();
    }
}
