package root.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import root.mocks.BuildingMocks;
import root.models.Building;
import root.neo4j.BuildingExtractor;
import root.neo4j.Neo4jDriver;

public class EscapeApp extends Application {

    Building building;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getClassLoader().getResource("sample_new.fxml"));
        Scene scene = new Scene(root, 800, 630);

        BuildingExtractor buildingExtractor = new BuildingExtractor(new Neo4jDriver());
        building = buildingExtractor.extractBuilding();
//        building = BuildingMocks.getSmallBuilding2();
        BuildingCreator buildingCreator = new BuildingCreator(scene, building);
        buildingCreator.init();

        stage.setTitle("Adaptacyjny model ewakuacji");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
