//Non-Preemptive Shortest- Job First (SJF) (Must solve the starvation problem however any solution is acceptable)

import java.util.*;

public class SJF {

    /*static class Process implements Comparable<Process>{
        public String processName;
        public int arrivalTime;
        public int burstTime;
        public int completeTime; // current burst + totalTime
        public int turnAroundTime; //CompleteTime - Arrival Time
        public int waitingTime; // Turn Around Time - Burst Time
        public boolean completed;
        public Process(){
            completed= false;
        }

        @Override
        public int compareTo(Process o) {
            if(burstTime >= o.burstTime)
                return 1;
            return -1;
        }
    }*/

    public static void main(String[] args) {
        SJF sjf = new SJF();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Number of processes -> ");
        int processNum = scanner.nextInt();

        ArrayList<Process> processes = new ArrayList<>();

        System.out.println("Enter the processes names, arrival times and burst times:");
        for(int i=0; i<processNum; i++){
            String name = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            processes.add(new Process(name, burstTime, arrivalTime));
//            processes.get(i).processName = scanner.next();
//            processes.get(i).arrivalTime = scanner.nextInt();
//            processes.get(i).burstTime = scanner.nextInt();
        }
//        for (int i = 0; i < processNum; i++){
//            Process min = processes.get(i);
//            int index = i;
//            for (int j = i + 1; j < processNum; j++){
//                if (processes.get(j).arrivalTime <= min.arrivalTime){
//                    min = processes.get(j);
//                    index = j;
//                }
//            }
//            Process temp = processes.get(i);
//            processes.set(i, min);
//            processes.set(index, temp);
//        }
        Collections.sort(processes, new Process.ArrivalTimeSorter());
        sjf.SJF_Scheduling(processes);
    }

    public void SJF_Scheduling(ArrayList<Process> processes){
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
                if(processes.get(j).arrivalTime < totalTime){
                    arrived.add(processes.get(j));
                    processes.get(j).completed = true;
                }
            }
            Collections.sort(arrived, new Process.JobTimeSorter());
            for(int k=0; k<arrived.size(); k++){
                startProcess(arrived.get(k),totalTime);
                totalTime+= arrived.get(k).burstTime;
            }
            arrived.clear();
        }
    }

    public void startProcess(Process process,int totalTime){
        process.completeTime = totalTime +  process.burstTime;

        process.turnaroundTime = process.completeTime - process.arrivalTime;
        process.waitingTime = process.turnaroundTime - process.burstTime;
        System.out.println(process.name + " finished at: " + process.completeTime+"   " +
                "Turn Around Time: "+ process.turnaroundTime+"   Waiting Time: "
                +process.waitingTime);
        process.completed=true;
    }

}