import java.util.*;

public class GeneticAlgorithm {

    private DataSet dataSet;

    private final int POPULATION_NUMBER=100;
    private final int MAX_GENERATIONS=100;
    private final int TOP_X_FOR_NEXT_GEN=5;
    private final int NUMBER_OF_MASCHINES=4;
    private final int OBX_CROSSOVER_NUMBER=8;

    public static void main(String args[]){
        GeneticAlgorithm geneticAlgorithm= new GeneticAlgorithm();
        Solution solution=geneticAlgorithm.run();
        geneticAlgorithm.printSolution(solution);

    }

    public Integer getWorkingTime(Solution solution){
        Map<Maschine,Integer> maschineTimes=new HashMap<>();
        for (Maschine maschine: solution.getSolution().keySet()
             ) {
            for (Operation op :
                    solution.getSolution().get(maschine)) {
                maschineTimes.put(maschine,maschineTimes.getOrDefault(maschine,0)+op.getTime());
            }
        }
        List<Integer> times=new ArrayList<>();
        for (Maschine m:maschineTimes.keySet()
             ) {
          times.add(maschineTimes.get(m));
        }
        return Collections.max(times);
    }

    public void printSolution(Solution solution){
        if (solution==null)throw new NullPointerException("solution must not be null");
        System.out.println("########################## SOLUTION ########################");
        for (Maschine m :solution.getSolution().keySet()
        ) {
            System.out.println();
            System.out.print(m.getName()+": ");
            for (Operation o :solution.getSolution().get(m)
            ) {
                System.out.print(" "+o.getName());
                for (int i=0;i<o.getTime();i++){
                    System.out.print("-");
                }
            }
        }
        System.out.println();
        System.out.println("Maschine Runtime: "+getWorkingTime(solution));
        System.out.println("############################################################");
    }

    void init(){
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

        this.dataSet=new DataSet(
                new ArrayList<>(Arrays.asList(maschineA,maschineB,maschineC,maschineD))
                ,new ArrayList<>(Arrays.asList(jobList1,jobList2,jobList3,jobList4))
        );

    }

    public Solution run(){
        init();
        dataSet.updatePopulation(getStartPopulation());

        for (int i=0;i<MAX_GENERATIONS;i++){
            nextGen();
        }

        Solution solution= chooseFittest().get(0);

        return solution;
    }

    List<Solution> getStartPopulation(){
        List<Solution> startPopulation=new ArrayList<>();

        for (int i=0;i<POPULATION_NUMBER;i++){
            startPopulation.add(fetchSolution(dataSet));
        }
        return startPopulation;
    }

    Solution fetchSolution(DataSet dataSet){
        List<List<Operation>> jobs=dataSet.getJobs();
        List<Maschine> maschines=dataSet.getMaschines();

        Map<Maschine, List<Operation>> solution=new HashMap<>() ;


        //BUILD FEASIBLE SOLUTION
        for (List<Operation> job :
                jobs) {
            for (Operation op :
                    job) {
                for (Maschine m :
                        maschines) {
                    if (m.getFeasibleOps().contains(op)){
                        if (!solution.containsKey(m))
                            solution.put(m,new ArrayList<>());
                        solution.get(m).add(op);
                        //fixDependancyIssues(solution,job,op);
                    }
                }
            }
        }
        return fixDependancyIssues(new Solution(solution));
    }



    private void nextGen() {
        List<Solution> nextGen=new ArrayList<>();
        List<Solution> fittest=chooseFittest();
        nextGen=recombine(fittest);
        nextGen=mutate(nextGen);
        dataSet.updatePopulation(nextGen);
    }

