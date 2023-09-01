package kitchenpos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    private MenuGroup 추천메뉴;
    private Product 강정치킨;
    private Product 양념치킨;
    private Menu 오늘의치킨;

    @BeforeEach
    void init() {
        추천메뉴 = MenuGroupFixture.builder().build();
        menuGroupRepository.save(추천메뉴);

        강정치킨 = ProductFixture.Data.강정치킨();
        productRepository.save(강정치킨);

        양념치킨 = ProductFixture.Data.양념치킨();
        productRepository.save(양념치킨);

        오늘의치킨 = MenuFixture.builder(추천메뉴)
                .menuProducts(List.of(
                        MenuProductFixture.builder(강정치킨).build())
                )
                .name("오늘의 치킨").build();
        menuRepository.save(오늘의치킨);
    }

    @Test
    void 주문_생성_실패__주문타입이_null() {
        Order request = OrderFixture.builder()
                .type(null)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__주문항목이_null() {
        Order request = OrderFixture.builder()
                .orderLineItem(null)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__주문항목이_비어있음() {
        Order request = OrderFixture.builder()
                .orderLineItem(List.of())
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__주문항목의_메뉴_내용이_실제_메뉴_내용과_다름() {
        Order request = OrderFixture.builder()
                .orderLineItem(List.of(OrderLineItemFixture.builder(오늘의치킨)
                                .menuId(UUID.randomUUID()).build()))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
