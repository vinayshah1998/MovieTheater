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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class Theater {
    private int clientID;
    private String show;
    private PriorityBlockingQueue<Seat> seats;
    private final List<Seat> reservedSeats;
    private List<Ticket> transactionHistory;
    private boolean soldOut;
    private boolean printedOnce;

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public PriorityBlockingQueue<Seat> getSeats() {
        return seats;
    }

    public void setSeats(PriorityBlockingQueue<Seat> seats) {
        this.seats = seats;
    }

    public List<Seat> getReservedSeats() {
        return reservedSeats;
    }

    public List<Ticket> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<Ticket> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public boolean isPrintedOnce() {
        return printedOnce;
    }

    public void setPrintedOnce(boolean printedOnce) {
        this.printedOnce = printedOnce;
    }

    /**
     * the delay time you will use when print tickets
     */
    private int printDelay = 50; // 50 ms.  Use it in your Thread.sleep()

    public void setPrintDelay(int printDelay) {
        this.printDelay = printDelay;
    }

    public int getPrintDelay() {
        return printDelay;
    }

    /**
     * Represents a seat in the theater
     * A1, A2, A3, ... B1, B2, B3 ...
     */
    static class Seat {
        private int rowNum;
        private int seatNum;

        public Seat(int rowNum, int seatNum) {
            this.rowNum = rowNum;
            this.seatNum = seatNum;
        }

        public int getSeatNum() {
            return seatNum;
        }

        public int getRowNum() {
            return rowNum;
        }

        @Override
        public String toString() {
            String result = "";
            int tempRowNumber = rowNum + 1;
            do {
                tempRowNumber--;
                result = ((char) ('A' + tempRowNumber % 26)) + result;
                tempRowNumber = tempRowNumber / 26;
            } while (tempRowNumber > 0);
            result += seatNum;
            return result;
        }
    }

    /**
     * Represents a ticket purchased by a client
     */
    static class Ticket {
        private String show;
        private String boxOfficeId;
        private Seat seat;
        private int client;
        public static final int ticketStringRowLength = 31;


        public Ticket(String show, String boxOfficeId, Seat seat, int client) {
            this.show = show;
            this.boxOfficeId = boxOfficeId;
            this.seat = seat;
            this.client = client;
        }

        public Seat getSeat() {
            return seat;
        }

        public String getShow() {
            return show;
        }

        public String getBoxOfficeId() {
            return boxOfficeId;
        }

        public int getClient() {
            return client;
        }

        @Override
        public String toString() {
            String result, dashLine, showLine, boxLine, seatLine, clientLine, eol;

            eol = System.getProperty("line.separator");

            dashLine = new String(new char[ticketStringRowLength]).replace('\0', '-');

            showLine = "| Show: " + show;
            for (int i = showLine.length(); i < ticketStringRowLength - 1; ++i) {
                showLine += " ";
            }
            showLine += "|";

            boxLine = "| Box Office ID: " + boxOfficeId;
            for (int i = boxLine.length(); i < ticketStringRowLength - 1; ++i) {
                boxLine += " ";
            }
            boxLine += "|";

            seatLine = "| Seat: " + seat.toString();
            for (int i = seatLine.length(); i < ticketStringRowLength - 1; ++i) {
                seatLine += " ";
            }
            seatLine += "|";

            clientLine = "| Client: " + client;
            for (int i = clientLine.length(); i < ticketStringRowLength - 1; ++i) {
                clientLine += " ";
            }
            clientLine += "|";

            result = dashLine + eol +
                    showLine + eol +
                    boxLine + eol +
                    seatLine + eol +
                    clientLine + eol +
                    dashLine;

            return result;
        }
    }

    public Theater(int numRows, int seatsPerRow, String show) {
        // TODO: Implement this constructor

        //adds seats to the theater so they are in order of bestSeat
        this.seats = new PriorityBlockingQueue<Seat>(numRows*seatsPerRow, new SeatComparator());
        for(int i = 0; i < numRows; i++){
            for(int j = 1; j <= seatsPerRow; j++){
                this.seats.add(new Seat(i, j));
            }
        }

        //creating a linked list to keep track of the reserved seats
        this.reservedSeats = (List<Seat>) Collections.synchronizedList(new LinkedList<Seat>());

        //and one to keep track of sold tickets
        this.transactionHistory = (List<Ticket>) Collections.synchronizedList(new LinkedList<Ticket>());

        //fix the name of the show
        this.show = show;

        //set the status of the show
        this.soldOut = false;
        if(this.seats.size() == 0){
            this.soldOut = true;
        }
        this.printedOnce = false;
    }

    /**
     * Calculates the best seat not yet reserved
     *
     * @return the best seat or null if theater is full
     */
    public Seat bestAvailableSeat() {
        try {
            if (this.seats.size() != 0){
                synchronized (this.reservedSeats){
                    Seat best = this.seats.take();
                    reservedSeats.add(best);
                    if(this.seats.size() == 0) this.soldOut = true;
                    return best;
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.soldOut = true;
        return null;
    }

    /**
     * Prints a ticket for the client after they reserve a seat
     * Also prints the ticket to the console
     *
     * @param seat a particular seat in the theater
     * @return a ticket or null if a box office failed to reserve the seat
     */
    public Ticket printTicket(String boxOfficeId, Seat seat, int client) {
        if (seat == null){
            return null;
        }
        Ticket ticket = new Ticket(this.show, boxOfficeId, seat, client);
        transactionHistory.add(ticket);
        System.out.println(ticket);
        try {
            Thread.sleep(getPrintDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ticket;
    }

    /**
     * Lists all tickets sold for this theater in order of purchase
     *
     * @return list of tickets sold
     */
    public List<Ticket> getTransactionLog() {
        return transactionHistory;
    }
}
