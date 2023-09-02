package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static kitchenpos.fixture.OrderFixtures.createOrder;
import static kitchenpos.fixture.OrderFixtures.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;
    Product product1;
    MenuProduct menuProduct1;
    Product product2;
    MenuProduct menuProduct2;
    MenuGroup menuGroup;
    Menu menu;

    @BeforeEach
    void setUp() {
        product1 = createProduct("상품1", new BigDecimal(1000));
        menuProduct1 = createMenuProduct(product1, 1);
        product2 = createProduct("상품2", new BigDecimal(2000));
        menuProduct2 = createMenuProduct(product2, 2);
        menuGroup = createMenuGroup("메뉴그룹1");
        menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup, false, List.of(menuProduct1, menuProduct2));
    }

    @Test
    void 주문을_생성한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderService.create(any()))
                .willReturn(order);

        //when
        ResultActions result = mvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.orderDateTime").exists())
                .andExpect(jsonPath("$.status").value(order.getStatus().name()))
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.orderLineItems", hasSize(order.getOrderLineItems().size())))
                .andExpect(jsonPath("$.orderTable.name").value(orderTable.getName()))
                .andExpect(jsonPath("$.orderTable.occupied").value(orderTable.isOccupied()))
                .andExpect(jsonPath("$.orderTable.numberOfGuests").value(orderTable.getNumberOfGuests()));
    }

    @Test
    void 주문_승인_상태로_변경_가능하다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.ACCEPTED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderService.accept(any()))
                .willReturn(order);

        //when
        ResultActions result = mvc.perform(put("/api/orders/{orderId}/accept", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.name()));
    }

    @Test
    void 주문_서빙완료_상태로_변경_가능하다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderService.serve(any()))
                .willReturn(order);

        //when
        ResultActions result = mvc.perform(put("/api/orders/{orderId}/serve", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.SERVED.name()));
    }

    @Test
    void 배송을_시작한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERING, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderService.startDelivery(any()))
                .willReturn(order);

        //when
        ResultActions result = mvc.perform(put("/api/orders/{orderId}/start-delivery", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERING.name()));
    }

    @Test
    void 배송을_완료한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderService.completeDelivery(any()))
                .willReturn(order);

        //when
        ResultActions result = mvc.perform(put("/api/orders/{orderId}/complete-delivery", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.name()));
    }

    @Test
    void 주문을_완료한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.COMPLETED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderService.complete(any()))
                .willReturn(order);

        //when
        ResultActions result = mvc.perform(put("/api/orders/{orderId}/complete", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.name()));
    }

    @Test
    void 모든_주문을_조회한다() throws Exception {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderTable orderTable1 = createOrderTable("주문테이블1", false, 1);
        OrderLineItem orderLineItem1 = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        Order order1 = createOrder("서울", orderTable1, orderDateTime, OrderStatus.DELIVERED, OrderType.EAT_IN, List.of(orderLineItem1));
        OrderTable orderTable2 = createOrderTable("주문테이블2", false, 1);
        OrderLineItem orderLineItem2 = createOrderLineItem(menu, 1L, new BigDecimal("2000"), 1L);
        Order order2 = createOrder("서울", orderTable2, orderDateTime, OrderStatus.ACCEPTED, OrderType.EAT_IN, List.of(orderLineItem2));

        List<Order> orders = List.of(order1, order2);

        given(orderService.findAll())
                .willReturn(orders);

        //when
        ResultActions result = mvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orders)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orders.size())));
    }
}