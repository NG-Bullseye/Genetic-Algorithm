import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private List<Maschine> maschines =new ArrayList<>();
    private List<List<Operation>> jobs=new ArrayList<>();
    private List<Solution> oldPopulation=new ArrayList<>();
    private List<Solution> currentPopulation =new ArrayList<>();

    public DataSet(List<Maschine> maschines, List<List<Operation>> jobs) {
        this.maschines = maschines;
        this.jobs = jobs;
    }

    public List<Maschine> getMaschines() {
        return maschines;
    }

    public void setMaschines(List<Maschine> maschines) {
        this.maschines = maschines;
    }

    public List<List<Operation>> getJobs() {
        return jobs;
    }

    public void setJobs(List<List<Operation>> jobs) {
        this.jobs = jobs;
    }

    public boolean updatePopulation(List<Solution> population){
        this.newPopulation();
        return currentPopulation.addAll(population);
    }

    public void newPopulation(){
        oldPopulation.clear();
        oldPopulation= currentPopulation;
        currentPopulation.clear();
    }

    public List<Solution> getOldPopulation() {
        return oldPopulation;
    }


    public List<Solution> getCurrentPopulation() {
        return currentPopulation;
    }
}
