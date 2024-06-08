package kitchenpos.application.fixture;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kitchenpos.domain.MenuGroup;
import org.springframework.test.util.ReflectionTestUtils;

public class MenuGroupFixture {
  public static MenuGroup normal() {
    return create("메뉴그룹");
  }
  public static MenuGroup create(String name) {
    MenuGroup menuGroup = new MenuGroup();
    ReflectionTestUtils.setField(menuGroup, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(menuGroup, "name", name);
    return menuGroup;
  }

  public static List<MenuGroup> createList(int size) {
    return IntStream.range(0, size)
        .mapToObj(cur -> create("메뉴리스트"+cur))
        .collect(Collectors.toList());
  }
}
