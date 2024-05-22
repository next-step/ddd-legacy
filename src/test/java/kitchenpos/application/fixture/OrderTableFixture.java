package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
  public static OrderTable create(String name) {
    OrderTable request = new OrderTable();
    request.setName(name);
    return request;
  }

  public static OrderTable create(String name, int numberOfGuests) {
    OrderTable request = create(name);
    request.setNumberOfGuests(numberOfGuests);
    return request;
  }
}
