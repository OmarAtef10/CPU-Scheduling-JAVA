import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class PriorityScheduler {
    private ArrayList<Process> _readyQueue;
    private Process _currProcess;
    private int _agingCounter;
    private ArrayList<Process> _waitingQueue;
    private int _currTime;
    private ArrayList<Process> _deadList;


    public PriorityScheduler(int agingParameter){
        _readyQueue = new ArrayList<>();
        _currProcess = null;
        _agingCounter = agingParameter;
        _waitingQueue = new ArrayList<>();
        _currTime = 0;
        _deadList = new ArrayList<>();
    }

    public void addToWaiting(Process process){
        _waitingQueue.add(process);
    }

    private void addToReady(Process process){
        _waitingQueue.remove(process);
        _readyQueue.add(process);
    }

    private void invokeContextSwitch(){
        Collections.sort(_waitingQueue, new Process.ArrivalTimeSorter());
        while(!_waitingQueue.isEmpty()){
            Process process = _waitingQueue.get(0);
            if(_currTime == process.arrivalTime || process.arrivalTime == -1){
                addToReady(process);
            }
            else
                break;
        }

        _currProcess = Collections.min(_readyQueue, new Process.PrioritySorter());
        _readyQueue.remove(_currProcess);
    }

    public void schedule(){
        int burstTime = _currProcess.burstTime;
        int currAgingCounter = _agingCounter;
        while (burstTime > 0) {
            if(currAgingCounter == 0){
                for(Process process: _readyQueue){
                    process.ageProcess();
                }
                currAgingCounter = _agingCounter;
            }
            burstTime--;
            currAgingCounter--;
            incWaitingTime();
            _currTime++;
        }
        _deadList.add(_currProcess);
        _currProcess.turnaroundTime = _currProcess.waitingTime + _currProcess.burstTime;
        System.out.println(_currProcess.toString() + " finished at time, t = " + _currTime);
    }

    public void incWaitingTime(){
        for(Process process: _readyQueue){
            process.waitingTime++;
        }
    }

    public void invokeScheduler(){
        while(!_readyQueue.isEmpty() || !_waitingQueue.isEmpty()){
            invokeContextSwitch();
            schedule();
        }
        printStats();
    }

    public void printStats(){
        // Print waiting time
        System.out.println("=============================================================");
        System.out.println("Process waiting times");
        int waitingSum = 0;
        for(Process process: _deadList){
            waitingSum += process.waitingTime;
            System.out.println(process.toString() + ": " + process.waitingTime);
        }
        System.out.println("Average Waiting time: " + (double)waitingSum/_deadList.size());
        System.out.println("=============================================================");
        System.out.println("Process turnaround times");
        int turnaroundSum = 0;
        for(Process process: _deadList){
            turnaroundSum += process.turnaroundTime;
            System.out.println(process.toString() + ": " + process.turnaroundTime);
        }
        System.out.println("Average turnaround time: " + (double)turnaroundSum/_deadList.size());

    }

    public static void main(String[] args) {
        PriorityScheduler priorityScheduler = new PriorityScheduler(-1);
        Scanner scan = new Scanner(System.in);
        System.out.print("Number of processes -> ");
        int procNum = scan.nextInt();
        System.out.println("Enter processes names, arrival times, burst times and priority");
        for (int i = 0; i < procNum; i++){
            String name = scan.next();
            int arrival = scan.nextInt();
            int burst = scan.nextInt();
            int prio = scan.nextInt();
            priorityScheduler.addToReady(new Process(name, prio, burst, arrival));
        }
        priorityScheduler.invokeScheduler();
    }
}