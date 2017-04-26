package paging;

import java.util.Stack;

public class LIFOPagingSimulator extends PagingSimulator {

	private Stack<Integer> stack;

	public LIFOPagingSimulator(int machineSize, int pageSize, int processSize,
			int jobMix, int reference, String algorithm, String debug) {
		super(machineSize, pageSize, processSize, jobMix, reference, algorithm,
				debug);
		// TODO Auto-generated constructor stub
		stack = new Stack<Integer>();
	}

	@Override
	protected int evict() {
		// TODO Auto-generated method stub
		return stack.pop();
	}

	@Override
	protected void updateAfterHit(int hit) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateAfterPageFault(int frameNum) {
		// TODO Auto-generated method stub
		stack.push(frameNum);
	}

}
