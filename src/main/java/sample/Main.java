package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Plan ewakuacji");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        Building building = BuildingGenerator.getAppBuilding();
        building.print();

        Formula formula = new Formula(building);
        formula.generate();
        formula.print();

        EvacSolver solver = new EvacSolver(formula);
        solver.solve();
        solver.printEvacPlan();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
