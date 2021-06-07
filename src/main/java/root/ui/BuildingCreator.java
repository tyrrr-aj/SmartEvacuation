package root.ui;

import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import root.models.Building;
import root.models.ConnectionDirection;
import root.models.Neighbour;
import root.solver.EvacuationSolver;
import root.solver.Formula;

import java.util.ArrayList;
import java.util.List;

public class BuildingCreator {
    private Scene scene;
    private Building building;
    private int startColumn = 0;
    private int startRow = 0;
    private int floorNumber = 0;

    public BuildingCreator(Scene scene, Building building) {
        this.scene = scene;
        this.building = building;
    }

    public void init() throws Exception {
        createScene();
        createResetButton();
        createFloorButtons();
        generateNewEvacuationPlan();
    }

    private void createScene() {
        GridPane gridPane = (GridPane) scene.lookup("#grid");

        ColumnConstraints column = new ColumnConstraints(80);
        gridPane.getColumnConstraints().add(column);

        RowConstraints row = new RowConstraints(80);
        gridPane.getRowConstraints().add(row);

        initializeBuilding();
    }

    private void initializeBuilding() {
        GridPane gridPane = (GridPane) scene.lookup("#grid");
        gridPane.getChildren().clear();
        printRoomWithNeighbours(building.getFloors().get(this.floorNumber).getAreas().entrySet().iterator().next().getKey());
    }

