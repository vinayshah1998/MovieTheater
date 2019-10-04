/* MULTITHREADING <MyClass.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * Vinay Shah
 * vss452
 * 16205
 * Slip days used: <0>
 * Spring 2019
 */
package assignment6;

import java.util.*;
import java.lang.Thread;

public class BookingClient {
    public Map<String, Integer> office;
    public Theater theater;


    /**
     * @param office  maps box office id to number of customers in line
     * @param theater the theater where the show is playing
     */
    public BookingClient(Map<String, Integer> office, Theater theater) {
        this.office = office;
        this.theater = theater;
    }

    /**
     * Starts the box office simulation by creating (and starting) threads
     * for each box office to sell tickets for the given theater
     *
     * @return list of threads used in the simulation,
     * should have as many threads as there are box offices
     */
    public List<Thread> simulate() {
        List<Thread> threads = new LinkedList<Thread>();
        Iterator<Map.Entry<String, Integer>> it = office.entrySet().iterator();

        //iterate through the map entries and create threads with their respective processes
        while (it.hasNext()){
            Map.Entry<String, Integer> entry = it.next();
            threads.add(new Thread(new BookingClientTask(entry.getKey(), entry.getValue(), this.theater)));
        }
        //start all the newly created threads
        for (Thread t : threads){
            t.start();
        }
        return threads;
    }

    public static void main(String[] args) {
        // TODO: Initialize test data to description

        Map<String, Integer> offices = new HashMap<String, Integer>(){{
            put("BX1", 3);
            put("BX3", 3);
            put("BX2", 4);
            put("BX5", 3);
            put("BX4", 3);
        }};
        Theater theater = new Theater(3, 5, "Ouija");
        BookingClient bc = new BookingClient(offices, theater);
        List<Thread> threads = bc.simulate();
        for(Thread t : threads){
            try{
                t.join();
            } catch(InterruptedException e){
                throw new RuntimeException(e);
            }
        }
//        System.out.println("Printing transaction log");
//        System.out.println(theater.getTransactionLog());
    }
}
