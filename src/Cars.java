public class Cars {
    private boolean waiting = true;
    private boolean inProgress;
    private boolean arrived;
    private String name;

    // Maybe these are not needed we will see
    private Direction startingPoint;
    private Direction destination;

    public Cars(String carName, Direction carDirection) {
        name = carName;
        startingPoint = carDirection;
    }

    // Get Status

    public boolean isArrived() {
        return arrived;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public Direction getDestination() {
        return destination;
    }

    public String getName() {
        return name;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public void setDestination(Direction destination) {
        this.destination = destination;
    }
}
