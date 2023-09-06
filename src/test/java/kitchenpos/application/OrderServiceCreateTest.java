package kitchenpos.application;

import static kitchenpos.application.constant.KitchenposTestConst.TEST_DELIVERY_ADDRESS;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.application.fakerepository.MenuFakeRepository;
import kitchenpos.application.fakerepository.OrderFakeRepository;
import kitchenpos.application.fakerepository.OrderTableFakeRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService의 create메소드 테스트")
public class OrderServiceCreateTest {

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
    void 배달주문을_생성하여_반환한다() {
        // given
        final Order orderRequest = configDelieverOrderRequest();

        // when
        final Order actual = sut.create(orderRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getType()).isEqualTo(OrderType.DELIVERY);
            softly.assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
            softly.assertThat(actual.getOrderTable()).isNull();
            softly.assertThat(actual.getDeliveryAddress())
                .isEqualTo(orderRequest.getDeliveryAddress());
        });
    }

    private Order configDelieverOrderRequest() {
        final BigDecimal price = BigDecimal.valueOf(1_000L);
        final Menu menuRequest = new Menu();
        menuRequest.setDisplayed(true);
        menuRequest.setPrice(price);

        final Menu menu = menuRepository.save(menuRequest);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(price);

        final Order orderRequest = new Order();
        orderRequest.setType(OrderType.DELIVERY);
        orderRequest.setDeliveryAddress(TEST_DELIVERY_ADDRESS);
        orderRequest.setOrderLineItems(List.of(orderLineItem));

        return orderRequest;
    }

    @Test
    void 포장주문을_생성하여_반환한다() {
        // given
        final Order orderRequest = configTakeoutOrderRequest();

        // when
        final Order actual = sut.create(orderRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getType()).isEqualTo(OrderType.TAKEOUT);
            softly.assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
            softly.assertThat(actual.getOrderTable()).isNull();
            softly.assertThat(actual.getDeliveryAddress()).isNull();
        });
    }

    private Order configTakeoutOrderRequest() {
        final BigDecimal price = BigDecimal.valueOf(1_000L);
        final Menu menuRequest = new Menu();
        menuRequest.setDisplayed(true);
        menuRequest.setPrice(price);

        final Menu menu = menuRepository.save(menuRequest);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(price);

        final Order orderRequest = new Order();
        orderRequest.setType(OrderType.TAKEOUT);
        orderRequest.setOrderLineItems(List.of(orderLineItem));

        return orderRequest;
    }

    @Test
    void 매장주문을_생성하여_반환한다() {
        // given
        final Order orderRequest = configEatinOrderRequest();

        // when
        final Order actual = sut.create(orderRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN);
            softly.assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
            softly.assertThat(actual.getOrderTable()).isNotNull();
            softly.assertThat(actual.getDeliveryAddress()).isNull();
        });
    }

    private Order configEatinOrderRequest() {
        final BigDecimal price = BigDecimal.valueOf(1_000L);
        final Menu menuRequest = new Menu();
        menuRequest.setDisplayed(true);
        menuRequest.setPrice(price);

        final Menu menu = menuRepository.save(menuRequest);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(price);

        final OrderTable orderTableRequest = new OrderTable();
        orderTableRequest.setOccupied(true);

        final OrderTable orderTable = orderTableRepository.save(orderTableRequest);

        final Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderLineItems(List.of(orderLineItem));
        orderRequest.setOrderTableId(orderTable.getId());

        return orderRequest;
    }
}
