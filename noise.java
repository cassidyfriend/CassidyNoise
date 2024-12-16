package cassidynoise;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

// A Simple Java program 
// to show working of user defined 
// Generic classes 

// We use < > to specify Parameter type 

@SuppressWarnings("serial")
public class Noise extends JPanel {
	static JFrame frame = new JFrame("game");
	static int frameWidth = 1200, frameHeight = 750;
	double scales[] = {0.0001, 0.0005, 0.001, 0.01, 0.03};
	int divider[] = {1, 70, 240, 30, 10};
	static int ix = 0;
	static int currentseed = 0;
	static cassidynoise noise = new cassidynoise(675567, 0.1);
	static void print(Object o) {
		System.out.println(o);
	}
	public void paint(Graphics g) {
		 g.setColor(Color.white);
		 g.fillRect(0, 0, frameWidth, frameHeight);
		 g.setColor(Color.black);
		 int i = 0;
		 for(i = 0; i < frameWidth; i++) {
			 g.fillRect(i, (int)noise.getNoiseAt(i+Keylistener.placement, (double x) -> getminandmax(x)), 1, 1);
		 }
		 frameWidth = frame.getWidth();
		 frameHeight = frame.getHeight();
		 try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 repaint();
	}
	
	public static double[] getminandmax(double x) {
		double toreturn[] = {0, 500};
		return toreturn;
	}

	
	public static void main(String[] args){
		Noise game = new Noise();
	   	frame.add(game);
	   	frame.setSize(frameWidth, frameHeight);
	   	new Keylistener();
	   	frame.setVisible(true);
	   	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   	for(int i = 0; i < 100; i++) {
	   		noise.getRandomNumber(i);
		   	//print(noise.getRandomNumber(i));
	   	}
	}
}

class cassidynoise {
    long seed;
    double scalex;
    static int rangeArray[] = {
			1,
			10,
			100,
			1000,
			10000,
			100000,
			1000000,
			10000000,
			100000000
			};
    
    //public record distance(double min, double max) {}
    
    public interface locationdetails{
    	public double[] getrange(double x);
    }
    
    static void print(Object o) {
		System.out.println(o);
	}

    public cassidynoise(long seed, double scale) {
              this.seed = seed;
			  this.scalex = scale;
    }
	
    public static long middleSquareNumber(long seed, int digits) {
        long square = seed * seed;
        int numDigits = String.valueOf(square).length();

        // Add leading zeros if necessary
        String squareStr = String.format("%0" + (2 * digits) + "d", square);

        // Extract middle digits
        int start = (numDigits - digits) / 2;
        start = start < 0 ? 0 : start;
        int end = start + digits;
        return Integer.parseInt(squareStr.substring(start, end));
    }
	
	double getRandomNumber(int input) {
        final long multiplier = 1664525L;
        final long increment = 1013904223L;
        final long modulus = (1L << 32);
        long seed = Math.abs(middleSquareNumber(this.seed, 1));
        for(int i = 0; i < seed; i++) {
        	input = (int)middleSquareNumber(input, 3);
        	//print(input);
        }
        seed = Math.abs(middleSquareNumber(input, 3));
        long randomValue = (multiplier * seed + increment) % modulus;
        randomValue ^= (randomValue >>> 16);
        randomValue ^= (randomValue << 5);
        randomValue = randomValue & 0x7FFFFFFF;
        return randomValue / (double) 0x7FFFFFFF;
    }

    public double getNoiseAt(double x, locationdetails base) {
              double output = 0;
              x = x*scalex;
              int minx = (int)Math.floor(x), maxx = (int)Math.ceil(x);
              minx = (int)Math.floor(getRandomNumber(minx + (int)seed) * (base.getrange(x)[1] - base.getrange(x)[0]));
              maxx = (int)Math.ceil(getRandomNumber(maxx + (int)seed) * (base.getrange(x)[1] - base.getrange(x)[0]));
              return maxx;
    }
    //t for target, s for start. equation: y=((6x^5-15x^4+10x^3)*(t-s))+s
}



class Keylistener {
	public static int placement = 0;
	public static int speed = 50;
	
	@SuppressWarnings("static-access")
	public Keylistener() {
		Noise.frame.addKeyListener(new KeyAdapter() {
	    	public void keyPressed(KeyEvent e) {
	    		int keyCode = e.getKeyCode();
	    		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
	    			placement -= speed;
	    		}
	    		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
	    			placement += speed;
	    		}
	    	}
	    	public void keyReleased(KeyEvent e) {
	    		int keyCode = e.getKeyCode();
	    		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
	    			placement--;
	    		}
	    		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
	    			placement++;
	    		}
	    	}
	  	});
	}
}
