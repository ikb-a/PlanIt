package edu.toronto.cs.PlanIt.scraping;

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
	
	private long minTimeBetweenCalls;
	private long previousCall;
	
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
	 */
	public boolean next(){
		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - previousCall;
		//wait...
		boolean success = true;
		if (elapsed < minTimeBetweenCalls){
			try {
				Thread.sleep(minTimeBetweenCalls - elapsed);
			} catch (InterruptedException e) {
				e.printStackTrace();
				success = false;
			}
		}
		previousCall = System.currentTimeMillis();
		return success;
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
