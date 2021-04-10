package sample.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.mocks.BuildingMocks;
import sample.models.Building;
import sample.solver.EvacuationSolver;
import sample.solver.Formula;

public class EscapeApp extends Application {

    Building building = BuildingMocks.getSmallBuilding2();


//    @FXML
//    private void reset(Scene scene) throws Exception {
//        for(int i = 0; i < building.getAreas().size(); i++) {
//            building.getAreas().get(i).setIsInDanger(false);
//        }
//        initSigns();
//        generateFormula(scene);
//    }

//    @FXML
//    private void toggleDanger(Scene scene, MouseEvent event) throws Exception {
//        Integer clickedId = Integer.parseInt(event.getPickResult().getIntersectedNode().getId().split("-")[1]);
//        Label label = (Label) scene.lookup("#label-" + clickedId);
//
//        if(building.getAreas().get(clickedId).isInDanger()){
//            label.setTextFill(Color.web("#000000", 1));
//            building.getAreas().get(clickedId).setIsInDanger(false);
//            System.out.println(clickedId + " - no danger");
//        } else{
//            label.setTextFill(Color.web("#ff0000", 1));
//            building.getAreas().get(clickedId).setIsInDanger(true);
//            System.out.println(clickedId + " - danger");
//        }
//        generateFormula(scene);
//    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getClassLoader().getResource("sample_new.fxml"));
        Scene scene = new Scene(root, 800, 630);

        BuildingCreator buildingCreator = new BuildingCreator(scene, building);
        buildingCreator.init();

        Formula formula = new Formula(building);
        formula.generate();

        EvacuationSolver evacuationSolver = new EvacuationSolver(formula);
        evacuationSolver.solve();

        EvacuationPathCreator evacuationPathCreator = new EvacuationPathCreator(scene, formula, evacuationSolver, building);
        evacuationPathCreator.init();

        stage.setTitle("Adaptacyjny model ewakuacji");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
