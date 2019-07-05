import java.util.List;
import java.util.Map;

public class Solution {
    Map<Maschine, List<Operation>> solution;
    int processingTime=0;

    public Solution(Map<Maschine, List<Operation>> solution) {
        this.solution = solution;
    }

    public Map<Maschine, List<Operation>> getSolution() {
        return solution;
    }

    public void setSolution(Map<Maschine, List<Operation>> solution) {
        this.solution = solution;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }
}
