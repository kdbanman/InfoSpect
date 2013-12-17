package infospect;

/**
 *
 * @author kdbanman
 */
public class InfoSpectTests {
    
    
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
        
        InformationSpectrum testSpect = new InformationSpectrum(test);
        System.out.println("Result:");
        System.out.println(testSpect);
        
        testSpect = new InformationSpectrum(test, true);
        System.out.println("Contiguous Result:");
        System.out.println(testSpect);
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
        // non-contiguous documentation example
        testArray(new int[]{2,1,0,2,2,2,1,0,2});
        
        // contiguous documentation examples
        testArray(new int[]{0,1,2,0,1,1});
        testArray(new int[]{0,1,0,1,1,1,0,1});
        
        //both
        testArray(new int[]{1,1,1,1,1});
        
        testArray(new int[]{1,1,0,1,1,0,1,1,0,2});
        testArray(new int[]{0,1,1,0,1,1,0,1,1});
        testArray(new int[]{0,1,2,2,1,0,0,1,2,2,1,1});
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
