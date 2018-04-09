/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

/**
 *
 * @author sofia
 */
public class Scaling extends Matrix4x4 {
    
    public Scaling() {
        super();
    }
    
    public Scaling(double sx, double sy, double sz) {
        super();
        double [][] mat = {
            {sx, 0d, 0d, 0d},
            {0d, sy, 0d, 0d},
            {0d, 0d, sz, 0d},
            {0d, 0d, 0d, 1d}
        };
        matrix = mat;
    }
    
}
