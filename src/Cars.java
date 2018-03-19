public class Cars {
    private String name;

    // Maybe these are not needed we will see
    private Direction startingPoint;
    private Direction destination;

    public Cars(String carName, Direction carDirection) {
        name = carName;
        startingPoint = carDirection;
    }

    public Direction getDestination() {
        return destination;
    }

    public String getName() {
        return name;
    }

    public void setDestination(Direction destination) {
        this.destination = destination;
    }
}
