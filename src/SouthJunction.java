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
    boolean passed = false;

    Semaphore pCarsInIntersection;

    public SouthJunction(String name, Semaphore northEast, Semaphore northWest, Semaphore southWest, Semaphore carsInIntersection) {
        super(name);
        pNorthEast = northEast;
        pNorthWest = northWest;
        pSouthWest = southWest;
        pCarsInIntersection = carsInIntersection;
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
        int randomWait = ThreadLocalRandom.current().nextInt(100,2000);
        switch (direction) {
            case WEST:
                // needs to acquire southEast Semaphore
                if (pSouthWest.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pSouthWest);
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }

                break;
            case NORTH:
                // needs to acquire southEast and northEast
                if (pSouthWest.tryAcquire()) {
                }
                if (pNorthEast.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pSouthWest);
                    currentlyHeldSemaphores.add(pNorthEast);
                } else {
                    pSouthWest.release();
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                break;
            case EAST:
                // needs to acquire southEast and northEast and northWest
                    if (pSouthWest.tryAcquire()) {
                    }

                    if (pNorthEast.tryAcquire()) {
                    } else {
                        pSouthWest.release();
                    }

                    if (pNorthWest.tryAcquire()) {
                        passed = true;
                        currentlyHeldSemaphores.add(pSouthWest);
                        currentlyHeldSemaphores.add(pNorthEast);
                        currentlyHeldSemaphores.add(pNorthWest);
                    } else {
                        pSouthWest.release();
                        pNorthEast.release();
                    }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                break;
        }
    }

    @Override
    public void drive(Cars car) {

        switch (car.getDestination()) {
            case WEST:
                System.out.println("Car from " + getName() + " has entered tile number 7");
                break;
            case NORTH:
                System.out.println("Car from " + getName() + " has entered tile number 7");
                System.out.println("Car from " + getName() + " has entered tile number 3");
                break;
            case EAST:
                System.out.println("Car from " + getName() + " has entered tile number 7");
                System.out.println("Car from " + getName() + " has entered tile number 3");
                System.out.println("Car from " + getName() + " has entered tile number 4");
                break;
        }

        System.out.println(getName() + ": SUCCESS: " + car.getName() + " from SOUTH has successfully got to its desired location");
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
                passed = false;

                decideDirection();
                carsList.get(i).setDestination(direction);
                System.out.println(getName() + ": " + carsList.get(i).getName() + " from SOUTH has chosen to go " + carsList.get(i).getDestination());

                while (!passed) {
                    if (pCarsInIntersection.tryAcquire()) {
                        try {
                            getNeededTiles(carsList.get(i).getDestination());
                        } catch (InterruptedException e) {
                            System.out.println("Car could not get needed semaphores");
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                // In critical section
                drive(carsList.get(i));


                // Leaves critical section
                pCarsInIntersection.release();
                for (Semaphore semaphore: currentlyHeldSemaphores
                        ) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
            }
        }
    }
}
