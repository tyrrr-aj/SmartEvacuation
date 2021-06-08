package root.neo4j.corridors;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.javatuples.Pair;
import root.geometry.GeometryUtils;
import root.geometry.Line;
import root.geometry.Point;
import root.models.ConnectionDirection;
import root.neo4j.AreaResult;
import root.neo4j.ConnectionResult;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CorridorSplitter {
    private final AreaResult corridor;
    private final List<LocalNeighbour> topGroup;
    private final List<LocalNeighbour> bottomGroup;
    private final List<LocalNeighbour> leftGroup;
    private final List<LocalNeighbour> rightGroup;

    private List<SubAreaResult[]> subareas;

    public CorridorSplitter(AreaResult corridor, Map<AreaResult, Point> neighboursWithDoors) {

        this.corridor = corridor;
        Map<ConnectionDirection, List<LocalNeighbour>> neighGroups = neighboursWithDoors
                .entrySet()
                .stream()
                .map(entry -> new LocalNeighbour(entry.getKey(), entry.getValue()))
                .collect(Collectors.groupingBy(LocalNeighbour::getDirection));
        neighGroups = sortedNeighbourGroups(neighGroups);

        topGroup = neighGroups.getOrDefault(ConnectionDirection.TOP, new ArrayList<>());
        bottomGroup = neighGroups.getOrDefault(ConnectionDirection.BOTTOM, new ArrayList<>());
        leftGroup = neighGroups.getOrDefault(ConnectionDirection.LEFT, new ArrayList<>());
        rightGroup = neighGroups.getOrDefault(ConnectionDirection.RIGHT, new ArrayList<>());

        splitCorridor();
    }

    public List<AreaResult> getAreas() {
        return subareas.stream().flatMap(Arrays::stream).collect(Collectors.toList());
    }

    public List<ConnectionResult> getConnections() {
        return subareas
                .stream()
                .flatMap(Arrays::stream)
                .flatMap(subAreaResult -> subAreaResult.getBidirectionalConnections().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private void splitCorridor() {
        List<SubAreaResult> verticalSlices = splitVertically();
        subareas = splitHorizontally(verticalSlices);
    }

    private List<SubAreaResult> splitVertically() {
        return splitAlongAxis(
                topGroup,
                bottomGroup,
                corridor.getHorizontalAxis(),
                () -> new SubAreaResultBuilder(corridor),
                SubAreaResultBuilder::build,
                SubAreaResultBuilder::addTopNeighbour,
                SubAreaResultBuilder::addBottomNeighbour,
                SubAreaResultBuilder::setLUCorner,
                SubAreaResultBuilder::setRUCorner,
                SubAreaResultBuilder::setLBCorner,
                SubAreaResultBuilder::setRBCorner,
                SubAreaResult::setRightNeighbour,
                SubAreaResult::setLeftNeighbour
        );
    }

    private List<SubAreaResult[]> splitHorizontally(List<SubAreaResult> verticalSlices) {
        return splitAlongAxis(
                leftGroup,
                rightGroup,
                corridor.getVerticalAxis(),
                () -> {
                    var newSlices = verticalSlices
                            .stream()
                            .map(SubAreaResult::new)
                            .toArray(SubAreaResult[]::new);
                    Streams.zip(Arrays.stream(newSlices).limit(newSlices.length - 1), Arrays.stream(newSlices).skip(1), Pair::new)
                            .forEach(pair -> {
                                pair.getValue0().setRightNeighbour(pair.getValue1());
                                pair.getValue1().setLeftNeighbour(pair.getValue0());
                            });
                    return newSlices;
                    },
                r -> r,
                (SubAreaResult[] currentRow, AreaResult neigh) -> currentRow[0].setLeftNeighbour(neigh),
                (SubAreaResult[] currentRow, AreaResult neigh) -> currentRow[currentRow.length-1].setRightNeighbour(neigh),
                (SubAreaResult[] currentRow, Point point) ->
                        Arrays.stream(currentRow).forEach(subAreaResult -> subAreaResult.setTopBoundary(point)),
                (SubAreaResult[] currentRow, Point point) ->
                        Arrays.stream(currentRow).forEach(subAreaResult -> subAreaResult.setTopBoundary(point)),
                (SubAreaResult[] currentRow, Point point) ->
                        Arrays.stream(currentRow).forEach(subAreaResult -> subAreaResult.setBottomBoundary(point)),
                (SubAreaResult[] currentRow, Point point) ->
                        Arrays.stream(currentRow).forEach(subAreaResult -> subAreaResult.setBottomBoundary(point)),
                (SubAreaResult[] currentRow, SubAreaResult[] lastRow) ->
                        Streams.zip(Arrays.stream(currentRow), Arrays.stream(lastRow), Pair::new)
                                .forEach(pair -> pair.getValue0().setTopNeighbour(pair.getValue1())),
                (SubAreaResult[] lastRow, SubAreaResult[] currentRow) ->
                        Streams.zip(Arrays.stream(lastRow), Arrays.stream(currentRow), Pair::new)
                                .forEach(pair -> pair.getValue0().setBottomNeighbour(pair.getValue1()))
        );
    }

    private <T, K> List<T> splitAlongAxis(
            List<LocalNeighbour> firstNeighGroup,
            List<LocalNeighbour> secondNeighGroup,
            Line axis,
            Supplier<K> createNewElement,
            Function<K, T> prepareToAdd,
            BiConsumer<K, AreaResult> setFirstSideNeighbour,
            BiConsumer<K, AreaResult> setSecondSideNeighbour,
            BiConsumer<K, Point> setFirstSidePrimaryCorner,
            BiConsumer<K, Point> setFirstSideSecondaryCorner,
            BiConsumer<K, Point> setSecondSidePrimaryCorner,
            BiConsumer<K, Point> setSecondSideSecondaryCorner,
            BiConsumer<T, T> addNextOrthogonalNeigh,
            BiConsumer<T, T> addPrevOrthogonalNeigh) {

        List<T> accumulator = new LinkedList<>();

        int firstGroupIndex = 0;
        int secondGroupIndex = 0;

        Point topLeftLastSplitPoint = corridor.LeftUpperCorner();

        while (firstGroupIndex < firstNeighGroup.size() || secondGroupIndex < secondNeighGroup.size()) {
            K newElement = createNewElement.get();

            setFirstSidePrimaryCorner.accept(newElement, topLeftLastSplitPoint);
            setSecondSidePrimaryCorner.accept(newElement, axis.symmetricalPoint(topLeftLastSplitPoint));

            if (firstGroupIndex >= firstNeighGroup.size()) {
                setSecondSideNeighbour.accept(newElement, secondNeighGroup.get(secondGroupIndex).getArea());

                if (secondGroupIndex + 1 < secondNeighGroup.size()) {
                    Point newBottomRightSplit = getMiddlePoint(secondNeighGroup.get(secondGroupIndex).getDoorCoord(),
                            secondNeighGroup.get(secondGroupIndex + 1).getDoorCoord());
                    setSecondSideSecondaryCorner.accept(newElement, newBottomRightSplit);
                    setFirstSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newBottomRightSplit));
                    topLeftLastSplitPoint = axis.symmetricalPoint(newBottomRightSplit);
                }

                secondGroupIndex += 1;
            }

            else {
                if (secondGroupIndex >= secondNeighGroup.size()) {
                    setFirstSideNeighbour.accept(newElement, firstNeighGroup.get(firstGroupIndex).getArea());

                    if (firstGroupIndex + 1 < firstNeighGroup.size()) {
                        Point newTopSplit = getMiddlePoint(firstNeighGroup.get(firstGroupIndex).getDoorCoord(),
                                firstNeighGroup.get(firstGroupIndex + 1).getDoorCoord());
                        setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                        setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                        topLeftLastSplitPoint = newTopSplit;
                    }

                    firstGroupIndex += 1;
                }

                else {
                    Point topNeighDoor = firstNeighGroup.get(firstGroupIndex).getDoorCoord();
                    Point bottomNeighDoor = secondNeighGroup.get(secondGroupIndex).getDoorCoord();

                    Point nextTopNeighDoor = firstGroupIndex + 1 < firstNeighGroup.size() ? firstNeighGroup.get(firstGroupIndex + 1).getDoorCoord() : null;
                    Point nextBottomNeighDoor = secondGroupIndex + 1 < secondNeighGroup.size() ? secondNeighGroup.get(secondGroupIndex + 1).getDoorCoord() : null;

                    if (nextTopNeighDoor != null
                            && axis.pointLiesEarlierAlong(topNeighDoor, bottomNeighDoor)
                            && axis.pointLiesEarlierAlong(nextTopNeighDoor, bottomNeighDoor)) {
                        setFirstSideNeighbour.accept(newElement, firstNeighGroup.get(firstGroupIndex).getArea());

                        Point newTopSplit = getMiddlePoint(topNeighDoor, nextTopNeighDoor);
                        setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                        setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                        topLeftLastSplitPoint = newTopSplit;

                        firstGroupIndex += 1;
                    }

                    else if (nextBottomNeighDoor != null
                            && axis.pointLiesEarlierAlong(bottomNeighDoor, topNeighDoor)
                            && axis.pointLiesEarlierAlong(nextBottomNeighDoor, topNeighDoor)) {
                        setSecondSideNeighbour.accept(newElement, secondNeighGroup.get(secondGroupIndex).getArea());

                        Point newTopSplit  = axis.symmetricalPoint(getMiddlePoint(bottomNeighDoor, nextBottomNeighDoor));
                        setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                        setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                        topLeftLastSplitPoint = newTopSplit;

                        secondGroupIndex += 1;
                    }

                    else {
                        setFirstSideNeighbour.accept(newElement, firstNeighGroup.get(firstGroupIndex).getArea());
                        setSecondSideNeighbour.accept(newElement, secondNeighGroup.get(secondGroupIndex).getArea());

                        if (nextTopNeighDoor != null && nextBottomNeighDoor == null) {
                            Point newTopSplit = getMiddlePoint(topNeighDoor, nextTopNeighDoor);
                            setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                            setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                            topLeftLastSplitPoint = newTopSplit;
                        }
                        else if (nextTopNeighDoor == null && nextBottomNeighDoor != null) {
                            Point newTopSplit = axis.symmetricalPoint(getMiddlePoint(bottomNeighDoor, nextBottomNeighDoor));
                            setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                            setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                            topLeftLastSplitPoint = newTopSplit;
                        }
                        else if (nextTopNeighDoor != null && nextBottomNeighDoor != null) {
                            if (axis.pointLiesEarlierAlong(nextTopNeighDoor, nextBottomNeighDoor)) {
                                Point newTopSplit = getMiddlePoint(topNeighDoor, nextTopNeighDoor);
                                setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                                setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                                topLeftLastSplitPoint = newTopSplit;
                            }
                            else {
                                Point newTopSplit = axis.symmetricalPoint(getMiddlePoint(bottomNeighDoor, nextBottomNeighDoor));
                                setFirstSideSecondaryCorner.accept(newElement, newTopSplit);
                                setSecondSideSecondaryCorner.accept(newElement, axis.symmetricalPoint(newTopSplit));
                                topLeftLastSplitPoint = newTopSplit;
                            }
                        }

                        firstGroupIndex += 1;
                        secondGroupIndex += 1;
                    }
                }
            }

            T actualElement = prepareToAdd.apply(newElement);

            if (!accumulator.isEmpty()) {
                T lastSlice = accumulator.get(accumulator.size() - 1);
                addPrevOrthogonalNeigh.accept(actualElement, lastSlice);
                addNextOrthogonalNeigh.accept(lastSlice, actualElement);
            }

            accumulator.add(actualElement);
        }

        return accumulator;
    }

    private Map<ConnectionDirection, List<LocalNeighbour>> sortedNeighbourGroups(Map<ConnectionDirection, List<LocalNeighbour>> neighbourGroups) {
        return neighbourGroups
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        directionAndNeighs -> sortedNeighGroup(directionAndNeighs.getValue(),
                                directionAndNeighs.getKey() == ConnectionDirection.TOP
                                        || directionAndNeighs.getKey() == ConnectionDirection.LEFT)
                ));
    }

    private List<LocalNeighbour> sortedNeighGroup(List<LocalNeighbour> group, boolean isReversed) {
        List<LocalNeighbour> sortedGroup = group
                .stream()
                .sorted((neigh1, neigh2) ->
                GeometryUtils.orient(neigh1.getDoorCoord(), corridor.getCenterCoord(), neigh2.getDoorCoord()))
                .collect(Collectors.toList());

        return isReversed ? Lists.reverse(sortedGroup) : sortedGroup;
    }

    private Point getMiddlePoint(Point P1, Point P2) {
        return new Point((P1.x() + P2.x()) / 2, (P1.y() + P2.y()) / 2);
    }

    private class LocalNeighbour {
        private final AreaResult area;
        private final Point doorCoord;
        private final ConnectionDirection direction;

        private LocalNeighbour(AreaResult area, Point doorCoord) {
            this.area = area;
            this.doorCoord = doorCoord;
            direction = corridor.getRelativeDirection(doorCoord);
        }

        public AreaResult getArea() {
            return area;
        }

        public Point getDoorCoord() {
            return doorCoord;
        }

        public ConnectionDirection getDirection() {
            return direction;
        }
    }
}
