import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * Lane is the driving class behind my simulation. This controls all of the settings
 * for the simulation, and also has objects inside of it to simulate time, distance, and so forth.
 * @author Connor Henderson (cjhenderson@plymouth.edu)
 */
public class Lane {
    /**
     * This is the scale for the simulation, if you want to run it faster or slower than real time.
     * Some values: 1 for 24 hours, .1 for 10% of that, .01 for 1%... .01 usually takes ~15
     * minutes real time to complete.
     * The higher this number is, the longer the simulation. I highly recommend not going over 1
     */
    float SIMSCALE = .01f;
    
    /**
     * This is the number of cars which will pass through our "Critical Area" per day. I chose 15000
     * because that is the number my town reports passes through the main road every day.
     * More cars leads to longer wait times, naturally.
     */
    final long NUMCARS = 15000;

    /**
     * The length of the buffer for the critical section, this determines how long cars can pass
     * through from one side before it switches to let other cars come through.
     * This is in MS, so 120000 = 2 minutes, 360000 = 4 minutes...
     */
    long BUFLENGTH = Math.round(120000 * SIMSCALE); // in MS
   

    /**
     * This is the travel time of the car through the Critical Area. This was calculated using the 
     * values:
     * Car Speed: 25 MPH / 36.66 FPS
     * Critical Area Lenth: 180 ft
     * Car Length: 15 ft
     * Vehicle Gap: 3 ft
     * Time spent in critical area: 180 ft / 36.66 fps = 4.91 (Rounded to 5) OR 5000 MS
     * Time spent in crticial area with vehicle length and gap: (180ft + 15ft + 3ft) / 36.66 fps = 5.40 OR 5400 MS
     * Changing this will lead to more or less cars being able to go through, almost like changing their speed.
     * At 5 seconds, ~23 cars can make it through in a two minutes period.
     */
    final long TRAVELTIME = Math.round(5000 * SIMSCALE); // Travel time through critical area in MS
    
    /**
     * This is how "long" we want the simulation to run for. This is the bound for the random 
     * generation of the arrival time of our cars.
     * Changing this will make the simulation take less/more time.
     */
    final int SIMTIMEFRAME = 1450; // Time Frame in minutes (1450 is 24 hours)

    /**
     * This is our semaphore for controlling access to the critical area
     * I used a binary semaphore, with fairness turned on, because only one
     * process is allowed to access the shared resource (Road) at a time.
     * I had to turn fairness on because it would hand out permits to one
     * lane until it was empty, and then let the other lane go.
     */
    Semaphore binary = new Semaphore(1, true);

    /**
     * An ArrayList to hold the Cars that we are sending through the road.
     */
    ArrayList<Car> cars = new ArrayList<Car>();

    /**
     * An ArrayList to hold the cars that have passed through the critical section.
     */
    ArrayList<Car> passedCars = new ArrayList<Car>();

    /**
     * This is a LinkedList which holds all cars who are currently waiting to go North
     */
    LinkedList<Car> northBoundQueue = new LinkedList<Car>();

    /**
     * This is a LinkedList which holds all cars waiting to go South
     */
    LinkedList<Car> southBoundQueue = new LinkedList<Car>();

    /**
     * For use in random generation
     */
    Random rand = new Random();

    /**
     * The Shared Resource of our Critical Area(s)
     */
    Road road = new Road();

    /**
     * Flag variable for exiting while loops
     */
    boolean exit = false;

    /**
     * Boolean to keep track of the southbound queue, set to true if it is empty.
     * Flag variable to get out of While loops
     */
    boolean southEmpty = false;

    /**
     * Boolean to keep track of the Northbound queue, set to true if it is empty.
     * Flag variable to get out of while loops.
     */
    boolean northEmpty = false;

    /**
     * Class Constructor
     * @param buflength The length (in MS) of the buffer for this lane.
     */
    public Lane(long buflength){
        this.BUFLENGTH = Math.round(buflength * SIMSCALE);
    }
    
    /**
     * Class Constructor
     * @param buflength The length (in MS) of the buffer for this lane.
     * @param simscale The scale of the simulation (...1.0, .1, .01...)
     */
    public Lane(long buflength, float simscale){
        this.SIMSCALE = simscale;
        this.BUFLENGTH = Math.round(buflength * SIMSCALE);
    }

    /**
     * This is a thread to handle the northbound queue
     */
    Thread northThread = new Thread(){

        public void run(){

            while(exit == false && northEmpty == false){

                if(northBoundQueue.size() == 0 && cars.size() == 0){
                    northEmpty = true;
                }

                try {
                    binary.acquire();
                    long startTime = System.currentTimeMillis();

                    while(System.currentTimeMillis() < (startTime + BUFLENGTH)){

                        if (northBoundQueue.size() > 0){
                            road.arrive(northBoundQueue);
                            sleep(TRAVELTIME);
                            passedCars.add(road.depart());
                        }

                    }

                } catch (Exception exc) {

                    exc.printStackTrace();

                }
                binary.release();
            }
        }
    };

    /**
     * This is a thread to handle the southbound cars
     */
    Thread southThread = new Thread(){

        public void run(){

            while(exit == false && southEmpty == false){

                if(southBoundQueue.size() == 0 && cars.size() == 0){
                    southEmpty = true;
                }

                try {
                    binary.acquire();
                    long startTime = System.currentTimeMillis();

                    while(System.currentTimeMillis() < (startTime + BUFLENGTH)){

                        if(southBoundQueue.size() > 0){
                            road.arrive(southBoundQueue);
                            sleep(TRAVELTIME);
                            passedCars.add(road.depart());
                        }

                    }
                } catch (Exception exc) {

                    exc.printStackTrace();

                }
                binary.release();
            }
        }
    };
    
    /**
     * Populates the cars ArrayList, starts all of the cars waiting times, and starts both simulation threads.
     * @return A double with the average wait time of the simulation
     */
    public double startSim(){

        // Generate NUMCARS random cars
        for (int i = 0; i < NUMCARS; i++){
            cars.add(new Car((rand.nextFloat() < .5), Math.round((rand.nextInt(SIMTIMEFRAME) * 60000) * SIMSCALE)));
        }
        
        // Get the time the sim started and sort
        long startTime = System.currentTimeMillis();
        Collections.sort(cars);

        // This thread adds all of the cars when their arrival time comes
        Thread addThread = new Thread(){
            public void run() {
                while(cars.size() > 0){
                    if((cars.get(0).arrivalTime + startTime) <= System.currentTimeMillis()){
                        if(cars.get(0).northBound){
                            cars.get(0).startingWaitTime = System.currentTimeMillis();
                            northBoundQueue.add(cars.remove(0));
                        } else if (!cars.get(0).northBound){
                            cars.get(0).startingWaitTime = System.currentTimeMillis();
                            southBoundQueue.add(cars.remove(0));
                        }
                    }
                }
            }
        };

        northThread.start();
        southThread.start();
        addThread.start();

        try{
            northThread.join();
            southThread.join();
            addThread.join();
        } catch (Exception exc){
            exc.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        double avgWait = 0;

        for (Car c : passedCars) {
            avgWait += c.totalWaitTime;
        }

        avgWait = avgWait / NUMCARS;

        // divide average wait time by simulation scale to get real wait time in MS
        return (avgWait / SIMSCALE);
    }

}


