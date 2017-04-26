package paging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class PagingSimulator {
	protected final int QUANTUM = 3; 
	protected StringBuilder result;
	protected int machineSize;
	protected int pageSize;
	protected int processSize;
	protected int jobMix;
	protected int reference;
	protected String algorithm;
	protected Process[] processes;
	protected List<Integer> randomNumber;
	protected int[] time;
	protected List<Frame> frameTable;
	protected int randomIndex;
	
	public PagingSimulator(int machineSize,int pageSize,int processSize,int jobMix,int reference,String algorithm){
		this.machineSize=machineSize;
		this.pageSize=pageSize;
		this.processSize=processSize;
		this.jobMix=jobMix;
		this.reference=reference;
		this.algorithm=algorithm;
		switch(jobMix){
			case 1: 
				processes = new Process[1];
				processes[0]=new Process(1,1,0,0);
				break;
			case 2: 
				processes = new Process[4];
				for(int i=0;i<processes.length;i++){
					processes[i]=new Process(i+1,1,0,0);
				}
				break;
			case 3: 
				processes = new Process[4];
				for(int i=0;i<processes.length;i++){
					processes[i]=new Process(i+1,0,0,0);
				}
				break;
			case 4:
				processes = new Process[4];
				processes[0]=new Process(1,0.75,0.25,0);
				processes[1]=new Process(2,0.75,0,0.25);
				processes[2]=new Process(3,0.75,0.125,0.125);
				processes[3]=new Process(4,0.5,0.125,0.125);
				break;
		}
		this.time=new int[processes.length];
		//initial word
		for(int k=1;k<=processes.length;k++){
			processes[k-1].setWord((111*k)%processSize);
		}
		this.randomNumber=readRandom("random-numbers");
		//initial frameTable
		this.frameTable =new ArrayList<Frame>();
		int frameSize=machineSize/pageSize;
		for(int i=0;i<frameSize;i++){
			frameTable.add(new Frame(i,true));
		}
		this.randomIndex=0;
	}
	
	public abstract void run();
	
	protected int hitNum(Process process){
		for(Frame f:frameTable){
			if(f.getProcessId()==process.getId()&&f.getResident().getNumber()==process.getPage().getNumber()){
				return f.getFrameId();
			}
		}
		return -1;
	}
	
	
	protected boolean isFreeFrame(){
		for(Frame f:frameTable){
			if(f.isFree()) return true;
		}
		return false;
	}
	
	protected int findFreeFrame(){
		int index=frameTable.size()-1;
		while(index>=0){
			if(frameTable.get(index).isFree()){
				break;
			}
			index--;
		}
		return index;
	}
	
	protected void insertPage(Process process,int time,int index){
		frameTable.get(index).setResident(process.getPage());
		frameTable.get(index).setTime(time);
		frameTable.get(index).setFree(false);
		frameTable.get(index).setProcessId(process.getId());		
	}
	
	abstract protected int evict();
	
	protected int computeW(double y, Process p){
		System.out.println(p.getA()+" "+p.getB()+" "+p.getC());
		if(y<p.getA()) return (p.getWord()+1)%processSize;
		else if(y<p.getA()+p.getB()) return (p.getWord()-5+processSize)%processSize;
		else if(y<p.getA()+p.getB()+p.getC()) return (p.getWord()+4)%processSize;
		else {
			System.out.println(randomNumber.get(randomIndex));
			return randomNumber.get(randomIndex++)%processSize;
		}
	}
//	double y = randomNumber.get(time) / (Integer.MAX_VALUE + 1d);

//	w+1 mod S with probability A
//	w-5 mod S with probability B
//	w+4 mod S with probability C
//	a random value in 0..S-1 each with probability (1-A-B-C)/S
	
	protected void printResult(){
		int totalEvictTime=0;
		int totalPageFault=0;
		int totalResidencyTime=0;
		for(int i=0;i<processes.length;i++){
			System.out.print("Process "+(i+1)+" had "+processes[i].getPageFault()+" faults ");
			totalPageFault+=processes[i].getPageFault();
			if(processes[i].getEvictTime()==0){
				System.out.println("\n\t\tWith no evictions, the average residence is undefined.");
			}
			else{
				totalResidencyTime+=processes[i].getResidencyTime();
				totalEvictTime+=processes[i].getEvictTime();
				double avg=(double)(processes[i].getResidencyTime())/(double)(processes[i].getEvictTime());
				System.out.println("and "+avg+" average residency.");
				System.out.println("Rtime: "+processes[i].getResidencyTime()+" Etime: "+processes[i].getEvictTime());
			}
		}
		double totalAvg=(double)(totalResidencyTime)/(double)(totalEvictTime);
		System.out.println("The total number of faults is "+totalPageFault+" and the overall average residency is "+totalAvg+".");
	}

	private List<Integer> readRandom(String path) {
		String pathname = path;
		StringBuffer sb = new StringBuffer();
		try {
			File filename = new File(pathname);
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader);

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + " ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] string = sb.toString().replace('\n', ' ').replace('(', ' ')
				.replace(')', ' ').trim().split("\\s+");
		List<Integer> randomNumber = new ArrayList<Integer>();
		for (int i = 0; i < string.length; i++) {
			randomNumber.add(Integer.parseInt(string[i]));
		}
		return randomNumber;
	}


}
