package com.maxgarfinkel.suffixTree;

/**
 * Represents the remaining suffix to be inserted during suffix tree
 * construction. This is essentially a start and lastElement pointer into the
 * underlying sequence. This is like a kind of sliding window where the head
 * can never fall behind the tail, and the tail can never fall behind the head.
 * 
 * @author max garfinkel
 * 
 * @param <T>
 */
class Suffix<T,S extends Iterable<T>> {
	private int start;
	private int end;
	private Sequence<T,S> sequence;

	/**
	 * Construct a subsequence of sequence. The subsequence will be a suffix of
	 * the sequence UP TO the point in the sequence we have reached whilst
	 * running Ukonnen's algorithm. In this sense it is not a true suffix of the
	 * sequence but only a suffix of the portion of the sequence we have so far
	 * parsed.
	 * @param start The start position of the suffix within the sequence
	 * @param end The lastElement position of the suffix within the sequence
	 * @param sequence The main sequence
	 */
	public Suffix(int start, int end, Sequence<T,S> sequence) {
		testStartAndEndValues(start, end);
		testStartEndAgainstSequenceLength(start, end, sequence.getLength());
		this.start = start;
		this.end = end;
		this.sequence = sequence;
	}
	
	private void testStartEndAgainstSequenceLength(int start, int end, int sequenceLength){
		if(start > sequenceLength || end > sequenceLength)
			throw new IllegalArgumentException("Suffix start and lastElement must be less than or equal to sequence length");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[(");
		sb.append(start).append(", ").append(end).append(")");
		int end = getEndPosition();
		for (int i = start; i < end; i++) {
			sb.append(sequence.getItem(i)).append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 
	 * @return The position in the master sequence of the lastElement item in this
	 *         suffix. This value is inclusive, thus and lastElement of 0 implies the
	 *         suffix contains only the item at <code>sequence[0]</code>
	 */
	int getEndPosition() {
		return end;
	}

    /**
     *
     * @return The position in the master sequence of the start item in this
     *         suffix. This value is inclusive, thus and start of 0 implies the
     *         suffix contains only the item at <code>sequence[0]</code>
     */
    int getStartPosition() { return start;}

	/**
	 * Get the lastElement item of this suffix.
	 * 
	 * @return The lastElement item of sequence
	 */
	Object getEndItem() {
		if(isEmpty())
			return null;
		return sequence.getItem(end-1);
	}

	/**
	 * Get the start of this suffix.
	 * 
	 * @return
	 */
	Object getStart() {
		if(isEmpty())
			return null;
		return sequence.getItem(start);
	}

	/**
	 * Decrement the length of this suffix. This is done by incrementing the
	 * start position. This is reducing its length from the back.
	 */
	void decrement() {
		if(start==end)
			increment();
		start++;
	}

	/**
	 * Increments the length of the suffix by incrementing the lastElement position. The
	 * effectivly moves the suffix forward, along the master sequence.
	 */
	void increment() {
		end++;
		if(end > sequence.getLength())
			throw new IndexOutOfBoundsException("Incremented suffix beyond lastElement of sequence");
		
	}

	/**
	 * Indicates if the suffix is empty.
	 * 
	 * @return
	 */
	boolean isEmpty() {
		return start >= end || end > sequence.getLength();
	}

	/**
	 * Retrieves the count of remaining items in the suffix.
	 * 
	 * @return The number of items in the suffix.
	 */
	int getRemaining() {
		if(isEmpty())
			return 0;
		else
			return (end - start);
	}

	/**
	 * Retrieves the item the given distance from the lastElement of the suffix.
	 * 
	 * @param distanceFromEnd
	 *            The distance from the lastElement.
	 * @return The item the given distance from the lastElement.
	 * @throws IllegalArgumentException
	 *             if the distance from lastElement is greater than the length of the
	 *             suffix.
	 */
	public Object getItemXFromEnd(int distanceFromEnd) {
		if ((end - (distanceFromEnd)) < start){
			throw new IllegalArgumentException(distanceFromEnd
					+ " extends before the start of this suffix: ");
		}
		return sequence.getItem(end - distanceFromEnd);
	}
	
	void reset(int start, int end){
		testStartAndEndValues(start, end);
		this.start = start;
		this.end = end;
	}
	
	private void testStartAndEndValues(int start, int end){
		if(start < 0 || end < 0)
			throw new IllegalArgumentException("You cannot set a suffix start or lastElement to less than zero.");
		if(end < start)
			throw new IllegalArgumentException("A suffix lastElement position cannot be less than its start position.");
	}
}
