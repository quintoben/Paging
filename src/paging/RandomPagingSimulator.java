package paging;

public class RandomPagingSimulator extends PagingSimulator {

	public RandomPagingSimulator(int machineSize, int pageSize,
			int processSize, int jobMix, int reference, String algorithm,
			String debug) {
		super(machineSize, pageSize, processSize, jobMix, reference, algorithm,
				debug);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int evict() {
		// TODO Auto-generated method stub
		int random = randomNumber.get(randomIndex++);
		return random % frameTable.size();
	}

	@Override
	protected void updateAfterHit(int hit) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateAfterPageFault(int frameNum) {
		// TODO Auto-generated method stub

	}
}
