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
	static cassidynoise noise = new cassidynoise(54646, 0.005);
	static void print(Object o) {
		System.out.println(o);
	}
	public void paint(Graphics g) {
		 g.setColor(Color.white);
		 g.fillRect(0, 0, frameWidth, frameHeight);
		 g.setColor(Color.black);
		 int i = 0;
		 for(i = 0; i < frameWidth; i++) {
			 int current = (int)Math.round(noise.getNoiseAt((i+Keylistener.placement), (double x) -> getminandmax(x)));
			 //print(current);
			 g.fillRect(i, current, 3, 3);
		 }
		 frameWidth = frame.getWidth();
		 frameHeight = frame.getHeight();
		 try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
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
	   	//noise.getRandomNumber(31);
	   	//print(noise.createcurve(0.25));
	   	for(int i = 0; i < 101; i++) {
	   		//noise.getRandomNumber(i);
		   	
	   		//print(" ");
	   	}
	}
}

class cassidynoise {
    long seed;
    double scalex;
    
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
	
    long hashSeed(long x) {
        x = (x ^ (x >>> 21)) * 0x45d9f3b;
        x = (x ^ (x >>> 15)) * 0x3335b369;
        return Math.abs(x);
    }
    
    double roundtonearest(double base, double amount) {
    	int start = (int)Math.round(base / amount);
		return start * amount;
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
        final long multiplier = 1664525L;
        final long increment = 1013904223L;
        final long modulus = (1L << 32);
        long foramount = Math.abs(middleSquareNumber(input, 3));
        long targetseed = seed;
        for(int i = 0; i < foramount % 150; i++) {
			targetseed = hashSeed(targetseed);
        }
        long randomValue = (multiplier * targetseed + increment) % modulus;
        randomValue ^= (randomValue >>> 16);
        randomValue ^= (randomValue << 5);
        randomValue = randomValue & 0x7FFFFFFF;
        return randomValue / (double) 0x7FFFFFFF;
    }

    public double getNoiseAt(double x, locationdetails base) {
              double output = 0;
              double scaledx = x*scalex;
              int currentx = (int)Math.floor(scaledx);
              int targetx = (int)Math.ceil(scaledx);
              int currentxheight = (int)Math.floor((getRandomNumber((int)currentx) * (base.getrange(currentx)[1] - base.getrange(currentx)[0]) ) + base.getrange(currentx)[0]);
			  int targetxheight = (int)Math.floor((getRandomNumber((int)targetx) * (base.getrange(targetx)[1] - base.getrange(targetx)[0]) ) + base.getrange(targetx)[0]);
			  
			  
			  
			  
			  //print(targetxheight + " " + currentxheight);
			  output = smooth(scaledx - currentx, currentxheight, targetxheight);

              return output;
    }
    //t for target, s for start. equation: y=((6x^5-15x^4+10x^3)*(t-s))+s
    //output = (Math.pow(6 * x, 5) - Math.pow(15 * x, 4) + Math.pow(10 * x, 3) * (targetx - currentx)) + currentx;
}



class Keylistener {
	public static int placement = 0;
	public static int speed = 50;
	
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
