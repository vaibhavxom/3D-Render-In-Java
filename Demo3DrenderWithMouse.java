package demo3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.awt.geom.Point2D;

public class Demo3DrenderWithMouse {

    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Renderer with Mouse Control");
        RenderPanel panel = new RenderPanel();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    static class RenderPanel extends JPanel implements MouseMotionListener {
        private double angleX = 0, angleY = 0;
        private int lastX, lastY;
        private java.util.List<Triangle> triangles;

        public RenderPanel() {
            this.addMouseMotionListener(this);
            triangles = create3DObject();
        }

        private List<Triangle> create3DObject() {
            // Create a tetrahedron
            Point3D p1 = new Point3D(1, 1, 1);
            Point3D p2 = new Point3D(-1, -1, 1);
            Point3D p3 = new Point3D(-1, 1, -1);
            Point3D p4 = new Point3D(1, -1, -1);

            List<Triangle> tris = new ArrayList<>();
            tris.add(new Triangle(p1, p2, p3));
            tris.add(new Triangle(p1, p2, p4));
            tris.add(new Triangle(p1, p3, p4));
            tris.add(new Triangle(p2, p3, p4));

            // Recursively subdivide triangles for smoother rendering
            return inflate(tris, 3);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.GREEN);
            int w = getWidth(), h = getHeight();

            for (Triangle tri : triangles) {
                Point3D[] r = new Point3D[]{
                        rotate(tri.p1), rotate(tri.p2), rotate(tri.p3)
                };

                int[] xPoints = new int[3];
                int[] yPoints = new int[3];
                for (int i = 0; i < 3; i++) {
                    Point2D projected = project(r[i]);
                    xPoints[i] = (int) projected.getX();
                    yPoints[i] = (int) projected.getY();
                }

                g2.setColor(Color.GREEN);
                g2.drawPolygon(xPoints, yPoints, 3);

                // Optional: fill for better visibility
                g2.setColor(new Color(0, 255, 0, 40));  // Transparent fill
                g2.fillPolygon(xPoints, yPoints, 3);
            }
        }

        private Point3D rotate(Point3D p) {
            double cosY = Math.cos(angleY), sinY = Math.sin(angleY);
            double cosX = Math.cos(angleX), sinX = Math.sin(angleX);

            double x = p.x * cosY - p.z * sinY;
            double z = p.x * sinY + p.z * cosY;
            double y = p.y;

            double newY = y * cosX - z * sinX;
            double newZ = y * sinX + z * cosX;

            return new Point3D(x, newY, newZ);
        }

        private Point2D project(Point3D p) {
            double scale = 1000 / (5 + p.z);
            double x = p.x * scale + getWidth() / 2.0;
            double y = p.y * scale + getHeight() / 2.0;
            return new Point2D.Double(x, y);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int dx = e.getX() - lastX;
            int dy = e.getY() - lastY;
            angleY += dx * 0.01;
            angleX += dy * 0.01;
            lastX = e.getX();
            lastY = e.getY();
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
        }

        // Recursive inflate method
        private List<Triangle> inflate(List<Triangle> tris, int depth) {
            List<Triangle> result = new ArrayList<>(tris);
            for (int i = 0; i < depth; i++) {
                result = inflateOnce(result);
            }
            return result;
        }

        // One-level triangle subdivision
        private List<Triangle> inflateOnce(List<Triangle> tris) {
            List<Triangle> result = new ArrayList<>();
            for (Triangle t : tris) {
                Point3D a = midpoint(t.p1, t.p2);
                Point3D b = midpoint(t.p2, t.p3);
                Point3D c = midpoint(t.p3, t.p1);

                result.add(new Triangle(t.p1, a, c));
                result.add(new Triangle(t.p2, a, b));
                result.add(new Triangle(t.p3, b, c));
                result.add(new Triangle(a, b, c));
            }
            return result;
        }

        private Point3D midpoint(Point3D p1, Point3D p2) {
            return new Point3D(
                    (p1.x + p2.x) / 2,
                    (p1.y + p2.y) / 2,
                    (p1.z + p2.z) / 2
            );
        }
    }

    static class Point3D {
        double x, y, z;

        public Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static class Triangle {
        Point3D p1, p2, p3;

        public Triangle(Point3D p1, Point3D p2, Point3D p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }
    }
}
