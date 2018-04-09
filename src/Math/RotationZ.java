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
public class RotationZ extends Matrix4x4 {
    
    public RotationZ() {
        super();
    }
    
    public RotationZ(double theta) {
        double [][] mat = {
            {Math.cos(theta), -Math.sin(theta), 0d, 0d},
            {Math.sin(theta), Math.cos(theta), 0d, 0d},
            {0d, 0d, 1d, 0d},
            {0d, 0d, 0d, 1d}
        };
        matrix = mat;
    }
    
}
