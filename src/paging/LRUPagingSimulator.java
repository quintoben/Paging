package paging;

import java.util.HashMap;
import java.util.Map;

public class LRUPagingSimulator extends PagingSimulator{

	private Node head=null;
	private Node tail=null;
	Map<Integer,Node> map=new HashMap<Integer,Node>();
	public LRUPagingSimulator(int machineSize, int pageSize, int processSize,
			int jobMix, int reference, String algorithm) {
		super(machineSize, pageSize, processSize, jobMix, reference, algorithm);
		// TODO Auto-generated constructor stub
	}
	public void run(){
		
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
						//insert the node in front
						Node node=new Node(frameNum);
						node.next=head;
						node.pre=null;
						if(head!=null){
							head.pre=node;
						}
						head=node;
						if(tail==null){
							tail=head;
						}
						System.out.println("insert: "+tail.frameNumber);
						map.put(frameNum,node);
					}
					//hit and update 
					else{
						System.out.println("hit "+hit);
						Node node = map.get(hit);

						if(node.pre!=null){
							node.pre.next=node.next;
							if(node.next!=null){
								node.next.pre=node.pre;
							}
							//node is a tail
							else{
								tail=node.pre;
							}
							node.pre=null;
							node.next=head;
							head.pre=node;
							head=node;
						}
						
					}
					int rand=randomNumber.get(randomIndex++);
					double y = rand / (Integer.MAX_VALUE + 1d);
					System.out.println(y+" "+rand);
					processes[pNum].setWord(computeW(y,processes[pNum]));
					System.out.println("head: "+head.frameNumber);
					Node h=head;
					Node t=tail;

					while(h!=null){
						System.out.print(h.frameNumber+" ");
						h=h.next;
					}
					System.out.println();
					while(t!=null){
						System.out.print(t.frameNumber+" ");
						t=t.pre;
					}
					System.out.println();
				}
			}
		}
		printResult();
	}
	private class Node{
		public Node(int frameNumber){
			this.frameNumber=frameNumber;
		}
		int frameNumber;
		Node next;
		Node pre;
	}
	@Override
	protected int evict() {
		// TODO Auto-generated method stub
		int frameNum=tail.frameNumber;
		System.out.println("tail: "+tail.frameNumber);
		if(tail.pre==null){
			tail=null;
			head=null;
		}
		else{
			System.out.println("pre: "+tail.pre.frameNumber);
			
			tail=tail.pre;
			tail.next=null;
			System.out.println("current tail: "+tail.frameNumber);
		}
		map.remove(frameNum);
		
		return frameNum;
	}
}
