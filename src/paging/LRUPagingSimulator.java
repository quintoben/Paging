package paging;

import java.util.HashMap;
import java.util.Map;

public class LRUPagingSimulator extends PagingSimulator {

	private class Node {
		public Node(int frameNumber) {
			this.frameNumber = frameNumber;
		}

		int frameNumber;
		Node next;
		Node pre;
	}

	private Node head;
	private Node tail;
	Map<Integer, Node> map;

	public LRUPagingSimulator(int machineSize, int pageSize, int processSize,
			int jobMix, int reference, String algorithm, String debug) {
		super(machineSize, pageSize, processSize, jobMix, reference, algorithm,
				debug);
		// TODO Auto-generated constructor stub
		this.head = null;
		this.tail = null;
		this.map = new HashMap<Integer, Node>();
	}

	@Override
	protected int evict() {
		// TODO Auto-generated method stub
		int frameNum = tail.frameNumber;
		if (tail.pre == null) {
			tail = null;
			head = null;
		} else {
			tail = tail.pre;
			tail.next = null;
		}
		map.remove(frameNum);

		return frameNum;
	}

	@Override
	protected void updateAfterHit(int hit) {
		// TODO Auto-generated method stub
		Node node = map.get(hit);
		if (node.pre != null) {
			node.pre.next = node.next;
			if (node.next != null) {
				node.next.pre = node.pre;
			}
			// node is a tail
			else {
				tail = node.pre;
			}
			node.pre = null;
			node.next = head;
			head.pre = node;
			head = node;
		}
	}

	@Override
	protected void updateAfterPageFault(int frameNum) {
		// TODO Auto-generated method stub
		Node node = new Node(frameNum);
		node.next = head;
		node.pre = null;
		if (head != null) {
			head.pre = node;
		}
		head = node;
		if (tail == null) {
			tail = head;
		}
		map.put(frameNum, node);
	}
}
