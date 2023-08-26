package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.fixture.OrderFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.OrderFixtures.*;
import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private static final String BASE_URL = "/api/orders";

    private OrderLineItem orderLineItem;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        MenuProduct menuProduct = createMenuProduct();
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));
        orderTable = createOrderTable();
        orderLineItem = createOrderLineItem(1L, menu.getPrice(), menu);
    }

    @DisplayName("주문을 생성한다")
    @Test
    void create() throws Exception {
        // given
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), null);
        Order order = createOrder(OrderType.EAT_IN, List.of(orderLineItem), null);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderTable(orderTable);

        given(orderService.create(any())).willReturn(order);

        // when
        ResultActions result = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()))
                .andExpect(jsonPath("$.orderLineItems.size()").value(1))
                .andExpect(jsonPath("$.deliveryAddress").doesNotExist())
                .andExpect(jsonPath("$.orderTable").exists());
    }

    @DisplayName("주문을 수락한다")
    @Test
    void accept() throws Exception {
        // given
        Order request = OrderFixtures.createOrder(OrderType.TAKEOUT, OrderStatus.WAITING);
        Order order = OrderFixtures.createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderService.accept(any())).willReturn(order);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderId}/accept", order.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()));
    }

    @DisplayName("주문을 제공한다")
    @Test
    void serve() throws Exception {
        // given
        Order request = OrderFixtures.createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);
        Order order = OrderFixtures.createOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

        given(orderService.serve(any())).willReturn(order);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderId}/serve", order.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()));
    }

    @DisplayName("배달을 시작한다")
    @Test
    void startDelivery() throws Exception {
        // given
        Order request = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED);
        Order order = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderService.startDelivery(any())).willReturn(order);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderId}/start-delivery", order.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()));
    }

    @DisplayName("배달을 완료한다")
    @Test
    void completeDelivery() throws Exception {
        // given
        Order request = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);
        Order order = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

        given(orderService.completeDelivery(any())).willReturn(order);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderId}/complete-delivery", order.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()));
    }

    @DisplayName("주문을 완료한다")
    @Test
    void complete() throws Exception {
        // given
        Order request = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);
        Order order = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.COMPLETED);

        given(orderService.complete(any())).willReturn(order);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderId}/complete", order.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(order.getType().name()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()));
    }

    @DisplayName("모든 주문을 조회한다")
    @Test
    void findAll() throws Exception {
        // given
        Order order1 = OrderFixtures.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);
        Order order2 = OrderFixtures.createOrder(OrderType.EAT_IN, OrderStatus.COMPLETED);

        given(orderService.findAll()).willReturn(List.of(order1, order2));

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].type").value(order1.getType().name()))
                .andExpect(jsonPath("$[1].type").value(order2.getType().name()))
                .andExpect(jsonPath("$[0].status").value(order1.getStatus().name()))
                .andExpect(jsonPath("$[1].status").value(order2.getStatus().name()));
    }
}
