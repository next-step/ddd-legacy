package kitchenpos.application.order;

import kitchenpos.application.OrderService;
import kitchenpos.application.menu.MenuTestFixture;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DisplayName("Application: 주문 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderLineItem orderLineItem_1;
    private OrderLineItem orderLineItem_2;
    private Order delivery_order;
    private Order eat_in_order;
    private Order takeout_order;

    @BeforeEach
    void setUp() {
        MenuGroup aMenuGroup = MenuTestFixture.aMenuGroup();
        Product product_1 = MenuTestFixture.aProduct("레드 한마리 순살", 12000L);
        Product product_2 = MenuTestFixture.aProduct("허니 한마리 순살", 12000L);
        MenuProduct menuProduct_1 = MenuTestFixture.aMenuProduct(1L, product_1, 1L);
        MenuProduct menuProduct_2 = MenuTestFixture.aMenuProduct(2L, product_2, 1L);
        Menu menu_1 = MenuTestFixture.aMenu("레드 한마리", 24000L, aMenuGroup, List.of(menuProduct_1));
        Menu menu_2 = MenuTestFixture.aMenu("허니 한마리", 24000L, aMenuGroup, List.of(menuProduct_2));

        OrderTable orderTable = OrderTestFixture.aOrderTable("1번_테이블", true);

        orderLineItem_1 = OrderTestFixture.aOrderLineItem(menu_1, 1L);
        orderLineItem_2 = OrderTestFixture.aOrderLineItem(menu_2, 1L);

        delivery_order = OrderTestFixture.aOrder(orderTable, OrderType.DELIVERY, OrderStatus.WAITING, List.of(orderLineItem_1, orderLineItem_2), "서울시 강남구");
        eat_in_order = OrderTestFixture.aOrder(orderTable, OrderType.EAT_IN, OrderStatus.WAITING, List.of(orderLineItem_1, orderLineItem_2), "");
        takeout_order = OrderTestFixture.aOrder(orderTable, OrderType.TAKEOUT, OrderStatus.WAITING, List.of(orderLineItem_1, orderLineItem_2), "");
    }

    @Nested
    @DisplayName("주문을 생성할 수 있다.")
    class create {

        @Test
        @DisplayName("주문 타입이 없는 경우 예외가 발생한다.")
        void case_1() {
            //given
            ReflectionTestUtils.setField(delivery_order, "type", null);

            //when && then
            Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.create(delivery_order));
        }

        @Test
        @DisplayName("주문 메뉴가 없는 경우 예외가 발생한다.")
        void case_2() {
            //given
            ReflectionTestUtils.setField(delivery_order, "orderLineItems", List.of());

            //when && then
            Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.create(delivery_order));
        }

        @Test
        @DisplayName("주문 메뉴 항목의 수와 데이터베이스에서 찾은 메뉴의 수가 일치하지 않는 경우 예외가 발생한다.")
        void case_3() {
            //given
            List<Menu> menus = List.of(orderLineItem_1.getMenu());
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(menus);

            //when && then
            Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.create(delivery_order));
        }

        @Test
        @DisplayName("주문 유형이 매장 식사가 아니고 주문 항목의 수량이 0보다 작은 경우 예외가 발생한다.")
        void case_4() {
            //given
            List<Menu> menus = List.of(orderLineItem_1.getMenu());
            OrderLineItem minusOrderLine = OrderTestFixture.aOrderLineItem(menus.getFirst(), -1L);
            ReflectionTestUtils.setField(delivery_order, "orderLineItems", List.of(minusOrderLine));

            //when
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(menus);

            //when && then
            Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.create(delivery_order));
        }

        @Test
        @DisplayName("노출되지 않은 메뉴가 있는 경우 예외가 발생한다.")
        void case_5() {
            //given
            Menu false_menu = orderLineItem_1.getMenu();
            ReflectionTestUtils.setField(false_menu, "displayed", false);
            List<Menu> menus = List.of(false_menu, orderLineItem_2.getMenu());

            //when
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(menus);
            when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(false_menu));

            //then
            Assertions.assertThrows(IllegalStateException.class, () -> orderService.create(delivery_order));
        }

        @Test
        @DisplayName("주문한 메뉴들의 가격이 주문한 가격과 다른 경우 예외가 발생한다.")
        void case_6() {
            //given
            List<Menu> menus = List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu());
            Menu false_menu = orderLineItem_1.getMenu();
            ReflectionTestUtils.setField(false_menu, "price", BigDecimal.valueOf(10000L));

            //when
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(menus);
            when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(false_menu));

            //then
            Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.create(delivery_order));
        }

        @Nested
        @DisplayName("배달 주문을 생성할 수 있다.")
        class delivery_create {
            @Test
            @DisplayName("배달 주문을 생성할 수 있다.")
            void case_1() {
                //given
                List<Menu> menus = List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu());

                //when
                when(menuRepository.findAllByIdIn(anyList()))
                        .thenReturn(menus);

                when(menuRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderLineItem_1.getMenu()));

                when(orderRepository.save(any(Order.class)))
                        .thenReturn(delivery_order);

                Order createdOrder = orderService.create(delivery_order);

                //then
                Assertions.assertEquals(OrderType.DELIVERY, createdOrder.getType());
                Assertions.assertEquals(OrderStatus.WAITING, createdOrder.getStatus());
                Assertions.assertEquals(delivery_order.getOrderDateTime(), createdOrder.getOrderDateTime());
                Assertions.assertEquals(delivery_order.getOrderLineItems(), createdOrder.getOrderLineItems());
            }

            @ParameterizedTest(name = "배달 주소가 {0} 인 경우")
            @ValueSource(strings = "")
            @NullSource
            @DisplayName("배달 주소가 공백이거나 null일 경우 예외가 발생한다.")
            void case_2(String address) {
                //given
                ReflectionTestUtils.setField(delivery_order, "deliveryAddress", address);

                //when
                when(menuRepository.findAllByIdIn(anyList()))
                        .thenReturn(List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu()));

                when(menuRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderLineItem_1.getMenu()));

                //then
                Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.create(delivery_order));
            }
        }

        @Nested
        @DisplayName("매장 주문을 생성할 수 있다.")
        class takeout_create {

            @Test
            @DisplayName("매장 주문을 생성할 수 있다.")
            void case_1() {
                //given
                List<Menu> menus = List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu());
                OrderTable orderTable = eat_in_order.getOrderTable();
                //when
                when(menuRepository.findAllByIdIn(anyList()))
                        .thenReturn(menus);

                when(menuRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderLineItem_1.getMenu()));

                when(orderTableRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderTable));

                when(orderRepository.save(any(Order.class)))
                        .thenReturn(eat_in_order);

                Order createdOrder = orderService.create(eat_in_order);

                //then
                Assertions.assertEquals(OrderType.EAT_IN, createdOrder.getType());
                Assertions.assertEquals(OrderStatus.WAITING, createdOrder.getStatus());
                Assertions.assertEquals(eat_in_order.getOrderTable(), createdOrder.getOrderTable());
                Assertions.assertEquals(takeout_order.getOrderLineItems(), createdOrder.getOrderLineItems());
            }

            @Test
            @DisplayName("매장 주문 시 자리가 없는 경우 예외가 발생한다.")
            void case_2() {
                //given
                ReflectionTestUtils.setField(takeout_order, "orderTable", null);

                //when
                when(menuRepository.findAllByIdIn(anyList()))
                        .thenReturn(List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu()));

                when(menuRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderLineItem_1.getMenu()));

                //then
                Assertions.assertThrows(NoSuchElementException.class, () -> orderService.create(eat_in_order));
            }

            @Test
            @DisplayName("매장 주문 시 테이블의 자리가 없으면 예외가 발생한다.")
            void case_3() {
                //given
                OrderTable orderTable = OrderTestFixture.aOrderTableWithGuests("1번_테이블", 1);
                ReflectionTestUtils.setField(takeout_order, "orderTable", orderTable);

                //when
                when(menuRepository.findAllByIdIn(anyList()))
                        .thenReturn(List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu()));

                when(menuRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderLineItem_1.getMenu()));
                when(orderTableRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderTable));
                //then
                Assertions.assertThrows(IllegalStateException.class, () -> orderService.create(eat_in_order));
            }
        }

        @Nested
        @DisplayName("포장주문을 생성할 수 있다.")
        class eat_in_order {
            @Test
            @DisplayName("포장 주문을 생성할 수 있다.")
            void case_1() {
                //given
                List<Menu> menus = List.of(orderLineItem_1.getMenu(), orderLineItem_2.getMenu());
                //when
                when(menuRepository.findAllByIdIn(anyList()))
                        .thenReturn(menus);

                when(menuRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.of(orderLineItem_1.getMenu()));

                when(orderRepository.save(any(Order.class)))
                        .thenReturn(takeout_order);

                Order createdOrder = orderService.create(takeout_order);

                //then
                Assertions.assertEquals(OrderType.TAKEOUT, createdOrder.getType());
                Assertions.assertEquals(OrderStatus.WAITING, createdOrder.getStatus());
                Assertions.assertEquals(takeout_order.getOrderDateTime(), createdOrder.getOrderDateTime());
                Assertions.assertEquals(takeout_order.getOrderLineItems(), createdOrder.getOrderLineItems());
            }
        }


    }

    @Nested
    @DisplayName("주문을 승인 할 수 있다.")
    class accept {

    }

    @Nested
    @DisplayName("주문을 제공 할 수 있다.")
    class serve {
    }

    @Nested
    @DisplayName("주문을 배달 할 수 있다.")
    class delivery {
        @Nested
        @DisplayName("배달을 시작 할 수 있다.")
        class start {
        }

        @Nested
        @DisplayName("배달을 완료 할 수 있다.")
        class complete {
        }
    }

    @Nested
    @DisplayName("주문을 완료 할 수 있다.")
    class complete {
    }
}
