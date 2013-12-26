package infospect;

import java.io.IOException;
import java.util.Scanner;

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
        System.out.println(testSpect);
        
        testSpect = new InformationSpectrum(test, true);
        System.out.println(testSpect);
        System.out.println("---------------------------------\n");
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
        
        System.out.println("Processing arrays (non-contiguous analysis)...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfArrays; i++) {
            InformationSpectrum test = new InformationSpectrum(tests[i]);
        }
        long finishTime = System.currentTimeMillis();
        
        double arraysPerSec = (double) (numberOfArrays * 1000) / (double) (finishTime - startTime);
        System.out.println("  " + arraysPerSec + " arrays processed per second");
        
        System.out.println("Processing arrays (contiguous analysis)...");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfArrays; i++) {
            InformationSpectrum test = new InformationSpectrum(tests[i], true);
        }
        finishTime = System.currentTimeMillis();
        
        arraysPerSec = (double) (numberOfArrays * 1000) / (double) (finishTime - startTime);
        System.out.println("  " + arraysPerSec + " arrays processed per second\n");
    }
    
    private static void runTests() {
        
        // print examples from documentation
        // non-contiguous
        testArray(new int[]{2,1,0,2,2,2,1,0,2});
        // contiguous documentation examples
        testArray(new int[]{0,1,2,0,1,1});
        testArray(new int[]{0,1,0,1,1,1,0,1});
        //both
        testArray(new int[]{1,1,1,1,1});
        
        // print examples not from documentation
        testArray(new int[]{1,1,0,1,1,0,1,1,0,2});
        testArray(new int[]{0,1,1,0,1,1,0,1,1});
        testArray(new int[]{0,1,2,2,1,0,0,1,2,2,1,1});
        testArray(new int[]{22,11,0,22,22,22,11,0,22});
        testArray(new int[]{2,1,0,1,2,0});
        testArray(new int[]{9,8,7,8,9,7});
        
        System.out.println("Running correctness tests...");
        // assert correctness
        boolean testFailed = false;
        InformationSpectrum test = new InformationSpectrum(new int[]{2,1,0,2,2,2,1,0,2}, false);
        testFailed |= !assertBounds(test, 2, 8);
        testFailed |= !assertFrequency(test, new int[]{-1,-1,5,4,3,2,1,0,0});
        
        test = new InformationSpectrum(new int[]{0,1,2,0,1,1}, true);
        testFailed |= !assertBounds(test, 1, 3);
        testFailed |= !assertFrequency(test, new int[]{-1,1,0,0});
        
        test = new InformationSpectrum(new int[]{0,1,0,1,1,1,0,1}, true);
        testFailed |= !assertBounds(test, 1, 4);
        testFailed |= !assertFrequency(test, new int[]{-1,2,4,0,0});
        
        test = new InformationSpectrum(new int[]{1,1,1,1,1}, false);
        testFailed |= !assertBounds(test, 2, 4);
        testFailed |= !assertFrequency(test, new int[]{-1,-1,4,4,4});
        
        test = new InformationSpectrum(new int[]{1,1,1,1,1}, true);
        testFailed |= !assertBounds(test, 1, 2);
        testFailed |= !assertFrequency(test, new int[]{-1,4,3});
        
        if (testFailed) {
            System.out.println("\nTESTS FAILED!");
            System.out.println("=============");
        } else {
            System.out.println("\nAll tests Passed!");
            System.out.println("=================");
        }
        
        Scanner in = new Scanner(System.in);
        
        System.out.println("\nRun performance tests (y/N)? ");
        boolean performanceTests = false;
        String choice = in.nextLine();
        performanceTests = choice.equalsIgnoreCase("y");
        if (performanceTests) {
            runsPerSecond(1000, 10);
            runsPerSecond(1000, 20);
            runsPerSecond(100, 50);
            runsPerSecond(20, 100);
            runsPerSecond(5, 130);
        }
    }
    
    /**
     * pass test spectrum along with desired min and max block sizes.
     */
    private static boolean assertBounds(InformationSpectrum test, int min, int max) {
        boolean passed = true;
        if (test.getMinBlockSize() != min) {
            passed = false;
            System.err.println("TEST FAILED: minBlocksize() != " + min + " for spectrum:");
            System.err.println(test);
        }
        if (test.getMaxBlockSize() != max) {
            passed = false;
            System.err.println("TEST FAILED: maxBlocksize() != " + max + " for spectrum:");
            System.err.println(test);
        }
        return passed;
    }
    
    /**
     * pass {x,x,freq1,freq2,...,freqN} for non contiguous,
     * and  {x,freq1,freq2,...,freqN} for contiguous.
     */
    private static boolean assertFrequency(InformationSpectrum test, int[] frequencies) {
        if (frequencies.length != test.getMaxBlockSize() + 1) {
            throw new ArrayIndexOutOfBoundsException("Incorrectly sized reference array passed to assertion method");
        }
        
        boolean passed = true;
        for (int i = test.getMinBlockSize(); i <= test.getMinBlockSize(); i++) {
            if (test.getFrequency(i) != frequencies[i]) {
                passed = false;
                System.err.println("TEST FAILED: getFrequency(" + i + ") != " + frequencies[i] + " in spectrum:");
                System.err.println(test);
            }
        }
        return passed;
    }
    
    /**
     * Convenience method to run hard-coded tests, including performance tests
     * for your machine.
     */
    public static void main(String[] args) {
        runTests();
    }
}
