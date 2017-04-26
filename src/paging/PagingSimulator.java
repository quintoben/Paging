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
	protected String debug;

	public PagingSimulator(int machineSize, int pageSize, int processSize,
			int jobMix, int reference, String algorithm, String debug) {
		this.machineSize = machineSize;
		this.pageSize = pageSize;
		this.processSize = processSize;
		this.jobMix = jobMix;
		this.reference = reference;
		this.algorithm = algorithm;
		this.debug = debug;
		switch (jobMix) {
		case 1:
			processes = new Process[1];
			processes[0] = new Process(1, 1, 0, 0);
			break;
		case 2:
			processes = new Process[4];
			for (int i = 0; i < processes.length; i++) {
				processes[i] = new Process(i + 1, 1, 0, 0);
			}
			break;
		case 3:
			processes = new Process[4];
			for (int i = 0; i < processes.length; i++) {
				processes[i] = new Process(i + 1, 0, 0, 0);
			}
			break;
		case 4:
			processes = new Process[4];
			processes[0] = new Process(1, 0.75, 0.25, 0);
			processes[1] = new Process(2, 0.75, 0, 0.25);
			processes[2] = new Process(3, 0.75, 0.125, 0.125);
			processes[3] = new Process(4, 0.5, 0.125, 0.125);
			break;
		}
		this.time = new int[processes.length];
		// initial word
		for (int k = 1; k <= processes.length; k++) {
			processes[k - 1].setWord((111 * k) % processSize);
		}
		this.randomNumber = readRandom("random-numbers");
		// initial frameTable
		this.frameTable = new ArrayList<Frame>();
		int frameSize = machineSize / pageSize;
		for (int i = 0; i < frameSize; i++) {
			frameTable.add(new Frame(i, true));
		}
		this.randomIndex = 0;
	}

	public void run() {
		int total = 0;
		while (total < reference * processes.length) {
			for (int pNum = 0; pNum < processes.length
					&& time[pNum] < reference; pNum++) {
				for (int i = 0; i < QUANTUM && time[pNum] < reference; i++, time[pNum]++, total++) {
					int pageNum = processes[pNum].getWord() / pageSize;
					processes[pNum].setPage(new Page(pageNum));
					// if hit return non-negative else return -1
					int hit = hitNum(processes[pNum]);
					if (hit == -1) {
						processes[pNum].addPageFault();
						// System.out.print("fault ");
						int frameNum;
						// free frame exist
						if (isFreeFrame()) {

							frameNum = findFreeFrame();
							insertPage(processes[pNum], total + 1, frameNum);

						} else {

							frameNum = evict();
							// System.out.println("evict frame"+frameNum);
							int startTime = frameTable.get(frameNum).getTime();
							int processId = frameTable.get(frameNum)
									.getProcessId();
							insertPage(processes[pNum], total + 1, frameNum);
							processes[processId - 1].addEvictTime();
							processes[processId - 1].addResidencyTime(total + 1
									- startTime);
						}
						updateAfterPageFault(frameNum);
					}
					// hit and update
					else {
						// System.out.println("hit "+hit);
						updateAfterHit(hit);
					}
					int rand = randomNumber.get(randomIndex++);
					double y = rand / (Integer.MAX_VALUE + 1d);
					processes[pNum].setWord(computeW(y, processes[pNum]));

				}
			}
		}
		printResult();
	}

	protected abstract void updateAfterHit(int hit);

	protected abstract void updateAfterPageFault(int frameNum);

	protected int hitNum(Process process) {
		for (Frame f : frameTable) {
			if (f.getProcessId() == process.getId()
					&& f.getResident().getNumber() == process.getPage()
							.getNumber()) {
				return f.getFrameId();
			}
		}
		return -1;
	}

	protected boolean isFreeFrame() {
		for (Frame f : frameTable) {
			if (f.isFree())
				return true;
		}
		return false;
	}

	protected int findFreeFrame() {
		int index = frameTable.size() - 1;
		while (index >= 0) {
			if (frameTable.get(index).isFree()) {
				break;
			}
			index--;
		}
		return index;
	}

	protected void insertPage(Process process, int time, int index) {
		frameTable.get(index).setResident(process.getPage());
		frameTable.get(index).setTime(time);
		frameTable.get(index).setFree(false);
		frameTable.get(index).setProcessId(process.getId());
	}

	abstract protected int evict();

	protected int computeW(double y, Process p) {
		if (y < p.getA())
			return (p.getWord() + 1) % processSize;
		else if (y < p.getA() + p.getB())
			return (p.getWord() - 5 + processSize) % processSize;
		else if (y < p.getA() + p.getB() + p.getC())
			return (p.getWord() + 4) % processSize;
		else {
			return randomNumber.get(randomIndex++) % processSize;
		}
	}

	protected void printResult() {
		System.out.println("The machine size is " + this.machineSize + ".");
		System.out.println("The page size is " + this.pageSize + ".");
		System.out.println("The process size is " + this.processSize + ".");
		System.out.println("The job mix number is " + this.jobMix + ".");
		System.out.println("The number of references per process is "
				+ this.reference + ".");
		System.out.println("The replacement algorithm is " + this.algorithm
				+ ".");
		System.out.println("The level of debugging output is " + this.debug);
		System.out.println();
		int totalEvictTime = 0;
		int totalPageFault = 0;
		int totalResidencyTime = 0;
		for (int i = 0; i < processes.length; i++) {
			System.out.print("Process " + (i + 1) + " had "
					+ processes[i].getPageFault() + " faults");
			totalPageFault += processes[i].getPageFault();
			if (processes[i].getEvictTime() == 0) {
				System.out
						.println(".\n\tWith no evictions, the average residence is undefined.");
			} else {
				totalResidencyTime += processes[i].getResidencyTime();
				totalEvictTime += processes[i].getEvictTime();
				double avg = (double) (processes[i].getResidencyTime())
						/ (double) (processes[i].getEvictTime());
				System.out.println(" and " + avg + " average residency.");
			}
		}
		double totalAvg = (double) (totalResidencyTime)
				/ (double) (totalEvictTime);
		System.out.print("\nThe total number of faults is " + totalPageFault);
		if (totalEvictTime == 0) {
			System.out
					.println(".\n\tWith no evictions, the overall average residence is undefined.");
		} else {
			System.out.println(" and the overall average residency is "
					+ totalAvg + ".");
		}
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
