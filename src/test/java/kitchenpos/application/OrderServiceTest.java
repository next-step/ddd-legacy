package kitchenpos.application;

import kitchenpos.ApplicationServiceTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static kitchenpos.domain.OrderStatus.WAITING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class OrderServiceTest extends ApplicationServiceTest {

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
            Order givenOrder = OrderFixture.create(null, OrderType.EAT_IN, givenOrderTable, List.of(givenOrderLineItem));

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
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, List.of(givenOrderLineItem));

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
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목이 null 이어선 안됩니다.")
        @Test
        void create_fail_because_order_line_item_is_null() {
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null);

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
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, List.of(givenOrderLineItem));

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
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, List.of(givenOrderLineItem));

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
            Menu givenMenu = MenuFixture.create(
                UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(19_000L),
                givenMenuProducts, false
            );
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, List.of(givenOrderLineItem));

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
            Menu givenMenu = MenuFixture.create(
                UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(19_000L),
                givenMenuProducts, true
            );
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            givenMenu.setPrice(BigDecimal.valueOf(20_000L));

            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }


        @DisplayName("[예외] 배달 주문 유형인 주문은 배달 주소가 있어야 합니다.")
        @Test
        void create_fail_because_delivery_order_type_but_delivery_address_is_null() {
            List<Product> givenProducts = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            Order givenOrder = OrderFixture.create(null, OrderType.DELIVERY, null, List.of(givenOrderLineItem));
            givenOrder.setDeliveryAddress(null);

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }



        @DisplayName("[정상] 매장식사 주문 유형인 주문은 주문 테이블이 점유 중이어야 합니다.")
        @Test
        void create_fail_because_eat_in_order_type_but_order_table_is_not_occupied() {
            List<Product> givenProducts = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu givenMenu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), givenProducts);
            OrderLineItem givenOrderLineItem = OrderLineItemFixture.create(givenMenu, 1L);
            OrderTable givenOrderTable = OrderTableFixture.create( );
            Order givenOrder = OrderFixture.create(null, OrderType.EAT_IN, givenOrderTable, List.of(givenOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(givenMenu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(givenMenu));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));

            assertThrows(IllegalStateException.class, () -> orderService.create(givenOrder));
        }

    }

}