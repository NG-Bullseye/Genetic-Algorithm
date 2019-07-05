import org.junit.Test;

import java.util.*;

public class GeneticAlgorithmTests {


    private Map<Maschine,List<Operation>> solutionMap;
    private GeneticAlgorithm geneticAlgorithm;

    private void init(){

        this.geneticAlgorithm=new GeneticAlgorithm();

        Operation op11=new Operation("op11",1);
        Operation op12=new Operation("op12",2);
        Operation op13=new Operation("op13",3);
        Operation op14=new Operation("op14",4);

        Operation op21=new Operation("op21",4);
        Operation op22=new Operation("op22",3);
        Operation op23=new Operation("op23",2);
        Operation op24=new Operation("op24",1);

        Operation op31=new Operation("op31",4);
        Operation op32=new Operation("op32",1);
        Operation op33=new Operation("op33",2);
        Operation op34=new Operation("op34",3);

        Operation op41=new Operation("op41",3);
        Operation op42=new Operation("op42",2);
        Operation op43=new Operation("op43",1);
        Operation op44=new Operation("op44",4);

        List<Operation> jobList1=new ArrayList<>(Arrays.asList(op11,op12,op13,op14));
        List<Operation> jobList2=new ArrayList<>(Arrays.asList(op21,op22,op23,op24));
        List<Operation> jobList3=new ArrayList<>(Arrays.asList(op31,op32,op33,op34));
        List<Operation> jobList4=new ArrayList<>(Arrays.asList(op41,op42,op43,op44));

        Maschine maschineA=new Maschine("m1",Arrays.asList(op11,op21,op31,op41));
        Maschine maschineB=new Maschine("m2",Arrays.asList(op12,op22,op32,op42));
        Maschine maschineC=new Maschine("m3",Arrays.asList(op13,op23,op33,op43));
        Maschine maschineD=new Maschine("m4",Arrays.asList(op14,op24,op34,op44));

        this.geneticAlgorithm.setDataSet(new DataSet(
                new ArrayList<>(Arrays.asList(maschineA,maschineB,maschineC,maschineD))
                ,new ArrayList<>(Arrays.asList(jobList1,jobList2,jobList3,jobList4))
        ));

        this.solutionMap=new HashMap<>();
        List<Operation> tasks1=new ArrayList<>();
        tasks1.add(geneticAlgorithm.getDataSet().getJobs().get(0).get(0) );
        tasks1.add(geneticAlgorithm.getDataSet().getJobs().get(1).get(0) );
        tasks1.add(geneticAlgorithm.getDataSet().getJobs().get(2).get(0) );
        tasks1.add(geneticAlgorithm.getDataSet().getJobs().get(3).get(0) );
        this.solutionMap.put(geneticAlgorithm.getDataSet().getMaschines().get(0),tasks1); //op11,op21,op31,op41

        List<Operation> tasks2=new ArrayList<>();
        tasks2.add(geneticAlgorithm.getDataSet().getJobs().get(1-1).get(2-1) );
        tasks2.add(geneticAlgorithm.getDataSet().getJobs().get(2-1).get(2-1) );
        tasks2.add(geneticAlgorithm.getDataSet().getJobs().get(3-1).get(2-1) );
        tasks2.add(geneticAlgorithm.getDataSet().getJobs().get(4-1).get(2-1) );
        this.solutionMap.put(geneticAlgorithm.getDataSet().getMaschines().get(1),tasks2); //op12,op22,op32,op42
    }

    @Test
    public void createSolutionToFix(){
        boolean check=true;
        init();
        DataSet dataSet=geneticAlgorithm.getDataSet();
        for (Maschine m:
             solutionMap.keySet()) {
            for (Operation op :
                    solutionMap.get(m)) {
                List<Operation> dependencyList=null;
                for (List<Operation> dependencies:
                        dataSet.getJobs()) {
                    for (Operation dependancyOp :
                            dependencies) {
                        if (op.equals(dependancyOp))dependencyList=dependencies;
                    }
                }
                if (dependencyList==null) throw new NullPointerException("dependencyList is null");
                for (Operation dependency:
                            dependencyList) {
                    if (!dependency.isDone()) check=false;
                }

            }
        }
        assert(!check);

    }



    @Test
    public void fixDependencyTest(){
        boolean check=true;
        init();
        DataSet dataSet=this.geneticAlgorithm.getDataSet();
        Solution testSolution=new Solution(this.solutionMap);

        this.geneticAlgorithm.fixDependancyIssues(testSolution);  //ändert die object reference auf testSolution
        System.out.println("TestLoop");
        for (Maschine m:
                testSolution.getSolution().keySet()) {
            for (Operation op :
                    testSolution.getSolution().get(m)) {
                List<Operation> dependencyList=null;
                for (List<Operation> dependencies:
                        dataSet.getJobs()) {
                    for (Operation dependancyOp :
                            dependencies) {
                        System.out.println("is "+op.getName()+" equal "+dependancyOp.getName()+"?");
                        if (dependancyOp.equals(op)){
                            dependencyList=dependencies;
                            System.out.println("Yes");
                            break;
                        }
                        else System.out.println("No");
                    }
                }
                if (dependencyList==null) throw new NullPointerException("dependencyList is null");
                for (Operation dependency:
                        dependencyList) {
                    if (!dependency.isDone()) check=false;
                }
            }
        }

        assert(check);
    }
}
