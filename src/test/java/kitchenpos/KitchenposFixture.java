package kitchenpos;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class KitchenposFixture {

  public static MenuGroup menuGroup() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName("menu group name");
    return menuGroup;
  }

  public static OrderTable orderTable() {
    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName("order table name");
    orderTable.setEmpty(false);
    return orderTable;
  }

}
