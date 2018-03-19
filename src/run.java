import java.util.concurrent.Semaphore;

public class run {
    public static void main(String[] args) {

        // Create Semaphores
        Semaphore northEast = new Semaphore(1, true);
        Semaphore northWest = new Semaphore(1, true);
        Semaphore southEast = new Semaphore(1, true);
        Semaphore southWest = new Semaphore(1, true);

        // create Junctions
        NorthJunction northJunction = new NorthJunction("North", northEast, southEast, southWest);
        SouthJunction southJunction = new SouthJunction("South", northEast, northWest, southWest);
        EastJunction eastJunction = new EastJunction("East", northWest, southEast, southWest);
        WestJunction westJunction = new WestJunction("West", northWest, northEast, southEast);

        northJunction.start();
        southJunction.start();
        eastJunction.start();
        westJunction.start();

    }
}
