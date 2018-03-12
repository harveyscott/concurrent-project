import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class SouthJunction extends Thread implements IJunction {

    Semaphore pNorthEast;
    Semaphore pNorthWest;
    Semaphore pSouthWest;
    ArrayList<Semaphore> currentlyHeldSemaphores = new ArrayList<>();

    Direction direction;
    ArrayList<Cars> carsList = new ArrayList<>();

    public SouthJunction(String name, Semaphore northEast, Semaphore northWest, Semaphore southWest) {
        super(name);
        pNorthEast = northEast;
        pNorthWest = northWest;
        pSouthWest = southWest;
        createCars();
    }

    @Override
    public void decideDirection() {
        int randomNum = ThreadLocalRandom.current().nextInt(1,4);
        switch (randomNum) {
            case 1: direction = Direction.WEST;
            break;
            case 2: direction = Direction.NORTH;
            break;
            case 3: direction = Direction.EAST;
            break;
            default: direction = Direction.WEST;
        }
    }

    @Override
    public void getNeededTiles(Direction direction) throws InterruptedException {
        switch (direction) {
            case WEST:
                // needs to acquire southEast Semaphore
                pSouthWest.acquire();
                currentlyHeldSemaphores.add(pSouthWest);
                break;
            case NORTH:
                // needs to acquire southEast and northEast
                pSouthWest.acquire();
                pNorthEast.acquire();
                currentlyHeldSemaphores.add(pSouthWest);
                currentlyHeldSemaphores.add(pNorthEast);
                break;
            case EAST:
                // needs to acquire southEast and northEast and northWest
                pSouthWest.acquire();
                pNorthEast.acquire();
                pNorthWest.acquire();
                currentlyHeldSemaphores.add(pSouthWest);
                currentlyHeldSemaphores.add(pNorthEast);
                currentlyHeldSemaphores.add(pNorthWest);
                break;
        }
    }

    @Override
    public void drive(Cars car) {
        car.setArrived(true);
        car.setWaiting(false);
        car.setInProgress(false);
        System.out.println(getName() + ": SUCCESS:" + car.getName() + " from SOUTH has successfully got to its desired location");
        carsList.remove(car);
    }

    @Override
    public void createCars() {
        carsList.add(new Cars("Car 1", Direction.SOUTH));
        carsList.add(new Cars("Car 2", Direction.SOUTH));
        carsList.add(new Cars("Car 3", Direction.SOUTH));
        carsList.add(new Cars("Car 4", Direction.SOUTH));
        carsList.add(new Cars("Car 5", Direction.SOUTH));
        carsList.add(new Cars("Car 6", Direction.SOUTH));
        carsList.add(new Cars("Car 7", Direction.SOUTH));
        carsList.add(new Cars("Car 8", Direction.SOUTH));
        carsList.add(new Cars("Car 9", Direction.SOUTH));
        carsList.add(new Cars("Car 10", Direction.SOUTH));
    }

    public void run() {
        while (!carsList.isEmpty()) {
            for (int i = 0; i < carsList.size(); i++) {
                carsList.get(i).setInProgress(true);
                carsList.get(i).setWaiting(false);
                decideDirection();
                carsList.get(i).setDestination(direction);
                System.out.println(getName() + ": " + carsList.get(i).getName() + " from SOUTH has chosen to go " + carsList.get(i).getDestination());
                try {
                    getNeededTiles(carsList.get(i).getDestination());
                } catch (InterruptedException e) {
                    System.out.println("Car could not get needed semaphores :(");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                drive(carsList.get(i));
                for (Semaphore semaphore: currentlyHeldSemaphores
                        ) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
            }
        }
    }
}