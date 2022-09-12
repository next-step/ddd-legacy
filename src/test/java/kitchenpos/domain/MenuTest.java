package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuTest {
    @DisplayName("메뉴는 이름, 가격, 공개여부, 상품목록을 가지고 있다.")
    @Test
    void properties() {
        final var tunaSushi = new Product();
        tunaSushi.setName("참치초밥");
        tunaSushi.setPrice(new BigDecimal(2000));
        final var salmonSushi = new Product();
        salmonSushi.setName("연어초밥");
        salmonSushi.setPrice(new BigDecimal(3000));

        final var tunaMenuProduct = new MenuProduct();
        tunaMenuProduct.setProduct(tunaSushi);
        tunaMenuProduct.setQuantity(6);
        final var salmonMenuProduct = new MenuProduct();
        salmonMenuProduct.setProduct(salmonSushi);
        salmonMenuProduct.setQuantity(6);

        final var menu = new Menu();
        menu.setName("반반초밥");
        menu.setPrice(new BigDecimal(20000));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(tunaMenuProduct, salmonMenuProduct));

        assertAll(
                () -> assertThat(menu.getName()).isEqualTo("반반초밥"),
                () -> assertThat(menu.getPrice()).isEqualTo(new BigDecimal(20000)),
                () -> assertThat(menu.isDisplayed()).isTrue(),
                () -> assertThat(menu.getMenuProducts())
                        .extracting("product.name", "product.price", "quantity")
                        .containsExactlyInAnyOrder(
                                tuple("참치초밥", new BigDecimal(2000), 6L),
                                tuple("연어초밥", new BigDecimal(3000), 6L)
                        )
        );
    }
}
