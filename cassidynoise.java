package cassidynoise;

/**
 * @author Cassidy Friend
 * @version 1.0
 * 
 * <p> This is a noise method that is purpose built for 1D terrain generation, 2D will be coming soon. 
 * <p> The noise has a built in cache system.
 * <p> When using this method it is recommended to hash the x @hashSeed, this is the hashing method that is built in but it could be different.
 * <p> Pseudo-random numbers are used to create the terrain using the middle square method, this is built in but most programming languages
 * as a random number generator library and other methods can be used as well.
 * <p> The function (x-start)/(target-start) is used to offset slopes. To create the smooth terrain the smoothing function is used: 6x^5 - 15x^4 + 10x^3.
 * To create different heighten slopes the smoothing function is multiplied by the adjustment function: (max - min) + min. this can be found in the @smooth method.
 * <p> The @createUnitInterval method is to create numbers from 0 to 1 from a long.
 * <p> Finally the @getNoiseAt method is used to get the height of the terrain at any x value. It scales the x input by the @scalex and floors it.
 * Next it checks if the cache is occupied by the @doesCacheContain method and if it is occupied by the current x.
 * It gets the height from the @getCached method and the offset from the @getCachedOffset. If not then it creates the height with the @getRandomNumber method and
 * applies the minimum and maximum height using (max - min) + min and caches it using the @writeToCache. Finally the smooth function is used to smooth the height
 * from the cache and the output is returned.
 */
public class cassidynoise {
    long seed;
    double scalex;
    double cached[] = {0,0}, offsetcached[] = {0,0};
    int cachedLocation[] = {0,0};
    boolean isCacheOcupied = false;
    
    public interface locationdetails{
    	public double[] getrange(double x);
    }
    /**
     * 
     * @param seed The starting number for creating pseudo-random numbers for the heights of the terrain.
     * @param scale The scale of the terrain.
     */
    public cassidynoise(long seed, double scale) {
              this.seed = seed;
			  this.scalex = scale;
    }
	
    long hashSeed(long x) {
        x = (x ^ (x >>> 21)) * 0x45d9f3b;
        x = (x ^ (x >>> 15)) * 0x3335b369;
        return Math.abs(x);
    }
    
    double smooth(double x, double startheight, double targetheight, double startoffset, double targetoffset) {
    	x = (x - startoffset) / (targetoffset - startoffset);
        double start = 6.0 * Math.pow(x, 5);
        double middle = 15.0 * Math.pow(x, 4);
        double end = 10.0 * Math.pow(x, 3);
        return (start - middle + end) * (targetheight - startheight) + startheight;
    }

    long middleSquareNumber(long seed, int digits) {
		seed = hashSeed(seed);
        if (digits <= 0) throw new IllegalArgumentException("Digits must be positive.");
        long square = seed * seed;
        String squareStr = String.format("%0" + (2 * digits) + "d", square);
        int start = (squareStr.length() - digits) / 2;
        return Long.parseLong(squareStr.substring(start, start + digits));
    }
    
    double createUnitInterval(long input) {
    	final long multiplier = 1664525L;
        final long increment = 1013904223L;
        final long modulus = 4294967296L;
        long randomValue = (multiplier * input + increment) % modulus;
        randomValue ^= (randomValue >>> 16);
        randomValue ^= (randomValue << 5);
        randomValue = randomValue & 0x7FFFFFFF;
        return randomValue / (double) 0x7FFFFFFF;
	}
	
	double[] getRandomNumber(int input, double scalefactor) {
        long targetseed = seed;
        int scaled = (int)(Math.sqrt(scalefactor) * 4.0);
        for(int i = 0; i < Math.abs(middleSquareNumber(input, 3)) % scaled; i++) {
			targetseed = hashSeed(targetseed);
        }
        return new double[] {createUnitInterval(targetseed), createUnitInterval(Math.abs(middleSquareNumber(targetseed, 3)))};
    }
	
	boolean doesCacheContain(int x) {
		if((cachedLocation[0] == x || cachedLocation[1] == x) && isCacheOcupied) {
			return true;
		}
		return false;
	}
	
	double getCached(int x) {
		if(cachedLocation[0] == x) {
			return cached[0];
		}
		else if(cachedLocation[1] == x) {
			return cached[1];
		}
		return 0;
	}
	
	double getCachedOffset(int x) {
		if(cachedLocation[0] == x) {
			return offsetcached[0];
		}
		else if(cachedLocation[1] == x) {
			return offsetcached[1];
		}
		return 0;
	}
	void writeToCache(int cacheLocation, double cachedHeight, int Location, double offset) {
		cached[cacheLocation] = cachedHeight;
		offsetcached[cacheLocation] = offset;
		cachedLocation[cacheLocation] = Location;
		isCacheOcupied = true;
	}
	/**
	 * 
	 * @param x The location to get the height of.
	 * @param base This is the effector for the terrain.
	 * It is interchangeable on the go 3 numbers,
	 * The first two should be in order of minimum to maximum height.
	 * The third should be the maximum allowed offset for the rounded whole number to change the peak location of the current slope.
	 * WARNING: The number should be between 0 and 1 to prevent tearing and to keep the terrain smooth.
	 * @return A double that is the height of the terrain at the location of x.
	 */
    public double getNoiseAt(double x, locationdetails base) {
        double scaledx = x*scalex;
        int currentx = (int)Math.floor(scaledx);
        double currentrange[] = base.getrange(currentx);
        if(isCacheOcupied && doesCacheContain(currentx)) {
        	writeToCache(0, getCached(currentx), currentx, getCachedOffset(currentx));
        }
        else {
        	double current[] = getRandomNumber(currentx, currentrange[1] - currentrange[0]);
        	writeToCache(0, (current[0] * (currentrange[1] - currentrange[0]) ) + currentrange[0], currentx, current[1]);
        }
        if(offsetcached[0] + currentx < scaledx) {
        	int targetx = (int)Math.ceil(scaledx);
        	if(isCacheOcupied && doesCacheContain(targetx)) {
        		writeToCache(1, getCached(targetx), targetx, getCachedOffset(targetx));
        	}
        	else {
	        	double targetrange[] = base.getrange(targetx);
	        	double target[] = getRandomNumber(targetx, targetrange[1] - targetrange[0]);
	        	writeToCache(1, (target[0] * (targetrange[1] - targetrange[0]) ) + targetrange[0], targetx, target[1]);
        	}
        	return smooth(scaledx - (currentx), cached[0], cached[1], offsetcached[0], offsetcached[1] + 1);
        }
        else {
        	int targetx = (int)Math.floor(scaledx) - 1;
        	if(isCacheOcupied && doesCacheContain(targetx)) {
        		writeToCache(1, getCached(targetx), targetx, getCachedOffset(targetx));
        	}
        	else {
        		double targetrange[] = base.getrange(targetx);
        		double target[] = getRandomNumber(targetx, targetrange[1] - targetrange[0]);
				writeToCache(1, (target[0] * (targetrange[1] - targetrange[0]) ) + targetrange[0], targetx, target[1]);
        	}
        	return smooth(scaledx - (currentx), cached[1], cached[0], offsetcached[1] - 1, offsetcached[0]);
        }
    }
}
