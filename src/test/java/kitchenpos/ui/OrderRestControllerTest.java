package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.application.MenuServiceTest.createMenu;
import static kitchenpos.application.MenuServiceTest.createMenuProduct;
import static kitchenpos.application.OrderServiceTest.*;
import static kitchenpos.application.OrderTableServiceTest.createOrderTable;
import static kitchenpos.application.ProductServiceTest.createProduct;
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

    private Menu menu;
    private OrderLineItem orderLineItem;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        Product product = createProduct("햄버거", new BigDecimal("1000"));
        MenuProduct menuProduct = createMenuProduct(product, 1L);

        menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));
        orderTable = createOrderTable("테이블1", 3);
        orderLineItem = createOrderLineItem(1L, menu.getPrice(), menu);
    }

    @Test
    void 주문을_생성한다() throws Exception {
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

    @Test
    void 주문을_수락한다() throws Exception {
        // given
        Order request = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.WAITING);
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

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

    @Test
    void 주문을_제공한다() throws Exception {
        // given
        Order request = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

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

    @Test
    void 배달을_시작한다() throws Exception {
        // given
        Order request = createOngoingOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED);
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

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

    @Test
    void 배달을_완료한다() throws Exception {
        // given
        Order request = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

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

    @Test
    void 주문을_완료한다() throws Exception {
        // given
        Order request = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.COMPLETED);

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

    @Test
    void 모든_주문을_조회한다() throws Exception {
        // given
        Order order1 = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);
        Order order2 = createOngoingOrder(OrderType.EAT_IN, OrderStatus.COMPLETED);

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
