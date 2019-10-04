package assignment6;

public class BookingClientTask implements Runnable {
    String office;
    Integer customers;
    Theater theater;

    public BookingClientTask(String office, Integer customers, Theater theater){
        this.office = office;
        this.customers = customers;
        this.theater = theater;
    }

    @Override
    public void run() {
        while ((theater.getSeats().size() != 0) && (customers > 0)){
            Theater.Seat newSeat = theater.bestAvailableSeat();
//            synchronized (theater){
//                theater.clientID++;
//                theater.printTicket(office, newSeat, theater.clientID);
//            }
            theater.setClientID(theater.getClientID() + 1);
            theater.printTicket(office, newSeat, theater.getClientID());
            customers--;
        }
        synchronized (theater){
            if(theater.isSoldOut() & !theater.isPrintedOnce()){
                System.out.println("Sorry, we are sold out!");
                theater.setPrintedOnce(true);
            }
        }
    }
}
