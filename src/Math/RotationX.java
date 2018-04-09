/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

/**
 *
 * @author helmuthtrefftz
 */
public class RotationX extends Matrix4x4 {
    
    public RotationX() {
        super();
    }
    
    public RotationX(double theta) {
        double [][] mat = {
            {1d, 0d, 0d, 0d},
            {0d, Math.cos(theta), -Math.sin(theta), 0d},
            {0d, Math.sin(theta), Math.cos(theta), 0d},
            {0d, 0d, 0d, 1d}
        };
        matrix = mat;
    }
}
