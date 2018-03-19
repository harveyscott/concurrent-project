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
    boolean passed = false;

    Semaphore pCarsInIntersection;

    public EastJunction(String name, Semaphore northWest, Semaphore southEast, Semaphore southWest, Semaphore carsInIntersection) {
        super(name);
        pNorthWest = northWest;
        pSouthEast = southEast;
        pSouthWest = southWest;
        pCarsInIntersection = carsInIntersection;
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
        int randomWait = ThreadLocalRandom.current().nextInt(100,2000);
        switch (direction) {
            case SOUTH:
                if (pSouthEast.tryAcquire()) {
                    System.out.println(getName() + ": Just acquired a semaphore pSouthEast");
                    passed = true;
                    currentlyHeldSemaphores.add(pSouthEast);
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                // needs to acquire southEast
                break;
            case WEST:
                if (pSouthEast.tryAcquire()) {
                }

                if (pSouthWest.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pSouthEast);
                    currentlyHeldSemaphores.add(pSouthWest);
                } else {
                    pSouthEast.release();
                }


                if (!passed) {
                    Thread.sleep(randomWait);
                }
                // needs to acquire southEast southWest
                break;
            case NORTH:
                if (pSouthEast.tryAcquire()) {
                }

                if (pSouthWest.tryAcquire()) {
                } else {
                    pSouthEast.release();
                }

                if (pNorthWest.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pSouthEast);
                    currentlyHeldSemaphores.add(pSouthWest);
                    currentlyHeldSemaphores.add(pNorthWest);
                } else {
                    pSouthEast.release();
                    pSouthWest.release();
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

        // Print out car movements
        switch (car.getDestination()) {
            case SOUTH:
                System.out.println("Car from " + getName() + " has entered tile number 8");
                break;
            case WEST:
                System.out.println("Car from " + getName() + " has entered tile number 8");
                System.out.println("Car from " + getName() + " has entered tile number 7");
                break;
            case NORTH:
                System.out.println("Car from " + getName() + " has entered tile number 8");
                System.out.println("Car from " + getName() + " has entered tile number 7");
                System.out.println("Car from " + getName() + " has entered tile number 3");
                break;
        }

        System.out.println(getName() + ": SUCCESS: " + car.getName() + " from EAST has successfully got to its desired location");
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
                passed = false;
                carsList.get(i).setInProgress(true);
                carsList.get(i).setWaiting(false);

                //decides direction
                decideDirection();
                carsList.get(i).setDestination(direction);
                System.out.println(getName() + ": " + carsList.get(i).getName() + " from EAST has chosen to go " + carsList.get(i).getDestination());

                while (!passed) {
                    // car tries to enter intersection
                    if (pCarsInIntersection.tryAcquire()) {
                        System.out.println(getName() + ": has acquired entrance to the intersection. Cars that can enter = " + pCarsInIntersection.availablePermits());

                        // acquires needed semaphores
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

                drive(carsList.get(i));

                // car leaves the intersection
                pCarsInIntersection.release();
                for (Semaphore semaphore: currentlyHeldSemaphores) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
            }
        }
    }
}
