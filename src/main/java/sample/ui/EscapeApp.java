package sample.ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sample.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscapeApp extends Application {

    Building building = BuildingGenerator.getSmallBuilding4();
    private Formula formula;
    private EvacSolver evacSolver;

    private List<Map<String, String>> allActions = new ArrayList<>();
    private Map<Integer, Label> labelVars = new HashMap<>();

    private void initSigns() {
        // Todo use Map.ofEntries and create it automatically
        allActions.add(Map.of("1", "→", "S", "S"));
        allActions.add(Map.of("E", "↑"));
        allActions.add(Map.of("1", "←", "S", "S"));
        allActions.add(Map.of("4", "→", "S", "S"));
        allActions.add(Map.of("1", "↑", "5", "→", "3", "←", "6", "↓", "S", "S"));
        allActions.add(Map.of("4", "←", "S", "S"));
        allActions.add(Map.of("4", "↑", "9", "↓", "S", "S"));
        allActions.add(Map.of("10", "↓", "S", "S"));
        allActions.add(Map.of("9", "→", "S", "S"));
        allActions.add(Map.of("6", "↑", "10", "→", "12", "↓", "8", "←", "S", "S"));
        allActions.add(Map.of("9", "←", "18", "→", "7", "↑", "13", "↓", "S", "S"));
        allActions.add(Map.of("8", "↑", "S", "S"));
        allActions.add(Map.of("9", "↑", "S", "S"));
        allActions.add(Map.of("10", "↑", "S", "S"));
        allActions.add(Map.of("18", "↓", "S", "S"));
        allActions.add(Map.of("E", "↑"));
        allActions.add(Map.of("20", "↓", "S", "S"));
        allActions.add(Map.of("21", "↓", "S", "S"));
        allActions.add(Map.of("10", "←", "19", "→", "14", "↑", "23", "↓", "S", "S"));
        allActions.add(Map.of("18", "←", "20", "→", "15", "↑", "24", "↓", "S", "S"));
        allActions.add(Map.of("19", "←", "21", "→", "16", "↑", "25", "↓", "S", "S"));
        allActions.add(Map.of("20", "←", "24", "→", "17", "↑", "26", "↓", "S", "S"));
        allActions.add(Map.of("27", "↓","21", "←", "S", "S"));
        allActions.add(Map.of("18", "↑", "S", "S"));
        allActions.add(Map.of("19", "↑", "S", "S"));
        allActions.add(Map.of("20", "↑", "S", "S"));
        allActions.add(Map.of("21", "↑", "S", "S"));
        allActions.add(Map.of("22", "↑", "S", "S"));

//        labelVars.put(0, area0Label);
//        labelVars.put(1, area1Label);
//        labelVars.put(2, area2Label);
//        labelVars.put(3, area3Label);
//        labelVars.put(4, area4Label);
//        labelVars.put(5, area5Label);
//        labelVars.put(6, area6Label);
//        labelVars.put(7, area7Label);
//        labelVars.put(8, area8Label);
//        labelVars.put(9, area9Label);
//        labelVars.put(10, area10Label);
//        labelVars.put(11, area11Label);
//        labelVars.put(12, area12Label);
//        labelVars.put(13, area13Label);
//        labelVars.put(14, area14Label);
//        labelVars.put(15, area15Label);
//        labelVars.put(16, area16Label);
//        labelVars.put(17, area17Label);
//        labelVars.put(18, area18Label);
//        labelVars.put(19, area19Label);
//        labelVars.put(20, area20Label);
//        labelVars.put(21, area21Label);
//        labelVars.put(22, area22Label);
//        labelVars.put(23, area23Label);
//        labelVars.put(24, area24Label);
//        labelVars.put(25, area25Label);
//        labelVars.put(26, area26Label);
//        labelVars.put(27, area27Label);
     }

    private void setSigns(){
        for(int i = 0; i < building.getBuildingSize(); i++){
            labelVars.get(i).setText(allActions.get(i).get(building.getAreas().get(i).getAction()));

            if(!building.getAreas().get(i).isInDanger() && !building.getAreas().get(i).isContainsExit()) {
                labelVars.get(i).setTextFill(Color.web("#000000", 1));
            }
        }
    }

    @FXML
    private void generateFormula() throws Exception {
        formula = new Formula(building);
        formula.generate();
        evacSolver = new EvacSolver(formula);
        evacSolver.solve();
        evacSolver.printEvacPlan();
        formula.print();
        setSigns();
    }

    @FXML
    private void reset() throws Exception {
        for(int i=0; i<28; i++) {
            building.getAreas().get(i).setIsInDanger(false);
        }

//        initSigns();
//        generateFormula();
    }

    @FXML
    private void toggleDanger(MouseEvent event) throws Exception{
        Integer clickedId = Integer.parseInt(event.getPickResult().getIntersectedNode().getId().split("_")[1]);
        String labelVar = "area" + clickedId + "Label";

        if(building.getAreas().get(clickedId).isInDanger()){
            labelVars.get(clickedId).setTextFill(Color.web("#000000", 1));
            building.getAreas().get(clickedId).setIsInDanger(false);
            System.out.println(clickedId + " - no danger");
        }
        else{
            labelVars.get(clickedId).setTextFill(Color.web("#ff0000", 1));
            building.getAreas().get(clickedId).setIsInDanger(true);
            System.out.println(clickedId + " - danger");
        }

        System.out.println(labelVar);
        generateFormula();
    }

    public void initialize() throws Exception {
//        initSigns();
//        generateFormula();
    }

    private int startColumn = 0;
    private int startRow = 0;

    public void printConnectedRooms(Scene scene, int roomId) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");

        int roomColumnIndex = this.startColumn;
        int roomRowIndex = this.startRow;

        try {
            ImageView roomImage = (ImageView) scene.lookup("#room-" + roomId);
            roomColumnIndex = GridPane.getColumnIndex(roomImage);
            roomRowIndex = GridPane.getRowIndex(roomImage);
        } catch (Exception e) {
            addRoom(gridPane, "/rooms/room0.png", roomColumnIndex, roomRowIndex, roomId);
        }
        var neighbours = building.getNeighbours().get(roomId);

        for (var neighbour : neighbours) {
            try {
                ImageView roomImage = (ImageView) scene.lookup("#room-" + roomId);
                roomColumnIndex = GridPane.getColumnIndex(roomImage);
                roomRowIndex = GridPane.getRowIndex(roomImage);
            } catch (Exception exc) {}

            try {
                // continue if room exists
                ImageView imageView = (ImageView) scene.lookup("#room-" + neighbour.getNeighbourId());
                imageView.getImage();
            } catch (Exception ex) {
                switch (neighbour.getNeighboursConnection()) {
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
                    addRoom(gridPane, "/rooms/room0.png", roomColumnIndex, roomRowIndex, neighbour.getNeighbourId());
                    printConnectedRooms(scene, neighbour.getNeighbourId());
                } catch (Exception e) {
                    if(roomRowIndex < 0) {
                        this.startRow++;
                        gridPane.getChildren().clear();
                        printConnectedRooms(scene, 0);
                    }
                    if(roomColumnIndex < 0) {
                        this.startColumn++;
                        gridPane.getChildren().clear();
                        printConnectedRooms(scene, 0);
                    }
                }
            }
        }
    }

    public void initRooms(Scene scene) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");

        // Todo make it automatically

        ColumnConstraints column = new ColumnConstraints(80);
        gridPane.getColumnConstraints().add(column);


        RowConstraints row = new RowConstraints(80);
        gridPane.getRowConstraints().add(row);

        for (var roomId : building.getAreas().keySet()) {
            printConnectedRooms(scene, roomId);
        }
    }

    public void addRoomLabel(GridPane gridPane, int columnIndex, int rowIndex, int roomId) {
        Label label = new Label(String.valueOf(roomId));
        label.setId(String.valueOf(roomId));

        Font font = new Font(16);
        label.setFont(font);

        gridPane.add(label, columnIndex, rowIndex);
        GridPane.setHalignment(label, HPos.CENTER);
    }

    public void addRoom(GridPane gridPane, String imageURL, int columnIndex, int rowIndex, int roomId) {
        Image image = new Image(imageURL);
        ImageView imageView = new ImageView();
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        imageView.setId("room-" + roomId);
        imageView.setImage(image);
        gridPane.add(imageView, columnIndex, rowIndex);
        addRoomLabel(gridPane, columnIndex, rowIndex, roomId);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Formula form_b = new Formula(building);
        form_b.generate();

        EvacSolver evac = new EvacSolver(form_b);
        evac.solve();
        evac.printSolution();

        building.updateArea(1, true, false);
        form_b = new Formula(building);
        form_b.generate();
        evac = new EvacSolver(form_b);
        evac.solve();
        evac.printSolution();

        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getClassLoader().getResource("sample_new.fxml"));
        Scene scene = new Scene(root, 800, 630);

        stage.setTitle("Adaptacyjny model ewakuacji");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        initRooms(scene);
        building.print();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
