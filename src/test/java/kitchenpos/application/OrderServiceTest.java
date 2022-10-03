package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.helper.InMemoryMenuRepository;
import kitchenpos.helper.InMemoryOrderRepository;
import kitchenpos.helper.InMemoryOrderTableRepository;
import kitchenpos.helper.MenuFixture;
import kitchenpos.helper.OrderFixture;
import kitchenpos.helper.OrderLineItemFixture;
import kitchenpos.helper.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final KitchenridersClient kitchenridersClient = new KitchenridersClient();

    private OrderService testTarget;

    @BeforeEach
    void setUp() {
        testTarget = new OrderService(
            orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        );
    }

    @DisplayName("주문 등록 테스트")
    @Nested
    class CreateTest {

        @DisplayName("매장 주문을 등록 할 수 있다.")
        @Test
        void test01() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE);
            Order request = OrderFixture.request(
                OrderType.EAT_IN,
                OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()),
                orderTable.getId()
            );

            // when
            Order actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(actual.getOrderTable().getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(actual.getOrderTable().isOccupied()).isTrue(),
                () -> assertThat(actual.getOrderLineItems())
                    .singleElement()
                    .matches(orderLineItem -> orderLineItem.getMenu().getId().equals(menu.getId()))
                    .matches(orderLineItem -> orderLineItem.getQuantity() == 1L)
            );
        }

        @DisplayName("배달 주문을 등록 할 수 있다.")
        @Test
        void test02() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Order request = OrderFixture.request(
                OrderType.DELIVERY,
                OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()),
                "delivery address"
            );

            // when
            Order actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getType()).isEqualTo(OrderType.DELIVERY),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(actual.getDeliveryAddress()).isEqualTo("delivery address"),
                () -> assertThat(actual.getOrderLineItems())
                    .singleElement()
                    .matches(orderLineItem -> orderLineItem.getMenu().getId().equals(menu.getId()))
                    .matches(orderLineItem -> orderLineItem.getQuantity() == 1L)
            );
        }

        @DisplayName("포장 주문을 등록 할 수 있다.")
        @Test
        void test03() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Order request = OrderFixture.request(
                OrderType.TAKEOUT,
                List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()))
            );

            // when
            Order actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getType()).isEqualTo(OrderType.TAKEOUT),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(actual.getOrderLineItems())
                    .singleElement()
                    .matches(orderLineItem -> orderLineItem.getMenu().getId().equals(menu.getId()))
                    .matches(orderLineItem -> orderLineItem.getQuantity() == 1L)
            );
        }

        @DisplayName("주문 타입없이 주문을 등록 할 수 없다.")
        @Test
        void test04() {
            // given
            Order request = new Order();

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("주문 아이템없이 주문을 등록 할 수 없다.")
        @Test
        void test05() {
            // given
            Order request = OrderFixture.request(
                OrderType.TAKEOUT,
                Collections.emptyList()
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("존재하지 않는 메뉴를 주문 할 수 없다.")
        @Test
        void test06() {
            // given
            Order request = OrderFixture.request(
                OrderType.TAKEOUT,
                List.of(OrderLineItemFixture.request(UUID.randomUUID(), 1, BigDecimal.valueOf(6000)))
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("매장 주문이 아닌 경우, 주문 아이템의 수량은 0 보다 많아야 한다.")
        @Test
        void test07() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Order request = OrderFixture.request(
                OrderType.TAKEOUT,
                List.of(OrderLineItemFixture.request(menu.getId(), -1, menu.getPrice()))
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("감춰진 메뉴를 주문 할 수 없다.")
        @Test
        void test08() {
            // given
            Menu menu = menuRepository.save(MenuFixture.NO_DISPLAYED_MENU);
            Order request = OrderFixture.request(
                OrderType.TAKEOUT,
                List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()))
            );

            // when & then
            assertThatIllegalStateException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴의 가격과 주문 아이템의 가격이 다른 경우, 주문 할 수 없다.")
        @Test
        void test09() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            BigDecimal orderItemPrice = menu.getPrice().add(BigDecimal.valueOf(1000));
            Order request = OrderFixture.request(
                OrderType.TAKEOUT,
                List.of(OrderLineItemFixture.request(menu.getId(), 1, orderItemPrice))
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("배달 주문의 경우, 배달 주소가 없는 경우 주문 할 수 없다.")
        @ParameterizedTest
        @NullAndEmptySource
        void test10(String deliveryAddress) {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Order request = OrderFixture.request(
                OrderType.DELIVERY,
                OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()),
                deliveryAddress
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("매장 주문의 경우, 존재하지 않는 주문 테이블에 주문을 등록 할 수 없다.")
        @Test
        void test11() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Order request = OrderFixture.request(
                OrderType.EAT_IN,
                OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()),
                UUID.randomUUID()
            );

            // when & then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("매장 주문의 경우, 빈 테이블이 아닌 경우 주문을 등록 할 수 없다.")
        @Test
        void test12() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_TABLE);
            Order request = OrderFixture.request(
                OrderType.EAT_IN,
                OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()),
                orderTable.getId()
            );

            // when & then
            assertThatIllegalStateException()
                .isThrownBy(() -> testTarget.create(request));
        }
    }
}