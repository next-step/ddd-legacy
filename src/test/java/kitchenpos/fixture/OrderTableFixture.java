package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
  public static OrderTable createTable(final String name){
    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName(name);
    return orderTable;
  }

  public static OrderTable changeOrderTableNumberOfGuests(final String name, final int numberOfGuests){
    OrderTable orderTable = new OrderTable();

    orderTable.setName(name);
    orderTable.setNumberOfGuests(numberOfGuests);
    return orderTable;
  }
}
