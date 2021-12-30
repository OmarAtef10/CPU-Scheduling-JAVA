//Non-Preemptive Shortest- Job First (SJF) (Must solve the starvation problem however any solution is acceptable)

import java.util.*;

public class SJF {

    public int ageLimit = 10;

    public static void main(String[] args) {
        SJF sjf = new SJF();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Number of processes -> ");
        int processNum = scanner.nextInt();

        ArrayList<Process> processes = new ArrayList<>();

        System.out.println("Enter the processes names, arrival times and burst times:");
        for (int i = 0; i < processNum; i++) {
            String name = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            processes.add(new Process(name, burstTime, arrivalTime));

        }

        Collections.sort(processes, new Process.ArrivalTimeSorter());
        Collections.sort(processes, new Process.JobTimeSorter());
        sjf.SJF_Scheduling(processes);

        Double avgWaiting = 0.0, avgTurnAround = 0.0;
        for (Process process : processes) {
            avgWaiting += process.waitingTime;
            avgTurnAround += process.turnaroundTime;
        }
        avgWaiting /= processes.size();
        avgTurnAround /= processes.size();
        System.out.println("======================================");
        System.out.println("Average Waiting : " + avgWaiting);
        System.out.println("Average TurnAround: " + avgTurnAround);
    }

    public void SJF_Scheduling(ArrayList<Process> processes) {
        List<Process> arrived = new ArrayList<>();
        int totalTime=0;

        for(int i=0; i<processes.size(); i++) {
            Process p = processes.get(i);
            if(p.completed){
                continue;
            }
            startProcess(p,totalTime);
            totalTime+= p.burstTime;
            for(int j=i+1; j<processes.size(); j++){
                if(processes.get(j).arrivalTime < totalTime && !processes.get(j).completed){
                    arrived.add(processes.get(j));
                    processes.get(j).completed = true;
                }
            }
            Collections.sort(arrived,new Process.JobTimeSorter());
            for(int k=0; k<arrived.size(); k++){
                int maxAge = 0;
                int index = -1;
                for (int x = 0; x < processes.size(); x++){
                    Process proc = processes.get(x);
                    if (proc.age > this.ageLimit && !proc.completed && proc.age > maxAge){
                        maxAge = proc.age;
                        index = x;
                    }
                }
                if (index != -1){
                    startProcess(processes.get(index), totalTime);
                    this.ageLimit += 10;
                }
                startProcess(arrived.get(k),totalTime);
                totalTime+= arrived.get(k).burstTime;
            }
            arrived.clear();
            for (Process process : processes){
                process.age = totalTime - process.arrivalTime;
            }
        }
    }

    public void startProcess(Process process, int totalTime) {
        process.completeTime = totalTime + process.burstTime;

        process.turnaroundTime = process.completeTime - process.arrivalTime;
        process.waitingTime = process.turnaroundTime - process.burstTime;
        System.out.println(process.name + " finished at: " + process.completeTime + "   " +
                "Turn Around Time: " + process.turnaroundTime + "   Waiting Time: "
                + process.waitingTime);
        process.completed = true;
    }

}