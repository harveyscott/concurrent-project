import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class EastJunction extends Thread implements IJunction {

    Semaphore pNorthEast;
    Semaphore pNorthWest;
    Semaphore pSouthWest;
    ArrayList<Semaphore> currentlyHeldSemaphores = new ArrayList<>();

    Direction direction;
    ArrayList<Cars> carsList = new ArrayList<>();
    
    @Override
    public void decideDirection() {

    }

    @Override
    public void getNeededTiles(Direction direction) throws InterruptedException {

    }

    @Override
    public void drive(Cars car) {

    }

    @Override
    public void createCars() {

    }
}
