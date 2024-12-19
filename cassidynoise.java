package cassidynoise;

public class cassidynoise {
    long seed;
    double scalex;
    double cached[] = {0,0};
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
    
    double smooth(double x, double startheight, double targetheight) {
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
	
	double getRandomNumber(int input) {
        long targetseed = seed;
        for(int i = 0; i < Math.abs(middleSquareNumber(input, 3)) % 150; i++) {
			targetseed = hashSeed(targetseed);
        }
        final long multiplier = 1664525L;
        final long increment = 1013904223L;
        final long modulus = (1L << 32);
        long randomValue = (multiplier * targetseed + increment) % modulus;
        randomValue ^= (randomValue >>> 16);
        randomValue ^= (randomValue << 5);
        randomValue = randomValue & 0x7FFFFFFF;
        return randomValue / (double) 0x7FFFFFFF;
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

    public double getNoiseAt(double x, locationdetails base) {
        double scaledx = x*scalex;
        int currentx = (int)Math.floor(scaledx);
        int targetx = (int)Math.ceil(scaledx);
        if(!(currentx == cachedLocation[0] && targetx == cachedLocation[1] && isCacheOcupied)) {	
        	double currentbase[] = base.getrange(currentx);
        	double targetbase[] = base.getrange(targetx);
        	cached[0] = doescachecontain(currentx) ? getcached(currentx) : (getRandomNumber((int)currentx) * (currentbase[1] - currentbase[0])) + currentbase[0];
        	cached[1] = doescachecontain(targetx) ? getcached(targetx) : (getRandomNumber((int)targetx) * (targetbase[1] - targetbase[0]) ) + targetbase[0];
        	cachedLocation[0] = currentx;
			cachedLocation[1] = targetx;
        	isCacheOcupied = true;
        }
        return smooth(scaledx - currentx, cached[0], cached[1]);
    }
}
