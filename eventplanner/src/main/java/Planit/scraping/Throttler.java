package Planit.scraping;

import java.util.concurrent.TimeUnit;

/**
 * When doing any web scraping (internally controlled in sources, or for generating training data sets)
 * a Throttler instance can be used to regulate the speed at which web pages are accessed.
 * @author wginsberg
 *
 */
public class Throttler {

	private int maxCalls;
	private TimeUnit timeUnit;
	
	protected long minTimeBetweenCalls;
	protected long previousCall;
	
	/**
	 * Instantiate a throttler by specifying how many calls per time-unit can be allowed.
	 * @param maxCalls
	 * @param timeUnit
	 */
	public Throttler (int maxCalls, TimeUnit timeUnit){
		setMaxCalls(maxCalls);
		setTimeUnit(timeUnit);
		previousCall = 0;
	}
	
	/**
	 * Call this to 'request' another call to a source, it will block until one can be granted.
	 * @return false if the thread is interrupted.
	 * @throws RuntimeException if the thread was interrupted
	 */
	public synchronized void next() throws RuntimeException{
		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - previousCall;
		//wait...
		if (elapsed < minTimeBetweenCalls){
			try {
				Thread.sleep(minTimeBetweenCalls - elapsed);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		previousCall = System.currentTimeMillis();
	}

	public void setMaxCalls(int maxCalls) {
		this.maxCalls = maxCalls;
		setMinTimeBetweenCalls();
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		setMinTimeBetweenCalls();
	}
	
	/**
	 * Updates minTimeBetweenCalls based on the variables maxCalls and timeUnit
	 * If timeUnit is null, then minTimeBetweenCalls will be zero.
	 */
	private void setMinTimeBetweenCalls(){
		if (timeUnit == null){
			minTimeBetweenCalls = 0;
		}
		else{
			minTimeBetweenCalls = Math.round((((double) 1 / maxCalls) * timeUnit.toMillis(1)));
		}
	}
	
	public int getMaxCalls() {
		return maxCalls;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * Returns the processor time of the previous call.
	 * @return
	 */
	public long getPreviousCall() {
		return previousCall;
	}

	/**
	 * Returns the processor time when the call call will be allowed.
	 */
	public long getNextCall(){
		return previousCall + minTimeBetweenCalls;
	}

	/**
	 * Minimum amount to wait between calls, in the units set in this object.
	 * @return
	 */
	public long getMinTimeBetweenCalls() {
		return minTimeBetweenCalls;
	}
}
