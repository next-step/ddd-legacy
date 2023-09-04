package kitchenpos.application;

import kitchenpos.UnitTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static kitchenpos.domain.OrderStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class OrderServiceTest extends UnitTest {

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

    @DisplayName("주문을 등록합니다.")
    @Nested
    class create {


        @DisplayName("[정상] 배달 주문 유형인 주문을 등록합니다.")
        @Test
        void create_success_delivery_order_type_one_order_line_item() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.DELIVERY, null, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));
            when(orderRepository.save(any())).then(invocation -> invocation.getArgument(0));

            Order savedOrder = orderService.create(givenOrder);

            assertNotNull(savedOrder.getId());
            assertEquals(givenOrder.getType(), savedOrder.getType());
            assertEquals(WAITING, savedOrder.getStatus());
            assertEquals(givenOrder.getDeliveryAddress(), savedOrder.getDeliveryAddress());
            assertEquals(givenOrder.getOrderLineItems().size(), savedOrder.getOrderLineItems().size());
        }

        @DisplayName("[정상] 매장식사 주문 유형인 주문을 등록합니다.")
        @Test
        void create_success_eat_in_order_type_one_order_line_item() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(null, OrderType.EAT_IN, null, givenOrderTable, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));
            when(orderRepository.save(any())).then(invocation -> invocation.getArgument(0));

            Order savedOrder = orderService.create(givenOrder);

            assertNotNull(savedOrder.getId());
            assertEquals(givenOrder.getType(), savedOrder.getType());
            assertEquals(WAITING, savedOrder.getStatus());
            assertNull(savedOrder.getDeliveryAddress());
            assertNotNull(savedOrder.getOrderTable());
            assertEquals(givenOrder.getOrderLineItems().size(), savedOrder.getOrderLineItems().size());
        }

        @DisplayName("[정상] 포장 주문 유형인 주문을 등록합니다.")
        @Test
        void create_success_takeout_order_type_one_order_line_item() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));
            when(orderRepository.save(any())).then(invocation -> invocation.getArgument(0));

            Order savedOrder = orderService.create(givenOrder);

            assertNotNull(savedOrder.getId());
            assertEquals(givenOrder.getType(), savedOrder.getType());
            assertEquals(WAITING, savedOrder.getStatus());
            assertNull(savedOrder.getDeliveryAddress());
            assertNull(savedOrder.getOrderTable());
            assertEquals(givenOrder.getOrderLineItems().size(), savedOrder.getOrderLineItems().size());
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 유형이 null 이어선 안된다.")
        @Test
        void create_fail_because_order_type_null() {
            Order givenOrder = OrderFixture.create(null, null, null, null);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목이 비어선 안됩니다.")
        @Test
        void create_fail_because_order_line_item_is_empty() {
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목이 null 이어선 안됩니다.")
        @Test
        void create_fail_because_order_line_item_is_null() {
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, null);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목에 해당하는 메뉴가 존재해야 합니다.")
        @Test
        void create_fail_because_some_menu_is_not_exist() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 유형이 매장식사가 아닌 경우 주문 품목의 개수가 음수 일 수 없다.")
        @Test
        void create_fail_because_eat_in_order_type_and_order_line_item_quantity_is_minus() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, -1L);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문된 메뉴가 전시 중이어야 합니다.")
        @Test
        void create_fail_because_some_menu_is_not_displayed() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            List<MenuProduct> givenMenuProducts = givenProducts.stream()
                    .map(e -> MenuProductFixture.create(e, 1))
                    .collect(Collectors.toList());
            MenuGroup menuGroup = MenuGroupFixture.create();
            Menu givenMenu = MenuFixture.create(
                    UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(19_000L),
                    givenMenuProducts, menuGroup, false
            );
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));

            assertThrows(IllegalStateException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문된 메뉴의 가격과 주문 품목의 가격이 일치해야 합니다.")
        @Test
        void create_fail_because_menu_and_order_line_item_is_not_same() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            List<MenuProduct> givenMenuProducts = givenProducts.stream()
                    .map(e -> MenuProductFixture.create(e, 1))
                    .collect(Collectors.toList());
            MenuGroup menuGroup = MenuGroupFixture.create();
            Menu givenMenu = MenuFixture.create(
                    UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(19_000L),
                    givenMenuProducts, menuGroup, true
            );
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, BigDecimal.valueOf(20_000L), 1L);

            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }


        @DisplayName("[예외] 배달 주문 유형인 주문은 배달 주소가 있어야 합니다.")
        @NullAndEmptySource
        @ParameterizedTest
        void create_fail_because_delivery_order_type_but_delivery_address_is_null(String deliveryAddress) {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.DELIVERY, null, List.of(givenOrderLineItem));
            givenOrder.setDeliveryAddress(deliveryAddress);

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }


        @DisplayName("[예외] 매장식사 주문 유형인 주문은 주문 테이블이 점유 중이어야 합니다.")
        @Test
        void create_fail_because_eat_in_order_type_but_order_table_is_not_occupied() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create();
            Order givenOrder = OrderFixture.create(null, OrderType.EAT_IN, null, givenOrderTable, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));

            assertThrows(IllegalStateException.class, () -> orderService.create(givenOrder));
        }

    }

    @DisplayName("'매장 식사' 주문 유형인 주문에 대해 주문 상태를 변경합니다.")
    @Nested
    class changeStatusOfEatInOrderType {

        @DisplayName("[정상] 주문이 수락됩니다.")
        @Test
        void accept_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, WAITING, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.accept(givenOrder.getId());

            assertEquals(OrderStatus.ACCEPTED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 대기 중인 주문만 수락이 가능합니다.")
        @Test
        void accept_fail_because_not_waiting_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, ACCEPTED, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.accept(givenOrder.getId()));
        }

        @DisplayName("[정상] 주문의 서빙이 완료 됩니다.")
        @Test
        void serve_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, ACCEPTED, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.serve(givenOrder.getId());

            assertEquals(OrderStatus.SERVED, givenOrder.getStatus());
        }


        @DisplayName("[예외] 수락된 주문만 서빙이 가능합니다.")
        @Test
        void serve_fail_because_not_accepted_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, WAITING, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.serve(givenOrder.getId()));
        }

        @DisplayName("[예외] 매장식사는 배달 시작를 할 수 없습니다.")
        @Test
        void start_delivery_fail_because_not_eat_in_order_type() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, SERVED, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(givenOrder.getId()));
        }

        @DisplayName("[버그 예상] 예상 정상 동작: '매장 식사는 배달 완료 처리가 불가합니다' / 실제 : '매장 식사도 배달 중 상태인 경우 배달 완료 처리가 가능합니다.'")
        @Test
        void complete_delivery_fail_because_not_eat_in_order_type() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, DELIVERING, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.completeDelivery(givenOrder.getId());

            assertEquals(DELIVERED, givenOrder.getStatus());
        }

        @DisplayName("[버그 예상] " +
                "예상 정상 동작: '서빙 완료된 주문을 완료 처리하고 관련된 주문 테이블이 있는 경우 정리합니다.' / " +
                "실제 : '서빙 완료된 주문을 완료 처리하고 관련된 주문 테이블이 없는 경우 정리합니다.'")
        @Test
        void complete_bug_about_exist_order_table_not_clear() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, SERVED, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
            assertEquals(0, givenOrderTable.getNumberOfGuests());
            assertFalse(givenOrderTable.isOccupied());
        }

        @DisplayName("[버그 예상] " +
                "예상 정상 동작: '서빙 완료된 주문을 완료 처리하고 관련된 주문 테이블이 없는 경우 정리합니다.' / " +
                "실제 : '서빙 완료된 주문을 완료 처리하고 관련된 주문 테이블이 있는 경우 정리합니다.'")
        @Test
        void complete_bug_about_no_exist_order_table_clear() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, SERVED, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
            assertEquals(4, givenOrderTable.getNumberOfGuests());
            assertTrue(givenOrderTable.isOccupied());
        }
        @DisplayName("[예외] 서빙 완료된 주문만 완료 처리할 수 있습니다.")
        @Test
        void complete_error_because_no_serverd_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, WAITING, givenOrderTable, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.complete(givenOrder.getId()));
        }

    }

    @DisplayName("'포장' 주문 유형인 주문에 대해 주문 상태를 변경합니다.")
    @Nested
    class changeStatusOfTakeoutOrderType {

        @DisplayName("[정상] 주문이 수락됩니다.")
        @Test
        void accept_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.accept(givenOrder.getId());

            assertEquals(OrderStatus.ACCEPTED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 대기 중인 주문만 수락이 가능합니다.")
        @Test
        void accept_fail_because_not_waiting_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블 1", 4, true);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, ACCEPTED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.accept(givenOrder.getId()));
        }

        @DisplayName("[정상] 주문의 서빙이 완료 됩니다.")
        @Test
        void serve_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, ACCEPTED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.serve(givenOrder.getId());

            assertEquals(OrderStatus.SERVED, givenOrder.getStatus());
        }


        @DisplayName("[예외] 수락된 주문만 서빙이 가능합니다.")
        @Test
        void serve_fail_because_not_accepted_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.serve(givenOrder.getId()));
        }

        @DisplayName("[예외] 포장은 배달 시작를 할 수 없습니다.")
        @Test
        void start_delivery_fail_because_not_eat_in_order_type() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, SERVED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(givenOrder.getId()));
        }

        @DisplayName("[버그 예상] 예상 정상 동작: '포장은 배달 완료 처리가 불가합니다' / 실제 : '포장도 배달 중 상태인 경우 배달 완료 처리가 가능합니다.'")
        @Test
        void complete_delivery_fail_because_not_eat_in_order_type() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, DELIVERING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.completeDelivery(givenOrder.getId());

            assertEquals(DELIVERED, givenOrder.getStatus());
        }

        @DisplayName("[정상] 서빙 완료인 주문을 완료 처리합니다.")
        @Test
        void complete_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, SERVED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 서빙 완료된 주문만 완료 처리할 수 있습니다.")
        @Test
        void complete_error_because_no_serverd_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.complete(givenOrder.getId()));
        }

    }
    @DisplayName("'배달' 주문 유형인 주문에 대해 주문 상태를 변경합니다.")
    @Nested
    class changeStatusOfDeliveryOrderType {

        @DisplayName("[정상] 주문이 수락됩니다.")
        @Test
        void accept_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.accept(givenOrder.getId());

            assertEquals(OrderStatus.ACCEPTED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 대기 중인 주문만 수락이 가능합니다.")
        @Test
        void accept_fail_because_not_waiting_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, ACCEPTED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.accept(givenOrder.getId()));
        }

        @DisplayName("[정상] 주문의 서빙이 완료 됩니다.")
        @Test
        void serve_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, ACCEPTED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.serve(givenOrder.getId());

            assertEquals(OrderStatus.SERVED, givenOrder.getStatus());
        }


        @DisplayName("[예외] 수락된 주문만 서빙이 가능합니다.")
        @Test
        void serve_fail_because_not_accepted_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.serve(givenOrder.getId()));
        }

        @DisplayName("[정상] 배달 시작을 시작합니다.")
        @Test
        void start_delivery_fail_because_not_eat_in_order_type() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, SERVED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.startDelivery(givenOrder.getId());

            assertEquals(DELIVERING, givenOrder.getStatus());
        }

        @DisplayName("[예외] 서빙 완료된 주문만 배달을 시작할 수 있습니다.")
        @Test
        void serve_fail_because_not_served_status() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(givenOrder.getId()));
        }


        @DisplayName("[정상] 배달이 완료된 주문을 배달완료 처리 합니다.")
        @Test
        void complete_delivery_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, DELIVERING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.completeDelivery(givenOrder.getId());

            assertEquals(DELIVERED, givenOrder.getStatus());
        }
        @DisplayName("[예외] 배달 중이지 않은 주문은 배달 완료 처리가 불가합니다")
        @Test
        void complete_delivery_fail_because_because_not_start_delivery() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(givenOrder.getId()));
        }

        @DisplayName("[정상] 배달 완료된 주문을 완료 처리 합니다.")
        @Test
        void complete_success() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, DELIVERED, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 배달 완료가 아닌 주문은 완료 처리 할 수 없습니다.")
        @Test
        void complete_fail_because_no_delivered() {
            List<Product> givenProducts = Arrays.asList(
                    ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                    ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, List.of(givenOrderLineItem));

            given(orderRepository.findById(any())).willReturn(Optional.of(givenOrder));

            assertThrows(IllegalStateException.class, () -> orderService.complete(givenOrder.getId()));
        }

    }


}
