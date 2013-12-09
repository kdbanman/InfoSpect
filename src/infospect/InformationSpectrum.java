package infospect;

/**
 * Data structure representing the information content (patterns and
 * repetition) of an array.
 * 
 * Any length N array can have repeated blocks (subarrays) ranging in length
 * from length 2 to length N-1.  (Length 1 blocks are ignored for reasons that
 * are explained later.)  For instance, the length 9 array
 *   {1,1,0,2,2,1,1,0,2}
 * has three length 2 blocks that repeat twice, {1.1}, {1,0}, and {0,2}, so 6
 * total length 2 block repetitions. It also has two length 3 blocks that repeat
 * twice, {1,1,0} and {1,0,2}, and it has one length 4 block that repeats twice,
 * {1,1,0,2}.  The information spectrum is the following table:
 * ---------------------------------
 * | Block Size | Repetition Count |
 * ---------------------------------
 * |      2     |         6        |
 * |      3     |         4        |
 * |      4     |         2        |
 * ---------------------------------
 * 
 * If the an array is treated toroidally, where the first and last members of
 * the array are neighbors of each other, then the spectrum may change. First,
 * repeated blocks can now be up to length N-1.  Length N blocks are ignored
 * because a length N toroidal array is defined as a repetition of length N. 
 * Consider the length 9 toroidal array
 *   {2,1,0,2,2,2,1,0,2}
 * Repetition analysis (assuming I have made no errors) yields:
 * --------------------------------------
 * | Repeating block | Repetition Count |
 * --------------------------------------
 * |     2,1         |        2         |
 * |     1,0         |        2         |
 * |     0,2         |        2         |
 * |     2,2         |        3         |
 * |    2,1,0        |        2         |
 * |    1,0,2        |        2         |
 * |    0,2,2        |        2         |
 * |    2,2,1        |        2         |
 * |   2,1,0,2       |        2         |
 * |   1,0,2,2       |        2         |
 * |   2,2,1,0       |        2         |
 * |  2,1,0,2,2      |        2         |
 * |  2,2,1,0,2      |        2         |
 * | 2,2,1,0,2,2     |        2         |
 * --------------------------------------
 * This reduces to an information spectrum of:
 * ---------------------------------
 * | Block Size | Repetition Count |
 * ---------------------------------
 * |      2     |         9        |
 * |      3     |         8        |
 * |      4     |         6        |
 * |      5     |         4        |
 * |      6     |         0        |
 * |      7     |         0        |
 * |      8     |         0        |
 * ---------------------------------
 * 
 * It is worth noting that greater quantities in an information spectrum
 * correspond to less information in the source array, because more repetition
 * means more compressibility.  For instance, see the length 5 toroidal array
 *   {1,1,1,1,1}
 * which has the spectrum
 * ---------------------------------
 * | Block Size | Repetition Count |
 * ---------------------------------
 * |      2     |         5        |
 * |      3     |         5        |
 * |      4     |         5        |
 * ---------------------------------
 * The maximal repetition corresponds to minimal information content.
 * For contrast, consider the array
 *   {2,1,0,1,2,0}
 * It has no repeating blocks.  Length 1 blocks are repeated, but they are not
 * compressible blocks because they are already minimally represented.
 * 
 * @author kdbanman
 */
public class InformationSpectrum<T extends Comparable> {
    
    /**
     * 
     * @param source Array for non-toroidal spectral analysis.
     */
    public InformationSpectrum(T[] source) {
        this(source, false);
    }
    
    /**
     * 
     * @param source Array for information spectral analysis.
     * @param toroidal Pass rue for toroidal analysis, false otherwise.
     */
    public InformationSpectrum(T[] source, boolean toroidal) {
        
    }
    
    private T toroidalAccess(T[] source, integer i) {
        i = i % N;
        if (i < 0) {
            return source[N + i];
        } else {
            return source[i];
        }
    }
}
