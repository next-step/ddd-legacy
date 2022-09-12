package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

  public static OrderTable createOrderTable() {
    return createOrderTable("1번");
  }

  public static OrderTable createOrderTable(String name) {
    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName(name);
    orderTable.setOccupied(true);

    return orderTable;
  }
}
