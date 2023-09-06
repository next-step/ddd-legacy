package kitchenpos.application;

import static kitchenpos.application.constant.KitchenposTestConst.TEST_DELIVERY_ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.application.fakerepository.MenuFakeRepository;
import kitchenpos.application.fakerepository.OrderFakeRepository;
import kitchenpos.application.fakerepository.OrderTableFakeRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("OrderService 클래스의 accept메소드 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceAcceptTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient mockRidersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderFakeRepository();
        menuRepository = new MenuFakeRepository();
        orderTableRepository = new OrderTableFakeRepository();

        sut = new OrderService(orderRepository, menuRepository,
            orderTableRepository, mockRidersClient);
    }


    @Test
    void 주문이_없으면_예외를_발생시킨다() {

        // when & then
        assertThatThrownBy(() -> sut.accept(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 주문이_waiting상태가_아니면_예외를_발생시킨다() {
        // given
        final Order order = orderRepository.save(create(OrderType.EAT_IN, OrderStatus.ACCEPTED));

        // when & then
        assertThatThrownBy(() -> sut.accept(order.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }


    @Test
    void 배달주문일_때_베달정보를_가지고_라이더에게_배달을_요청한다() {
        // given
        final Order order = orderRepository.save(
            createDeliveryOrder(1_000L, 2, TEST_DELIVERY_ADDRESS));

        // when
        sut.accept(order.getId());

        // verify
        verify(mockRidersClient)
            .requestDelivery(order.getId(), BigDecimal.valueOf(2_000L), order.getDeliveryAddress());
    }

    @Nested
    class 배달주문이_아닐_때_라이더에게_배달_요청을_하지_않는다 {

        @Test
        void 매장주문() {
            // given
            final Order order = orderRepository.save(create(OrderType.EAT_IN, OrderStatus.WAITING));

            // when
            sut.accept(order.getId());

            // verify
            verify(mockRidersClient, never())
                .requestDelivery(any(), any(), anyString());
        }

        @Test
        void 포장주문() {
            // given
            final Order order = orderRepository.save(
                create(OrderType.TAKEOUT, OrderStatus.WAITING));

            // when
            sut.accept(order.getId());

            // verify
            verify(mockRidersClient, never())
                .requestDelivery(any(), any(), anyString());
        }
    }

    @Test
    void 주문을_accpted로_변경하여_반환한다() {
        // given
        final Order order = orderRepository.save(create(OrderType.EAT_IN, OrderStatus.WAITING));

        // when
        final Order actual = sut.accept(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }
    
    private Order createDeliveryOrder(final long menuPrice, final long quantity,
        final String address) {

        final Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(menuPrice));

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);

        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setDeliveryAddress(address);
        order.setOrderLineItems(List.of(orderLineItem));

        return order;
    }

    private Order create(final OrderType type, final OrderStatus status) {
        final Order order = new Order();
        order.setType(type);
        order.setStatus(status);

        return order;
    }
}
