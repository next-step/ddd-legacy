package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.OrderLineItemFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.MoneyConstants.만원;
import static kitchenpos.MoneyConstants.이만원;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.createEmptyTable;
import static kitchenpos.fixture.OrderTableFixture.createSittingTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 접수")
    class Waiting {

        @Test
        @DisplayName("주문이 접수완료된 경우 주문상태가 대기중(WAITING)이 된다.")
        void success1() {
            final var menu = createMenu("후라이드", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.save(any())).willReturn(order);

            Order actual = orderService.create(order);

            assertEquals(OrderStatus.WAITING, actual.getStatus());
        }

        @Test
        @DisplayName("하나의 주문에 여러개 메뉴 주문이 가능하다.")
        void success2() {
            final var menu1 = createMenu("후라이드", 만원);
            final var menu2 = createMenu("양념치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu1, menu2);
            final var response = createOrder(OrderType.TAKEOUT, menu1, menu2);

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu1, menu2));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu1));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu2));
            given(orderRepository.save(any())).willReturn(response);

            Order actual = orderService.create(order);

            assertEquals(2, actual.getOrderLineItems().size());
        }

        @Test
        @DisplayName("주문타입 정보가 없는 경우 주문을 받을 수 없다.")
        void typeFail() {
            final var order = createOrder(null, createMenu("치킨", 만원));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("메뉴를 하나도 선택하지 않은 경우 주문할 수 없다.")
        void fail1(List<OrderLineItem> input) {
            final var order = createOrder(input);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Test
        @DisplayName("주문한 메뉴들 중에 하나라도 등록되어 있지 않은 메뉴가 포함되어 있으면 주문할 수 없다.")
        void fail2() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Test
        @DisplayName("미노출된 메뉴가 포함되어있는 경우 주문할 수 없다.")
        void fail3() {
            final var menu = createMenu("치킨", 만원);
            menu.setDisplayed(false);
            final var order = createOrder(OrderType.TAKEOUT, menu);

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            assertThrows(IllegalStateException.class, () -> orderService.create(order));
        }

        @Test
        @DisplayName("주문한 메뉴의 금액과 등록된 메뉴 금액이 다른 경우 주문할 수 없다.")
        void fail4() {
            final var savedMenu = createMenu("치킨", 만원);
            final var orderMenu = createMenu("치킨", 이만원);
            final var order = createOrder(OrderType.TAKEOUT, orderMenu);

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(savedMenu));
            given(menuRepository.findById(any())).willReturn(Optional.of(savedMenu));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Nested
        @DisplayName("매장 주문")
        class EatIn {

            @Test
            @DisplayName("매장주문을 접수받을 수 있다.")
            void success() {
                final var menu = createMenu("치킨", 만원);
                final var orderTable = createSittingTable(2);
                final var order = createEatInOrder(orderTable, menu);

                final var response = createEatInOrder(orderTable, menu);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));
                given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
                given(orderRepository.save(any())).willReturn(response);

                Order actual = orderService.create(order);

                assertAll(
                        "매장주문 접수 완료 Assertions",
                        () -> assertThat(actual).isNotNull(),
                        () -> assertEquals(OrderStatus.WAITING, actual.getStatus())
                );
            }

            @ParameterizedTest
            @ValueSource(ints = {-1})
            @DisplayName("0개보다 적게 취소주문이 가능하다.")
            void fail2(int input) {
                final var menu = createMenu("치킨", 만원);
                final var 취소메뉴 = createMenu("치킨", -만원);
                OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItem(취소메뉴, input);
                final var orderTable = createSittingTable(2);
                final var order = createEatInOrder(OrderType.EAT_IN, orderTable, List.of(orderLineItem));

                final var response = createEatInOrder(OrderType.EAT_IN, orderTable, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));
                given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
                given(orderRepository.save(any())).willReturn(response);

                Order actual = orderService.create(order);

                assertAll(
                        "매장주문 0개 미만 취소 접수 완료 Assertions",
                        () -> assertThat(actual).isNotNull(),
                        () -> assertEquals(OrderStatus.WAITING, actual.getStatus()),
                        () -> assertEquals(input, actual.getOrderLineItems().get(0).getQuantity())
                );
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("등록된 테이블정보가 아닌 경우 접수가 불가하다.")
            void fail1(OrderTable input) {
                final var menu = createMenu("치킨", 만원);
                final var order = createEatInOrder(input, menu);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));

                assertThrows(NoSuchElementException.class, () -> orderService.create(order));
            }

            @Test
            @DisplayName("손님이 착석중이 아닌 테이블로 접수가 들어온 경우 접수가 불가능하다.")
            void fail2() {
                final var menu = createMenu("치킨", 만원);
                final var orderTable = createEmptyTable("손님 미착석 테이블");
                final var order = createEatInOrder(orderTable, menu);

                final var response = createEatInOrder(orderTable, menu);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));
                given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));

                assertThrows(IllegalStateException.class, () -> orderService.create(order));
            }
        }

        @Nested
        @DisplayName("배달 주문")
        class Delivery {
            @Test
            @DisplayName("배달 주문을 접수받을 수 있다.")
            void success() {
                final var menu = createMenu("치킨", 만원);
                final var order = createDeliveryOrder("배달주소", menu);

                final var response = createDeliveryOrder("배달주소", menu);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));
                given(orderRepository.save(any())).willReturn(response);

                Order actual = orderService.create(order);

                assertAll(
                        "배달주문 접수 완료 Assertions",
                        () -> assertThat(actual).isNotNull(),
                        () -> assertEquals(OrderStatus.WAITING, actual.getStatus())
                );
            }

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("배달 주소 정보는 필수로 입력해야한다.")
            void fail1(String input) {
                final var menu = createMenu("치킨", 만원);
                final var order = createDeliveryOrder(input, menu);

                final var response = createDeliveryOrder(input, menu);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));

                assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
            }

            @ParameterizedTest
            @ValueSource(ints = {-1, -100})
            @DisplayName("주문한 메뉴 중 0개보다 작은 메뉴가 있는 경우 주문할 수 없다.")
            void fail2(int input) {
                final var menu = createMenu("치킨", 만원);
                OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItem(menu, input);
                final var order = createDeliveryOrder(OrderType.DELIVERY, "배달주소", List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

                assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
            }
        }

        @Nested
        @DisplayName("포장 주문")
        class TakeOut {
            @Test
            @DisplayName("포장 주문을 접수받을 수 있다.")
            void success() {
                final var menu = createMenu("치킨", 만원);
                final var order = createOrder(OrderType.TAKEOUT, menu);

                final var response = createOrder(OrderType.TAKEOUT, menu);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));
                given(orderRepository.save(any())).willReturn(response);

                Order actual = orderService.create(order);

                assertAll(
                        "배달주문 접수 완료 Assertions",
                        () -> assertThat(actual).isNotNull(),
                        () -> assertEquals(OrderStatus.WAITING, actual.getStatus())
                );
            }

            @ParameterizedTest
            @ValueSource(ints = {-1, -100})
            @DisplayName("주문한 메뉴 중 0개보다 작은 메뉴가 있는 경우 주문할 수 없다.")
            void fail2(int input) {
                final var menu = createMenu("치킨", 만원);
                OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItem(menu, input);
                final var order = createOrder(OrderType.TAKEOUT, null, null, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

                assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
            }
        }
    }

    @Nested
    @DisplayName("주문 수락")
    class Accepted {
        @Test
        @DisplayName("주문수락이 완료된 경우 주문상태가 대기중 에서 수락(ACCEPTED) 상태로 변경된다.")
        void success() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.accept(order.getId());

            assertAll(
                    "주문 수락 완료 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.ACCEPTED, actual.getStatus())
            );
        }

        @Test
        @DisplayName("배달주문인 경우 배달라이더에게 배달 요청이 완료되어야 수락된다.")
        void success2() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달주소",  menu);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.accept(order.getId());

            assertAll(
                    "배달주문 수락 완료 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.ACCEPTED, actual.getStatus())
            );
        }

        @Test
        @DisplayName("접수된적 없는 주문인 경우 수락이 불가능하다.")
        void fail1() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);

            assertThrows(NoSuchElementException.class, () -> orderService.accept(order.getId()));
        }

        @Test
        @DisplayName("주문상태가 대기중이 아닌 경우 처리가 불가능하다.")
        void fail2() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));
        }
    }

    @Nested
    @DisplayName("음식 제공 완료")
    class Served {
        @Test
        @DisplayName("처리가 되는경우 주문상태가 수락에서 에서 음식제공 완료(SERVED) 로 변경된다.")
        void success() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.serve(order.getId());

            assertAll(
                    "주문 수락 완료 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.SERVED, actual.getStatus())
            );
        }

        @Test
        @DisplayName("접수된적 없는 주문인 경우 처리할 수 없다.")
        void fail1() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);
            order.setStatus(OrderStatus.ACCEPTED);

            assertThrows(NoSuchElementException.class, () -> orderService.accept(order.getId()));
        }

        @Test
        @DisplayName("주문상태가 수락상태가 아닌 경우 처리할 수 없다.")
        void fail2() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));
        }
    }

    @Nested
    @DisplayName("배달 중")
    class Delivering {
        @Test
        @DisplayName("배달이 시작되면 주문 상태는 음식 제공에서 배달중(DELIVERING) 으로 변경된다.")
        void success() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달장소", menu);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.startDelivery(order.getId());

            assertAll(
                    "배달중 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.DELIVERING, actual.getStatus())
            );
        }

        @Test
        @DisplayName("배달주문(DELIVERY)이 아닌 경우 배달을 시작할 수 없다.")
        void fail1() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
        }

        @Test
        @DisplayName("음식이 아직 제공되지 않은 경우(SERVED) 배달을 시작할 수 없다.")
        void fail2() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달장소", menu);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
        }

        @Test
        @DisplayName("접수된적 없는 주문인 경우 배달을 시작할 수 없다.")
        void fail3() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달장소", menu);

            assertThrows(NoSuchElementException.class, () -> orderService.startDelivery(order.getId()));
        }
    }

    @Nested
    @DisplayName("배달 완료")
    class Delivered {
        @Test
        @DisplayName("배달완료상태가 되는 경우 배달 중 에서 배달 완료(DELIVERED) 로 변경된다.")
        void success() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달장소", menu);
            order.setStatus(OrderStatus.DELIVERING);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.completeDelivery(order.getId());

            assertAll(
                    "배달완료 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.DELIVERED, actual.getStatus())
            );
        }

        @Test
        @DisplayName("접수된적 없는 주문인 경우 처리할 수 없다.")
        void fail1() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달장소", menu);

            assertThrows(NoSuchElementException.class, () -> orderService.completeDelivery(order.getId()));
        }

        @Test
        @DisplayName("주문상태가 배달 중이 아니면 배달완료로 처리할 수 없다.")
        void fail2() {
            final var menu = createMenu("치킨", 만원);
            final var order = createDeliveryOrder("배달장소", menu);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(order.getId()));
        }
    }

    @Nested
    @DisplayName("주문 처리 완료")
    class Completed {
        @Test
        @DisplayName("완료 상태가 되면 주문 처리 완료(COMPLETED) 가 된다.")
        void success() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.complete(order.getId());

            assertAll(
                    "주문완료 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.COMPLETED, actual.getStatus())
            );
        }

        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"EAT_IN"})
        @DisplayName("메징식사인 경우 주문상태가 완료가 되면 주문테이블의 손님 수와 착석여부 상태를 빈 좌석으로 정리한다. (손님 수 0명, 미착석 상태로 변경)")
        void success2(OrderType orderType) {
            final var menu = createMenu("치킨", 만원);
            final var orderTable = createSittingTable(2);
            final var order = createEatInOrder(orderTable, menu);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            Order actual = orderService.complete(order.getId());

            assertAll(
                    "매장 주문완료 상태 Assertions",
                    () -> assertNotNull(actual),
                    () -> assertEquals(OrderStatus.COMPLETED, actual.getStatus()),
                    () -> assertEquals(0, order.getOrderTable().getNumberOfGuests()),
                    () -> assertFalse(order.getOrderTable().isOccupied())
            );
        }

        @Test
        @DisplayName("접수된적 없는 주문인 경우 처리할 수 없다.")
        void fail1() {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(OrderType.TAKEOUT, menu);

            assertThrows(NoSuchElementException.class, () -> orderService.complete(order.getId()));
        }

        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"DELIVERY"})
        @DisplayName("배달주문인데 배달 완료 상태가 아니면 처리할 수 없다.")
        void fail2(OrderType orderType) {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(orderType, menu);
            order.setStatus(OrderStatus.DELIVERING);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
        }

        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
        @DisplayName("포장주문이거나 매장식사인 경우 음식 제공 완료 상태가 아니면 처리할 수 없다.")
        void fail3(OrderType orderType) {
            final var menu = createMenu("치킨", 만원);
            final var order = createOrder(orderType, menu);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
        }

    }
}
