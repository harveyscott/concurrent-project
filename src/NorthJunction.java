import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class NorthJunction extends Thread implements IJunction {

    //Semphores that represent the needed tiles
    Semaphore pNorthEast;
    Semaphore pSouthEast;
    Semaphore pSouthWest;
    ArrayList<Semaphore> currentlyHeldSemaphores = new ArrayList<>();

    //Direction that the car will move;
    Direction direction;
    ArrayList<Cars> carsList = new ArrayList<>();


    // Constructor
    public NorthJunction(String name, Semaphore northEast, Semaphore southEast, Semaphore southWest) {
        super(name);
        pNorthEast = northEast;
        pSouthEast = southEast;
        pSouthWest = southWest;
        createCars();
    }

    // Create a list of the cars waiting cars
    @Override
    public void createCars() {
        carsList.add(new Cars("Car 1", Direction.NORTH));
        carsList.add(new Cars("Car 2", Direction.NORTH));
        carsList.add(new Cars("Car 3", Direction.NORTH));
        carsList.add(new Cars("Car 4", Direction.NORTH));
        carsList.add(new Cars("Car 5", Direction.NORTH));
        carsList.add(new Cars("Car 6", Direction.NORTH));
        carsList.add(new Cars("Car 7", Direction.NORTH));
        carsList.add(new Cars("Car 8", Direction.NORTH));
        carsList.add(new Cars("Car 9", Direction.NORTH));
        carsList.add(new Cars("Car 10", Direction.NORTH));
    }


    // Junction methods
    @Override
    public void decideDirection() {
        // Going in a clockwise direction 1,2,3 - east,south,west
        int randomNum = ThreadLocalRandom.current().nextInt(1,4);
        switch (randomNum) {
            case 1: direction = Direction.EAST;
            break;
            case 2: direction = Direction.SOUTH;
            break;
            case 3: direction = Direction.WEST;
            default: direction = Direction.EAST;
            break;
        }
    }

    @Override
    public void getNeededTiles(Direction direction) throws InterruptedException {
        switch (direction) {
            case EAST:
                // needs to acquire the northEast Semaphore
                pNorthEast.acquire();
                currentlyHeldSemaphores.add(pNorthEast);
                break;
            case SOUTH:
                // needs to acquire northEast + southEast Semaphores
                pNorthEast.acquire();
                pSouthEast.acquire();
                currentlyHeldSemaphores.add(pNorthEast);
                currentlyHeldSemaphores.add(pSouthEast);
                break;
            case WEST:
                // needs to acquire northEast + southEast + southWest Semaphores
                pNorthEast.acquire();
                pSouthEast.acquire();
                pSouthWest.acquire();
                currentlyHeldSemaphores.add(pNorthEast);
                currentlyHeldSemaphores.add(pSouthEast);
                currentlyHeldSemaphores.add(pSouthWest);
                break;
        }
    }

    @Override
    public void drive(Cars car) {
        // Assumes that the semaphores could be acquired
        car.setArrived(true);
        car.setWaiting(false);
        car.setInProgress(false);

        carsList.remove(car);
        // release the needed semaphores
        // Exit critical section
    }

    public void run() {
        while (!carsList.isEmpty()) {
            for (int i = 0; i < carsList.size(); i++) {
                carsList.get(i).setInProgress(true);
                carsList.get(i).setWaiting(false);
                decideDirection();
                carsList.get(i).setDestination(direction);

                // Enter critical section
                try {
                    getNeededTiles(carsList.get(i).getDestination());
                } catch (InterruptedException e) {
                    System.out.println("Car could not get needed semaphores :(");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                // In critical section
                drive(carsList.get(i));
                for (Semaphore semaphore: currentlyHeldSemaphores
                        ) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
                System.out.println("Car has successfully got to its desired location");

            }

        }
    }
}

