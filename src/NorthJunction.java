import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class NorthJunction extends Thread implements IJunction {

    //Semaphores that represent the needed tiles
    Semaphore pNorthEast;
    Semaphore pSouthEast;
    Semaphore pSouthWest;
    ArrayList<Semaphore> currentlyHeldSemaphores = new ArrayList<>();

    //Direction that the car will move;
    Direction direction;
    ArrayList<Cars> carsList = new ArrayList<>();
    boolean passed = false;

    Semaphore pCarsInIntersection;


    // Constructor
    public NorthJunction(String name, Semaphore northEast, Semaphore southEast, Semaphore southWest, Semaphore carsInIntersection) {
        super(name);
        pNorthEast = northEast;
        pSouthEast = southEast;
        pSouthWest = southWest;
        pCarsInIntersection = carsInIntersection;
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
            break;
            default: direction = Direction.EAST;
            break;
        }
    }

    @Override
    public void getNeededTiles(Direction direction) throws InterruptedException {
        int randomWait = ThreadLocalRandom.current().nextInt(100,2000);
        switch (direction) {
            case EAST:
                // needs to acquire the northEast Semaphore
                if (pNorthEast.tryAcquire()) {
                    currentlyHeldSemaphores.add(pNorthEast);
                    passed = true;
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                break;
            case SOUTH:
                // needs to acquire northEast + southEast Semaphores
                if (pNorthEast.tryAcquire()) {
                }

                if (pSouthEast.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthEast);
                    currentlyHeldSemaphores.add(pSouthEast);
                } else {
                    pSouthEast.release();
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                break;
            case WEST:
                // needs to acquire northEast + southEast + southWest Semaphores
                if (pNorthEast.tryAcquire()) {
                }

                if (pSouthEast.tryAcquire()) {

                } else {
                    pNorthEast.release();
                }

                if (pSouthWest.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthEast);
                    currentlyHeldSemaphores.add(pSouthEast);
                    currentlyHeldSemaphores.add(pSouthWest);
                } else {
                    pNorthEast.release();
                    pSouthEast.release();
                    //Thread.sleep(randomWait);
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }
                break;
        }
    }

    @Override
    public void drive(Cars car) {
        // Assumes that the semaphores could be acquired
        car.setArrived(true);
        car.setWaiting(false);
        car.setInProgress(false);

        switch (car.getDestination()) {
            case EAST:
                System.out.println("Car from " + getName() + " has entered tile number 4");
                break;
            case SOUTH:
                System.out.println("Car from " + getName() + " has entered tile number 4");
                System.out.println("Car from " + getName() + " has entered tile number 8");
                break;
            case WEST:
                System.out.println("Car from " + getName() + " has entered tile number 4");
                System.out.println("Car from " + getName() + " has entered tile number 8");
                System.out.println("Car from " + getName() + " has entered tile number 7");
                break;
        }

        System.out.println(getName() + ": SUCCESS: " + car.getName() + " from NORTH has successfully got to its desired location");

        carsList.remove(car);
        // release the needed semaphores
        // Exit critical section
    }

    public void run() {
        while (!carsList.isEmpty()) {
            for (int i = 0; i < carsList.size(); i++) {
                Cars aCarsList = carsList.get(i);
                passed = false;
                aCarsList.setInProgress(true);
                aCarsList.setWaiting(false);


                decideDirection();
                aCarsList.setDestination(direction);
                System.out.println(getName() + ": " + aCarsList.getName() + " from NORTH has chosen to go " + aCarsList.getDestination());


                while (!passed) {
                    if (pCarsInIntersection.tryAcquire()) {
                        // Enter critical section
                        try {
                            getNeededTiles(aCarsList.getDestination());
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
                drive(aCarsList);


                // Leave critical section
                pCarsInIntersection.release();
                for (Semaphore semaphore : currentlyHeldSemaphores
                        ) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
            }

        }
    }
}

