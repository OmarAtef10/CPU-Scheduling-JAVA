import java.util.Comparator;

class Process {
    String name;
    int priority;
    int burstTime;
    int arrivalTime;
    int waitingTime;
    int turnaroundTime;
    int burstTimeStore;
    int completeTime;
    boolean completed;
    int remainingBurstTime;
    int quantum;
    double factor;
    int prevQuantum;
    int age = 0;

    static class PrioritySorter implements Comparator<Process> {
        @Override
        public int compare(Process obj1, Process obj2) {
            if (obj1.priority >= obj2.priority)
                return 1;
            return -1;
        }
    }

    static class ArrivalTimeSorter implements Comparator<Process> {
        @Override
        public int compare(Process obj1, Process obj2) {
            if (obj1.arrivalTime >= obj2.arrivalTime)
                return 1;
            return -1;
        }
    }
    static class SJFTimeSorter implements Comparator<Process> {
        @Override
        public int compare(Process obj1, Process obj2) {
            if (obj1.arrivalTime >= obj2.arrivalTime && obj1.burstTime >= obj2.burstTime)
                return 1;
            return -1;
        }
    }

    static class JobTimeSorter implements Comparator<Process> {
        @Override
        public int compare(Process obj1, Process obj2) {
            if (obj1.burstTime >= obj2.burstTime)
                return 1;
            return -1;
        }
    }

    public Process(String _name, int _prio, int _burstTime, int _arrivalTime) {
        name = _name;
        priority = _prio;
        burstTime = _burstTime;
        arrivalTime = _arrivalTime;
        waitingTime = 0;
        turnaroundTime = 0;
        burstTimeStore = _burstTime;
        completed = false;
        completeTime = 0;
        remainingBurstTime = 0;
        quantum = 0;
        factor = 0.0;
        prevQuantum = 0;
    }

    public Process(String _name, int _burstTime, int _arrivalTime) {
        name = _name;
        priority = 0;
        burstTime = _burstTime;
        arrivalTime = _arrivalTime;
        waitingTime = 0;
        turnaroundTime = 0;
        burstTimeStore = _burstTime;
        completed = false;
        completeTime = 0;
        remainingBurstTime = 0;
        quantum = 0;
        factor = 0.0;
        prevQuantum = 0;
    }

    public Process(String _name, int _burstTime, int _arrivalTime, int _priority, int _quantum) {
        name = _name;
        priority = _priority;
        burstTime = _burstTime;
        arrivalTime = _arrivalTime;
        waitingTime = 0;
        turnaroundTime = 0;
        burstTimeStore = _burstTime;
        completed = false;
        completeTime = 0;
        remainingBurstTime = _burstTime;
        quantum = _quantum;
        factor = 0.0;
        prevQuantum = _quantum;
    }

    public void ageProcess() {
        if (priority == 0)
            return;
        priority--;
    }

    // @Override
    // public String toString() {
    //     return "[" + _name + ", " + _priority + "]";
    // }
    @Override
    public String toString() {
        return "[" + name + "]";
    }
}