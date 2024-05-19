package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.application.OrderFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static kitchenpos.fixture.application.MenuFixture.전시_메뉴;
import static kitchenpos.fixture.application.MenuFixture.커플_강정_후라이드_메뉴;
import static kitchenpos.fixture.application.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.application.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.application.OrderFixture.*;
import static kitchenpos.fixture.application.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.application.OrderTableFixture.테이블_생성;
import static kitchenpos.fixture.application.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private Product 후라이드_치킨_상품;
    private Product 강정_치킨_상품;
    private MenuGroup 커플메뉴_메뉴그룹;
    private MenuProduct 후라이드_치킨_메뉴상품;
    private MenuProduct 강정_치킨_메뉴상품;
    private Menu 커플_강정_후라이드_메뉴;

    private OrderTable 테이블_1번;

    private OrderLineItem 커플_강정_후라이드_메뉴_주문서;
    private Order 매장식사_주문_요청서;
    private Order 매장식사_주문_결과;
    private UUID 매장식사_주문_결과_ID;

    private Order 배달_주문_요청서;
    private Order 배달_주문_결과;
    private UUID 배달_주문_결과_ID;

    private Order 포장_주문_요청서;
    private Order 포장_주문_결과;
    private UUID 포장_주문_결과_ID;

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

    @BeforeEach
    void setUp() {
        후라이드_치킨_상품 = createProduct("후라이드 치킨", BigDecimal.valueOf(12_000));
        강정_치킨_상품 = createProduct("강정 치킨", BigDecimal.valueOf(15_000));
        커플메뉴_메뉴그룹 = createMenuGroup("커플 메뉴");
        후라이드_치킨_메뉴상품 = createMenuProduct(후라이드_치킨_상품, 1);
        강정_치킨_메뉴상품 = createMenuProduct(강정_치킨_상품, 1);
        커플_강정_후라이드_메뉴 = 커플_강정_후라이드_메뉴(커플메뉴_메뉴그룹, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);

        테이블_1번 = 테이블_생성(true);
        커플_강정_후라이드_메뉴_주문서 = createOrderLineItem(커플_강정_후라이드_메뉴.getId(), 커플_강정_후라이드_메뉴);

        매장식사_주문_요청서 = createEatInOrderRequest(테이블_1번, 테이블_1번.getId(), 커플_강정_후라이드_메뉴_주문서);
        매장식사_주문_결과 = createEatInOrderResponse(테이블_1번, 테이블_1번.getId(), 커플_강정_후라이드_메뉴_주문서);
        매장식사_주문_결과_ID = 매장식사_주문_결과.getId();

        배달_주문_요청서 = createDeliveryOrderRequest("지구가 아닌 어딘가", 커플_강정_후라이드_메뉴_주문서);
        배달_주문_결과 = createDeliveryOrderResponse("지구가 아닌 어딘가", 커플_강정_후라이드_메뉴_주문서);
        배달_주문_결과_ID = 배달_주문_결과.getId();

        포장_주문_요청서 = createTakeOutOrderRequest(커플_강정_후라이드_메뉴_주문서);
        포장_주문_결과 = createTakeOutOrderResponse(커플_강정_후라이드_메뉴_주문서);
        포장_주문_결과_ID = 포장_주문_결과.getId();
    }

    @Nested
    @DisplayName("주문 등록")
    class OrderCreate {
        @Nested
        @DisplayName("매장, 배달, 포장 실패")
        class OrderCreateFail {
            private static Stream<List<OrderLineItem>> provideOrderLineItem() {
                return Stream.of(
                        Collections.emptyList(),
                        null
                );
            }

            @Test
            @DisplayName("주문의 타입은 null이 될 수 없다.")
            void fail1() {
                Order request = OrderFixture.createOrderRequest(null, OrderStatus.WAITING, List.of(커플_강정_후라이드_메뉴_주문서));

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @ParameterizedTest
            @DisplayName("메뉴 주문서는 null 이거나 비어있을 수 없다.")
            @MethodSource("provideOrderLineItem")
            void fail2(List<OrderLineItem> items) {
                Order request = OrderFixture.createOrderRequest(OrderType.EAT_IN, OrderStatus.WAITING, items);

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문서에 동일한 메뉴를 담을 수 없다")
            void fail3() {
                when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

                assertThatThrownBy(() -> orderService.create(매장식사_주문_요청서))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문 요청서에 있는 메뉴가 매장에 등록되어 있어야 한다.")
            void fail4() {
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                when(menuRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.create(매장식사_주문_요청서))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("주문 요청서에 있는 메뉴는 전시 되어 있어야 한다")
            void fail5() {
                Menu 미전시_메뉴 = 전시_메뉴(false);
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(미전시_메뉴));
                when(menuRepository.findById(any())).thenReturn(Optional.of(미전시_메뉴));

                assertThatThrownBy(() -> orderService.create(매장식사_주문_요청서))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("주문 요청서에 있는 메뉴의 가격과 등록되어 있는 메뉴의 가격은 동일해야 한다")
            void fail6() {
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                when(menuRepository.findById(any())).thenReturn(Optional.of(커플_강정_후라이드_메뉴));
                Order request = OrderFixture.createOrderRequest(OrderType.EAT_IN, OrderStatus.WAITING, List.of(createOrderLineItem(50_00)));

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("매장 주문")
        class EatInOrder {
            @Nested
            @DisplayName("성공")
            class EatInOrderSuccess {
                @Test
                @DisplayName("매장 주문을 생성한다")
                void eatInOrder() {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    when(menuRepository.findById(any())).thenReturn(Optional.of(커플_강정_후라이드_메뉴));
                    when(orderTableRepository.findById(테이블_1번.getId())).thenReturn(Optional.of(테이블_1번));
                    when(orderRepository.save(any())).thenReturn(매장식사_주문_결과);

                    Order result = orderService.create(매장식사_주문_요청서);

                    assertAll(
                            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                            () -> assertThat(result.getType()).isEqualTo(매장식사_주문_요청서.getType()),
                            () -> assertThat(result.getOrderLineItems()).containsOnly(커플_강정_후라이드_메뉴_주문서),
                            () -> assertThat(result.getOrderTable().isOccupied()).isTrue(),
                            () -> assertThat(result.getOrderTable()).isEqualTo(테이블_1번)
                    );
                }
            }

            @Nested
            @DisplayName("실패")
            class EatInOrderFail {
                @Test
                @DisplayName("주문 테이블은 미리 등록 되어 있어야 한다.")
                void orderTableIsAlreadyRegistered() {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    when(menuRepository.findById(any())).thenReturn(Optional.of(커플_강정_후라이드_메뉴));
                    when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

                    assertThatThrownBy(() -> orderService.create(매장식사_주문_요청서))
                            .isInstanceOf(NoSuchElementException.class);
                }

                @Test
                @DisplayName("손님이 테이블을 사용중이어야 한다.")
                void orderTableIsOccupied() {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    when(menuRepository.findById(any())).thenReturn(Optional.of(커플_강정_후라이드_메뉴));
                    when(orderTableRepository.findById(any())).thenReturn(Optional.of(테이블_생성(false)));

                    assertThatThrownBy(() -> orderService.create(매장식사_주문_요청서))
                            .isInstanceOf(IllegalStateException.class);
                }
            }
        }

        @Nested
        @DisplayName("배달 주문")
        class DeliveryOrder {
            @Nested
            @DisplayName("성공")
            class DeliveryOrderSuccess {
                @Test
                @DisplayName("배달 주문을 생성 한다")
                void deliveryOrder() {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    when(menuRepository.findById(any())).thenReturn(Optional.of(커플_강정_후라이드_메뉴));
                    when(orderRepository.save(any())).thenReturn(배달_주문_결과);

                    Order result = orderService.create(배달_주문_요청서);

                    assertAll(
                            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                            () -> assertThat(result.getType()).isEqualTo(배달_주문_요청서.getType()),
                            () -> assertThat(result.getOrderLineItems()).containsOnly(커플_강정_후라이드_메뉴_주문서),
                            () -> assertThat(result.getDeliveryAddress()).isEqualTo(배달_주문_요청서.getDeliveryAddress())
                    );
                }
            }

            @Nested
            @DisplayName("실패")
            class DeliveryOrderFail {
                @ParameterizedTest
                @ValueSource(longs = {-1, -10})
                @DisplayName("주문 상품의 갯수는 음수 일 수 없다.")
                void orderLineItemQuantityIsNotNegativeNumber(long quantity) {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    Order request = createOrderRequest(OrderType.DELIVERY, OrderStatus.WAITING, List.of(createOrderLineItem(quantity)));

                    assertThatThrownBy(() -> orderService.create(request))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }
        }

        @Nested
        @DisplayName("포장 주문")
        class TakeoutOrder {
            @Nested
            @DisplayName("성공")
            class TakeoutOrderSuccess {
                @Test
                @DisplayName("포장 주문을 생성 한다")
                void deliveryOrder() {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    when(menuRepository.findById(any())).thenReturn(Optional.of(커플_강정_후라이드_메뉴));
                    when(orderRepository.save(any())).thenReturn(포장_주문_결과);

                    Order result = orderService.create(포장_주문_요청서);

                    assertAll(
                            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                            () -> assertThat(result.getType()).isEqualTo(포장_주문_요청서.getType()),
                            () -> assertThat(result.getOrderLineItems()).containsOnly(커플_강정_후라이드_메뉴_주문서),
                            () -> assertThat(result.getDeliveryAddress()).isEqualTo(포장_주문_요청서.getDeliveryAddress())
                    );
                }
            }

            @Nested
            @DisplayName("실패")
            class TakeoutOrderFail {
                @ParameterizedTest
                @ValueSource(longs = {-1, -10})
                @DisplayName("주문 상품의 갯수는 음수 일 수 없다.")
                void orderLineItemQuantityIsNotNegativeNumber(long quantity) {
                    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(커플_강정_후라이드_메뉴));
                    Order request = createOrderRequest(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(createOrderLineItem(quantity)));

                    assertThatThrownBy(() -> orderService.create(request))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }
        }
    }

    @Nested
    @DisplayName("주문 accept")
    class OrderAccept {
        @Nested
        @DisplayName("매장, 배달, 포장 실패")
        class OrderAcceptFail {
            private static Stream<OrderStatus> provideInvalidOrderStatus() {
                return Stream.of(
                        OrderStatus.ACCEPTED,
                        OrderStatus.SERVED,
                        OrderStatus.DELIVERING,
                        OrderStatus.DELIVERED,
                        OrderStatus.COMPLETED
                );
            }

            @Test
            @DisplayName("주문이 등록되어 있어야 한다.")
            void fail1() {
                when(orderRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.accept(매장식사_주문_결과_ID))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @ParameterizedTest
            @MethodSource("provideInvalidOrderStatus")
            @DisplayName("등록되어 있는 주문의 상태는 WAITING 이어야 한다")
            void fail2(OrderStatus status) {
                when(orderRepository.findById(any())).thenReturn(Optional.of(createOrderResponse(status)));

                assertThatThrownBy(() -> orderService.accept(매장식사_주문_결과_ID))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("매장 주문")
        class EatInOrder {
            @Test
            @DisplayName("매장 주문을 accept 한다")
            void eatInOrderAccept() {
                when(orderRepository.findById(any())).thenReturn(Optional.of(매장식사_주문_결과));

                Order result = orderService.accept(매장식사_주문_결과_ID);

                assertThat(result.getId()).isEqualTo(매장식사_주문_결과_ID);
                assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
                assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }
        }

        @Nested
        @DisplayName("배달 주문")
        class DeliveryOrder {
            @Test
            @DisplayName("배달 주문을 accept 한다")
            void eatInOrderAccept() {
                when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                Order result = orderService.accept(배달_주문_결과_ID);

                assertThat(result.getId()).isEqualTo(배달_주문_결과_ID);
                assertThat(result.getDeliveryAddress()).isEqualTo(배달_주문_결과.getDeliveryAddress());
                assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
                assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOutOrder {
            @Test
            @DisplayName("포장 주문을 accept 한다")
            void eatInOrderAccept() {
                when(orderRepository.findById(any())).thenReturn(Optional.of(포장_주문_결과));

                Order result = orderService.accept(포장_주문_결과_ID);

                assertThat(result.getId()).isEqualTo(포장_주문_결과_ID);
                assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
                assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }
        }
    }

    @Nested
    @DisplayName("주문 served")
    class OrderServed {
        @Nested
        @DisplayName("매장, 배달, 포장 실패")
        class OrderServedFail {
            private static Stream<OrderStatus> provideInvalidOrderStatus() {
                return Stream.of(
                        OrderStatus.WAITING,
                        OrderStatus.SERVED,
                        OrderStatus.DELIVERING,
                        OrderStatus.DELIVERED,
                        OrderStatus.COMPLETED
                );
            }

            @Test
            @DisplayName("주문이 등록되어 있어야 한다.")
            void fail1() {
                when(orderRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.serve(매장식사_주문_결과_ID))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @ParameterizedTest
            @MethodSource("provideInvalidOrderStatus")
            @DisplayName("등록되어 있는 주문의 상태는 ACCEPTED 이어야 한다")
            void fail2(OrderStatus status) {
                when(orderRepository.findById(any())).thenReturn(Optional.of(createOrderResponse(status)));

                assertThatThrownBy(() -> orderService.serve(매장식사_주문_결과_ID))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("매장 주문")
        class EatInOrder {
            @Test
            @DisplayName("매장 주문 손님에게 음식을 제공한다")
            void eatInOrderServed() {
                매장식사_주문_결과.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(매장식사_주문_결과));

                Order result = orderService.serve(매장식사_주문_결과_ID);

                assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
                assertThat(result.getId()).isEqualTo(매장식사_주문_결과_ID);
                assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
            }
        }

        @Nested
        @DisplayName("배달 주문")
        class DeliveryOrder {
            @Test
            @DisplayName("배달기사님에게 음식을 제공한다")
            void eatInOrderServed() {
                배달_주문_결과.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                Order result = orderService.serve(배달_주문_결과_ID);

                assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
                assertThat(result.getId()).isEqualTo(배달_주문_결과_ID);
                assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
            }
        }

        @Nested
        @DisplayName("포장 주문")
        class TakeoutOrder {
            @Test
            @DisplayName("포장 주문 손님에게 음식을 제공한다")
            void eatInOrderServed() {
                포장_주문_결과.setStatus(OrderStatus.ACCEPTED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(포장_주문_결과));

                Order result = orderService.serve(포장_주문_결과_ID);

                assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
                assertThat(result.getId()).isEqualTo(배달_주문_결과_ID);
                assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
            }
        }
    }

    @Nested
    @DisplayName("배달 delivering")
    class StartDelivery {
        @Nested
        @DisplayName("실패")
        class StartDeliveryFail {
            @Test
            @DisplayName("주문이 등록되어 있어야 한다.")
            void fail1() {
                when(orderRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.startDelivery(배달_주문_결과_ID))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("주문의 타입은 `배달`이어야 한다")
            void fail2() {
                when(orderRepository.findById(any())).thenReturn(Optional.of(매장식사_주문_결과));

                assertThatThrownBy(() -> orderService.startDelivery(매장식사_주문_결과_ID))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("주문의 상태은 `배달기사님에게 음식 제공됨`이어야 한다")
            void fail3() {
                when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                assertThatThrownBy(() -> orderService.startDelivery(배달_주문_결과_ID))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("성공")
        class StartDeliverySuccess {
            @Test
            @DisplayName("배달을 시작한다")
            void startDelivery() {
                배달_주문_결과.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                Order result = orderService.startDelivery(배달_주문_결과_ID);

                assertThat(result.getId()).isEqualTo(배달_주문_결과_ID);
                assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
            }
        }
    }

    @Nested
    @DisplayName("배달 complete")
    class ComplteDelivery {
        @Nested
        @DisplayName("실패")
        class CompleteDeliveryFail {
            @Test
            @DisplayName("주문이 등록되어 있어야 한다.")
            void fail1() {
                when(orderRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.completeDelivery(배달_주문_결과_ID))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("주문의 상태는 `배달시작`이어야 한다")
            void fail2() {
                when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                assertThatThrownBy(() -> orderService.completeDelivery(배달_주문_결과_ID))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("성공")
        class CompleteDeliverySuccess {
            @Test
            @DisplayName("배달을 완료한다")
            void completeDelivery() {
                배달_주문_결과.setStatus(OrderStatus.DELIVERING);
                when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                Order result = orderService.completeDelivery(배달_주문_결과_ID);

                assertThat(result.getId()).isEqualTo(배달_주문_결과_ID);
                assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            }
        }
    }

    @Nested
    @DisplayName("주문 complete")
    class OrderComplete {
        @Nested
        @DisplayName("매장, 배달, 포장 실패")
        class OrderCompleteFail {
            @Test
            @DisplayName("실패 - 주문이 등록되어 있어야 한다.")
            void fail1() {
                when(orderRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.complete(매장식사_주문_결과_ID))
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("매장 주문")
        class EatInOrder {
            @Nested
            @DisplayName("실패")
            class EatInOrderCompleteFail {
                private static Stream<OrderStatus> 음식제조완료가아닌상태() {
                    return Stream.of(
                            OrderStatus.WAITING,
                            OrderStatus.ACCEPTED,
                            OrderStatus.DELIVERING,
                            OrderStatus.DELIVERED,
                            OrderStatus.COMPLETED
                    );
                }

                @ParameterizedTest
                @MethodSource("음식제조완료가아닌상태")
                @DisplayName("실패 - 등록되어 있는 주문의 상태는 SERVED 이어야 한다")
                void fail1(OrderStatus status) {
                    매장식사_주문_결과.setStatus(status);
                    when(orderRepository.findById(any())).thenReturn(Optional.of(매장식사_주문_결과));

                    assertThatThrownBy(() -> orderService.complete(매장식사_주문_결과_ID))
                            .isInstanceOf(IllegalStateException.class);
                }
            }

            @Nested
            @DisplayName("성공")
            class EatInOrderCompleteSuccess {
                @Test
                @DisplayName("매장식사 주문을 완료한다")
                void complteEatInOrder() {
                    매장식사_주문_결과.setStatus(OrderStatus.SERVED);
                    when(orderRepository.findById(any())).thenReturn(Optional.of(매장식사_주문_결과));
                    when(orderRepository.existsByOrderTableAndStatusNot(테이블_1번, OrderStatus.COMPLETED)).thenReturn(false);

                    Order result = orderService.complete(매장식사_주문_결과_ID);

                    assertAll(
                            () -> assertThat(result.getId()).isEqualTo(매장식사_주문_결과_ID),
                            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                            () -> assertThat(테이블_1번.getNumberOfGuests()).isZero(),
                            () -> assertThat(테이블_1번.isOccupied()).isFalse()
                    );
                }
            }
        }

        @Nested
        @DisplayName("배달 주문")
        class DeliveryOrder {
            @Nested
            @DisplayName("실패")
            class DeliveryOrderFail {
                private static Stream<OrderStatus> 배달완료가아닌상태() {
                    return Stream.of(
                            OrderStatus.WAITING,
                            OrderStatus.ACCEPTED,
                            OrderStatus.SERVED,
                            OrderStatus.DELIVERING,
                            OrderStatus.COMPLETED
                    );
                }

                @ParameterizedTest
                @MethodSource("배달완료가아닌상태")
                @DisplayName("실패 - 주문의 상태는 DELIVERED 이어야 한다")
                void fail1(OrderStatus status) {
                    배달_주문_결과.setStatus(status);
                    when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                    assertThatThrownBy(() -> orderService.complete(배달_주문_결과_ID))
                            .isInstanceOf(IllegalStateException.class);
                }
            }

            @Nested
            @DisplayName("성공")
            class DeliveryOrderSuccess {
                @Test
                @DisplayName("배달 주문을 완료한다")
                void completeDeliveryOrder() {
                    배달_주문_결과.setStatus(OrderStatus.DELIVERED);
                    when(orderRepository.findById(any())).thenReturn(Optional.of(배달_주문_결과));

                    Order result = orderService.complete(배달_주문_결과_ID);

                    assertAll(
                            () -> assertThat(result.getId()).isEqualTo(배달_주문_결과_ID),
                            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                    );
                }
            }
        }

        @Nested
        @DisplayName("포장 주문")
        class TakeoutOrder {
            @Nested
            @DisplayName("실패")
            class TakeoutOrderFail {
                private static Stream<OrderStatus> 음식제조완료가아닌상태() {
                    return Stream.of(
                            OrderStatus.WAITING,
                            OrderStatus.ACCEPTED,
                            OrderStatus.DELIVERING,
                            OrderStatus.DELIVERED,
                            OrderStatus.COMPLETED
                    );
                }

                @ParameterizedTest
                @MethodSource("음식제조완료가아닌상태")
                @DisplayName("등록되어 있는 주문의 상태는 SERVED 이어야 한다")
                void fail1(OrderStatus status) {
                    포장_주문_결과.setStatus(status);
                    when(orderRepository.findById(any())).thenReturn(Optional.of(포장_주문_결과));

                    assertThatThrownBy(() -> orderService.complete(포장_주문_결과_ID))
                            .isInstanceOf(IllegalStateException.class);
                }
            }

            @Nested
            @DisplayName("성공")
            class TakeoutOrderSuccess {
                @Test
                @DisplayName("매장식사 주문을 완료한다")
                void completeTakeout() {
                    포장_주문_결과.setStatus(OrderStatus.SERVED);
                    when(orderRepository.findById(any())).thenReturn(Optional.of(포장_주문_결과));

                    Order result = orderService.complete(포장_주문_결과_ID);

                    assertAll(
                            () -> assertThat(result.getId()).isEqualTo(포장_주문_결과_ID),
                            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                    );
                }
            }
        }
    }


}

