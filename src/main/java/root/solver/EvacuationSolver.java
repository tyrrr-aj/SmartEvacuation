package root.solver;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import root.models.Area;

import java.util.List;
import java.util.Map;

public class EvacuationSolver {
    private Formula formula;
    private List<int []> clauses;
    private Map<String, Integer> vars;
    private Map<Integer, String> reversedVars;
    private Integer nVars;
    private IProblem problem;
    private int floorNumber;

    public EvacuationSolver(Formula formula) {
        this.formula = formula;
        this.clauses = formula.getClauses();
        this.vars = formula.getVars();
        this.reversedVars = formula.getReversedVars();
        this.nVars = formula.getNVars();
        this.floorNumber = formula.getFloorNumber();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void solve() throws Exception {
        int nClauses = clauses.size();

        ISolver solver = SolverFactory.newDefault();
        solver.newVar(nVars);
        solver.setExpectedNumberOfClauses(nClauses);

        for(int[] clause : clauses){
            solver.addClause(new VecInt(clause));
        }

        problem = solver;
    }

    public void printSolution() throws Exception{
        if(problem.isSatisfiable()){
            System.out.println("Problem is Satisfiable!");

            for(int i=1; i<nVars; i++) {
                System.out.println(reversedVars.get(i) + ": " + problem.model(i));
            }
        }
        else {
            System.out.println("Problem is Unsatisfiable!");
        }
    }

    public void printEvacuationPlan() throws Exception{
        if(!problem.isSatisfiable()) {
            System.out.println("Problem is Unsatisfiable!");
            return;
        }

        for (Map.Entry<Integer, Area> entry : formula.getBuilding().getFloors().get(this.floorNumber).getAreas().entrySet()) {
            Integer roomId = entry.getKey();

            if(formula.getBuilding().getFloors().get(this.floorNumber).getAreas().get(roomId).isContainsExit()) {
                continue;
            }

            System.out.print("Area " + roomId + ": ");
            if(problem.model(vars.get(formula.getVarNameStay(roomId)))) {
                System.out.println("stay");
                formula.getBuilding().getFloors().get(this.floorNumber).getAreas().get(roomId).setAction("S");
            }
            else {
                for (var neigh : formula.getBuilding().getFloors().get(this.floorNumber).getNeighbours().get(entry.getKey())) {
                    var neighId = neigh.getNeighbourId();
                    if(problem.model(vars.get(formula.getVarNameMove(roomId, neighId)))){
                        System.out.println("move to area " + neighId);
                        formula.getBuilding().getFloors().get(this.floorNumber).getAreas().get(roomId).setAction(neighId.toString());
                    }
                }
            }
        }
    }
}
