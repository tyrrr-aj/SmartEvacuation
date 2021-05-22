package root.solver;

import root.models.Area;
import root.models.Building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Formula {
    private Building building;
    private Map<String, Integer> vars = new HashMap<>();
    private Map<Integer, String> reversedVars = new HashMap<>();
    private List<int []> clauses = new ArrayList<>();
    private Integer nVars = 1;


    public Formula(Building building) {
        this.building = building;
    }

    public Building getBuilding(){
        return building;
    }

    public Map<String, Integer> getVars(){
        return vars;
    }

    public Map<Integer, String> getReversedVars(){
        return reversedVars;
    }

    public List<int[]> getClauses(){
        return clauses;
    }

    public Integer getNVars(){
        return nVars;
    }

    public void generate(){
        markDanger();
        checkNeighbours();
        checkConnection();
        preferMovement();
        makeUnambiguous();
    }

    private boolean isVarInitialized(String varName){
        return vars.get(varName) != null;
    }

    private void addClause(int[] varsToAdd){
        clauses.add(varsToAdd);
    }

    private Integer addVar(String varName){
        if(!isVarInitialized(varName)){
            vars.put(varName, nVars);
            reversedVars.put(nVars, varName);
            nVars++;

            return nVars-1;
        }
        return null;
    }

    private String getVarNameDanger(Integer area){
        return "Ds"+ area;
    }

    public String getVarNameMove(Integer area1, Integer area2){
        return "s"+ area1 +"Ms"+ area2;
    }

    private  String getVarNameConn(Integer area) {
        return "Cs" + area;
    }

    public  String getVarNameStay(Integer area) {
        return "Ss" + area;
    }

    private void markDanger(){
        for (Map.Entry<Integer, Area> entry : building.getAreas().entrySet()){
            Integer newVar = addVar(getVarNameDanger(entry.getValue().getId()));
            int danger = entry.getValue().isInDanger() == true ? 1 : -1;
            addClause(new int[] {danger*newVar});
        }
    }

    private void checkNeighbours(){
        for (Map.Entry<Integer, Area> entry : building.getAreas().entrySet()) {
            if(entry.getValue().isContainsExit()){
                continue;
            }

            for(var neigh : building.getNeighbours().get(entry.getKey())) {
                var neighId = neigh.getNeighbourId();
                Integer areaId = entry.getValue().getId();

                Area neighbour = building.getAreas().get(neighId);

                if(neighbour.isContainsExit()) {
                    Integer newVar = addVar(getVarNameMove(areaId, neighId));
                    addClause(new int[] {newVar, vars.get(getVarNameDanger(neighId))});
                    addClause(new int[] {-newVar, -vars.get(getVarNameDanger(neighId))});
                }
                else {
                    addVar(getVarNameMove(areaId, neighId));
                    addVar(getVarNameMove(neighId, areaId));
                    addVar(getVarNameConn(neighId));
                    addClause(new int[] {-vars.get(getVarNameMove(areaId, neighId)), -vars.get(getVarNameDanger(neighId))});
                    addClause(new int[] {-vars.get(getVarNameMove(areaId, neighId)), -vars.get(getVarNameMove(neighId, areaId))});
                    addClause(new int[] {-vars.get(getVarNameMove(areaId, neighId)), vars.get(getVarNameConn(neighId))});
                }
            }
        }
    }

    private void checkConnection(){
        for (Map.Entry<Integer, Area> entry : building.getAreas().entrySet()) {
            if(entry.getValue().isContainsExit()){
                continue;
            }

            List<int[]> tmpClauses = new ArrayList<>();

            for (var neigh : building.getNeighbours().get(entry.getKey())) {
                var neighId = neigh.getNeighbourId();

                Integer areaId = entry.getValue().getId();
                Area neighbour = building.getAreas().get(neighId);

                addVar(getVarNameConn(neighbour.getId()));
                addVar(getVarNameConn(areaId));
                if(neighbour.isContainsExit()){
                    tmpClauses.add(new int[] {vars.get(getVarNameMove(areaId, neighId))});
                    addClause(new int[] {-vars.get(getVarNameMove(areaId, neighId)), vars.get(getVarNameConn(areaId))});
                }
                else{
                    tmpClauses.add(new int[] {vars.get(getVarNameMove(areaId, neighId)), vars.get(getVarNameConn(neighId))});
                    addClause(new int[] {-vars.get(getVarNameMove(areaId, neighId)), -vars.get(getVarNameConn(neighId)), vars.get(getVarNameConn(areaId))});
                }
            }

            tmpClauses.add(new int[] {-vars.get(getVarNameConn(entry.getValue().getId()))});
            int iterations = 1;

            for(int i = 0; i<tmpClauses.size(); i++){
                iterations *= tmpClauses.get(i).length;
            }

            for(int i = 0; i<iterations; i++){
                List<Integer> tmpClauseToAdd = new ArrayList<>();
                int j = 1;

                for(int[] clause : tmpClauses){
                    tmpClauseToAdd.add(clause[(i/j)%clause.length]);
                    j *= clause.length;

                }
                addClause(tmpClauseToAdd.stream().mapToInt(x->x).toArray());
            }
        }
    }

    private void preferMovement(){
        for (Map.Entry<Integer, Area> entry : building.getAreas().entrySet()) {
            if(entry.getValue().isContainsExit()){
                continue;
            }

            for (var neigh : building.getNeighbours().get(entry.getKey())) {
                var neighId = neigh.getNeighbourId();

                Integer areaId = entry.getValue().getId();
                Area neighbour = building.getAreas().get(neighId);

                addVar(getVarNameStay(areaId));
                if(neighbour.isContainsExit()){
                    addClause(new int[] {-vars.get(getVarNameStay(areaId)), vars.get(getVarNameDanger(neighId))});
                }
                else{
                    addClause(new int[] {-vars.get(getVarNameStay(areaId)), vars.get(getVarNameDanger(neighId)), -vars.get(getVarNameConn(neighId))});
                }
            }
        }
    }

    private void makeUnambiguous() {
        for (Map.Entry<Integer, Area> entry : building.getAreas().entrySet()) {
            if (entry.getValue().isContainsExit()) {
                continue;
            }

            Integer areaId = entry.getValue().getId();
            List<Integer> lastFormula = new ArrayList<>();
            lastFormula.add(vars.get(getVarNameStay(areaId)));

            for (int i = 0; i < building.getNeighbours().get(entry.getKey()).size(); i++) {
                Integer neighId = building.getNeighbours().get(entry.getKey()).get(i).getNeighbourId();

                for (int j = i + 1; j < building.getNeighbours().get(entry.getKey()).size(); j++) {
                    Integer neigh2Id = building.getNeighbours().get(entry.getKey()).get(j).getNeighbourId();
                    if (neighId != neigh2Id) {
                        addClause(new int[]{-vars.get(getVarNameMove(areaId, neighId)), -vars.get(getVarNameMove(areaId, neigh2Id))});
                    }
                    addClause(new int[]{-vars.get(getVarNameMove(areaId, neighId)), -vars.get(getVarNameStay(areaId))});
                }
                lastFormula.add(vars.get(getVarNameMove(areaId, neighId)));
            }
            addClause(lastFormula.stream().mapToInt(x -> x).toArray());
        }
    }

    public String get(){
        String form = "\nFormula: ";
        for(int j=0; j<clauses.size(); j++){
            int[] clause = clauses.get(j);

            if(j != 0){
                form += "\u2227 (";
            }
            else{
                form += "(";
            }

            for(int i=0; i < clause.length; i++){
                if(clause[i] < 0){
                    form += "~";
                }
                if(i != clause.length-1){
                    form += reversedVars.get(java.lang.Math.abs(clause[i])) + " \u2228 ";
                }
                else{
                    form += reversedVars.get(java.lang.Math.abs(clause[i]));
                }

            }
            form += ")\n";
        }
        form += "\n";

        return form;
    }


    public void print(){
        //System.out.println("Vars: ");
        //System.out.println(vars);

        System.out.println("\nFormula: ");
        for(int j=0; j<clauses.size(); j++){
            int[] clause = clauses.get(j);

            if(j != 0){
                System.out.print("\\land (");
            }
            else{
                System.out.print("(");
            }

            for(int i=0; i < clause.length; i++){
                if(clause[i] < 0){
                    System.out.print("\\neg ");
                }
                if(i != clause.length-1){
                    System.out.print(reversedVars.get(java.lang.Math.abs(clause[i])) + " \\lor ");
                }
                else{
                    System.out.print(reversedVars.get(java.lang.Math.abs(clause[i])));
                }

            }
            System.out.print(")\n");
        }
        System.out.print('\n');
    }
}
