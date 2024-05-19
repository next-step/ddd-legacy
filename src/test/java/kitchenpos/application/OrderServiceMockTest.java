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

import static kitchenpos.fixture.MenuFixture.menuResponse;
import static kitchenpos.fixture.MenuFixture.가격_34000;
import static kitchenpos.fixture.MenuFixture.가격_38000;
import static kitchenpos.fixture.MenuFixture.이름_반반치킨;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupResponse;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.OrderFixture.orderDeliveryCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderDeliveryResponse;
import static kitchenpos.fixture.OrderFixture.orderEatInCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderEatInResponse;
import static kitchenpos.fixture.OrderFixture.orderTakeOutCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderTakeOutResponse;
import static kitchenpos.fixture.OrderFixture.배달주소;
import static kitchenpos.fixture.OrderLineItemFixture.orderLineItemCreate;
import static kitchenpos.fixture.OrderTableFixture.orderTableResponse;
import static kitchenpos.fixture.OrderTableFixture.이름_1번;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@DisplayName("주문 서비스 테스트")
@ApplicationMockTest
public class OrderServiceMockTest {
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

    private UUID ID_1번테이블;
    private OrderTable 주문테이블_1번;
    private OrderLineItem 주문메뉴항목;
    private Order 매장주문;
    private Order 배달주문;
    private Order 포장주문;
    private Menu 메뉴_반반치킨;

    @BeforeEach
    void setUp() {
        //메뉴
        MenuGroup menuGroup = menuGroupResponse(이름_추천메뉴);
        Product 양념치킨 = productResponse(이름_양념치킨, 가격_20000);
        Product 후라이드치킨 = productResponse(이름_후라이드치킨, 가격_18000);
        MenuProduct 메뉴구성상품_양념치킨 = menuProductResponse(양념치킨, 1);
        MenuProduct 메뉴구성상품_후라이드 = menuProductResponse(후라이드치킨, 1);
        메뉴_반반치킨 = menuResponse(이름_반반치킨, 가격_38000, menuGroup.getId(), true, 메뉴구성상품_양념치킨, 메뉴구성상품_후라이드);
        주문테이블_1번 = orderTableResponse(이름_1번, 0, true);
        ID_1번테이블 = 주문테이블_1번.getId();
        주문메뉴항목 = orderLineItemCreate(메뉴_반반치킨, 가격_38000, 1);
        매장주문 = orderEatInResponse(OrderStatus.WAITING, 주문테이블_1번, 주문메뉴항목);
        배달주문 = orderDeliveryResponse(OrderStatus.WAITING, 배달주소, 주문메뉴항목);
        포장주문 = orderTakeOutResponse(OrderStatus.WAITING, 주문메뉴항목);
    }


