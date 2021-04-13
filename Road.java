import java.util.LinkedList;

/**
 * This class is to represent the closed down portion of a road
 * @author Connor Henderson (cjhenderson@plymouth.edu)
 */
public class Road {

    /**
     * A LinkedList to represent cars currently in the critical section
     */
    LinkedList<Car> road = new LinkedList<Car>();

    /**
     * This method is to represent a car moving into the critical section, the closed down "lane"
     * @param queue The queue to take the car from (North or South)
     */
    public void arrive(LinkedList<Car> queue){
        // Car car = queue.removeFirst();
        // car.totalWaitTime = System.currentTimeMillis() - car.startingWaitTime;
        // road.add(car);
        if(queue.size() > 0){
            road.add(queue.removeFirst());
        }
    }
    /**
     * A method to represent a car leaving the critical section
     * @return The car which left the lane
     */
    public Car depart(){
        Car car = road.removeFirst();
        car.totalWaitTime = System.currentTimeMillis() - car.startingWaitTime;
        return car;
    }
}