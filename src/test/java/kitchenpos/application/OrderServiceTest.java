package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.config.OrderTestContextConfiguration;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupTestHelper;
import kitchenpos.helper.MenuProductTestHelper;
import kitchenpos.helper.MenuTestHelper;
import kitchenpos.helper.OrderLineItemTestHelper;
import kitchenpos.helper.OrderTableTestHelper;
import kitchenpos.helper.OrderTestHelper;
import kitchenpos.helper.ProductTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Transactional
@Import(OrderTestContextConfiguration.class)
public class OrderServiceTest extends SetupTest{
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private OrderTable 사용중인_주문테이블;
    private Order 대기상태인_먹고가기_주문;
    private Order 대기상태인_포장하기_주문;
    private Order 대기상태인_배달하기_주문;
    private OrderLineItem 마라세트_주문;
    private OrderLineItem 나홀로세트_주문;
    private List<OrderLineItem> 주문할_메뉴들 = new ArrayList<>();
    private List<Order> 주문들 = Arrays.asList(대기상태인_먹고가기_주문, 대기상태인_포장하기_주문, 대기상태인_배달하기_주문);

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        Product 마라탕 = ProductTestHelper.음식_생성("마라탕", BigDecimal.valueOf(10000));
        Product 미니꿔바로우 = ProductTestHelper.음식_생성("미니꿔바로우", BigDecimal.valueOf(8000));
        Product 콜라 = ProductTestHelper.음식_생성("콜라", BigDecimal.valueOf(3000));

        MenuProduct 마라탕메뉴 = MenuProductTestHelper.음식메뉴_생성(마라탕, 1);
        MenuProduct 미니꿔바로우메뉴 = MenuProductTestHelper.음식메뉴_생성(미니꿔바로우, 1);
        MenuProduct 콜라메뉴 = MenuProductTestHelper.음식메뉴_생성(콜라, 1);

        MenuGroup 추천메뉴 = MenuGroupTestHelper.메뉴카테고리_생성("추천메뉴");

