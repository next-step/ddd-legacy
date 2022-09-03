package kitchenpos.application;

import static kitchenpos.domain.MenuFixture.*;
import static kitchenpos.domain.MenuGroupFixture.MenuGroup;
import static kitchenpos.domain.MenuGroupFixture.MenuGroupWithUUID;
import static kitchenpos.domain.MenuProductFixture.*;
import static kitchenpos.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuProductFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductFixture;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("새로운 메뉴를 생성할 수 있다.")
    @Nested
    class Create {

        @DisplayName("성공")
        @Test
        void create() {
            // given
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProduct(햄버거.getId(), 1);
            MenuProduct 콜라_메뉴상품 = MenuProduct(콜라.getId(), 1);

            Menu request = Menu(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴.getId(),
                햄버거_메뉴상품, 콜라_메뉴상품
            );

            // when
            Menu savedMenu = menuService.create(request);

            // then
            assertThat(savedMenu.getId()).isNotNull();
            assertThat(savedMenu.getName()).isEqualTo("햄버거 + 콜라 세트메뉴");
            assertThat(savedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(17_000).setScale(2));
            assertThat(savedMenu.getMenuGroup()).usingRecursiveComparison()
                .isEqualTo(세트메뉴);
            assertThat(savedMenu.getMenuProducts()).usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                "seq",
                "product",
                "productId"
            ).containsExactly(햄버거_메뉴상품, 콜라_메뉴상품);
        }
    }
}
