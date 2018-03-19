import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class WestJunction extends Thread implements IJunction {

    Semaphore pNorthWest;
    Semaphore pNorthEast;
    Semaphore pSouthEast;
    ArrayList<Semaphore> currentlyHeldSemaphores = new ArrayList<>();

    Direction direction;
    ArrayList<Cars> carsList = new ArrayList<>();
    boolean passed = false;

    public WestJunction(String name, Semaphore northWest, Semaphore northEast, Semaphore southEast) {
        super(name);
        pNorthEast = northEast;
        pNorthWest = northWest;
        pSouthEast = southEast;
        createCars();
    }


    @Override
    public void decideDirection() {
        int randomNum = ThreadLocalRandom.current().nextInt(1,4);
        switch (randomNum) {
            case 1: direction = Direction.NORTH;
            break;
            case 2: direction = Direction.EAST;
            break;
            case 3: direction = Direction.SOUTH;
            break;
            default: direction = Direction.NORTH;
        }
    }

    @Override
    public void getNeededTiles(Direction direction) throws InterruptedException {
        int randomWait = ThreadLocalRandom.current().nextInt(100, 2000);
        switch (direction) {
            case NORTH:
                if (pNorthWest.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pNorthWest");
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthWest);
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }

                break;

            case EAST:
                if (pNorthWest.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pNorthWest");
                }

                if (pNorthEast.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pNorthEast");
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthWest);
                    currentlyHeldSemaphores.add(pNorthEast);
                } else {
                    pNorthWest.release();
                    System.out.println(getName() + ": Just released semaphore pNorthEast");
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }

                break;

            case SOUTH:
                if (pNorthWest.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pNorthWest");
                }

                if (pNorthEast.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pNorthEast");
                } else {
                    pNorthWest.release();
                    System.out.println(getName() + ": Just released semaphore pNorthWest");
                }

                if (pSouthEast.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pSouthEast");
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthWest);
                    currentlyHeldSemaphores.add(pNorthEast);
                    currentlyHeldSemaphores.add(pSouthEast);
                } else {
                    pNorthWest.release();
                    System.out.println(getName() + ": Just released semaphore pNorthWest");
                    pNorthEast.release();
                    System.out.println(getName() + ": Just released semaphore pNorthEast");
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                break;
        }
    }

    @Override
    public void drive(Cars car) {
        car.setArrived(true);
        car.setWaiting(false);
        car.setInProgress(false);
        System.out.println(getName() + ": SUCCESS:" + car.getName() + " from WEST has successfully got to its desired location");
        carsList.remove(car);
    }

    @Override
    public void createCars() {
        carsList.add(new Cars("Car 1", Direction.WEST));
        carsList.add(new Cars("Car 2", Direction.WEST));
        carsList.add(new Cars("Car 3", Direction.WEST));
        carsList.add(new Cars("Car 4", Direction.WEST));
        carsList.add(new Cars("Car 5", Direction.WEST));
        carsList.add(new Cars("Car 6", Direction.WEST));
        carsList.add(new Cars("Car 7", Direction.WEST));
        carsList.add(new Cars("Car 8", Direction.WEST));
        carsList.add(new Cars("Car 9", Direction.WEST));
        carsList.add(new Cars("Car 10", Direction.WEST));
    }

    public void run() {
        while (!carsList.isEmpty()) {
            for (int i = 0; i < carsList.size(); i++) {
                passed = false;
                carsList.get(i).setInProgress(true);
                carsList.get(i).setWaiting(false);

                while (!passed) {
                    decideDirection();
                    carsList.get(i).setDestination(direction);
                    System.out.println(getName() + ": " + carsList.get(i).getName() + " from WEST has chosen to go " + carsList.get(i).getDestination());
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
                }

                drive(carsList.get(i));
                for (Semaphore semaphore : currentlyHeldSemaphores) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
            }
        }
    }
}
