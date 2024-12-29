package cassidynoise;

public class cassidynoise {
    long seed;
    double scalex;
    double cached[] = {0,0}, offsetcached[] = {0,0};
    int cachedLocation[] = {0,0};
    boolean isCacheOcupied = false;
    
    public interface locationdetails{
    	public double[] getrange(double x);
    }
    
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
	
	double[] getRandomNumber(int input, double scalefactor) {
        long targetseed = seed;
        int scaled = (int)(Math.sqrt(scalefactor) * 4.0);
        for(int i = 0; i < Math.abs(middleSquareNumber(input, 3)) % scaled; i++) {
			targetseed = hashSeed(targetseed);
        }
        final long multiplier = 1664525L;
        final long increment = 1013904223L;
        final long modulus = (1L << 32);
        long randomValue = (multiplier * targetseed + increment) % modulus;
        randomValue ^= (randomValue >>> 16);
        randomValue ^= (randomValue << 5);
        randomValue = randomValue & 0x7FFFFFFF;
        long randomoffset = (multiplier * Math.abs(middleSquareNumber(targetseed, 3)) + increment) % modulus;
		randomoffset ^= (randomoffset >>> 16);
		randomoffset ^= (randomoffset << 5);
		randomoffset = randomoffset & 0x7FFFFFFF;
        return new double[] {randomValue / (double) 0x7FFFFFFF, randomoffset / (double) 0x7FFFFFFF};
    }
	
	boolean doescachecontain(int x) {
		if((cachedLocation[0] == x || cachedLocation[1] == x) && isCacheOcupied) {
			return true;
		}
		return false;
	}
	
	double getcached(int x) {
		if(cachedLocation[0] == x) {
			return cached[0];
		}
		else if(cachedLocation[1] == x) {
			return cached[1];
		}
		return 0;
	}
	
	double getcachedoffset(int x) {
		if(cachedLocation[0] == x) {
			return offsetcached[0];
		}
		else if(cachedLocation[1] == x) {
			return offsetcached[1];
		}
		return 0;
	}

    public double getNoiseAt(double x, locationdetails base) {
        double scaledx = x*scalex;
        int currentx = (int)Math.floor(scaledx);
        double currentrange[] = base.getrange(currentx);
        if(isCacheOcupied && doescachecontain(currentx)) {
        	cached[0] = getcached(currentx);
			offsetcached[0] = getcachedoffset(currentx);
			cachedLocation[0] = currentx;
        }
        else {
        	double current[] = getRandomNumber(currentx, currentrange[1] - currentrange[0]);
        	cached[0] = (current[0] * (currentrange[1] - currentrange[0]) ) + currentrange[0];
			offsetcached[0] = (current[1] * currentrange[2]);
        	cachedLocation[0] = currentx;
        	isCacheOcupied = true;
        }
        if(offsetcached[0] + currentx < scaledx) {
        	int targetx = (int)Math.ceil(scaledx);
        	if(isCacheOcupied && doescachecontain(targetx)) {
        		cached[1] = getcached(targetx);
    			offsetcached[1] = getcachedoffset(targetx);
    			cachedLocation[1] = targetx;
        	}
        	else {
	        	double targetrange[] = base.getrange(targetx);
	        	double target[] = getRandomNumber(targetx, targetrange[1] - targetrange[0]);
	        	cached[1] = (target[0] * (targetrange[1] - targetrange[0]) ) + targetrange[0];
	        	offsetcached[1] = (target[1] * targetrange[2]);
	        	cachedLocation[1] = targetx;
	        	isCacheOcupied = true;
        	}
        	return smooth(scaledx - (currentx), cached[0], cached[1], offsetcached[0], offsetcached[1] + 1);
        }
        else {
        	int targetx = (int)Math.floor(scaledx) - 1;
        	if(isCacheOcupied && doescachecontain(targetx)) {
        		cached[1] = getcached(targetx);
    			offsetcached[1] = getcachedoffset(targetx);
    			cachedLocation[1] = targetx;
        	}
        	else {
        		double targetrange[] = base.getrange(targetx);
        		double target[] = getRandomNumber(targetx, targetrange[1] - targetrange[0]);
        		cached[1] = (target[0] * (targetrange[1] - targetrange[0]) ) + targetrange[0];
				offsetcached[1] = (target[1] * targetrange[2]);
        		cachedLocation[1] = targetx;
        		isCacheOcupied = true;
        	}
        	return smooth(scaledx - (currentx), cached[1], cached[0], offsetcached[1] - 1, offsetcached[0]);
        }
    }
}
