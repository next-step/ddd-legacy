package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.List;
import java.util.stream.Collectors;

public class MenuProductFixture {

    private static Long seq = 1L;

    private MenuProductFixture() {
    }

    public static MenuProduct create(Product product, int quantity) {
        return create(seq++, product, quantity);
    }

    public static MenuProduct create(Long seq, Product product, int quantity) {
        MenuProduct result = new MenuProduct();
        result.setSeq(seq);
        result.setProduct(product);
        result.setQuantity(quantity);
        result.setProductId(product.getId());
        return result;
    }

    public static List<MenuProduct> create(List<Product> products, int quantity) {
        return products.stream()
                .map(e -> create(e, quantity))
                .collect(Collectors.toList());
    }

}
