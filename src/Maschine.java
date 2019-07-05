import java.util.List;

public class Maschine {
    private List<Operation> feasibleOps;
    private int overAllRunTime;
    private String name;

    public Maschine(String name,List<Operation> feasibleOps) {
        this.name=name;
        this.feasibleOps = feasibleOps;
    }

    public List<Operation> getFeasibleOps() {
        return feasibleOps;
    }

    public void setFeasibleOps(List<Operation> feasibleOps) {
        this.feasibleOps = feasibleOps;
    }

    public int getOverAllRunTime() {
        return overAllRunTime;
    }

    public void setOverAllRunTime(int overAllRunTime) {
        this.overAllRunTime = overAllRunTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
