package Geometry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.JFrame;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import Math.Matrix4x4;
import Math.RotationY;
import Math.RotationZ;
import Math.Vector4;
import Math.Projection;
import Math.RotationX;
import Math.Scaling;
import Math.Translation;
import Bezier.BezierUtils;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This example reads the description of an object (a polygon) from a file and
 * draws it on a jPanel
 *
 * @author htrefftz
 */
public class DibujarBezier extends JPanel implements KeyListener {

    /**
     * Original (untransformed) PolygonObject
     */
    PolygonObject po;
    private double xm, ym, zm;

    Vector4[][] controlPoints;
    int STEPS = 50;

    /**
     * Transformed object to be drawn
     */
    PolygonObject transformedObject;
    PolygonObject surface;

    /**
     * Current transformations. This is the accumulation of transformations done
     * to the object
     */
    Matrix4x4 currentTransformation = new Matrix4x4();

    public static int FRAME_WIDTH = 600;
    public static int FRAME_HEIGHT = 400;

    public static int AXIS_SIZE = 500;

    Dimension size;
    Graphics2D g2d;
    /**
     * Distance to the projection plane.
     */
    int proyectionPlaneDistance;

    public DibujarBezier() {
        super();
        // El panel, por defecto no es "focusable". 
        // Hay que incluir estas líneas para que el panel pueda
        // agregarse como KeyListsener.
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);
    }

    /**
     * This method draws the object. The graphics context is received in
     * variable Graphics. It is necessary to cast the graphics context into
     * Graphics 2D in order to use Java2D.
     *
     * @param g Graphics context
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g2d = (Graphics2D) g;
        // Size of the window.
        size = getSize();

        // Draw the X axis
        g2d.setColor(Color.RED);
        drawOneLine(-DibujarBezier.AXIS_SIZE, 0, DibujarBezier.AXIS_SIZE, 0);

        // Draw the Y axis
        g2d.setColor(Color.GREEN);
        drawOneLine(0, -DibujarBezier.AXIS_SIZE, 0, DibujarBezier.AXIS_SIZE);

        // Draw the polygon object
        g2d.setColor(Color.BLUE);
        //po.drawObject(this);

        // Transform the object
        transformObject();

        // Apply UVN matrix
        applyUVN();

        // Apply projection
        applyProjection();

        // Draw the object
        transformedObject.drawObject(this);
        drawOneLine(0, 0, 0, 0);
        drawOneLine(1, 1, 1, 1);

        createBezierSurface(g2d);

    }

    /**
     * Apply the current transformation to the original object.
     * currentTransformation is the accumulation of the transforms that the user
     * has entered.
     */
    private void transformObject() {
        transformedObject = PolygonObject.transformObject(po, currentTransformation);
    }

    /**
     * Based on the position and orientation of the camera, create and apply the
     * UVN matrix.
     */
    private void applyUVN() {

    }

    /**
     * Create and apply the projection matrix First: create the projection
     * matrix. Then The parameter is the negative value of the distance from the
     * origin to the projection plane (see constant above)
     */
    private void applyProjection() {
        // Create the projection matrix
        // The parameter is the negative value of the
        // distance from the origin to the projection plane
        Projection proj = new Projection(-proyectionPlaneDistance);
        // Apply the projection using method transformObject in PolygonObject
        // The input object is transformedObject
        // The outout object is also transformedObject
        // The transformation to be applyed is the projection matrix
        // just created
        transformedObject = PolygonObject.transformObject(transformedObject, proj);
    }

    /**
     * This function draws one line on this JPanel. A mapping is done in order
     * to: - Have the Y coordinate grow upwards - Have the origin of the
     * coordinate system in the middle of the panel
     *
     * @param x1 Starting x coordinate of the line to be drawn
     * @param y1 Starting y coordinate of the line to be drawn
     * @param x2 Ending x coordinate of the line to be drawn
     * @param y2 Ending x coordinate of the line to be drawn
     */
    public void drawOneLine(int x1, int y1, int x2, int y2) {

        x1 = x1 + size.width / 2;
        x2 = x2 + size.width / 2;

        y1 = size.height / 2 - y1;
        y2 = size.height / 2 - y2;

        g2d.drawLine(x1, y1, x2, y2);
    }

    /**
     * Read the description of the object from the given file
     *
     * @param fileName Name of the file with the object description
     */
    public void readObjectDescription(String fileName) {
        Scanner in;
        po = new PolygonObject();
        surface = new PolygonObject();
        try {

            in = new Scanner(new File(fileName));
            // Read the number of vertices
            int numVertices = in.nextInt();
            Vector4[] vertexArray = new Vector4[numVertices];
            // Read the vertices
            for (int i = 0; i < numVertices; i++) {
                // Read a vertex
                int x = in.nextInt();
                xm += x;
                int y = in.nextInt();
                ym += y;
                int z = in.nextInt();
                zm += z;
                vertexArray[i] = new Vector4(x, y, z);
            }

            //Here the centroid is calculated, around this point the object is rotated
            //The centroid is an average of all x,y,z
            po.setCentroid(new Vector4(xm / numVertices, ym / numVertices, zm / numVertices));

            // Read the number of edges
            int numEdges = in.nextInt();
            // Read the edges
            for (int i = 0; i < numEdges; i++) {
                // Read an edge
                int start = in.nextInt();
                int end = in.nextInt();
                Edge edge = new Edge(vertexArray[start], vertexArray[end]);
                po.addEdge(edge);
            }
            // Read the Project Plane Distance to the virtual camera
            proyectionPlaneDistance = in.nextInt();

            controlPoints = new Vector4[3][4];
            controlPoints[0][0] = new Vector4(-150, -100, 1100);
            controlPoints[1][0] = new Vector4(-150, 0, 1000);
            controlPoints[2][0] = new Vector4(-150, -100, 900);

            controlPoints[0][1] = new Vector4(-50, 0, 1100);
            controlPoints[1][1] = new Vector4(-50, 0, 1000);
            controlPoints[2][1] = new Vector4(-50, 0, 900);

            controlPoints[0][2] = new Vector4(50, 0, 1100);
            controlPoints[1][2] = new Vector4(50, 0, 1000);
            controlPoints[2][2] = new Vector4(50, 0, 900);

            controlPoints[0][3] = new Vector4(150, 100, 1100);
            controlPoints[1][3] = new Vector4(150, 0, 1000);
            controlPoints[2][3] = new Vector4(150, 100, 900);

        } catch (FileNotFoundException e) {
            System.out.println(e);
        }

    }

    public void createBezierSurface(Graphics2D g2d) {
        Vector4[] points = new Vector4[STEPS + 1];
        double step = 1d / STEPS;

        double u = 0;
        double v = 0;
        for (int i = 0; i <= STEPS; i++) {
            points[i] = bezier3x4(u, v, 11);
            u += step;
            v += step;
        }

        for (int i = 0; i < points.length - 3; i += 3) {
            surface.addEdge(new Edge(points[i], points[i + 1]));
            surface.addEdge(new Edge(points[i + 1], points[i + 2]));
            surface.addEdge(new Edge(points[i + 2], points[i + 3]));
        }

        surface.drawObject(this);
    }

    public Vector4 bezier3x4(double u, double v, int n) {
        double x = 0;
        double y = 0;
        double z = 0;
        for (int i = 0; i < 3; i++) {
            double blendu = BezierUtils.blending(u, 3, i);
            for (int j = 0; j < 4; j++) {
                double blendv = BezierUtils.blending(v, 4, j);
                x += controlPoints[i][j].getX() * blendu * blendv;
                y += controlPoints[i][j].getY() * blendu * blendv;
                z += controlPoints[i][j].getZ() * blendu * blendv;
            }
        }
        System.out.println(x + "," + y + "," + z);
        return new Vector4(x, y, z);
    }

    /**
     * Draw a line segment. The coordinates are transformed so that the origin
     * is at the middle of the frame
     *
     * @param g2d Graphics context
     * @param p0 inicial point
     * @param p1 final point
     */
    private void drawEdge(Graphics2D g2d, Vector4 p0, Vector4 p1) {
        int x0 = (int) p0.getX() + FRAME_WIDTH / 2;
        int y0 = FRAME_HEIGHT / 2 - (int) p0.getY();
        int x1 = (int) p1.getX() + FRAME_WIDTH / 2;
        int y1 = FRAME_HEIGHT / 2 - (int) p1.getY();
        g2d.drawLine(x0, y0, x1, y1);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        System.out.println("Key Released");
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        System.out.println("Key Pressed");
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_A: {
                // Left
                Translation trans = new Translation(-10, 0, 0);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_D: {
                // Right
                Translation trans = new Translation(10, 0, 0);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_W: {
                // Up
                Translation trans = new Translation(0, 10, 0);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_S: {
                // Down
                Translation trans = new Translation(0, -10, 0);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_R:
                // Reset
                currentTransformation = new Matrix4x4();
                break;
            case KeyEvent.VK_X: {
                // Rotate +X
                RotationX trans = new RotationX(5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_C: {
                // Rotate -X
                RotationX trans = new RotationX(-5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_V: {
                // Rotate +Z
                RotationZ trans = new RotationZ(5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_B: {
                // Rotate -Z
                RotationZ trans = new RotationZ(-5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_UP:
                // projection distance +10
                proyectionPlaneDistance += 10;
                break;
            case KeyEvent.VK_DOWN:
                // projection distance -10
                proyectionPlaneDistance -= 10;
                break;
            case KeyEvent.VK_N: {
                // Rotate +Y
                RotationY trans = new RotationY(5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_M: {
                //Rotate -Y
                RotationY trans = new RotationY(-5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_F: {
                //Scaling +
                Scaling trans = new Scaling(0.7, 0.7, 0.7);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_G: {
                //Scaling -
                Scaling trans = new Scaling(1 / 0.7, 1 / 0.7, 1 / 0.7);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_J: {
                Projection trans = new Projection(10);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            case KeyEvent.VK_K: {
                Projection trans = new Projection(-10);
                currentTransformation = Matrix4x4.times(currentTransformation, trans);
                break;
            }
            // Self rotation +X
            case KeyEvent.VK_I: {
                RotationX rotx = new RotationX(5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, createSelfRotationMatrix(rotx));
                break;
            }
            // Self rotation -X
            case KeyEvent.VK_O: {
                RotationX rotxi = new RotationX(-5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, createSelfRotationMatrix(rotxi));
                break;
            }
            // Self rotation +Y
            case KeyEvent.VK_9: {
                RotationY roty = new RotationY(5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, createSelfRotationMatrix(roty));
                break;
            }
            // Self rotation -Y
            case KeyEvent.VK_0: {
                RotationY rotyi = new RotationY(-5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, createSelfRotationMatrix(rotyi));
                break;
            }
            // Self rotation +Z
            case KeyEvent.VK_7: {
                RotationZ rotz = new RotationZ(5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, createSelfRotationMatrix(rotz));
                break;
            }
            // Self rotation -Z
            case KeyEvent.VK_8: {
                RotationZ rotzi = new RotationZ(-5d * Math.PI / 180d);
                currentTransformation = Matrix4x4.times(currentTransformation, createSelfRotationMatrix(rotzi));
                break;
            }
            // Go to origin
            case KeyEvent.VK_Q: {
                Translation origin = new Translation(-po.getCentroid().getX(), -po.getCentroid().getY(), -po.getCentroid().getZ());
                currentTransformation = Matrix4x4.times(currentTransformation, origin);
                break;
            }
            default:
                break;
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        System.out.println("Key Typed");
    }

    /**
     * This method creates a composed matrix to rotate around a pivot, the pivot
     * is the centroid of the casita calculated as an average of all the points
     * x,y and z.
     *
     * @param rotationMatrix is the rotation matrix that will be used
     * @return a composed transformation matrix of the form
     * [T^-1]*[Rotation]*[T]
     */
    public Matrix4x4 createSelfRotationMatrix(Matrix4x4 rotationMatrix) {
        //This matrix translates the casita to the origin 0,0,0
        Translation origin = new Translation(-po.getCentroid().getX(), -po.getCentroid().getY(), -po.getCentroid().getZ());
        //This matrix translates the casita to the original point
        Translation back = new Translation(po.getCentroid().getX(), po.getCentroid().getY(), po.getCentroid().getZ());
        //Here the complete transform is returned
        return Matrix4x4.times(Matrix4x4.times(back, rotationMatrix), origin);
    }

    public static void main(String[] args) {
        DibujarBezier dc = new DibujarBezier();
        // Read the file with the object description
        dc.readObjectDescription("objeto3D.txt");
        // Create a new Frame
        JFrame frame = new JFrame("Bezier Surface");
        // Upon closing the frame, the application ends
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add a panel called DibujarBezier
        frame.add(dc);
        // Asignarle tamaño
        frame.setSize(DibujarBezier.FRAME_WIDTH, DibujarBezier.FRAME_HEIGHT);
        // Put the frame in the middle of the window
        frame.setLocationRelativeTo(null);
        // Show the frame
        frame.setVisible(true);
    }
}
