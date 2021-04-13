/**
 * A main driver class for the car simulation.
 * To run this code all one should have to do is:
 * 1. Compile all files using JavaSE-15 (javac *.java)
 * 2. Run Main (java Main)
 * 3. Wait an amount of time for the simulation to complete depending on the scale (E.g. 15 minutes for .01...)
 * 
 * If one would like to change any values in the simulation, they are documented
 * in the Lane class. the Road and Car classes are simple object created to make
 * the code more cleanly.
 * @author Connor Henderson (cjhenderson@plymouth.edu)
 */
public class Main {
    
    public static void main(String[] args) {
        
        Lane twoMinutes = new Lane(120000);
        Lane sixMinutes = new Lane(360000);
        Lane eightMinutes = new Lane(480000);
        Lane tenMinutes = new Lane(600000);
        Lane twelveMinutes = new Lane(720000);

        new Thread(){
            public void run() {
                System.out.println("lane0: " + twoMinutes.startSim());
            }
        }.start();

        new Thread(){
            public void run() {
                System.out.println("lane1: " + sixMinutes.startSim());
            }
        }.start();

        new Thread(){
            public void run() {
                System.out.println("lane2: " + eightMinutes.startSim());
            }
        }.start();

        new Thread(){
            public void run() {
                System.out.println("lane3: " + tenMinutes.startSim());
            }
        }.start();

        new Thread(){
            public void run() {
                System.out.println("lane4: " + twelveMinutes.startSim());
            }
        }.start();
    
    }
}
