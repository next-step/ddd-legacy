package kitchenpos.application.fixture;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

public class ProductFixture {

  public static Product normal() {
    return create("상품", 500L);
  }

  public static Product create(String name) {
    return create(name, 500L);
  }

  public static Product create(Long price) {
    return create("상품", price);
  }

  public static Product create(String name, Long price) {
    Product product = new Product();
    ReflectionTestUtils.setField(product, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(product, "name", name);
    ReflectionTestUtils.setField(product, "price", price);
    return product;
  }

  public static List<Product> createList(int size) {
    return IntStream.range(0, size)
        .mapToObj(cur -> create("상품리스트"+cur))
        .collect(Collectors.toList());
  }

}