    private List<Solution> chooseFittest(){
        List<Solution> topX=new ArrayList<>();
        List<Solution> population= dataSet.getCurrentPopulation();

        for (Solution solution:population
        ){
            Maschine longestMaschine=null;
            for (Maschine m : solution.getSolution().keySet()) {

                int maschineRunTime=0;
                for (Operation op : solution.getSolution().get(m)) {
                   maschineRunTime=maschineRunTime+ op.getTime();
                }
                
                m.setOverAllRunTime(maschineRunTime);

                if(longestMaschine==null)longestMaschine=m;
                else if(longestMaschine.getOverAllRunTime()<m.getOverAllRunTime())
                    longestMaschine=m;
            }
            System.out.println(longestMaschine);
            solution.setProcessingTime(longestMaschine.getOverAllRunTime());
        }
        population.sort(new Comparator<Solution>() {
            @Override
            public int compare(Solution s1, Solution s2) {
                if (s1.getProcessingTime()<s2.getProcessingTime())
                    return s1.getProcessingTime();
                else return s2.getProcessingTime();
            }
        });
        int i=0;
        for (Solution s :
                population) {
            if(i>=TOP_X_FOR_NEXT_GEN)break;
            topX.add(s);
            i++;
        }


        return topX;
    }

    private List<Solution> recombine(List<Solution> fittest){
        List<Solution> nextGen=new ArrayList<>();
        while (nextGen.size()<MAX_GENERATIONS){

            Solution solution1=fittest.get((new Random()).nextInt(fittest.size()));
            Solution solution2=fittest.get((new Random()).nextInt(fittest.size()));
            Operation[] opLineCombined;
            Map<Maschine,List<Operation>> combinedRepresentation=new HashMap<>();
            int currentMaschineNumber=0;
            while(currentMaschineNumber<NUMBER_OF_MASCHINES){
                List<Operation> opLineSol1= solution1.getSolution().get(currentMaschineNumber);
                List<Operation> opLineSol2= solution2.getSolution().get(currentMaschineNumber);
                int maxOpLineLength;
                if(opLineSol1.size()>=opLineSol2.size()){
                    maxOpLineLength=opLineSol1.size();
                }
                else maxOpLineLength=opLineSol2.size();
                opLineCombined=new Operation[maxOpLineLength];

                int i=0;
                List<Operation> crossoverList1=new ArrayList<>();
                List<Operation> crossoverList2=new ArrayList<>();
                while(i<OBX_CROSSOVER_NUMBER){
                    Operation randomOp1= opLineSol1.get((new Random()).nextInt(opLineSol1.size()));
                    Operation randomOp2= opLineSol2.get((new Random()).nextInt(opLineSol2.size()));
                    crossoverList1.add(randomOp1);
                    crossoverList2.add(randomOp2);
                    i++;
                }

                //order crossoverList1 nach Reihnfolge in OpLine2 aber behalte position von OpLine1
                crossoverList1.sort(new Comparator<Operation>() {
                    @Override
                    public int compare(Operation oOne, Operation oTwo) {
                       if (opLineSol2.indexOf(oOne)<opLineSol2.indexOf(oTwo)){
                          return -1;
                       }
                       if (opLineSol2.indexOf(oOne)>opLineSol2.indexOf(oTwo)){
                           return 1;
                       }
                       return 0;
                    }
                });
                for (Operation o :
                        crossoverList1) {
                    opLineCombined[opLineSol1.indexOf(o)]=o;
                }

                for (int k=0;k<maxOpLineLength;k++){
                    if (opLineCombined[k]==null){
                        opLineCombined[k]=opLineSol1.get(k);
                    }
                }

                combinedRepresentation.put(dataSet.getMaschines().get(currentMaschineNumber),Arrays.asList(opLineCombined));
                currentMaschineNumber++;
            }
            Solution combinedSolution=new Solution(combinedRepresentation);

            combinedSolution= makeFeasable(combinedSolution);
            if (combinedSolution!=null){
                nextGen.add(combinedSolution); //fügt so lange keine neue generation hinzu bis eine gültige ensteht. schleife bricht nach zahl von gültigen ab
            }
        }
        return nextGen;
    }

    private Solution makeFeasable(Solution combinedSolution) {
        for (Maschine m :
                combinedSolution.getSolution().keySet()) {
            List<Operation> maschineOpList=combinedSolution.getSolution().get(m);
            for (Operation operation:
            maschineOpList ) {
                //check if maschines only contain feasable Ops in potential Solution
                if (!m.getFeasibleOps().contains(operation)){
                    return  null;
                }

            }
            return fixDependancyIssues(combinedSolution);
        }
        return null;
    }

