package cassidynoise;

import java.util.Random;

public class cassidynoise2D {
	int seed, chunksize;
	
	public interface locationdetails{
    	public double[] getrange(int x, int y); 
    }
	
	record chunkdetails(int x, int y, double height) {};
	
	public static long hashCoords(int x, int y, long userSeed) {
        long h1 = (long) x * 0x9e3779b97f4a7c15L;
        long h2 = (long) y * 0xbf58476d1ce4e5b9L;
        long h3 = userSeed * 0x1a868469b4c09d57L;
        long combined = h1 ^ h2 ^ h3;
        combined *= 0xda60b493b821437bL;
        combined ^= combined >>> 32;
        combined *= 0x76b200b3e660b86bL;
        combined ^= combined >>> 32;
        return combined;
    }
	
	cassidynoise2D (int seed, int chunksize) {
		this.seed = seed;
		this.chunksize = chunksize;
	}
	
	chunkdetails getpoint(int x, int y, int chunkoffsetx, int chunkoffsety, locationdetails localdetails) {
		Random r = new Random(hashCoords(x + chunkoffsetx, y + chunkoffsety, seed));
		chunkdetails details = new chunkdetails(
				r.nextInt(0, (int)(chunksize*localdetails.getrange(x+chunkoffsetx, y+chunkoffsety)[2])) + (chunkoffsetx * chunksize),
				r.nextInt(0, (int)(chunksize*localdetails.getrange(x+chunkoffsetx, y+chunkoffsety)[3])) + (chunkoffsety * chunksize),
				r.nextInt((int)(localdetails.getrange(x+chunkoffsetx, y+chunkoffsety)[0]), (int)(localdetails.getrange(x+chunkoffsetx, y+chunkoffsety)[1])));
		return details;
	}
	
	int getinsideofchunk(int input) {
		return input >= 0 ? input%chunksize : (input%chunksize + chunksize); 
	}
	
	int getoffsetchunk(double x, double point) {
		return x >= point ? 1 : -1;
	}
	
	double smooth(double x, double startheight, double targetheight, double startoffset, double targetoffset) {
    	x = (x - startoffset) / (targetoffset - startoffset);
        double start = 6.0 * Math.pow(x, 5);
        double middle = 15.0 * Math.pow(x, 4);
        double end = 10.0 * Math.pow(x, 3);
        return (start - middle + end) * (targetheight - startheight) + startheight;
    }
	
	int floor(double input) {
		return (int) (input >= 0? input: input-1);
	}
	
	int getheightat(int x, int y, locationdetails details) {
		int chunkx = floor((x+0.0) / chunksize);
		int chunky = floor((y+0.0) / chunksize);
		x = getinsideofchunk(x);
		y = getinsideofchunk(y);
		chunkdetails first = getpoint(chunkx, chunky, 0, 0, details);
		chunkdetails second = getpoint(chunkx, chunky, getoffsetchunk(x, first.x), 0, details);
		double yoffset = smooth(x, first.y, second.y, first.x, second.x);
		chunkdetails third = getpoint(chunkx, chunky, 0, getoffsetchunk(y, yoffset), details);
		chunkdetails last = getpoint(chunkx, chunky, getoffsetchunk(x, third.x), getoffsetchunk(y, yoffset), details);
		double basehight = smooth(x, first.height, second.height, first.x, second.x);
		double nextyoffset = smooth(x, third.y, last.y, third.x, last.x);
		double nextheight = smooth(x, third.height, last.height, third.x, last.x);
		int height = (int)smooth(y, basehight, nextheight, yoffset, nextyoffset);
		return height;
	}
}