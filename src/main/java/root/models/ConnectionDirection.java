package root.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ConnectionDirection {
        NONE,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT;

        private static final List<ConnectionDirection> directionsClockwise = List.of(TOP, BOTTOM, LEFT, RIGHT);

        public static List<ConnectionDirection> getClockwiseStartingFrom(ConnectionDirection firstDirection) {
                int dirIndex = directionsClockwise.indexOf(firstDirection);

                List<ConnectionDirection> directions = new ArrayList<>(directionsClockwise);
                Collections.rotate(directions, dirIndex);
                return directions;
        }
}
