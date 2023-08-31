package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
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

    @TestFactory
    List<DynamicTest> create() {
        var menu1 = TestFixture.createMenu("메뉴1", 10000L, true);
        var menu2 = TestFixture.createMenu("메뉴2", 15000L, true);
        var orderLineItems = List.of(
                TestFixture.createOrderLineItem(menu1, menu1.getPrice(), 10),
                TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
        );
        var orderTable = TestFixture.createOrderTable("테이블1", 4, true);

        return List.of(
                DynamicTest.dynamicTest("주문을 생성할 수 있다. 생성된 주문은 주문타입, 주문상태, 주문시간, 주문아이템 정보를 가진다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems, orderTable, "강남구 역삼동");
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
                    assertThat(result.getOrderDateTime()).isNotNull();
                }),

                DynamicTest.dynamicTest("주문을 생성할 수 있다. 매장식사 타입의 주문의 경우, 주문 테이블도 필수로 함께 가진다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems, orderTable, "강남구 역삼동");
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
                    assertThat(result.getOrderDateTime()).isNotNull();
                    assertThat(result.getOrderTable()).isNotNull();
                }),


                DynamicTest.dynamicTest("주문을 생성할 수 있다. 배달 타입의 주문의 경우, 배달 주소도 필수로 함께 가진다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, orderLineItems, orderTable, "강남구 역삼동");
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
                    assertThat(result.getOrderDateTime()).isNotNull();
                    assertThat(result.getDeliveryAddress()).isNotBlank();
                }),

                DynamicTest.dynamicTest("주문을 생성할 수 있다. 배달 타입의 주문의 경우, 배달 주소는 공백일 수 없다", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, orderLineItems, orderTable, "");
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType(), false);

                    var throwable = catchThrowable(() -> orderService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                DynamicTest.dynamicTest("주문은 (배달, 포장, 매장식사) 중 하나의 주문 타입으로 생성할 수 있다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, orderLineItems, orderTable);
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
                }),


                DynamicTest.dynamicTest("주문은 (배달, 포장, 매장식사) 중 하나의 주문 타입으로 생성할 수 있다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, orderLineItems, orderTable);
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
                }),

                DynamicTest.dynamicTest("주문은 ( 대기중, 승인됨, 제공됨, 배달중, 배달됨, 완료됨 ) 중 하나의 상태를 가진다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, orderLineItems, orderTable);
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                }),

                DynamicTest.dynamicTest("주문을 처음 만들면 입력과 관계없이 (대기중) 상태로 생성된다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.COMPLETED, OrderType.EAT_IN, orderLineItems, orderTable);
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                }),

                DynamicTest.dynamicTest("한 주문은 여러 개의 주문아이템을 가질 수 있다. 주문 아이템은 메뉴와 재고 수만 가지고 있다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems, orderTable);
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType());

                    var result = orderService.create(request);

                    result.getOrderLineItems().forEach(orderLineItem -> {
                        assertThat(orderLineItem.getMenu()).isNotNull();
                        assertThat(orderLineItem.getQuantity()).isNotZero();

                        assertThat(orderLineItem.getPrice()).isNull();
                        assertThat(orderLineItem.getSeq()).isNull();
                    });
                }),

                DynamicTest.dynamicTest("주문 아이템은 반드시 등록되어있는 전시상태인 메뉴만을 선택할 수 있다.", () -> {
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, orderLineItems, orderTable);
                    var hidMenu = TestFixture.copy(menu1);
                    hidMenu.setDisplayed(false);
                    mockCreateOrder(List.of(hidMenu, menu2), request.getOrderTable(), request.getType(), false);

                    var throwable = catchThrowable(() -> orderService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("(배달, 포장) 타입인 경우, 모든 주문아이템이 재고는 0 초과이어야 한다.", () -> {
                    var emptyOrderLineItems = List.of(
                            TestFixture.createOrderLineItem(menu1, menu1.getPrice(), -1),
                            TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
                    );
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, emptyOrderLineItems, orderTable);
                    doAnswer(args -> List.of(menu1, menu2)).when(menuRepository).findAllByIdIn(any());

                    var throwable = catchThrowable(() -> orderService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),


                DynamicTest.dynamicTest("(매장식사타입) 인 경우, 주문테이블이 사용중이어선 안된다.", () -> {
                    var usedOrderTable = TestFixture.copy(orderTable);
                    usedOrderTable.setOccupied(false);
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems, usedOrderTable);
                    mockCreateOrder(List.of(menu1, menu2), request.getOrderTable(), request.getType(), false);

                    var throwable = catchThrowable(() -> orderService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("주문 아이템의 가격은 해당 메뉴의 가격과 항상 동일해야한다.", () -> {
                    var emptyOrderLineItems = List.of(
                            TestFixture.createOrderLineItem(menu1, 125912840218L, -1),
                            TestFixture.createOrderLineItem(menu2, 129381231250L, 5)
                    );
                    var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, emptyOrderLineItems, orderTable);
                    doAnswer(args -> List.of(menu1, menu2)).when(menuRepository).findAllByIdIn(any());

                    var throwable = catchThrowable(() -> orderService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @TestFactory
    List<DynamicTest> accept() {
        var menu1 = TestFixture.createMenu("메뉴1", 10000L, true);
        var menu2 = TestFixture.createMenu("메뉴2", 15000L, true);
        var orderLineItems = List.of(
                TestFixture.createOrderLineItem(menu1, menu1.getPrice(), 10),
                TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
        );
        var orderTable = TestFixture.createOrderTable("테이블1", 4, true);

        return List.of(
                DynamicTest.dynamicTest("주문을 승인하면, (승인됨) 상태로 변경된다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.accept(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
                }),


                DynamicTest.dynamicTest("승인은 대기중 상태일때만 가능하다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.accept(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("배달 주문타입을 승인하면 배달서버에 배송 요청을 전달한다.", () -> {

                    var order = TestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.accept(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
                    verify(kitchenridersClient, times(1)).requestDelivery(eq(order.getId()), any(), eq(order.getDeliveryAddress()));
                })
        );
    }

    @TestFactory
    List<DynamicTest> serve() {
        var menu1 = TestFixture.createMenu("메뉴1", 10000L, true);
        var menu2 = TestFixture.createMenu("메뉴2", 15000L, true);
        var orderLineItems = List.of(
                TestFixture.createOrderLineItem(menu1, menu1.getPrice(), 10),
                TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
        );
        var orderTable = TestFixture.createOrderTable("테이블1", 4, true);

        return List.of(
                DynamicTest.dynamicTest("승인됨 상태의 주문이 제공되면, (제공됨) 상태로 변경된다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.ACCEPTED, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.serve(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
                }),

                DynamicTest.dynamicTest(" 제공은 반드시 승인됨 상태일 때만 가능하다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.serve(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest(" 제공은 반드시 승인됨 상태일 때만 가능하다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.COMPLETED, OrderType.TAKEOUT, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.serve(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                })
        );
    }

    @TestFactory
    List<DynamicTest> startDelivery() {
        var menu1 = TestFixture.createMenu("메뉴1", 10000L, true);
        var menu2 = TestFixture.createMenu("메뉴2", 15000L, true);
        var orderLineItems = List.of(
                TestFixture.createOrderLineItem(menu1, menu1.getPrice(), 10),
                TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
        );
        var orderTable = TestFixture.createOrderTable("테이블1", 4, true);

        return List.of(
                DynamicTest.dynamicTest("주문을 배달시작하면 배달시작상태로 변경된다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.SERVED, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.startDelivery(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
                }),


                DynamicTest.dynamicTest("이미 배달중인 주문은 다시 배달시작 할 수 없다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.DELIVERING, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.startDelivery(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("(배달타입, 제공됨) 주문만 배달을 시작할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.SERVED, OrderType.TAKEOUT, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.startDelivery(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                })

        );
    }

    @TestFactory
    List<DynamicTest> completeDelivery() {
        var menu1 = TestFixture.createMenu("메뉴1", 10000L, true);
        var menu2 = TestFixture.createMenu("메뉴2", 15000L, true);
        var orderLineItems = List.of(
                TestFixture.createOrderLineItem(menu1, menu1.getPrice(), 10),
                TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
        );
        var orderTable = TestFixture.createOrderTable("테이블1", 4, true);

        return List.of(
                DynamicTest.dynamicTest("배달완료 처리를 할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.DELIVERING, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.completeDelivery(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
                }),

                DynamicTest.dynamicTest("이미 배달완료된 주문은 다시 완료할 수 없다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.DELIVERED, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.completeDelivery(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("배달타입이 아닌 주문도 배달완료 처리할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.DELIVERING, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.completeDelivery(order.getId()));

                    assertThat(throwable).isNull();
                })
        );
    }

    @TestFactory
    List<DynamicTest> complete() {
        var menu1 = TestFixture.createMenu("메뉴1", 10000L, true);
        var menu2 = TestFixture.createMenu("메뉴2", 15000L, true);
        var orderLineItems = List.of(
                TestFixture.createOrderLineItem(menu1, menu1.getPrice(), 10),
                TestFixture.createOrderLineItem(menu2, menu2.getPrice(), 5)
        );
        var orderTable = TestFixture.createOrderTable("테이블1", 4, true);

        return List.of(
                DynamicTest.dynamicTest("배달 주문이 완료되었으면 주문을 완료 처리를 할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.DELIVERED, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.complete(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                }),

                DynamicTest.dynamicTest("배달 타입은 (배달됨) 주문만 완료할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.DELIVERING, OrderType.DELIVERY, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.complete(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("매장식사 주문이 제공되었으면 주문을 완료 처리를 할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.complete(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                }),

                DynamicTest.dynamicTest("매장식사 타입은 (제공됨) 주문만 완료할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.ACCEPTED, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.complete(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),

                DynamicTest.dynamicTest("포장 타입은 (제공됨) 주문만 완료할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.COMPLETED, OrderType.TAKEOUT, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var throwable = catchThrowable(() -> orderService.complete(order.getId()));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                }),


                DynamicTest.dynamicTest("포장 주문이 제공되었으면 주문을 완료 처리를 할 수 있다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.SERVED, OrderType.TAKEOUT, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());

                    var result = orderService.complete(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                }),

                DynamicTest.dynamicTest("매장타입 주문이 완료되면 주문테이블은 점유하지 않은 상태로 초기화된다.", () -> {
                    var order = TestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN, orderLineItems, orderTable);
                    doAnswer(args -> Optional.of(order)).when(orderRepository).findById(any());
                    doAnswer(args -> false).when(orderRepository).existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED));

                    var result = orderService.complete(order.getId());

                    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(result.getOrderTable().getNumberOfGuests()).isZero();
                    assertThat(result.getOrderTable().isOccupied()).isFalse();
                })
        );
    }

    @Test
    @DisplayName("전체 주문 리스트를 조회할 수 있다.")
    void findAll() {
        doAnswer(args -> List.of(TestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN)))
                .when(orderRepository).findAll();

        var result = orderRepository.findAll();

        assertThat(result).isNotEmpty();
    }

    private void mockCreateOrder(
            List<Menu> menus,
            OrderTable orderTable,
            OrderType orderType
    ) {
        mockCreateOrder(menus, orderTable, orderType, true);
    }

    private void mockCreateOrder(
            List<Menu> menus,
            OrderTable orderTable,
            OrderType orderType,
            boolean isSuccessCase
    ) {
        doAnswer(args -> menus)
                .when(menuRepository).findAllByIdIn(any());
        doAnswer(args -> menus.stream().filter(menu -> menu.getId().equals(args.getArgument(0, UUID.class))).findFirst())
                .when(menuRepository).findById(any());

        // Mockito Unnecessary stubbings detected 오류를 제거하기위해 추가.
        if (isSuccessCase) {
            doAnswer(args -> args.getArgument(0))
                    .when(orderRepository).save(any());
        }

        if (orderType == OrderType.EAT_IN)
            doAnswer(args -> Optional.of(orderTable))
                    .when(orderTableRepository).findById(any());
    }
}
