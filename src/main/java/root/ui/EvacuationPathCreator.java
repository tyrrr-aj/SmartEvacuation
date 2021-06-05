package root.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import root.models.Building;
import root.models.ConnectionDirection;
import root.solver.EvacuationSolver;
import root.solver.Formula;

import java.util.*;

import static java.util.Map.entry;

public class EvacuationPathCreator {
    private Scene scene;
    private Formula formula;
    private EvacuationSolver evacuationSolver;
    private Building building;
    private Map<Integer, LinkedHashMap<String, String>> allActions;
    private int floorNumber;

    public EvacuationPathCreator(Scene scene, Formula formula, EvacuationSolver evacuationSolver, Building building) {
        this.scene = scene;
        this.allActions = new HashMap<>();
        this.formula = formula;
        this.evacuationSolver = evacuationSolver;
        this.building = building;
        this.floorNumber = evacuationSolver.getFloorNumber();
    }

    public void init() throws Exception {
        generateActions();
        formula = new Formula(building, this.floorNumber);
        formula.generate();
        evacuationSolver = new EvacuationSolver(formula);
        evacuationSolver.solve();
        evacuationSolver.printEvacuationPlan();
        formula.print();
        updateSigns(scene);
    }

    private void generateActions() {
        for (var entry : building.getFloors().get(this.floorNumber).getNeighbours().entrySet()) {
            var map = new LinkedHashMap<String, String>();
            var entryList = new ArrayList<Map.Entry>();

            if(building.getFloors().get(this.floorNumber).getAreas().get(entry.getKey()).isContainsExit()) {
                entryList.add(entry("E", setLabelForNeighbour(building.getFloors().get(this.floorNumber).getAreas().get(entry.getKey()).getExitDirection())));
            }

            var neighbour = building.getFloors().get(this.floorNumber).getNeighbours().get(entry.getKey());

            for (var n : neighbour) {
                entryList.add(entry(n.getNeighbourId(), setLabelForNeighbour(n.getConnectionDirection())));
            }
            entryList.add(entry("S", "S"));

            for (var e : entryList) {
                System.out.println(e.getKey().toString() + ", " + e.getValue().toString());
                map.put(e.getKey().toString(), e.getValue().toString());
            }
            allActions.put(entry.getKey(), map);
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
        for (var area : building.getFloors().get(this.floorNumber).getAreas().values()) {
            Label label = (Label) scene.lookup("#label-" + area.getId());
            label.setText(allActions.get(area.getId()).get(area.getAction()));

            if(area.isContainsExit()) {
                label.setStyle("-fx-font-size: 32px");
            }

            if(!area.isInDanger() && !area.isContainsExit()) {
                label.setTextFill(Color.web("#000000", 1));
            }
        }
    }
}
