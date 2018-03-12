import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class EastJunction extends Thread implements IJunction {

    Semaphore pNorthWest;
    Semaphore pSouthWest;
    Semaphore pSouthEast;
    ArrayList<Semaphore> currentlyHeldSemaphores = new ArrayList<>();

    Direction direction;
    ArrayList<Cars> carsList = new ArrayList<>();

    public EastJunction(String name, Semaphore northWest, Semaphore southEast, Semaphore southWest) {
        super(name);
        pNorthWest = northWest;
        pSouthEast = southEast;
        pSouthWest = southWest;
        createCars();
    }

    @Override
    public void decideDirection() {
        int randomNum = ThreadLocalRandom.current().nextInt(1,4);
        switch (randomNum) {
            case 1: direction = Direction.SOUTH;
            break;
            case 2: direction = Direction.WEST;
            break;
            case 3: direction = Direction.NORTH;
            break;
            default: direction = Direction.SOUTH;
            break;
        }
    }

    @Override
    public void getNeededTiles(Direction direction) throws InterruptedException {
        switch (direction) {
            case SOUTH:
                // needs to acquire southEast
                pSouthEast.acquire();
                currentlyHeldSemaphores.add(pSouthEast);
                break;
            case WEST:
                // needs to acquire southEast southWest
                pSouthEast.acquire();
                pSouthWest.acquire();
                currentlyHeldSemaphores.add(pSouthEast);
                currentlyHeldSemaphores.add(pSouthWest);
                break;
            case NORTH:
                pSouthEast.acquire();
                pSouthWest.acquire();
                pNorthWest.acquire();
                currentlyHeldSemaphores.add(pSouthEast);
                currentlyHeldSemaphores.add(pSouthWest);
                currentlyHeldSemaphores.add(pNorthWest);
                break;
        }
    }

    @Override
    public void drive(Cars car) {
        car.setArrived(true);
        car.setWaiting(false);
        car.setInProgress(false);
        System.out.println(getName() + ": SUCCESS:" + car.getName() + " from EAST has successfully got to its desired location");
        carsList.remove(car);
    }

    @Override
    public void createCars() {
        carsList.add(new Cars("Car 1", Direction.EAST));
        carsList.add(new Cars("Car 2", Direction.EAST));
        carsList.add(new Cars("Car 3", Direction.EAST));
        carsList.add(new Cars("Car 4", Direction.EAST));
        carsList.add(new Cars("Car 5", Direction.EAST));
        carsList.add(new Cars("Car 6", Direction.EAST));
        carsList.add(new Cars("Car 7", Direction.EAST));
        carsList.add(new Cars("Car 8", Direction.EAST));
        carsList.add(new Cars("Car 9", Direction.EAST));
        carsList.add(new Cars("Car 10", Direction.EAST));
    }

    public void run() {
        while (!carsList.isEmpty()) {
            for (int i = 0; i < carsList.size(); i++) {
                carsList.get(i).setInProgress(true);
                carsList.get(i).setWaiting(false);
                decideDirection();
                carsList.get(i).setDestination(direction);
                System.out.println(getName() + ": " + carsList.get(i).getName() + " from EAST has chosen to go " + carsList.get(i).getDestination());
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
