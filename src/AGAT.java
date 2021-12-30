import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AGAT {
    private List<Process> readyQueue;
    private List<Process> waitingQueue;
    private ArrayList<String> processTimeLine;
    private ArrayList<Integer> timeLine;
    public ArrayList<Process> processes;

    public AGAT() {
        this.processes = new ArrayList<>();
        this.readyQueue = new ArrayList<>();
        this.waitingQueue = new ArrayList<>();
        this.processTimeLine = new ArrayList<>();
        this.timeLine = new ArrayList<>();
    }

    public static void main(String[] args) {
        AGAT agat = new AGAT();
        Scanner scan = new Scanner(System.in);
        System.out.print("Number of processes -> ");
        int procNum = scan.nextInt();
        System.out.println("Enter processes names, arrival times, burst times, priority and quantum");
        for (int i = 0; i < procNum; i++){
            String name = scan.next();
            int arrival = scan.nextInt();
            int burst = scan.nextInt();
            int prio = scan.nextInt();
            int quantum = scan.nextInt();
            agat.processes.add(new Process(name, burst, arrival, prio, quantum));
        }


        agat.schedule();
    }

    public int getMaxArrival() {
        int maxArrival = 0;
        for (Process process : processes) {
            if (process.arrivalTime > maxArrival) {
                maxArrival = process.arrivalTime;
            }
        }
        return maxArrival;
    }

    public int getMaxBurst() {
        int maxBurst = 0;
        for (Process process : readyQueue) {
            if (process.remainingBurstTime > maxBurst) {
                maxBurst = process.remainingBurstTime;
            }
        }
        return maxBurst;
    }

    public Process getBestFactor() {
        Process current = null;
        double min = Double.MAX_VALUE;
        for (Process process : readyQueue) {
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

    public double getFactor(Process process, double V1) {
        double factor = 0.0;

        double f1 = Math.ceil(process.arrivalTime / V1);
        double f2 = Math.ceil(process.remainingBurstTime / updateV2());
        factor = (10 - process.priority)
                + f1 + f2;

        return factor;
    }

    public void updateFactors() {
        for (Process process : readyQueue) {
            process.factor = getFactor(process, getV1());
        }
    }

    public void schedule() {
        double V1 = getV1();
        for (int i = 0; i < readyQueue.size(); i++) {
            readyQueue.get(i).factor = getFactor(readyQueue.get(i), V1);
        }

        int currentTime = 0;
        Process currentProcess = null;
        int tempQuantum = 0;

        waitingQueue.addAll(processes);

        while ((!readyQueue.isEmpty()) || (!waitingQueue.isEmpty()) || (currentProcess != null)) {

            checkForNewArrivals(currentTime);
            updateFactors();
            printTable();

            if (currentProcess == null) {
                currentProcess = getBestFactor();
                timeLine.add(currentTime);
                tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);

            }

            for (int quant = 0; quant < currentProcess.prevQuantum; quant++) {

                if (tempQuantum <= 0) {
                    Process newProcess = getBestFactor();
                    if (currentProcess != newProcess) {
                        currentProcess.quantum = currentProcess.prevQuantum + 2;
                        currentProcess.prevQuantum = currentProcess.quantum;
                        readyQueue.remove(currentProcess);
                        readyQueue.add(currentProcess);
                        timeLine.add(currentTime);
                        processTimeLine.add(currentProcess.name);
                        currentProcess = newProcess;
                        tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
                        break;
                    }
                }
                currentProcess.quantum--;
                currentProcess.remainingBurstTime--;
                tempQuantum--;
                currentTime++;

                if (currentProcess.quantum == 0) {
                    currentProcess.quantum = currentProcess.prevQuantum + 2;
                    currentProcess.prevQuantum = currentProcess.quantum;
                    readyQueue.remove(currentProcess);
                    readyQueue.add(currentProcess);
                    if (readyQueue.size() != 1) {
                        timeLine.add(currentTime);
                        processTimeLine.add(currentProcess.name);
                    }
                    currentProcess = readyQueue.get(0);
                    tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
                    break;
                }

                if (currentProcess.remainingBurstTime == 0) {
                    readyQueue.remove(currentProcess);
                    timeLine.add(currentTime);
                    processTimeLine.add(currentProcess.name);
                    currentProcess.completeTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completeTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    try {
                        currentProcess = readyQueue.get(0);
                    }catch (Exception e){
                        currentProcess = null;
                        break;
                    }
                    tempQuantum = (int) Math.round(currentProcess.quantum * 0.4);
                    break;
                }

                checkForNewArrivals(currentTime);
            }
        }
        timeLine.add(currentTime);
        printTime();
        printTimeLine();

    }

    private void checkForNewArrivals(int currentTime) {
        while (!waitingQueue.isEmpty()) {
            Process temp = waitingQueue.get(0);
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
        for (Process process : processes) {
            if (!waitingQueue.contains(process))
                System.out.println(printProcess(process));
        }
        System.out.println("========================================================================");
    }

    public void printTime(){
        double averageWaitingTime = 0;
        double averageTurnaroundTime = 0;
        int size = processes.size();
        for (Process process : processes){
            System.out.println("PName: " + process.name + ", Waiting Time: " + process.waitingTime + ", Turnaround Time: " + process.turnaroundTime);
            averageWaitingTime += process.waitingTime;
            averageTurnaroundTime += process.turnaroundTime;
        }
        if (size > 0){
            System.out.println("Average Waiting Time: " + averageWaitingTime / size + ", Average Turnaround Time: " + averageTurnaroundTime / size);
            System.out.println("========================================================================");
        }
    }

    void printTimeLine(){
        int i = 0;
        while (i < processTimeLine.size()){
            System.out.print(timeLine.get(i) + " " + processTimeLine.get(i) + " ");
            i++;
        }
        System.out.println(timeLine.get(i));
    }

    String printProcess(Process process){
        return "PName: " + process.name + ", Burst Time: " + process.burstTime + ", Arrival Time: " + process.arrivalTime +
                ", Priority: " + process.priority + ", Quantum: " + process.quantum + ", Factor: " + process.factor;
    }
}