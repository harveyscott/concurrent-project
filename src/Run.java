import java.util.concurrent.Semaphore;

public class Run {
    public static void main(String[] args) {

        // Create Semaphores
        Semaphore northEast = new Semaphore(1, true);
        Semaphore northWest = new Semaphore(1, true);
        Semaphore southEast = new Semaphore(1, true);
        Semaphore southWest = new Semaphore(1, true);

        Semaphore carsInIntersection = new Semaphore(3);

        // create Junctions
        NorthJunction northJunction = new NorthJunction("North", northEast, southEast, southWest, carsInIntersection);
        SouthJunction southJunction = new SouthJunction("South", northEast, northWest, southWest, carsInIntersection);
        EastJunction eastJunction = new EastJunction("East", northWest, southEast, southWest, carsInIntersection);
        WestJunction westJunction = new WestJunction("West", northWest, northEast, southEast, carsInIntersection);

        northJunction.start();
        southJunction.start();
        eastJunction.start();
        westJunction.start();

    }
}
