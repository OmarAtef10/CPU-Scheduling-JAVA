import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class AGAT_Process {
    String p_name;
    int burstTime;
    int arrivalTime;
    int priority;
    int quantum;
    double factor;
    public int completeTime; // current burst + totalTime
    public int turnAroundTime; //CompleteTime - Arrival Time
    public int waitingTime; // Turn Around Time - Burst Time
    int prevQuantum;

    AGAT_Process(String p_name, int burstTime, int arrivalTime, int priority, int quantum) {
        this.p_name = p_name;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.quantum = quantum;
        this.burstTime = burstTime;
        this.factor = 0.0;
        this.prevQuantum = quantum;
    }

    @Override
    public String toString() {
        return "PName: " + p_name + " Burst Time: " + burstTime + " Arrival Time:" + arrivalTime +
                " Priority: " + priority + " Quantum: " + quantum + " Factor: " + factor;
    }
}

public class AGAT {
    private List<AGAT_Process> readyQueue;
    private List<AGAT_Process> waitingQueue;
    public ArrayList<AGAT_Process> processes;

    public AGAT() {
        this.processes = new ArrayList<>();
        this.readyQueue = new ArrayList<>();
        this.waitingQueue = new ArrayList<>();
    }

    public static void main(String[] args) {
        AGAT agat = new AGAT();
        AGAT_Process p1 = new AGAT_Process("P1", 17, 0, 4, 4);
        AGAT_Process p2 = new AGAT_Process("P2", 6, 3, 9, 3);
        AGAT_Process p3 = new AGAT_Process("P3", 10, 4, 3, 5);
        AGAT_Process p4 = new AGAT_Process("P4", 4, 29, 8, 2);
        agat.processes.add(p1);
        agat.processes.add(p2);
        agat.processes.add(p3);
        agat.processes.add(p4);

        agat.schedule();
    }

    public int getMaxArrival() {
        int maxArrival = 0;
        for (AGAT_Process process : processes) {
            if (process.arrivalTime > maxArrival) {
                maxArrival = process.arrivalTime;
            }
        }
        return maxArrival;
    }

    public int getMaxBurst() {
        int maxBurst = 0;
        for (AGAT_Process process : readyQueue) {
            if (process.burstTime > maxBurst) {
                maxBurst = process.burstTime;
            }
        }
        return maxBurst;
    }

    public AGAT_Process getBestFactor() {
        AGAT_Process current = null;
        double min = Double.MAX_VALUE;
        for (AGAT_Process process : readyQueue) {
            if (process.factor < min) {
                min = process.factor;
                current = process;
            }
        }
        return current;
    }

    public double getV1() {
        double v1 = 0.0;
        if (getMaxArrival() > 10) {
            v1 = (processes.get(processes.size() - 1).arrivalTime) / 10.0;
        } else {
            v1 = 1.0;
        }
        return v1;
    }

    public double updateV2() {
        double v2 = 0.0;
        if (getMaxBurst() > 10) {
            v2 = (getMaxBurst()) / 10.0;
        } else {
            v2 = 1.0;
        }
        return v2;
    }

    public double getFactor(AGAT_Process process, double V1) {
        double factor = 0.0;

        double f1 = Math.ceil(process.arrivalTime / V1);
        double f2 = Math.ceil(process.burstTime / updateV2());
        factor = (10 - process.priority)
                + f1 + f2;

        return factor;
    }

    public void updateFactors() {
        for (AGAT_Process process : readyQueue) {
            process.factor = getFactor(process, getV1());
        }
    }

    public void schedule() {
        double V1 = getV1();
        for (int i = 0; i < readyQueue.size(); i++) {
            readyQueue.get(i).factor = getFactor(readyQueue.get(i), V1);
        }
        printTable();


        int currentTime = 0;
        AGAT_Process currentProcess = null;
        boolean isNewProcess = true;
        int tempQuantum = 0;

        waitingQueue.addAll(processes);

        while ((!readyQueue.isEmpty()) || (!waitingQueue.isEmpty()) || (currentProcess != null)) {

            checkForNewArrivals(currentTime);
            updateFactors();

            if (currentProcess == null) {
                currentProcess = getBestFactor();
                tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);

            }
            // else if(currentProcess != newProcess){
            //     currentProcess.quantum = currentProcess.prevQuantum + 2;
            //     currentProcess.prevQuantum = currentProcess.quantum;
            //     // readyQueue.add(currentProcess);
            //     currentProcess = newProcess;
            //     tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
            // }
            // readyQueue.remove(currentProcess);

            // if(isNewProcess)
            // tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);

            for (int quant = 0; quant < currentProcess.prevQuantum; quant++) {

                if (tempQuantum <= 0) {
                    AGAT_Process newProcess = getBestFactor();
                    if (currentProcess != newProcess) {
                        currentProcess.quantum = currentProcess.prevQuantum + 2;
                        currentProcess.prevQuantum = currentProcess.quantum;
                        readyQueue.remove(currentProcess);
                        readyQueue.add(currentProcess);
                        // readyQueue.add(currentProcess);
                        currentProcess = newProcess;
                        tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
                        break;
                    }
                }
                currentProcess.quantum--;
                currentProcess.burstTime--;
                tempQuantum--;
                currentTime++;

                if (currentProcess.quantum == 0) {
                    currentProcess.quantum = currentProcess.prevQuantum + 2;
                    currentProcess.prevQuantum = currentProcess.quantum;
                    readyQueue.remove(currentProcess);
                    readyQueue.add(currentProcess);
                    currentProcess = readyQueue.get(0);
                    tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
                    break;
                }

                if (currentProcess.burstTime == 0) {
                    readyQueue.remove(currentProcess);
                    try {
                        currentProcess = readyQueue.get(0);
                    }catch (Exception e){
                        System.out.println("ReadyQueue is Empty");
                        currentProcess=null;
                        break;
                    }
                    tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
                    break;
                }

                checkForNewArrivals(currentTime);
            }


            // printTable();
            System.out.println(updateV2());
        }


    }

    private void checkForNewArrivals(int currentTime) {
        while (!waitingQueue.isEmpty()) {
            AGAT_Process temp = waitingQueue.get(0);
            if (temp.arrivalTime == currentTime) {
                waitingQueue.remove(0);
                readyQueue.add(temp);
                updateFactors();
            } else {
                break;
            }
        }
    }

    public void printTable() {
        updateFactors();
        for (AGAT_Process process : processes) {
            System.out.println(process.toString());
        }
    }
}