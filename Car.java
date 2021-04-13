/**
 * A class to represent a Car going either North or South, with a time of arrival, and a starting and total wait time
 * @author Connor Henderson (cjhenderson@plymouth.edu)
 */
public class Car implements Comparable<Car> {

    /**
     * Whether this car is headed North
     */
    boolean northBound;

    /**
     * The time this car arrives at the critical section
     */
    int arrivalTime;

    /**
     * The time this car began waiting
     */
    long startingWaitTime;

    /**
     * The total amount of time this car was waiting
     */
    long totalWaitTime;

    /**
     * Class Constructor
     * @param nbound Whether this car is northbound
     * @param arrivalTime What time this car will arrive at the critical area
     */
    public Car(boolean nbound, int arrivalTime){
        this.northBound = nbound;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Inherited from Comparable
     */
    public int compareTo(Car o){
        int compareTime = o.arrivalTime;
        return this.arrivalTime - compareTime;
    }
    
}
