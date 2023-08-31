package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixture.MAX_PRICE;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.TEST_ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("새로운 주문을 등록한다")
    class createTestClass {

        @Test
        @DisplayName("새로운_배달_주문을_등록한다")
        void newOrderTest() {
            // given
            Menu menu = MenuFixture.TEST_MENU();
            Order order = TEST_ORDER_DELIVERY(OrderStatus.WAITING, menu);
            menuRepository.save(menu);

            // when
            Order actual = orderService.create(order);

            // then
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getType()).isEqualTo(OrderType.DELIVERY);
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(actual.getDeliveryAddress()).isNotEmpty();
        }

        @Test
        @DisplayName("새로운_테이크아웃_주문을_등록한다")
        void newTakeOutOrderTest() {
            // given
            Menu menu = MenuFixture.TEST_MENU();
            Order order = TEST_ORDER_TAKEOUT(OrderStatus.WAITING, menu);
            menuRepository.save(menu);

            // when
            Order actual = orderService.create(order);

            // then
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getType()).isEqualTo(OrderType.TAKEOUT);
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        @DisplayName("새로운_매장_주문을_등록한다")
        void newEatInTest() {
            // given
            Menu menu = MenuFixture.TEST_MENU();
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING, menu);
            menuRepository.save(menu);

            OrderTable orderTable = TEST_ORDER_TABLE(true);
            given(orderTableRepository.findById(order.getOrderTableId()))
                    .willReturn(Optional.of(orderTable));

            // when
            Order actual = orderService.create(order);

            // then
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN);
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(actual.getOrderTable()).isNotNull();
            assertThat(actual.getOrderTable().getId()).isNotNull();
        }

        @Test
        @DisplayName("주문_타입은_배달_매장_포장_중_하나이어야_한다")
        void menuTypeTest() {
            // given
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING);

            // when
            order.setType(null);

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문의_주문_내역은_비어있을_수_없다")
        void menuListNotEmpty() {
            // given
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING);

            // when
            order.setOrderLineItems(null);

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문_내역에_포함된_메뉴들은_존재하는_메뉴들이어야_한다")
        void menuShouldExist() {
            // given
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING);

            // when && then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @CsvSource(value = {"take_out", "delivery"})
        @DisplayName("주문_타입이_매장_타입이_아니라면_주문_내역의_수량이_0이상이어야_한다")
        void menuQuantityTest(String typeValue) {
            // given
            Order order = typeValue.equals("take_out") ?
                    TEST_ORDER_TAKEOUT(OrderStatus.WAITING) : TEST_ORDER_DELIVERY(OrderStatus.WAITING);

            order.setType(typeValue.equals("take_out") ? OrderType.TAKEOUT : OrderType.DELIVERY);
            initTestMenu();

            // when
            order.getOrderLineItems().forEach(item -> item.setQuantity(-1));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문_내역들의_메뉴들은_활성화_된_메뉴들이어야_한다")
        void menuShouldDisplay() {
            // given
            Menu menu = MenuFixture.TEST_MENU();
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING, menu);
            menuRepository.save(menu);

            // when
            menu.setDisplayed(false);

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문_내역의_가격과_메뉴의_가격이_일치하여야_한다")
        void menuAndMenuLinePriceTest() {
            // given
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING);
            Menu menu = initTestMenu();

            // when
            menu.setPrice(MAX_PRICE);

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문_타입이_배달이라면_주소가_적혀있어야_한다")
        void addressTest() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.WAITING);
            initTestMenu();

            // when
            order.setDeliveryAddress(null);

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문_타입이_매장이라면_사용_가능한_테이블이_지정되어야_한다")
        void orderTableShouldSet() {
            // given
            Menu menu = MenuFixture.TEST_MENU();
            Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING, menu);
            menuRepository.save(menu);
            OrderTable orderTable = TEST_ORDER_TABLE();
            given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));

            // when
            orderTable.setOccupied(false);

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문을 수락한다")
    class acceptTestClass {

        @ParameterizedTest
        @CsvSource(value = {"take_out", "eat_in"})
        @DisplayName("테이크아웃과_매장_주문을_수락한다")
        void takeOutAndEatInTest(String typeName) {
            // given
            Order order = initOrderByType(getOrderByTypeName(typeName, OrderStatus.WAITING));

            // when
            Order actual = orderService.accept(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("수락하려는_주문의_상태가_대기중이어야_한다")
        void acceptStatusTest() {
            // given
            Order order = initOrderByType(TEST_ORDER_EAT_IN(OrderStatus.COMPLETED));

            // when && then
            assertThatThrownBy(() -> orderService.accept(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달_주문이라면_배달을_요청한다")
        void deliveryAcceptTest() {
            // given
            Order order = initOrderByType(TEST_ORDER_DELIVERY(OrderStatus.WAITING));

            // when
            Order actual = orderService.accept(order.getId());

            // then
            verify(kitchenridersClient, times(1))
                    .requestDelivery(eq(order.getId()),any(BigDecimal.class), anyString());
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("주문을 조리한다.")
    class servedTestClass {

        @Test
        @DisplayName("주문을_조리_완료하고_제공_상태로_변경한다")
        void changeStatusTest() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            // when
            Order actual = orderService.serve(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("주문의_상태가_수락된_상태이어야_한다")
        void orderStatusShouldAccept() {
            // given
            Order order = TEST_ORDER_EAT_IN(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // when && then
            assertThatThrownBy(() -> orderService.serve(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문의 배달을 시작하고 완료한다.")
    class deliveryTest {

        @Test
        @DisplayName("주문의_배달을_시작한다")
        void startDelivery() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            Order actual = orderService.startDelivery(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @ParameterizedTest
        @CsvSource(value = {"take_out", "eat_in"})
        @DisplayName("주문_타입이_배달이어야_한다")
        void typeShouldDeliveryTest(String typeName) {
            // given
            Order order = getOrderByTypeName(typeName, OrderStatus.SERVED);
            orderRepository.save(order);

            // when && then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문_상태가_제공_상태이어야_한다")
        void statusShouldServed() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // when && then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문의_배달을_완료한다")
        void completeDelivery() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.DELIVERING);
            orderRepository.save(order);

            // when
            Order actual = orderService.completeDelivery(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("주문의_상태가_배달중이어야_한다")
        void statusShouldDelivering() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // when && then
            assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문을 완료한다.")
    class completeTest {

        @Test
        @DisplayName("배달_주문을_완료한다")
        void completeDeliveryTest() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.DELIVERED);
            orderRepository.save(order);

            // when
            Order actual = orderService.complete(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("주문이_배달이라면_주문_상태가_배달_완료이어야한다")
        void checkDeliveryStatus() {
            // given
            Order order = TEST_ORDER_DELIVERY(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // when && then
            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("매장_주문을_완료하면서_테이블도_정리한다")
        void clearTableTest() {
            // given
            Order order = TEST_ORDER_EAT_IN(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            Order actual = orderService.complete(order.getId());

            // then
            assertThat(actual.getOrderTable().isOccupied()).isFalse();
            assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("포장_주문을_완료한다")
        void completeTakeOutOrder() {
            // given
            Order order = TEST_ORDER_TAKEOUT(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            Order actual = orderService.complete(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @ParameterizedTest
        @CsvSource(value = {"take_out", "eat_in"})
        @DisplayName("주문이_매장이거나_포장이라면_주문_상태가_제공된_상태이여야한다")
        void checkTakeOutAndEatInStatus(String typeName) {
            // given
            Order order = getOrderByTypeName(typeName, OrderStatus.COMPLETED);
            orderRepository.save(order);

            // when && then
            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    private Order getOrderByTypeName(String typeName, OrderStatus status) {
        Order order;
        switch (typeName) {
            case "take_out":
                order = TEST_ORDER_TAKEOUT(status);
                break;
            case "eat_in":
            default:
                order = TEST_ORDER_EAT_IN(status);
        }
        return order;
    }

    private Menu initTestMenu() {
        Menu menu = MenuFixture.TEST_MENU();
        menuRepository.save(menu);
        return menu;
    }

    private Order initOrderByType(Order typeName) {
        orderRepository.save(typeName);
        return typeName;
    }
}