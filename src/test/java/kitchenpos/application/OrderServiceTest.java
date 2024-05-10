package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupTestHelper;
import kitchenpos.helper.MenuProductTestHelper;
import kitchenpos.helper.MenuTestHelper;
import kitchenpos.helper.OrderTableTestHelper;
import kitchenpos.helper.OrderTestHelper;
import kitchenpos.helper.ProductTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceTest extends SetupTest{
    @Autowired
    private OrderService orderService;

    private List<OrderLineItem> 주문할_메뉴들 = new ArrayList<>();
    private OrderTable 사용중인_주문테이블;
    private Order 대기상태인_먹고가기_주문;
    private Order 대기상태인_포장하기_주문;

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

        OrderLineItem 마라세트_주문 = new OrderLineItem();
        마라세트_주문.setMenuId(마라세트.getId());
        마라세트_주문.setMenu(마라세트);
        마라세트_주문.setQuantity(1);
        마라세트_주문.setPrice(마라세트.getPrice());

        OrderLineItem 나홀로세트_주문 = new OrderLineItem();
        나홀로세트_주문.setMenuId(나홀로세트.getId());
        나홀로세트_주문.setMenu(나홀로세트);
        나홀로세트_주문.setQuantity(2);
        나홀로세트_주문.setPrice(나홀로세트.getPrice());

        주문할_메뉴들 = Arrays.asList(마라세트_주문, 나홀로세트_주문);
        사용중인_주문테이블 = OrderTableTestHelper.사용중인_주문테이블_생성("사용중인_주문테이블", 4);

        대기상태인_먹고가기_주문 = OrderTestHelper.대기_주문_생성(OrderType.EAT_IN, 주문할_메뉴들, 사용중인_주문테이블);
        대기상태인_포장하기_주문 = OrderTestHelper.대기_주문_생성(OrderType.DELIVERY, 주문할_메뉴들, 사용중인_주문테이블);
    }

    @DisplayName("주문을 생성하다.")
    @Test
    void createOrder(){
        OrderTable orderTable = OrderTableTestHelper.사용중인_주문테이블_생성("주문테이블", 4);

        Order requestOrder = new Order();
        requestOrder.setType(OrderType.EAT_IN);
        requestOrder.setOrderLineItems(주문할_메뉴들);
        requestOrder.setOrderTable(orderTable);
        requestOrder.setOrderTableId(orderTable.getId());

        Order createOrder = orderService.create(requestOrder);
        assertThat(createOrder.getType()).isSameAs(requestOrder.getType());
        assertThat(createOrder.getStatus()).isSameAs(OrderStatus.WAITING);
    }

    @DisplayName("생성된 대기 주문을 접수로 상태를 변경한다.")
    @Test
    void acceptOrder(){
        Order acceptOrder = orderService.accept(대기상태인_먹고가기_주문.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.ACCEPTED);
    }

    @DisplayName("접수된 주문을 서빙 상태로 변경한다.")
    @Test
    void serveOrder(){
        Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_먹고가기_주문.getId(), OrderStatus.ACCEPTED);

        Order acceptOrder = orderService.serve(order.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.ACCEPTED);
    }

    @DisplayName("포장하기로 주문한 주문건의 상태를 배달시작으로 변경한다.")
    @Test
    void startDeliveryOrder(){
        Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_포장하기_주문.getId(), OrderStatus.SERVED);

        Order acceptOrder = orderService.startDelivery(order.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.DELIVERING);
    }

    @DisplayName("배달중인 주문건의 상태를 배달완료로 변경한다.")
    @Test
    void completeDeliveryOrder(){
        Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_포장하기_주문.getId(), OrderStatus.DELIVERING);

        Order acceptOrder = orderService.completeDelivery(order.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.DELIVERED);
    }

    @DisplayName("주문건의 상태를 주문완료로 변경한다.")
    @Test
    void completeOrder(){
        Order order = OrderTestHelper.생성한_주문_상태_변경(대기상태인_먹고가기_주문.getId(), OrderStatus.SERVED);

        Order acceptOrder = orderService.complete(order.getId());
        assertThat(acceptOrder.getStatus()).isSameAs(OrderStatus.COMPLETED);
    }
}
