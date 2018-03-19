public interface IJunction {
    public void decideDirection();
    public void getNeededTiles(Direction direction) throws InterruptedException;
    public void drive(Cars car);
    public void createCars();
}
