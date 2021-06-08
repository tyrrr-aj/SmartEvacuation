package root.geometry;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineTests {
    @ParameterizedTest
    @CsvSource({
            "-2,0,4,0,true",
            "8,-2,19,-2,true",
            "-3.5,0,-1,8,true",
            "3,4,7,-10,true",
            "0,0,-0.1,4,false",
            "5,2,-1,2,false",
            "1,2,1,5,false"
    })
    public void LinesEarlierAlongAxisHorizontalAxisTest(double x1, double y1, double x2, double y2, boolean expected) {
        Line axis = new Line(new Point(-1.0, 2.5), new Point(3.4, 2.5));
        Point firstPoint = new Point(x1, y1);
        Point secondPoint = new Point(x2, y2);

        assertEquals(expected, axis.pointLiesEarlierAlong(firstPoint, secondPoint));
    }

    @ParameterizedTest
    @CsvSource({
            "-2,0,-2,-6,true",
            "13,8,13,6,true",
            "-5,0,-1,-4,true",
            "-2,0,-2,1,false",
            "5,2,5,5,false",
            "6,2,-8,2.1,false",
            "-9,1.5,7,1.5,false"
    })
    public void LinesEarlierAlongAxisVerticalAxisTest(double x1, double y1, double x2, double y2, boolean expected) {
        Line axis = new Line(new Point(-2, 2.5), new Point(-2, 1));
        Point firstPoint = new Point(x1, y1);
        Point secondPoint = new Point(x2, y2);

        assertEquals(expected, axis.pointLiesEarlierAlong(firstPoint, secondPoint));
    }

    @ParameterizedTest
    @CsvSource({
            "-1,-2,3,6,true",
            "-5,-10,4,8,true",
            "10,-5,10,-4,true",
            "-2,-4,-2.5,-5,false",
            "0,2,-4,-2,false",
            "2,8,1,7,false"
    })
    public void LinesEarlierAlongAxisRandomAxisTest(double x1, double y1, double x2, double y2, boolean expected) {
        Line axis = new Line(new Point(0, 0), new Point(1, 2));
        Point firstPoint = new Point(x1, y1);
        Point secondPoint = new Point(x2, y2);

        assertEquals(expected, axis.pointLiesEarlierAlong(firstPoint, secondPoint));
    }
}
