package paging;

public class Paging {
	public static void main(String[] args) {
		if (args.length < 6) {
			System.out.println("Please check your arguments!");
		} else {
			int machineSize = Integer.parseInt(args[0]);
			int pageSize = Integer.parseInt(args[1]);
			int processSize = Integer.parseInt(args[2]);
			int jobMix = Integer.parseInt(args[3]);
			int reference = Integer.parseInt(args[4]);
			String algorithm = args[5];
			String debug = args[6];

			if (algorithm.equals("lru")) {
				LRUPagingSimulator lru = new LRUPagingSimulator(machineSize,
						pageSize, processSize, jobMix, reference, algorithm,
						debug);
				lru.run();
			} else if (algorithm.equals("lifo")) {
				LIFOPagingSimulator lifo = new LIFOPagingSimulator(machineSize,
						pageSize, processSize, jobMix, reference, algorithm,
						debug);
				lifo.run();
			} else if (algorithm.equals("random")) {
				RandomPagingSimulator random = new RandomPagingSimulator(
						machineSize, pageSize, processSize, jobMix, reference,
						algorithm, debug);
				random.run();
			} else {
				System.out.println("Please check the algorithm you choose!");
			}
		}
	}

}