    private void printRoomWithNeighbours(int roomId) {
        try {
            int roomColumnIndex = this.startColumn;
            int roomRowIndex = this.startRow;

            ImageView roomImage = (ImageView) scene.lookup("#room-" + roomId);
            if (roomImage != null) {
                roomColumnIndex = GridPane.getColumnIndex(roomImage);
                roomRowIndex = GridPane.getRowIndex(roomImage);
            } else {
                var neighbours = building.getFloors().get(this.floorNumber).getNeighbours().get(roomId);
                var connections = getListOfConnections(neighbours);
                var image = getRoomImage(connections);
                var rotate = getRoomRotation(connections);
                addRoom(image, rotate, roomColumnIndex, roomRowIndex, roomId);
            }
            printNeighbours(roomId, roomColumnIndex, roomRowIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRoom(Image image, int rotate, int columnIndex, int rowIndex, int roomId) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");
        var neighbours = building.getFloors().get(this.floorNumber).getNeighbours().get(roomId);
        ImageView imageView = new ImageView();
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                System.out.println("TOGGLE DANGER");
                toggleDanger(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.consume();
        });
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        imageView.setId("room-" + roomId);
        imageView.setImage(image);
        imageView.setRotate(rotate);
        gridPane.add(imageView, columnIndex, rowIndex);
        addRoomLabel(gridPane, columnIndex, rowIndex, roomId);
    }

    private void toggleDanger(MouseEvent event) throws Exception {
        Integer clickedId = Integer.parseInt(event.getPickResult().getIntersectedNode().getId().split("-")[1]);
        Label label = (Label) scene.lookup("#label-" + clickedId);

        if(building.getFloors().get(this.floorNumber).getAreas().get(clickedId).isInDanger()){
            label.setTextFill(Color.web("#000000", 1));
            building.getFloors().get(this.floorNumber).getAreas().get(clickedId).setIsInDanger(false);
            System.out.println(clickedId + " - no danger");
        } else{
            label.setTextFill(Color.web("#ff0000", 1));
            building.getFloors().get(this.floorNumber).getAreas().get(clickedId).setIsInDanger(true);
            System.out.println(clickedId + " - danger");
        }

        generateNewEvacuationPlan();
    }

    private void addRoomLabel(GridPane gridPane, int columnIndex, int rowIndex, int roomId) {
        Label label = new Label(String.valueOf(roomId));
        label.setId("label-" + roomId);

        Font font = new Font(16);
        label.setFont(font);
        if(building.getFloors().get(this.floorNumber).getAreas().get(roomId).isInDanger()) {
            label.setTextFill(Color.web("#ff0000", 1));
        }
        gridPane.add(label, columnIndex, rowIndex);
        GridPane.setHalignment(label, HPos.CENTER);
    }

    private void printNeighbours(int roomId, int roomColumnIndex, int roomRowIndex) {
        var neighbours = building.getFloors().get(this.floorNumber).getNeighbours().get(roomId);
        for (var neighbour : neighbours) {
            ImageView roomImage = (ImageView) scene.lookup("#room-" + roomId);
            if(roomImage != null) {
                roomColumnIndex = GridPane.getColumnIndex(roomImage);
                roomRowIndex = GridPane.getRowIndex(roomImage);
            }

            if(scene.lookup("#room-" + neighbour.getNeighbourId()) == null) {
                switch (neighbour.getConnectionDirection()) {
                    case BOTTOM:
                        roomRowIndex += 1;
                        break;
                    case TOP:
                        roomRowIndex -= 1;
                        break;
                    case RIGHT:
                        roomColumnIndex += 1;
                        break;
                    case LEFT:
                        roomColumnIndex -= 1;
                        break;
                    default:
                        break;
                }

                try {
                    var neighbours2 = building.getFloors().get(this.floorNumber).getNeighbours().get(neighbour.getNeighbourId());
                    var connections = getListOfConnections(neighbours2);
                    var image = getRoomImage(connections);
                    var rotate = getRoomRotation(connections);
                    addRoom(image, rotate, roomColumnIndex, roomRowIndex, neighbour.getNeighbourId());
                    printRoomWithNeighbours(neighbour.getNeighbourId());
                } catch (Exception e) {
                    if(roomRowIndex < 0) {
                        this.startRow++;
                        this.initializeBuilding();
                    }
                    if(roomColumnIndex < 0) {
                        this.startColumn++;
                        this.initializeBuilding();
                    }
                }
            }
        }
    }

    private List<ConnectionDirection> getListOfConnections(List<Neighbour> neighbours) {
        var connections = new ArrayList<ConnectionDirection>();
        for(var n: neighbours) {
            connections.add(n.getConnectionDirection());
        }
        return connections;
    }

    private Image getRoomImage(List<ConnectionDirection> connections) {
        String imagePath;
        if(connections.size() == 1) {
            imagePath = "/rooms/one_exit.png";
        } else if(connections.size() == 2) {
            if((connections.contains(ConnectionDirection.TOP) && connections.contains(ConnectionDirection.BOTTOM)) ||
                    connections.contains(ConnectionDirection.LEFT) && connections.contains(ConnectionDirection.RIGHT)) {
                imagePath = "/rooms/two_exit_bottom.png";
            } else {
                imagePath = "/rooms/two_exit_side.png";
            }
        } else if(connections.size() == 3) {
            imagePath = "/rooms/three_exit.png";
        } else if(connections.size() == 4) {
            imagePath = "/rooms/four_exit.png";
        } else {
            imagePath = "/rooms/no_walls.png";
        }
        return new Image(imagePath);
    }

    private int getRoomRotation(List<ConnectionDirection> connections) {
        int rotation = 0;
        if(connections.size() == 1) {
            if(connections.contains(ConnectionDirection.RIGHT)) {
                rotation = 90;
            } else if(connections.contains(ConnectionDirection.BOTTOM)) {
                rotation = 180;
            } else if(connections.contains(ConnectionDirection.LEFT)) {
                rotation = 270;
            }
        }
        else if(connections.size() == 2) {
            if(connections.contains(ConnectionDirection.RIGHT) && connections.contains(ConnectionDirection.LEFT)) {
                rotation = 90;
            } else if(connections.contains(ConnectionDirection.RIGHT) && connections.contains(ConnectionDirection.BOTTOM)) {
                rotation = 90;
            } else if(connections.contains(ConnectionDirection.BOTTOM) && connections.contains(ConnectionDirection.LEFT)) {
                rotation = 180;
            } else if(connections.contains(ConnectionDirection.LEFT) && connections.contains(ConnectionDirection.TOP)) {
                rotation = 270;
            }
        }
        else if(connections.size() == 3) {
            if(!connections.contains(ConnectionDirection.TOP)) {
                rotation = 90;
            } else if(!connections.contains(ConnectionDirection.RIGHT)) {
                rotation = 180;
            } else if(!connections.contains(ConnectionDirection.BOTTOM)) {
                rotation = 270;
            }
        }
        return rotation;
    }

    private void reset() throws Exception {
        for(var floor : building.getFloors()) {
            for(var area: floor.getAreas().values()) {
                area.setIsInDanger(false);
            }
        }
        generateNewEvacuationPlan();
    }

    private void createResetButton() {
        Button button = (Button) scene.lookup("#reset-button");
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                System.out.println("RESET");
                reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.consume();
        });
    }

    private void floorUp() throws Exception {
        Button floor = (Button) scene.lookup("#floor-number");
        if(this.floorNumber < this.building.getFloors().size() - 1) {
            this.floorNumber++;
            this.initializeBuilding();
            this.generateNewEvacuationPlan();
        }
        floor.setText("Floor #" + String.valueOf(floorNumber));

    }

    private void floorDown() throws Exception {
        Button floor = (Button) scene.lookup("#floor-number");
        if(this.floorNumber > 0) {
            this.floorNumber--;
            this.initializeBuilding();
            this.generateNewEvacuationPlan();
        }
        floor.setText("Floor #" + String.valueOf(floorNumber));
    }

    private void createFloorButtons() {
        Button buttonUp = (Button) scene.lookup("#up-button");
        buttonUp.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                this.floorUp();
            } catch (Exception e) {

            }
        });

        Button buttonDown = (Button) scene.lookup("#down-button");
        buttonDown.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                this.floorDown();
            } catch (Exception e) {

            }
        });
    }


    private void generateNewEvacuationPlan() throws Exception {

        Formula formula = new Formula(building, this.floorNumber);
        formula.generate();

        EvacuationSolver evacuationSolver = new EvacuationSolver(formula);
        evacuationSolver.solve();

        EvacuationPathCreator evacuationPathCreator = new EvacuationPathCreator(scene, formula, evacuationSolver, building);
        evacuationPathCreator.init();
    }
}
