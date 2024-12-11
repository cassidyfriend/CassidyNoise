import java.util.Random;

public final class ImprovedNoise {

          static void print(Object o) {
                    System.out.println(o);
          }

          public class noise {
                    long seed;
          
                    public noise(long seed) {
                              this.seed = seed;
                    }

                    double getpointnoise(int x){
                              Random random = new Random((int)Math.round(Math.abs(x > 0? (x*0.6)*seed : x*seed)));
                              return random.nextDouble();
                    }

                    public double getNoiseAt(double x, double min, double max) {
                              double output = 0;
                              int minx = (int)Math.floor(x), maxx = (int)Math.ceil(x);
                              minx = (int)Math.round(getpointnoise(minx) * (max - min));
                              maxx = (int)Math.round(getpointnoise(maxx) * (max - min));
                              return output;
                    }
                    //t for target, s for start. equation: y=((6x^5-15x^4+10x^3)*(t-s))+s
          }

          static noise currentnoise;

          public static void main(String args[]) {
                    ImprovedNoise noiseGenerator = new ImprovedNoise();
                    //double input[] = {5.3, 6.7};
                    noiseGenerator.currentnoise = noiseGenerator.new noise(42l);
                    print(noiseGenerator.currentnoise.getNoiseAt(-5, 0, 10));
          }
}
