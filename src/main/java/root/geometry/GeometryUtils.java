package root.geometry;

public abstract class GeometryUtils {
    public static int orient(Point a, Point b, Point c) { // returns -1 for point lying to the right of line
        final double epsilon = Math.pow(10, -12);

        double[][] matrix = buildOrientMatrix(a, b, c);
        double determinant = determinant_3x3(matrix);

        if (determinant <= -epsilon) {
            return -1;
        }
        else if (determinant >= epsilon) {
            return 1;
        }
        else {
            return 0;
        }
    }

    private static double[][] buildOrientMatrix(Point a, Point b, Point c) {
        return new double[][] {
            new double[] {a.x(), b.x(), c.x()},
            new double[] {a.y(), b.y(), c.y()},
            new double[] {1.0, 1.0, 1.0}
        };
    }

    private static double determinant_3x3(double[][] matrix) {
        return matrix[0][0] * matrix[1][1] * matrix[2][2] +
                matrix[1][0] * matrix[2][1] * matrix[0][2] +
                matrix[0][1] * matrix[1][2] * matrix[2][0] -
                matrix[0][2] * matrix[1][1] * matrix[2][0] -
                matrix[0][1] * matrix[1][0] * matrix[2][2] -
                matrix[1][2] * matrix[2][1] * matrix[0][0];
    }
}
