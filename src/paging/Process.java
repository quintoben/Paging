package paging;


public class Process {
	private double A,B,C;
	private int id;
	private int word;
	private Page page;
	private int residencyTime;
	private int evictTime;
	private int pageFault;
	public Process(int id,double A,double B,double C){
		this.id=id;
		this.A=A;
		this.B=B;
		this.C=C;
		this.residencyTime=0;
		this.evictTime=0;
		this.pageFault=0;
	}
	
	public void addPageFault(){
		this.pageFault++;
	}
	
	public void addEvictTime(){
		this.evictTime++;
	}
	
	public void addResidencyTime(int time){
		this.residencyTime+=time;
	}
	
	public int getPageFault() {
		return pageFault;
	}
	public void setPageFault(int pageFault) {
		this.pageFault = pageFault;
	}
	
	public int getResidencyTime() {
		return residencyTime;
	}
	public void setResidencyTime(int residencyTime) {
		this.residencyTime = residencyTime;
	}
	public int getEvictTime() {
		return evictTime;
	}
	public void setEvictTime(int evictTime) {
		this.evictTime = evictTime;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public int getId() {
		return id;
	}
	public int getWord() {
		return word;
	}
	public void setWord(int word) {
		this.word = word;
	}
	public double getA() {
		return A;
	}
	public double getB() {
		return B;
	}
	public double getC() {
		return C;
	}
	
}
