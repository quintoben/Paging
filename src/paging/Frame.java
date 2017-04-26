package paging;

public class Frame {
	private boolean isFree;
	private int processId;
	private int frameId;
	private int time;
	private Page resident;

	public Frame(int frameId, boolean isFree) {
		this.frameId = frameId;
		this.isFree = isFree;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getFrameId() {
		return frameId;
	}

	public void setFrameId(int frameId) {
		this.frameId = frameId;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public Page getResident() {
		return resident;
	}

	public void setResident(Page resident) {
		this.resident = resident;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

}
