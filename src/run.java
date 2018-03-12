import java.util.concurrent.Semaphore;

public class run {
    public static void main(String[] args) {

        // Create Semaphores
        Semaphore northEast = new Semaphore(1);
        Semaphore northWest = new Semaphore(1);
        Semaphore southEast = new Semaphore(1);
        Semaphore southWest = new Semaphore(1);

        // create Junctions
        NorthJunction northJunction = new NorthJunction("North", northEast, southEast, southWest);
        SouthJunction southJunction = new SouthJunction("South", northEast, northWest, southWest);

        northJunction.start();
        southJunction.start();

    }
}
