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

    Semaphore pCarsInIntersection;

    public WestJunction(String name, Semaphore northWest, Semaphore northEast, Semaphore southEast, Semaphore carsInIntersection) {
        super(name);
        pNorthEast = northEast;
        pNorthWest = northWest;
        pSouthEast = southEast;
        pCarsInIntersection = carsInIntersection;
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
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthWest);
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }

                break;

            case EAST:
                if (pNorthWest.tryAcquire()) {
                }

                if (pNorthEast.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthWest);
                    currentlyHeldSemaphores.add(pNorthEast);
                } else {
                    pNorthWest.release();
                }

                if (!passed) {
                    Thread.sleep(randomWait);
                }

                break;

            case SOUTH:
                if (pNorthWest.tryAcquire()) {
                }

                if (pNorthEast.tryAcquire()) {
                } else {
                    pNorthWest.release();
                }

                if (pSouthEast.tryAcquire()) {
                    passed = true;
                    currentlyHeldSemaphores.add(pNorthWest);
                    currentlyHeldSemaphores.add(pNorthEast);
                    currentlyHeldSemaphores.add(pSouthEast);
                } else {
                    pNorthWest.release();
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
            case NORTH:
                System.out.println("Car from " + getName() + " has entered tile number 3");
                break;
            case EAST:
                System.out.println("Car from " + getName() + " has entered tile number 3");
                System.out.println("Car from " + getName() + " has entered tile number 4");
                break;
            case SOUTH:
                System.out.println("Car from " + getName() + " has entered tile number 3");
                System.out.println("Car from " + getName() + " has entered tile number 4");
                System.out.println("Car from " + getName() + " has entered tile number 8");
                break;
        }


        System.out.println(getName() + ": SUCCESS: " + car.getName() + " from WEST has successfully got to its desired location");
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

                decideDirection();
                carsList.get(i).setDestination(direction);
                System.out.println(getName() + ": " + carsList.get(i).getName() + " from WEST has chosen to go " + carsList.get(i).getDestination());

                while (!passed) {
                    if (pCarsInIntersection.tryAcquire()) {
                        System.out.println(getName() + ": has acquired entrance to the intersection. Cars that can enter = " + pCarsInIntersection.availablePermits());


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

                pCarsInIntersection.release();
                for (Semaphore semaphore : currentlyHeldSemaphores) {
                    semaphore.release();
                }
                currentlyHeldSemaphores.clear();
            }
        }
    }
}