     public Solution fixDependancyIssues(Solution combinedSolution) {
        List<List<Operation>> jobs=this.dataSet.getJobs();
        Set<Maschine> maschines= combinedSolution.getSolution().keySet();
        Map<Operation,Maschine> topLayer=new HashMap<>();
        while (true) {
            //<editor-fold desc="update Toplayer">
            topLayer.clear();
            for (Maschine m :maschines
                  ) {
                for (Operation op :
                        combinedSolution.getSolution().get(m)) {
                    if (!op.isDone()||op.isInProgress()) {
                        topLayer.put(op,m);
                        break;
            }   }   }

            if (topLayer.keySet().size()==0)break; //wenn alle Ops Ready sind

            Operation shortestOp=null;

            for (Operation op :
                    topLayer.keySet()) {
                if (shortestOp==null) shortestOp=op;
                if (shortestOp.getRemainingTime()> op.getRemainingTime()){
                   shortestOp=op;
            }   }
            //</editor-fold>

            //<editor-fold desc="Find Dependancy List for SHortest OP in Toplayer">
            List<Operation> dependencies=null;
            if (jobs==null||jobs.size()==0)throw new NullPointerException("no jobs found");
            for (List<Operation> job:jobs){

                if (job==null||job.size()==0)throw new NullPointerException("no ops in Job");
                for (Operation op:
                     job) {

                    System.out.println("Is "+shortestOp.getName()+" equal "+op.getName()+"?");
                    if (shortestOp.equals(op)){
                        dependencies=job;
                        System.out.println("Yes");
                        break;
                    }
                    else System.out.println("No");
                }
                if (dependencies!=null){
                    System.out.println("dependancies found! "+dependencies.toString());
                    break;
                }
            }

            //</editor-fold>

            //wenn dependancy satisfied
            if (dependencies!=null){
                for (Operation dependency :
                        dependencies) {
                    if (dependency.equals(shortestOp)) break; //wenn alle wichtigen dependencies gecheckt
                    if (!dependency.isDone()){ //wenn dependency noch nicht fertig (problem gefunden)
                        Maschine maschineWithProblematicDependancy=null;
                        for (Maschine ma : //finde Maschine wo das Problem auftritt
                                combinedSolution.getSolution().keySet()) {
                            if(combinedSolution.getSolution().get(ma).contains(dependency))
                                maschineWithProblematicDependancy=ma;
                        }

                        int nopeTime=0;
                        if (maschineWithProblematicDependancy==null) throw new NullPointerException("No Maschine found for this op");
                        for (Operation opForNopeTime : //finde die Operationen der Solution in der Maschine mit dem Problem
                                combinedSolution.getSolution().get(maschineWithProblematicDependancy)
                        ) {
                            if (!opForNopeTime.equals(dependency)){//berechne vergangene Zeit bis zur Problemstelle
                                nopeTime=nopeTime+opForNopeTime.getRemainingTime();
                            }
                            else break; //stelle gefunden an der das Problem auftritt

                        }
                        //füge nope vor der Operation ein
                        List<Operation> OpListFromShortestOp=combinedSolution.getSolution().get(topLayer.get(shortestOp));
                        int positionForNope= OpListFromShortestOp.indexOf(shortestOp);
                        OpListFromShortestOp.add(positionForNope,(new Operation("nope",nopeTime))); //füge nope mit ensprechender wartezeit ein
                    }
                }
            }

            shortestOp.setDone(true); //shortestOp abgearbeitet

            for (Operation op :
                    topLayer.keySet()) {
                if (!op.equals(shortestOp)){
                    if (op.getRemainingTime()-shortestOp.getRemainingTime()==0){
                        //abgearbeitet weil mehrere gleichzeitig fertig
                        combinedSolution.getSolution().get(topLayer.get(shortestOp)).remove(shortestOp);
                        op.setDone(true);
                        op.setInProgress(false);
                    }
                    else{
                        op.setRemainingTime(op.getRemainingTime()-shortestOp.getRemainingTime());
                        op.setDone(false);
                        op.setInProgress(true);
                    }
                }
            }
        }
        return combinedSolution;
    }


    private List<Solution> mutate(List<Solution> nextGen){

        //MUTATE THE POPULATION THAT IS FEASIBLE
        return nextGen;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}
