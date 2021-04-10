package sample.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import sample.models.Building;
import sample.models.ConnectionDirection;
import sample.solver.EvacuationSolver;
import sample.solver.Formula;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class EvacuationPathCreator {
    private Scene scene;
    private Formula formula;
    private EvacuationSolver evacuationSolver;
    private Building building;
    private List<LinkedHashMap<String, String>> allActions;

    public EvacuationPathCreator(Scene scene, Formula formula, EvacuationSolver evacuationSolver, Building building) {
        this.scene = scene;
        this.allActions = new ArrayList<>();
        this.formula = formula;
        this.evacuationSolver = evacuationSolver;
        this.building = building;
    }

    public void init() throws Exception {
        generateActions();
        formula = new Formula(building);
        formula.generate();
        evacuationSolver = new EvacuationSolver(formula);
        evacuationSolver.solve();
        evacuationSolver.printEvacuationPlan();
        formula.print();
        updateSigns(scene);
    }

    private void generateActions() {
        for (var entry : building.getNeighbours().entrySet()) {
            var map = new LinkedHashMap<String, String>();
            var entryList = new ArrayList<Map.Entry>();

            if(building.getAreas().get(entry.getKey()).isContainsExit()) {
                entryList.add(entry("E", setLabelForNeighbour(building.getAreas().get(entry.getKey()).getExitDirection())));
            }

            var neighbour = building.getNeighbours().get(entry.getKey());

            for (var n : neighbour) {
                entryList.add(entry(n.getNeighbourId(), setLabelForNeighbour(n.getConnectionDirection())));
            }
            entryList.add(entry("S", "S"));

            for (var e : entryList) {
                System.out.println(e.getKey().toString() + ", " + e.getValue().toString());
                map.put(e.getKey().toString(), e.getValue().toString());
            }
            allActions.add(map);
        }
    }

    private String setLabelForNeighbour(ConnectionDirection direction) {
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

    private void updateSigns(Scene scene){
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
}
