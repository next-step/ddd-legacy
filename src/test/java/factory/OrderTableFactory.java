package factory;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderTableFactory {

    public static final String DEFAULT_NAME = "1번 테이블";
    public static final int DEFAULT_NUMBER_OF_GUESTS = 1;

    public static OrderTable of(boolean occupied){
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(DEFAULT_NAME);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(DEFAULT_NUMBER_OF_GUESTS);
        return orderTable;
    }

    public static OrderTable of(){
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(DEFAULT_NAME);
        orderTable.setNumberOfGuests(DEFAULT_NUMBER_OF_GUESTS);
        return orderTable;
    }
}
