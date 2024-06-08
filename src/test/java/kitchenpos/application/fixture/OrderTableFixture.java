package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;
import org.springframework.test.util.ReflectionTestUtils;

public class OrderTableFixture {

  public static OrderTable normal() {
    return create("주문테이블");
  }
  public static OrderTable create(String name) {
    return create(name, 0, false);
  }

  public static OrderTable create(String name, int numberOfGuests) {
    return create(name, numberOfGuests, true);
  }

  public static OrderTable create(String name, boolean isOccupied) {
    return create(name, 0, isOccupied);
  }

  public static OrderTable create(String name, int numberOfGuests, boolean isOccupied) {
    OrderTable orderTable = new OrderTable();
    ReflectionTestUtils.setField(orderTable, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(orderTable, "name", name);
    ReflectionTestUtils.setField(orderTable, "numberOfGuests", numberOfGuests);
    ReflectionTestUtils.setField(orderTable, "occupied", isOccupied);
    return orderTable;
  }
}
