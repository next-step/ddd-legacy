package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
  public static OrderTable create(String name) {
    OrderTable request = new OrderTable();
    request.setId(UUID.randomUUID());
    request.setName(name);
    return request;
  }

  public static OrderTable create(String name, int numberOfGuests) {
    OrderTable request = create(name);
    request.setNumberOfGuests(numberOfGuests);
    return request;
  }

  public static OrderTable create(String name, boolean isOccupied) {
    OrderTable request = create(name);
    request.setOccupied(isOccupied);
    return request;
  }
}
