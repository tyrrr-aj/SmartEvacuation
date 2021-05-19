package root.ui;

import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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
import root.solver.EvacuationSolver;
import root.solver.Formula;

public class BuildingCreator {
    private Scene scene;
    private Building building;
    private int startColumn = 0;
    private int startRow = 0;

    public BuildingCreator(Scene scene, Building building) {
        this.scene = scene;
        this.building = building;
    }

    public void init() throws Exception {
        createScene();
        createResetButton();
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
        System.out.println("ID: " + building.getAreas().entrySet().iterator().next().getKey());
        printRoomWithNeighbours(building.getAreas().entrySet().iterator().next().getKey());
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
                addRoom("/rooms/room0.png", roomColumnIndex, roomRowIndex, roomId);
            }
            printNeighbours(roomId, roomColumnIndex, roomRowIndex);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void addRoom(String imageURL, int columnIndex, int rowIndex, int roomId) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");

        Image image = new Image(imageURL);
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
        gridPane.add(imageView, columnIndex, rowIndex);
        addRoomLabel(gridPane, columnIndex, rowIndex, roomId);
    }

    private void toggleDanger(MouseEvent event) throws Exception {
        Integer clickedId = Integer.parseInt(event.getPickResult().getIntersectedNode().getId().split("-")[1]);
        Label label = (Label) scene.lookup("#label-" + clickedId);

        if(building.getAreas().get(clickedId).isInDanger()){
            label.setTextFill(Color.web("#000000", 1));
            building.getAreas().get(clickedId).setIsInDanger(false);
            System.out.println(clickedId + " - no danger");
        } else{
            label.setTextFill(Color.web("#ff0000", 1));
            building.getAreas().get(clickedId).setIsInDanger(true);
            System.out.println(clickedId + " - danger");
        }

        generateNewEvacuationPlan();
    }

    private void addRoomLabel(GridPane gridPane, int columnIndex, int rowIndex, int roomId) {
        Label label = new Label(String.valueOf(roomId));
        label.setId("label-" + roomId);

        Font font = new Font(16);
        label.setFont(font);

        gridPane.add(label, columnIndex, rowIndex);
        GridPane.setHalignment(label, HPos.CENTER);
    }

    private void printNeighbours(int roomId, int roomColumnIndex, int roomRowIndex) {
        var neighbours = building.getNeighbours().get(roomId);

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
                    addRoom("/rooms/room0.png", roomColumnIndex, roomRowIndex, neighbour.getNeighbourId());
                    printRoomWithNeighbours(neighbour.getNeighbourId());
                } catch (Exception e) {
                    if(roomRowIndex < 0) {
                        this.startRow++;
                        initializeBuilding();
                    }
                    if(roomColumnIndex < 0) {
                        this.startColumn++;
                        initializeBuilding();
                    }
                }
            }
        }
    }

    private void createResetButton() {
        ButtonBar buttonBar = (ButtonBar) scene.lookup("#buttons");
        Button button = new Button("Reset");
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                System.out.println("RESET");
                reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.consume();
        });
        buttonBar.getButtons().addAll(button);
    }

    private void reset() throws Exception {
        for(var area: building.getAreas().values()) {
            area.setIsInDanger(false);
        }
        generateNewEvacuationPlan();
    }

    private void generateNewEvacuationPlan() throws Exception {

        Formula formula = new Formula(building);
        formula.generate();

        EvacuationSolver evacuationSolver = new EvacuationSolver(formula);
        evacuationSolver.solve();

        EvacuationPathCreator evacuationPathCreator = new EvacuationPathCreator(scene, formula, evacuationSolver, building);
        evacuationPathCreator.init();
    }
}
