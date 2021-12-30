import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class SRTF {
    private ArrayList<Process> _readyQueue;
    private ArrayList<Process> _waitingQueue;
    private ArrayList<Process> _deadList;
    private Process _currProcess;
    private int _currTime;

    public SRTF() {
        _deadList = new ArrayList<>();
        _readyQueue = new ArrayList<>();
        _waitingQueue = new ArrayList<>();
        _currProcess = null;
        _currTime = 0;
    }

    public void addToWaiting(Process process) {
        _waitingQueue.add(process);
    }

    public void addToReady(Process process) {
        _readyQueue.add(process);
        _waitingQueue.remove(process);
    }

    public void invokeContextSwitch() {
        //sort asc by process arrival time
        Collections.sort(_waitingQueue, new Process.ArrivalTimeSorter());
        while (!_waitingQueue.isEmpty()) { //while loop to move processes from waiting queue to ready queue
            Process process = _waitingQueue.get(0); //get the earliest element
            if (_currTime == process.arrivalTime || process.arrivalTime == -1) { // -1 so that it can skip directly to the ready queue
                //move to readt queue if process arrival time equals to the current time, or equals -1
                addToReady(process);
            } else // if the earliest process hasn't arrived yet (arrival time > curr time) it's guarnteed that no other process can enter the ready queue. so break
                break;
        }
        if (!_readyQueue.isEmpty()) {
            //get the shortest job process in the ready queue
            Process minProcess = Collections.min(_readyQueue, new Process.JobTimeSorter());
            if (_currProcess == null) //if no process is running currently, start the shortest process
                _currProcess = minProcess;
            else if (minProcess.burstTime < _currProcess.burstTime) { //else check if min process burst time is shorter than current process burst time
                addToReady(_currProcess);
                System.out.println(_currProcess.toString() + " preempted for " + minProcess.toString() + " at time, t = " + _currTime);
                _currProcess = minProcess;
            }
            _readyQueue.remove(_currProcess); //remove the currently running process from ready queue, so the next call to the context switcher is valid and isn't affected
        }
    }

    public void incWaitingTimes() {
        for (Process process : _readyQueue) {
            process.waitingTime++;
        }
    }

    private void schedule() {
        //this loop advances current time and decreases burst time of current process only
        //while queues aren't empty or there's a running process
        while (!_readyQueue.isEmpty() || !_waitingQueue.isEmpty() || _currProcess != null) {
            invokeContextSwitch();
            if (_currProcess != null) {
                _currProcess.burstTime--;
                _currTime++;

                if (_currProcess.burstTime == 0) {
                    System.out.println(_currProcess.toString() + " exited at time, t = " + _currTime);
                    _currProcess.turnaroundTime = _currProcess.waitingTime + _currProcess.burstTimeStore;
                    _deadList.add(_currProcess);
                    _currProcess = null;
                }
            } else
                _currTime++;
            incWaitingTimes();
        }
        printStats();
    }

    public void printStats() {
        // Print waiting time
        System.out.println("=============================================================");
        System.out.println("Process waiting times");
        int waitingSum = 0;
        for (Process process : _deadList) {
            waitingSum += process.waitingTime;
            System.out.println(process.toString() + ": " + process.waitingTime);
        }
        System.out.println("Average Waiting time: " + (double) waitingSum / _deadList.size());
        System.out.println("=============================================================");
        System.out.println("Process turnaround times");
        int turnaroundSum = 0;
        for (Process process : _deadList) {
            turnaroundSum += process.turnaroundTime;
            System.out.println(process.toString() + ": " + process.turnaroundTime);
        }
        System.out.println("Average turnaround time: " + (double) turnaroundSum / _deadList.size());

    }

    public static void main(String[] args) {
        SRTF srtf = new SRTF();
        Scanner scan = new Scanner(System.in);
        System.out.print("Number of processes -> ");
        int procNum = scan.nextInt();
        System.out.println("Enter processes names,arrival times and burst time");
        for (int i = 0; i < procNum; i++) {
            String name = scan.next();
            int arrival = scan.nextInt();
            int burst = scan.nextInt();
            srtf.addToWaiting(new Process(name, burst, arrival));
        }

        srtf.schedule();
    }
}

