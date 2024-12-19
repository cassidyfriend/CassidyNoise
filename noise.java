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
	static cassidynoise noise = new cassidynoise(45327, 0.01);
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
		if(x % 23 == 0) {
			toreturn[0] = 500;
			toreturn[1] = 700;
		}
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
	   	//noise.getNoiseAt((-5), (double x) -> getminandmax(x));
	   	//print((int)Math.round(noise.getNoiseAt((-5), (double x) -> getminandmax(x))));
	   	for(int i = 0; i < 101; i++) {
	   		//noise.getRandomNumber(i);
		   	
	   		//print(" ");
	   	}
	}
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
