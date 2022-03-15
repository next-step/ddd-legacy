package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.Collections;
import java.util.List;

import static kitchenpos.KitchenposFixture.ID;

public class OrderTableFixture {
  private static final String ORDER_TABLE_NAME = "order table name";
  private static final int PLUS_TEN = 10;
  private static final int MINUS_TEN = -10;

  public static OrderTable 정상_오더_테이블() {
    OrderTable orderTable = new OrderTable();
    orderTable.setId(ID);
    orderTable.setName(ORDER_TABLE_NAME);
    orderTable.setEmpty(false);
    orderTable.setNumberOfGuests(0);
    return orderTable;
  }

  public static OrderTable 오더_테이블_손님_10명() {
    OrderTable orderTable = new OrderTable();
    orderTable.setNumberOfGuests(PLUS_TEN);
    return orderTable;
  }

  public static OrderTable 오더_테이블_손님_음수() {
    OrderTable orderTable = new OrderTable();
    orderTable.setNumberOfGuests(MINUS_TEN);
    return orderTable;
  }

  public static List<OrderTable> 오더_테이블_리스트_사이즈_1() {
    return Collections.singletonList(정상_오더_테이블());
  }

}
