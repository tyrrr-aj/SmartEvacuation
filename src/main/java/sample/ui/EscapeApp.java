package sample.ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
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
import javafx.stage.Stage;
import sample.*;

import java.util.*;

import static java.util.Map.entry;

public class EscapeApp extends Application {

    Building building = BuildingGenerator.getSmallBuilding3();
    private Formula formula;
    private EvacSolver evacSolver;

    private List<LinkedHashMap<String, String>> allActions = new ArrayList<>();

    private int startColumn = 0;
    private int startRow = 0;

    private void initSigns() {
        for (var entry : building.getNeighbours().entrySet()) {
            var map = new LinkedHashMap<String, String>();
            var entryList = new ArrayList<Map.Entry>();

            if(building.getAreas().get(entry.getKey()).isContainsExit()) {
                entryList.add(entry("E", setLabelForNeighbour(building.getAreas().get(entry.getKey()).getExitDirection())));
            }

            var neighbour = building.getNeighbours().get(entry.getKey());

            for (var n : neighbour) {
                entryList.add(entry(n.getNeighbourId(), setLabelForNeighbour(n.getNeighboursConnection())));
            }
            entryList.add(entry("S", "S"));

            for (var e : entryList) {
                System.out.println(e.getKey().toString() + ", " + e.getValue().toString());
                map.put(e.getKey().toString(), e.getValue().toString());
            }
            allActions.add(map);
        }
     }

    private String setLabelForNeighbour(Neighbour.NeighboursConnection direction) {
        String sign;
        switch (direction) {
            case RIGHT:
                sign = "→";
                break;
            case LEFT:
                sign = "←";
                break;
            case TOP:
                sign = "↑";
                break;
            default:
                sign = "↓";
                break;
        }
        return sign;
    }

    private void setSigns(Scene scene){
        for(int i = 0; i < building.getBuildingSize(); i++) {
            Label label = (Label) scene.lookup("#label-" + i);
            label.setText(allActions.get(i).get(building.getAreas().get(i).getAction()));

            if(building.getAreas().get(i).isContainsExit()) {
                label.setStyle("-fx-font-size: 32px");
            }

            if(!building.getAreas().get(i).isInDanger() && !building.getAreas().get(i).isContainsExit()) {
                label.setTextFill(Color.web("#000000", 1));
            }
        }
    }

    @FXML
    private void generateFormula(Scene scene) throws Exception {
        formula = new Formula(building);
        formula.generate();
        evacSolver = new EvacSolver(formula);
        evacSolver.solve();
        evacSolver.printEvacPlan();
        formula.print();
        setSigns(scene);
    }

    @FXML
    private void reset(Scene scene) throws Exception {
        for(int i = 0; i < building.getAreas().size(); i++) {
            building.getAreas().get(i).setIsInDanger(false);
        }
        initSigns();
        generateFormula(scene);
    }

    public void initResetButton(Scene scene) {
        ButtonBar buttonBar = (ButtonBar) scene.lookup("#buttons");
        Button button = new Button("Reset");
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                reset(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.consume();
        });
        buttonBar.getButtons().addAll(button);
    }

    @FXML
    private void toggleDanger(Scene scene, MouseEvent event) throws Exception {
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
        generateFormula(scene);
    }


    public void initialize(Scene scene) throws Exception {
        initSigns();
        generateFormula(scene);
    }

    private void printRoomWithNeighbours(Scene scene, int roomId) {

        int roomColumnIndex = this.startColumn;
        int roomRowIndex = this.startRow;

        ImageView roomImage = (ImageView) scene.lookup("#room-" + roomId);
        if(roomImage != null) {
            roomColumnIndex = GridPane.getColumnIndex(roomImage);
            roomRowIndex = GridPane.getRowIndex(roomImage);
        } else {
            addRoom(scene, "/rooms/room0.png", roomColumnIndex, roomRowIndex, roomId);
        }
        printNeighbours(scene, roomId, roomColumnIndex, roomRowIndex);
    }

    private void printNeighbours(Scene scene, int roomId, int roomColumnIndex, int roomRowIndex) {
        var neighbours = building.getNeighbours().get(roomId);

        for (var neighbour : neighbours) {
            ImageView roomImage = (ImageView) scene.lookup("#room-" + roomId);
            if(roomImage != null) {
                roomColumnIndex = GridPane.getColumnIndex(roomImage);
                roomRowIndex = GridPane.getRowIndex(roomImage);
            }

            if(scene.lookup("#room-" + neighbour.getNeighbourId()) == null) {
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
                    addRoom(scene, "/rooms/room0.png", roomColumnIndex, roomRowIndex, neighbour.getNeighbourId());
                    printRoomWithNeighbours(scene, neighbour.getNeighbourId());
                } catch (Exception e) {
                    if(roomRowIndex < 0) {
                        this.startRow++;
                        initializeBuilding(scene);
                    }
                    if(roomColumnIndex < 0) {
                        this.startColumn++;
                        initializeBuilding(scene);
                    }
                }
            }
        }
    }

    private void initializeBuilding(Scene scene) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");
        gridPane.getChildren().clear();
        printRoomWithNeighbours(scene, building.getAreas().entrySet().iterator().next().getKey());
    }

    public void createBuildingModel(Scene scene) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");

        ColumnConstraints column = new ColumnConstraints(80);
        gridPane.getColumnConstraints().add(column);

        RowConstraints row = new RowConstraints(80);
        gridPane.getRowConstraints().add(row);

        initializeBuilding(scene);
    }

    private void addRoomLabel(GridPane gridPane, int columnIndex, int rowIndex, int roomId) {
        Label label = new Label(String.valueOf(roomId));
        label.setId("label-" + roomId);

        Font font = new Font(16);
        label.setFont(font);

        gridPane.add(label, columnIndex, rowIndex);
        GridPane.setHalignment(label, HPos.CENTER);
    }

    private void addRoom(Scene scene, String imageURL, int columnIndex, int rowIndex, int roomId) {
        GridPane gridPane = (GridPane) scene.lookup("#grid");

        Image image = new Image(imageURL);
        ImageView imageView = new ImageView();
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                toggleDanger(scene, event);
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

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getClassLoader().getResource("sample_new.fxml"));
        Scene scene = new Scene(root, 800, 630);

        createBuildingModel(scene);
        initResetButton(scene);

        initialize(scene);
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

        stage.setTitle("Adaptacyjny model ewakuacji");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        building.print();

        for (var action : allActions) {
                System.out.println( action.entrySet());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
