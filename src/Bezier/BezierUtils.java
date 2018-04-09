package Bezier;

/**
 *
 * @author laurasofia180
 */
public class BezierUtils {
    
    /**
     * Compute the value of the blending functions at u
     * @param u value of the curve pararmeter
     * @param n there are n+1 control points
     * @param k current value of k
     * @return Value of the blending function
     */
    public static double blending(double u, int n, int k) {
        double ret = coeff(n, k) * Math.pow(u, k) * Math.pow(1-u, n-k);
        return ret;
    }
    
    /**
     * Compute binomial coefficients
     * @param n
     * @param k
     * @return 
     */
    public static int coeff(int n, int k) {
        int res = fact(n) / (fact(k) * fact(n-k));
        return res;
    }
    
    /**
     * Compute factorial
     * @param n
     * @return 
     */
    public static int fact(int n) {
        int acum = 1;
        for(int i = 1; i <= n; i++) {
            acum *= i;
        }
        return acum;
    }
    
}
