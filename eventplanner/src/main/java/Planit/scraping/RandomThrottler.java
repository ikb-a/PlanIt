package Planit.scraping;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomThrottler extends Throttler {

	private Random random;
	
	public RandomThrottler(int maxCalls, TimeUnit timeUnit) {
		super(maxCalls, timeUnit);
		random = new Random();
	}

	/**
	 * Returns the processor time when the call call will be allowed.
	 * Adds a random amount of time up to 50% of the set minimum interval of waiting
	 */
	@Override
	public long getNextCall(){
		float randomAddition = ((random.nextFloat() / 2) + 1) * minTimeBetweenCalls;
		return (long) (previousCall + minTimeBetweenCalls + randomAddition);
	}
	
}
