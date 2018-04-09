/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

/**
 *
 * @author htrefftz
 */
public class Projection extends Matrix4x4 {
    
    public Projection() {
        super();
    }
    
    public Projection(double d) {
        super();
        matrix [0][0]= 1;
        matrix [1][1]= 1;
        matrix [2][2]= 1;
        matrix [3][3] = 1;
        matrix [3][2] = 1/d;
    }
}