    @DisplayName("주문을 등록한다.")
    @Nested
    class CreateOrder {
        @DisplayName("매장주문")
        @Nested
        class EatIn {
            @DisplayName("[성공] 등록")
            @Test
            void success1() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));
                when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_반반치킨));
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(주문테이블_1번));
                when(orderRepository.save(any())).thenReturn(매장주문);
                Order request = orderEatInCreateRequest(ID_1번테이블, 주문메뉴항목);

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

            @DisplayName("[실패] 미리 `주문테이블`이 등록되어 있어야한다.")
            @Test
            void fail1() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));
                when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_반반치킨));
                Order request = orderEatInCreateRequest(ID_1번테이블, 주문메뉴항목);

                // when
                when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("[실패] `주문 테이블`은 사용 중이어야 한다.")
            @Test
            void fail2() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));
                when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_반반치킨));
                Order request = orderEatInCreateRequest(ID_1번테이블, 주문메뉴항목);

                // when
                주문테이블_1번.setOccupied(false);
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(주문테이블_1번));

                // then
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("배달주문")
        @Nested
        class Delivery {
            @DisplayName("[성공] 등록")
            @Test
            void success() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));
                when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_반반치킨));
                when(orderRepository.save(any())).thenReturn(배달주문);
                Order request = orderDeliveryCreateRequest(배달주소, 주문메뉴항목);

                // when
                Order result = orderService.create(request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(result.getDeliveryAddress()).isEqualTo(배달주소),
                        () -> assertThat(result.getOrderTable()).isNull()
                );
            }

            @DisplayName("[실패] 배달주소는 1자 이상이어야 한다.")
            @NullAndEmptySource
            @ParameterizedTest
            void fail1(String deliveryAddress) {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));
                when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_반반치킨));

                // when
                Order request = orderDeliveryCreateRequest(deliveryAddress, 주문메뉴항목);

                // then
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("포장주문")
        @Nested
        class TakeOut {
            @DisplayName("[성공] 등록")
            @Test
            void success() {
                // given
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));
                when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_반반치킨));
                when(orderRepository.save(any())).thenReturn(포장주문);
                Order request = orderTakeOutCreateRequest(주문메뉴항목);

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

        @DisplayName("[실패] 주문종류는 반드시 지정해줘야 한다.")
        @Test
        void fail1() {
            // given
            Order order = new Order();

            // when
            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }


        @DisplayName("[실패] `주문메뉴항목`은 `1`개이상 포함한다.")
        @Test
        void fail2() {
            // given
            Order order = new Order();
            order.setType(OrderType.TAKEOUT);

            // when
            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] `주문메뉴항목`은 `메뉴`를 중복해서 넣지 않는다.")
        @Test
        void fail3() {
            // given
            Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setOrderLineItems(List.of(주문메뉴항목));

            // when
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of());

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] 주문종류가 매장주문이 아니면 해당 `메뉴`의 수량은 `0`이상이어야 한다.")
        @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = {"EAT_IN"})
        @ParameterizedTest
        void fail4(OrderType type) {
            // given
            Order order = new Order();
            order.setType(type);
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));

            // when
            주문메뉴항목.setQuantity(-1);
            order.setOrderLineItems(List.of(주문메뉴항목));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] `주문메뉴항목`은 이미 만들어진 `메뉴`에서 선택한다.")
        @Test
        void fail5() {
            // given
            Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setOrderLineItems(List.of(주문메뉴항목));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));

            // when
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] `메뉴`는 노출된 상태여야 한다.")
        @Test
        void fail6() {
            // given
            Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setOrderLineItems(List.of(주문메뉴항목));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));

            // when
            메뉴_반반치킨.setDisplayed(false);
            when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(메뉴_반반치킨));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[실패] 각각의 {원래 `메뉴`의 가격}이 {현재 주문 시 요청한 메뉴의 가격}과 같아야 한다.")
        @Test
        void fail7() {
            // given
            Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setOrderLineItems(List.of(주문메뉴항목));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(메뉴_반반치킨));

            // when
            메뉴_반반치킨.setPrice(가격_34000);
            when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(메뉴_반반치킨));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("주문 접수를 수락한다.")
    @Nested
    class AcceptOrder {
        @DisplayName("매장주문")
        @Nested
        class EatIn {
            @DisplayName("[성공] 접수수락")
            @Test
            void success() {
                // given
                UUID orderId = 매장주문.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(매장주문));

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

        @DisplayName("배달주문")
        @Nested
        class Delivery {
            @DisplayName("[성공] 접수수락")
            @Test
            void success() {
                // given
                UUID orderId = 배달주문.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(배달주문));
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
            @DisplayName("[성공] 접수수락")
            @Test
            void success() {
                // given
                UUID orderId = 포장주문.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(포장주문));
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

        @DisplayName("[실패] 미 등록되어있는 `주문`이어야 한다.")
        @Test
        void fail1() {
            // given
            UUID orderId = 포장주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] 현재 주문 상태는 **수락대기** 상태여야 한다.")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"WAITING"})
        @ParameterizedTest
        void fail2(OrderStatus status) {
            // given
            Order order = 포장주문;
            UUID orderId = order.getId();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setStatus(status);

            // then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("매장주문을 제조완료한다.")
    @Nested
    class ServedOrder {
        @DisplayName("매장주문")
        @Nested
        class EatIn {

            @DisplayName("[성공] 제조완료")
            @Test
            void eatInOrder() {
                // given
                매장주문.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(매장주문));

                // when
                Order result = orderService.serve(매장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
                );
            }
        }

        @DisplayName("배달주문")
        @Nested
        class Delivery {
            @DisplayName("[성공] 제조완료")
            @Test
            void success() {
                // given
                배달주문.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(배달주문));

                // when
                Order result = orderService.serve(배달주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
                );
            }
        }

        @DisplayName("포장주문")
        @Nested
        class TakeOut {
            @DisplayName("[성공] 제조완료")
            @Test
            void success() {
                // given
                포장주문.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(포장주문));

                // when
                Order result = orderService.serve(포장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
                );
            }
        }

        @DisplayName("[실패] 이미 등록되어있는 `주문`이어야 한다.")
        @Test
        void fail1() {
            // given
            UUID orderId = 포장주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] 현재 주문 상태는 **주문수락** 상태여야 한다.")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"ACCEPTED"})
        @ParameterizedTest
        void fail2(OrderStatus status) {
            // given
            Order order = 포장주문;
            UUID orderId = order.getId();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setStatus(status);

            // then
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @DisplayName("배달주문을 시작한다.")
    @Nested
    class DeliveringOrder {
        @DisplayName("[성공] 배달주문중")
        @Test
        void success() {
            // given
            배달주문.setStatus(OrderStatus.SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(배달주문));

            // when
            Order result = orderService.startDelivery(배달주문.getId());

            // then
            assertAll(
                    () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING)
            );
        }

        @DisplayName("[실패] 주문종류는 **배달주문**이여야 한다.")
        @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERY"})
        @ParameterizedTest
        void fail1(OrderType type) {
            // given
            Order order = 포장주문;
            UUID orderId = order.getId();
            order.setStatus(OrderStatus.SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setType(type);

            // then
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[실패] 현재 주문 상태는 **제조완료** 상태여야 한다.")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
        @ParameterizedTest
        void fail2(OrderStatus status) {
            // given
            Order order = 배달주문;
            UUID orderId = order.getId();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setStatus(status);

            // then
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[실패] 이미 등록되어있는 `주문`이어야 한다.")
        @Test
        void fail3() {
            // given
            UUID orderId = 배달주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }


    @DisplayName("배달주문을 완료한다.")
    @Nested
    class DeliveredOrder {
        @DisplayName("[성공] 배달완료")
        @Test
        void success() {
            // given
            배달주문.setStatus(OrderStatus.DELIVERING);
            when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(배달주문));

            // when
            Order result = orderService.completeDelivery(배달주문.getId());

            // then
            assertAll(
                    () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED)
            );
        }

        @DisplayName("[실패] 이미 등록되어있는 `주문`이어야 한다.")
        @Test
        void fail1() {
            // given
            UUID orderId = 배달주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] 현재 주문 상태는 **배달중** 상태여야 한다.")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERING"})
        @ParameterizedTest
        void fail2(OrderStatus status) {
            // given
            Order order = 배달주문;
            UUID orderId = order.getId();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            order.setStatus(status);

            // then
            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }


    @DisplayName("주문을 종료합니다.")
    @Nested
    class CompleteOrder {
        @DisplayName("매장주문")
        @Nested
        class EatIn {
            @DisplayName("[성공] 주문종료")
            @Test
            void success() {

                // given
                매장주문.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(매장주문));
                when(orderRepository.existsByOrderTableAndStatusNot(주문테이블_1번, OrderStatus.COMPLETED))
                        .thenReturn(false);

                // when
                Order result = orderService.complete(매장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                        () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                        () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
                );
            }

            @DisplayName("[실패] 현재 주문 상태는 **제조완료** 상태이다.")
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
            @ParameterizedTest
            void fail1(OrderStatus status) {
                // given
                Order order = 매장주문;
                UUID orderId = order.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.of(order));

                // when
                order.setStatus(status);

                // then
                assertThatThrownBy(() -> orderService.complete(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {
            @DisplayName("[성공] 주문종료")
            @Test
            void success() {
                // given
                배달주문.setStatus(OrderStatus.DELIVERED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(배달주문));

                // when
                Order result = orderService.complete(배달주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }

            @DisplayName("[실패] 현재 주문 상태는 **배달완료** 상태이다.")
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERED"})
            @ParameterizedTest
            void fail1(OrderStatus status) {
                // given
                Order order = 배달주문;
                UUID orderId = order.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.of(order));

                // when
                order.setStatus(status);

                // then
                assertThatThrownBy(() -> orderService.complete(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {
            @DisplayName("[성공] 주문종료")
            @Test
            void success() {
                // given
                포장주문.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(포장주문));

                // when
                Order result = orderService.complete(포장주문.getId());

                // then
                assertAll(
                        () -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }

            @DisplayName("[실패] 현재 주문 상태는 **제조완료** 상태이다.")
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
            @ParameterizedTest
            void fail1(OrderStatus status) {
                // given
                Order order = 포장주문;
                UUID orderId = order.getId();
                when(orderRepository.findById(any())).thenReturn(Optional.of(order));

                // when
                order.setStatus(status);

                // then
                assertThatThrownBy(() -> orderService.complete(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("[실패] 이미 등록되어있는 `주문`이어야 한다.")
        @Test
        void notExistsOrderException() {
            // given
            UUID orderId = 배달주문.getId();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.complete(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
