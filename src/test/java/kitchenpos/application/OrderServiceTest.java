package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.NAME_반반치킨;
import static kitchenpos.fixture.MenuFixture.PRICE_34000;
import static kitchenpos.fixture.MenuFixture.PRICE_38000;
import static kitchenpos.fixture.MenuFixture.menuResponse;
import static kitchenpos.fixture.MenuGroupFixture.NAME_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupResponse;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.OrderFixture.ORDER_배달주소;
import static kitchenpos.fixture.OrderFixture.orderDeliveryCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderDeliveryResponse;
import static kitchenpos.fixture.OrderFixture.orderEatInCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderEatInResponse;
import static kitchenpos.fixture.OrderFixture.orderTakeOutCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderTakeOutResponse;
import static kitchenpos.fixture.OrderLineItemFixture.orderLineItemCreate;
import static kitchenpos.fixture.OrderTableFixture.NAME_1번;
import static kitchenpos.fixture.OrderTableFixture.orderTableResponse;
import static kitchenpos.fixture.ProductFixture.NAME_양념치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.PRICE_20000;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@DisplayName("주문 서비스 테스트")
@ApplicationMockTest
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;


    @InjectMocks
    private OrderService orderService;

    private UUID orderTableId;
    private OrderTable orderTable;
    private OrderLineItem orderLineItem;
    private Order ORDER_매장주문;
    private Order ORDER_배달주문;
    private Order ORDER_포장주문;
    private Menu menu;

    @BeforeEach
    void setUp() {
        //메뉴
        MenuGroup menuGroup = menuGroupResponse(NAME_추천메뉴);
        Product product1 = productResponse(NAME_양념치킨, PRICE_20000);
        Product product2 = productResponse(NAME_후라이드치킨, PRICE_18000);
        MenuProduct menuProduct_양념치킨 = menuProductResponse(product1, 1);
        MenuProduct menuProduct_후라이드 = menuProductResponse(product2, 1);
        menu = menuResponse(NAME_반반치킨, PRICE_38000, menuGroup.getId(), true, menuProduct_양념치킨, menuProduct_후라이드);
        //주문
        orderTable = orderTableResponse(NAME_1번, 0, true);
        orderTableId = orderTable.getId();
        orderLineItem = orderLineItemCreate(menu, PRICE_38000, 1);
        ORDER_매장주문 = orderEatInResponse(OrderStatus.WAITING, orderTable, orderLineItem);
        ORDER_배달주문 = orderDeliveryResponse(OrderStatus.WAITING, ORDER_배달주소, orderLineItem);
        ORDER_포장주문 = orderTakeOutResponse(OrderStatus.WAITING, orderLineItem);
    }


    @Nested
    @DisplayName("주문등록 테스트")
    class CreateOrder {
        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장 주문을 등록한다.")
            @Test
            void eatInOrder() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
                when(orderRepository.save(any())).thenReturn(ORDER_매장주문);
                Order request = orderEatInCreateRequest(orderTableId, orderLineItem);

                // when
                Order result = orderService.create(request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(result.getDeliveryAddress()).isNull(),
                        () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                        () -> assertThat(result.getOrderTable().isOccupied()).isTrue()
                );
            }

            @DisplayName("주문테이블이 미리 등록되어 있지 않으면 예외가 발생한다.")
            @Test
            void notExistsOrderTableException() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
                Order request = orderEatInCreateRequest(orderTableId, orderLineItem);

                // when
                when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문테이블 사용여부를 미리 사용중으로 하지않으면 예외가 발생한다.")
            @Test
            void notOccupiedOrderTableException() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
                Order request = orderEatInCreateRequest(orderTableId, orderLineItem);

                // when
                orderTable.setOccupied(false);
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

                // then
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달 주문을 등록한다.")
            @Test
            void deliveryOrder() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
                when(orderRepository.save(any())).thenReturn(ORDER_배달주문);
                Order request = orderDeliveryCreateRequest(ORDER_배달주소, orderLineItem);

                // when
                Order result = orderService.create(request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(result.getDeliveryAddress()).isEqualTo(ORDER_배달주소),
                        () -> assertThat(result.getOrderTable()).isNull()
                );
            }

            @DisplayName("배달 주소 정보가 없으면 예외가 발생한다.")
            @NullAndEmptySource
            @ParameterizedTest
            void nullOrEmptyAddressException(String deliveryAddress) {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

                // when
                Order request = orderDeliveryCreateRequest(deliveryAddress, orderLineItem);

                // then
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
                when(orderRepository.save(any())).thenReturn(ORDER_포장주문);
                Order request = orderTakeOutCreateRequest(orderLineItem);

                // when
                Order result = orderService.create(request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(result.getDeliveryAddress()).isNull(),
                        () -> assertThat(result.getOrderTable()).isNull()
                );
            }
        }

        @DisplayName("주문종류를 지정해주지 않으면 예외 발생한다.")
        @Test
        void nullOrderTypeException() {
            // given
            Order order = new Order();

            // when
            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }


        @DisplayName("주문메뉴항목이 1개 이하이면 예외 발생한다.")
        @Test
        void nullAndEmptyOrderLineItemException() {
            // given
            Order order = new Order();
            order.setType(OrderType.TAKEOUT);

            // when
            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문메뉴항목은 같은 메뉴가 중복해서 들어오면 예외 발생한다.")
        @Test
        void duplicatedOrderLineItemException() {
            // given
            Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setOrderLineItems(List.of(orderLineItem));

            // when
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of());

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장주문 제외하고, 주문메뉴항목의 수량이 0보다 작으면 예외 발생한다.")
        @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = {"EAT_IN"})
        @ParameterizedTest
        void noEatInType_orderLineItemsLessThanZeroException(OrderType type) {
            // given
            Order order = new Order();
            order.setType(type);
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

            // when
            orderLineItem.setQuantity(-1);
            order.setOrderLineItems(List.of(orderLineItem));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문메뉴항목이 미리 등록된 메뉴가 아니면 예외 발생한다.")
        @Test
        void notExistsOrderLineItem() {
            // given
            Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setOrderLineItems(List.of(orderLineItem));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

            // when
            when(menuRepository.findById(any())).thenReturn(Optional.empty());


            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문메뉴항목의 메뉴가 노출안함 상태이면 예외가 발생한다.")
        @Test
        void noDisplayedMenuException() {
            // given
            Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setOrderLineItems(List.of(orderLineItem));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

            // when
            menu.setDisplayed(false);
            when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문메뉴항목의 메뉴과 원래 메뉴의 가격이 다르면 예외가 발생한다.")
        @Test
        void differentMenuPriceException() {
            // given
            Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setOrderLineItems(List.of(orderLineItem));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

            // when
            menu.setPrice(PRICE_34000);
            when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("주문수락 테스트")
    class AcceptOrder {
        @Nested
        @DisplayName("매장주문")
        class EatIn {
            @DisplayName("매장주문 접수를 수락한다.")
            @Test
            void eatInOrder() {
                // given
                UUID orderId = ORDER_매장주문.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_매장주문));

                // when
                Order result = orderService.accept(orderId);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(orderId),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
                );
            }
        }

        @Nested
        @DisplayName("배달주문 접수를 수락한다.")
        class Delivery {

            @DisplayName("배달주문")
            @Test
            void deliveryOrder() {
                // given
                UUID orderId = ORDER_배달주문.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_배달주문));
                doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());
                // when
                Order result = orderService.accept(orderId);
                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(orderId),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
                );
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문 접수를 시작한다.")
            @Test
            void takeOutOrder() {
                // given
                UUID orderId = ORDER_포장주문.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_포장주문));
                // when
                Order result = orderService.accept(orderId);
                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(orderId),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
                );
            }
        }

        @DisplayName("미리 등록되어있는 주문이 아니면 예외가 발생한다.")
        @Test
        void notExistsOrderException() {
            // given
            UUID orderId = ORDER_포장주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("현재 주문상태는 `수락대기`가 아니면 예외가 발생한다.")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"WAITING"})
        @ParameterizedTest
        void notWaitingStatusException(OrderStatus status) {
            // given
            Order order = ORDER_포장주문;
            UUID orderId = order.getId();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setStatus(status);

            // then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("제조완료 테스트")
    class ServedOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문을 제조완료한다.")
            @Test
            void eatInOrder() {
                // given
                ORDER_매장주문.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_매장주문));

                // when
                Order result = orderService.serve(ORDER_매장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
                );
            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문을 제조완료한다.")
            @Test
            void deliveryOrder() {
                // given
                ORDER_배달주문.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_배달주문));

                // when
                Order result = orderService.serve(ORDER_배달주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
                );
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문을 제조완료한다.")
            @Test
            void takeOutOrder() {
                // given
                ORDER_포장주문.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_포장주문));

                // when
                Order result = orderService.serve(ORDER_포장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
                );
            }
        }

        @DisplayName("미리 등록되어있는 주문이 아니면 예외가 발생한다.")
        @Test
        void notExistsOrderException() {
            // given
            UUID orderId = ORDER_포장주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("현재 주문상태는 `주문수락`이 아니면 예외가 발생한다.")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"ACCEPTED"})
        @ParameterizedTest
        void notWaitingStatusException(OrderStatus status) {
            // given
            Order order = ORDER_포장주문;
            UUID orderId = order.getId();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setStatus(status);

            // then
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("배달 테스트")
    class DeliveryOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문")
            @Test
            void eatInOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문을 시작한다.")
            @Test
            void startDeliveryOrder() {
                // given
                ORDER_배달주문.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_배달주문));

                // when
                Order result = orderService.startDelivery(ORDER_배달주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING)
                );
            }

            @DisplayName("배달주문을 완료한다.")
            @Test
            void completeDeliveryOrder() {
                // given
                ORDER_배달주문.setStatus(OrderStatus.DELIVERING);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_배달주문));

                // when
                Order result = orderService.completeDelivery(ORDER_배달주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED)
                );
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given

                // when

                // then

            }
        }
    }

    @Nested
    @DisplayName("주문종료 테스트")
    class CompleteOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문을 종료합니다.")
            @Test
            void eatInOrder() {
                // given
                ORDER_매장주문.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_매장주문));
                when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                        .thenReturn(false);

                // when
                Order result = orderService.complete(ORDER_매장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                        () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                        () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
                );
            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문을 종료합니다.")
            @Test
            void deliveryOrder() {
                // given
                ORDER_배달주문.setStatus(OrderStatus.DELIVERED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_배달주문));

                // when
                Order result = orderService.complete(ORDER_배달주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문을 종료합니다.")
            @Test
            void takeOutOrder() {
                // given
                ORDER_포장주문.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(ORDER_포장주문));

                // when
                Order result = orderService.complete(ORDER_포장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }
        }
    }
}
