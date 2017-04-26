package paging;

import java.util.Stack;

public class LIFOPagingSimulator extends PagingSimulator{

	private Stack<Integer> stack;
	
	public LIFOPagingSimulator(int machineSize, int pageSize, int processSize,
			int jobMix, int reference, String algorithm) {
		super(machineSize, pageSize, processSize, jobMix, reference, algorithm);
		// TODO Auto-generated constructor stub
		stack = new Stack<Integer>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int total=0;
		while(total<reference*processes.length){
			for(int pNum=0;pNum<processes.length&&time[pNum]<reference;pNum++){
				for(int i=0;i<QUANTUM&&time[pNum]<reference;i++,time[pNum]++,total++){
					System.out.println("\nprocess: "+(pNum+1)+" time: "+(total+1));
					int pageNum=processes[pNum].getWord()/pageSize;
					System.out.println("word: "+processes[pNum].getWord()+" pageNum: "+pageNum);
					processes[pNum].setPage(new Page(pageNum));
					//if hit return non-negative else return -1
					int hit = hitNum(processes[pNum]);
					if(hit==-1){
						processes[pNum].addPageFault();
						System.out.print("fault ");
						int frameNum;
						//free frame exist
						if(isFreeFrame()){
							
							frameNum=findFreeFrame();
							System.out.println("load frame "+frameNum);
							insertPage(processes[pNum],total+1,frameNum);
							
						}
						else{
							
							frameNum=evict();
							System.out.println("evict frame"+frameNum);
							int startTime = frameTable.get(frameNum).getTime();
							int processId=frameTable.get(frameNum).getProcessId();
							insertPage(processes[pNum],total+1,frameNum);
							processes[processId-1].addEvictTime();
							processes[processId-1].addResidencyTime(total+1-startTime);
						}
						stack.push(frameNum);
					}
					//hit and update 
					else{
						System.out.println("hit "+hit);
						
					}
					int rand=randomNumber.get(randomIndex++);
					double y = rand / (Integer.MAX_VALUE + 1d);
					System.out.println(y+" "+rand);
					processes[pNum].setWord(computeW(y,processes[pNum]));

				}
			}
		}
		printResult();
	}

	@Override
	protected int evict() {
		// TODO Auto-generated method stub
		return stack.pop();
	}

}
