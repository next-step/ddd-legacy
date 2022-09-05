package kitchenpos.application;

import static kitchenpos.domain.MenuFixture.MenuWithUUIDAndMenuGroup;
import static kitchenpos.domain.MenuGroupFixture.MenuGroupWithUUID;
import static kitchenpos.domain.MenuProductFixture.MenuProductWithProduct;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.domain.ProductFixture.ProductWithUUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @DisplayName("새로운 주문을 생성할 수 있다.")
    @Nested
    class Create {

        private Menu 햄버거_콜라_세트메뉴;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            햄버거_콜라_세트메뉴 = menuRepository.save(MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            ));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // given
            햄버거_콜라_세트메뉴.setDisplayed(true);
            menuRepository.save(햄버거_콜라_세트메뉴);

            Order request = new Order();
//            request.setOrderTableId();
            request.setType(EAT_IN);
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setMenuId(햄버거_콜라_세트메뉴.getId());
            orderLineItem.setQuantity(1);
            orderLineItem.setPrice(햄버거_콜라_세트메뉴.getPrice());
            request.setOrderLineItems(List.of(orderLineItem));

            // when
            Order result = orderService.create(request);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getType()).isEqualTo(EAT_IN);
        }
    }
}
