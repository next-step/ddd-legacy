package kitchenpos.service;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
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

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    private MenuGroup 추천메뉴;
    private Product 강정치킨;
    private Product 양념치킨;
    private Menu 오늘의치킨;
    private OrderTable _1번테이블;

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

        _1번테이블 = OrderTableFixture.builder().build();
        orderTableRepository.save(_1번테이블);
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

    @Test
    void 주문_생성_실패__주문항목의_메뉴개수가_음수() {
        Order request = OrderFixture.builder()
                .orderLineItem(List.of(OrderLineItemFixture.builder(오늘의치킨)
                        .quantity(-1).build()))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__메뉴가_숨김임() {
        오늘의치킨.setDisplayed(false);
        menuRepository.save(오늘의치킨);

        Order request = OrderFixture.builder(오늘의치킨).build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_생성_실패__주문항목의_가격과_메뉴의_가격이_다름() {
        Order request = OrderFixture.builder()
                .orderLineItem(List.of(OrderLineItemFixture.builder(오늘의치킨)
                        .price(new BigDecimal(28000)).build()))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @NullAndEmptySource
    @ParameterizedTest
    void 주문_생성_실패__배달인데_주소가_null이거나_비어있음(String nullAndEmpty) {
        Order request = OrderFixture.builder(오늘의치킨)
                .type(DELIVERY)
                .deliveryAddress(nullAndEmpty)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__먹고가기인데_주문테이블_착석상태_아님() {
        _1번테이블.setOccupied(false);
        orderTableRepository.save(_1번테이블);

        Order request = OrderFixture.builder(오늘의치킨)
                .type(EAT_IN)
                .orderTable(_1번테이블)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_수락_실패__대기중_상태가_아님() {
        Order order = orderService.create(OrderFixture.builder(오늘의치킨).build());
        order.setStatus(ACCEPTED);
        orderRepository.save(order);

        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_서빙_실패__수락됨_상태가_아님() {
        Order order = orderService.create(OrderFixture.builder(오늘의치킨).build());
        order.setStatus(SERVED);
        orderRepository.save(order);

        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }
}
