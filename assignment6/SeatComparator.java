package assignment6;

import java.util.Comparator;

public class SeatComparator implements Comparator<Theater.Seat> {
    @Override
    public int compare(Theater.Seat o1, Theater.Seat o2) {
        if ((o1.getRowNum() < o2.getRowNum()) && (o1.getSeatNum() < o2.getSeatNum())){
            return -1;
        }
        if ((o1.getRowNum() < o2.getRowNum())){
            return -1;
        }
        if ((o1.getRowNum() == o2.getRowNum()) && (o1.getSeatNum() < o2.getSeatNum())){
            return -1;
        }
        if ((o1.getRowNum() == o2.getRowNum()) && (o1.getSeatNum() == o2.getSeatNum())){
            return 0;
        }
        return 1;
    }
}