        Menu 마라세트 = MenuTestHelper.메뉴_생성(추천메뉴, "마라세트", BigDecimal.valueOf(16000), Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴), true);
        Menu 나홀로세트 = MenuTestHelper.메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(11000), Arrays.asList(마라탕메뉴, 콜라메뉴), true);

        마라세트_주문 = OrderLineItemTestHelper.주문할_메뉴_생성(마라세트, 1);
        나홀로세트_주문 = OrderLineItemTestHelper.주문할_메뉴_생성(나홀로세트, 1);

        주문할_메뉴들 = Arrays.asList(마라세트_주문, 나홀로세트_주문);

        사용중인_주문테이블 = OrderTableTestHelper.사용중인_주문테이블_생성("사용중인_주문테이블", 4);

        대기상태인_먹고가기_주문 = OrderTestHelper.대기_주문_생성(OrderType.EAT_IN, 주문할_메뉴들, OrderTableTestHelper.사용중인_주문테이블_생성("사용중인_주문테이블1", 2));
        대기상태인_포장하기_주문 = OrderTestHelper.대기_주문_생성(OrderType.TAKEOUT, 주문할_메뉴들, OrderTableTestHelper.사용중인_주문테이블_생성("사용중인_주문테이블2", 3));
        대기상태인_배달하기_주문 = OrderTestHelper.대기_주문_생성(OrderType.DELIVERY, 주문할_메뉴들, OrderTableTestHelper.사용중인_주문테이블_생성("사용중인_주문테이블3", 4));
    }

    @DisplayName("주문을 생성하다.")
    @ParameterizedTest
    @ValueSource(strings = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void createOrder(String orderType){
        Order requestOrder = new Order();
        requestOrder.setType(OrderType.valueOf(orderType));
        requestOrder.setOrderLineItems(주문할_메뉴들);
        requestOrder.setOrderTable(사용중인_주문테이블);
        requestOrder.setOrderTableId(사용중인_주문테이블.getId());
        requestOrder.setDeliveryAddress(OrderType.DELIVERY.equals(requestOrder.getType()) ? "임시 배달 주소" : null);

        Order createOrder = orderService.create(requestOrder);
        assertThat(createOrder.getType()).isSameAs(requestOrder.getType());
        assertThat(createOrder.getStatus()).isSameAs(OrderStatus.WAITING);
    }

    @Nested
    @DisplayName("생성하려는 주문이 ")
    class createOrderExcetionTestCase{
        @DisplayName("만약에 없는 주문유형인 경우 IllegalArgumentException 에러가 발생한다.")
        @ParameterizedTest
        @NullSource
        void createNoOrderTypeOfOrder(OrderType type){
            Order requestOrder = new Order();
            requestOrder.setType(type);
            requestOrder.setOrderLineItems(주문할_메뉴들);
            requestOrder.setOrderTable(사용중인_주문테이블);
            requestOrder.setOrderTableId(사용중인_주문테이블.getId());

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }

        @DisplayName("만약에 주문한 메뉴정보가 없는 경우 IllegalArgumentException 에러가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createNoOrderLineItemOfOrder(List<OrderLineItem> orderLineItems){
            Order requestOrder = new Order();
            requestOrder.setType(OrderType.EAT_IN);
            requestOrder.setOrderLineItems(orderLineItems);
            requestOrder.setOrderTable(사용중인_주문테이블);
            requestOrder.setOrderTableId(사용중인_주문테이블.getId());

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }

        @DisplayName("만약에 없는 메뉴의 주문인 경우 IllegalArgumentException 에러가 발생한다.")
        @Test
        void createNoMenuOfOrder(){
            Menu 없는메뉴 = new Menu();
            없는메뉴.setId(UUID.randomUUID());

            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setMenu(없는메뉴);
            orderLineItem.setMenuId(없는메뉴.getId());

            Order requestOrder = new Order();
            requestOrder.setType(OrderType.EAT_IN);
            requestOrder.setOrderLineItems(Arrays.asList(orderLineItem));
            requestOrder.setOrderTable(사용중인_주문테이블);
            requestOrder.setOrderTableId(사용중인_주문테이블.getId());

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }

        @DisplayName("만약에 배달하기, 포장하기 주문이면서 주문할 메뉴의 수량이 음수인 경우 IllegalArgumentException 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"DELIVERY", "TAKEOUT"})
        void createNoEnoughQuantityOfNoEatInOrder(String orderType){
            OrderLineItem 수량이_음수인_주문메뉴 = 주문할_메뉴들.getFirst();
            수량이_음수인_주문메뉴.setQuantity(-1);

            Order requestOrder = new Order();
            requestOrder.setType(OrderType.valueOf(orderType));
            requestOrder.setOrderLineItems(Arrays.asList(수량이_음수인_주문메뉴));
            requestOrder.setOrderTable(사용중인_주문테이블);
            requestOrder.setOrderTableId(사용중인_주문테이블.getId());

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }

        @DisplayName("만약에 판매중단 상태인 메뉴의 주문인 경우 IllegalStateException 에러가 발생한다.")
        @Test
        void createNotDispayedMenuOfOrder(){
            Menu menu = 마라세트_주문.getMenu();
            menu = MenuTestHelper.메뉴_판매상태_변경(menu.getId(), false);

            마라세트_주문.setMenu(menu);

            Order requestOrder = new Order();
            requestOrder.setType(OrderType.EAT_IN);
            requestOrder.setOrderLineItems(Arrays.asList(마라세트_주문));
            requestOrder.setOrderTable(사용중인_주문테이블);
            requestOrder.setOrderTableId(사용중인_주문테이블.getId());

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }

        @DisplayName("만약에 배달하기 주문이면서 배달주소가 없는 주문인 경우 IllegalArgumentException 에러가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createNoDeliveryAddressOfOrder(String deliveryAddress){
            Order requestOrder = new Order();
            requestOrder.setType(OrderType.DELIVERY);
            requestOrder.setOrderLineItems(주문할_메뉴들);
            requestOrder.setOrderTable(사용중인_주문테이블);
            requestOrder.setOrderTableId(사용중인_주문테이블.getId());
            requestOrder.setDeliveryAddress(deliveryAddress);

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }

        @DisplayName("만약에 먹고가기 주문이면서 주문테이블이 가득찬 상태가 아닌 주문인 경우 IllegalStateException 에러가 발생한다.")
        @Test
        void createOrderTableIsNotOccupiedOfOrder(){
            OrderTable 미사용_주문테이블 = OrderTableTestHelper.특정_주문테이블_사용여부_변경(사용중인_주문테이블.getId(), false);

            Order requestOrder = new Order();
            requestOrder.setType(OrderType.EAT_IN);
            requestOrder.setOrderLineItems(주문할_메뉴들);
            requestOrder.setOrderTable(미사용_주문테이블);
            requestOrder.setOrderTableId(미사용_주문테이블.getId());

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.create(requestOrder));
        }
    }

    @DisplayName("생성된 대기 주문을 접수로 상태를 변경한다.")
    @ParameterizedTest
    @ValueSource(strings = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void acceptOrder(String orderType){
        Order requestOrder = OrderTestHelper.대기_주문_생성(OrderType.valueOf(orderType), 주문할_메뉴들, 사용중인_주문테이블);

        Order acceptOrder = orderService.accept(requestOrder.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.ACCEPTED);
    }

    @Nested
    @DisplayName("접수 상태로 변경하려는 대기 주문이 ")
    class acceptOrderExceptionTestCase{
        @DisplayName("만약에 생되어 있지 않는 주문인 경우 NoSuchElementException 에러가 발생한다.")
        @Test
        void acceptNoOrder(){
            UUID 없는_주문ID = UUID.randomUUID();

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.accept(없는_주문ID));
        }

        @DisplayName("만약에 주문상태가 대기가 아닌 경우 IllegalStateException 에러가 발생한다.")
        @Test
        void acceptNoWaitingOrder(){
            Order 대기상태가_아닌_주문 = OrderTestHelper.생성한_주문_상태_변경(대기상태인_먹고가기_주문.getId(), OrderStatus.COMPLETED);

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.accept(대기상태가_아닌_주문.getId()));
        }
    }

    @DisplayName("접수된 주문을 서빙 상태로 변경한다.")
    @ParameterizedTest
    @ValueSource(strings = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void serveOrder(String orderType){
        Order requestOrder = OrderTestHelper.대기_주문_생성(OrderType.valueOf(orderType), 주문할_메뉴들, 사용중인_주문테이블);
        requestOrder = OrderTestHelper.생성한_주문_상태_변경(requestOrder.getId(), OrderStatus.ACCEPTED);

        Order acceptOrder = orderService.serve(requestOrder.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.SERVED);
    }

    @DisplayName("배달하기로 주문한 주문건의 상태를 배달시작으로 변경한다.")
    @Test
    void startDeliveryOrder(){
        Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_배달하기_주문.getId(), OrderStatus.SERVED);

        Order acceptOrder = orderService.startDelivery(order.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.DELIVERING);
    }

    @Nested
    @DisplayName("배달시작으로 변경하려는 배달하기 주문이")
    class startDeliveryOrderExceptionTestCase{
        @DisplayName("만약에 주문유형이 배달하기가 아닌 주문인 경우 IllegalStateException 예외가 발생한다. ")
        @ParameterizedTest
        @ValueSource(strings = {"TAKEOUT", "EAT_IN"})
        void startDeliveryOrderOfNotDeliveryOrderType(String orderType){
            Order requestOrder = OrderTestHelper.대기_주문_생성(OrderType.valueOf(orderType), 주문할_메뉴들, 사용중인_주문테이블);
            requestOrder = OrderTestHelper.생성한_주문_상태_변경(requestOrder.getId(), OrderStatus.SERVED);

            UUID id = requestOrder.getId();
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.startDelivery(id));
        }

        @DisplayName("만약에 주문 상태가 서빙이 아닌 경우 IllegalStateException 예외가 발생한다. ")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void startDeliveryOrderOfNotServedOrderStatus(String orderStatus){
            Order requestOrder = OrderTestHelper.생성한_주문_상태_변경(대기상태인_배달하기_주문.getId(), OrderStatus.valueOf(orderStatus));

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.startDelivery(requestOrder.getId()));
        }
    }

    @DisplayName("배달중인 주문건의 상태를 배달완료로 변경한다.")
    @Test
    void completeDeliveryOrder(){
        Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_배달하기_주문.getId(), OrderStatus.DELIVERING);

        Order acceptOrder = orderService.completeDelivery(order.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.DELIVERED);
    }

    @Nested
    @DisplayName("배달완료로 변경하려는 배달중 상태인 주문이")
    class completeDeliveryOrderExceptionTestCase{
        @DisplayName("만약에 생성되지 않은 주문인 경우 NoSuchElementException 예외가 발생한다.")
        @Test
        void completeDeliveryOfNoOrder(){
            UUID 없는_주문ID = UUID.randomUUID();

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.completeDelivery(없는_주문ID));
        }

        @DisplayName("만약에 배달중 상태가 아닌 주문인 경우 IllegalStateException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
        void completeDeliveryOfNoDeliveringOrderStatus(String orderStatus){
            Order requestOrder = OrderTestHelper.생성한_주문_상태_변경(대기상태인_배달하기_주문.getId(), OrderStatus.valueOf(orderStatus));

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.completeDelivery(requestOrder.getId()));
        }
    }

    @DisplayName("주문건의 상태를 주문완료로 변경한다.")
    @ParameterizedTest
    @ValueSource(strings = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void completeOrder(String orderType){
        Order requestOrder = OrderTestHelper.대기_주문_생성(OrderType.valueOf(orderType), 주문할_메뉴들, 사용중인_주문테이블);
        requestOrder = OrderTestHelper.생성한_주문_상태_변경(requestOrder.getId(), OrderType.DELIVERY.equals(requestOrder.getType()) ? OrderStatus.DELIVERED : OrderStatus.SERVED);

        Order completeOrder = orderService.complete(requestOrder.getId());

        assertThat(completeOrder.getStatus()).isSameAs(OrderStatus.COMPLETED);

        if (OrderType.EAT_IN.equals(requestOrder.getType())) {
            Order order = orderRepository.findById(completeOrder.getId()).orElseThrow(() -> new IllegalArgumentException("can't find order"));
            assertThat(order.getOrderTable().getNumberOfGuests()).isSameAs(0);
            assertThat(order.getOrderTable().isOccupied()).isSameAs(false);
        }
    }

    @Nested
    @DisplayName("주문 완료로 변경하려는 주문이")
    class completeOrderExceptionTestCase{
        @DisplayName("만약에 생성되지 않은 주문인 경우 NoSuchElementException 예외가 발생한다.")
        @Test
        void completeNoOrder(){
            UUID 없는_주문_ID = UUID.randomUUID();

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.complete(없는_주문_ID));
        }

        @DisplayName("만약에 배달하기 주문이면서 주문상태가 배달완료가 아닌 경우 IllegalStateException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
        void completeDeliverOrderOfNoDeliveringOrderStatus(String orderStatus){
            Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_배달하기_주문.getId(), OrderStatus.valueOf(orderStatus));

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.complete(order.getId()));
        }

        @DisplayName("만약에 먹고가기 주문이면서 주문상태가 서빙이 아닌 경우 IllegalStateException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void completeEatInOrderOfServedOrderStatus(String orderStatus){
            Order eatInOrder = OrderTestHelper.생성한_주문_상태_변경(대기상태인_먹고가기_주문.getId(), OrderStatus.valueOf(orderStatus));

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.complete(eatInOrder.getId()));
        }

        @DisplayName("만약에 포장하기 주문이면서 주문상태가 서빙이 아닌 경우 IllegalStateException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void completeTakeOutOrderOfServedOrderStatus(String orderStatus){
            Order eatInOrder = OrderTestHelper.생성한_주문_상태_변경(대기상태인_포장하기_주문.getId(), OrderStatus.valueOf(orderStatus));

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.complete(eatInOrder.getId()));
        }
    }

    @DisplayName("모든 주문리스트를 조회한다.")
    @Test
    void findAllOrders(){
        List<Order> orders = orderService.findAll();
        assertThat(orders.size()).isSameAs(주문들.size());
    }
}
