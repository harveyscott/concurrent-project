import java.util.concurrent.Semaphore;

public class run {
    public static void main(String[] args) {
        Semaphore northEast = new Semaphore(1);
        Semaphore northWest = new Semaphore(1);
        Semaphore southEast = new Semaphore(1);
        Semaphore southWest = new Semaphore(1);

        NorthJunction northJunction = new NorthJunction("North", northEast, southEast, southWest);

        while (true) {
            northJunction.start();
        }

    }
}
