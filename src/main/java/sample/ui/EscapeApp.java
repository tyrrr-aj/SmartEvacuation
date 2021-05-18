package sample.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.mocks.BuildingMocks;
import sample.models.Building;

public class EscapeApp extends Application {

    Building building = BuildingMocks.getBuildingFromIFC();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getClassLoader().getResource("sample_new.fxml"));
        Scene scene = new Scene(root, 800, 630);

        BuildingCreator buildingCreator = new BuildingCreator(scene, building);
        buildingCreator.init();

        stage.setTitle("Adaptacyjny model ewakuacji");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
