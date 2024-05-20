package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        //given
        Product product = ProductTestFixture.createProduct("후라이드", 1000L);
        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        Menu menu = MenuTestFixture.createMenu("후라이드+후라이드", 19000L,true, List.of(menuProduct));
        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1,menu);
        Order request = OrderTestFixture.createOrderRequest(OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(),List.of(orderLineItem));
        Order response = OrderTestFixture.createOrder(OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(),List.of(orderLineItem));

        given(orderService.create(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(response.getId().toString()));

    }

    @Test
    void accept() throws Exception {

        //given
        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1,new Menu());
        Order order = OrderTestFixture.createOrder(OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(),List.of(orderLineItem));
        Order response = OrderTestFixture.createOrderRequest(OrderType.EAT_IN, OrderStatus.ACCEPTED, LocalDateTime.now(),List.of(orderLineItem));;

        given(orderService.accept(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/accept", order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void serve() throws Exception {

        //given
        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1,new Menu());
        Order order = OrderTestFixture.createOrder(OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(),List.of(orderLineItem));
        Order response = OrderTestFixture.createOrder(OrderType.EAT_IN, OrderStatus.SERVED, LocalDateTime.now(),List.of(orderLineItem));;

        given(orderService.serve(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/serve", order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));

    }

    @Test
    void startDelivery() throws Exception {

        //given
        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1,new Menu());
        Order order = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.SERVED, LocalDateTime.now(),List.of(orderLineItem));
        Order response = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING, LocalDateTime.now(),List.of(orderLineItem));;

        given(orderService.startDelivery(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));

    }

    @Test
    void completeDelivery() throws Exception {

        //given
        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1,new Menu());
        Order order = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING, LocalDateTime.now(),List.of(orderLineItem));
        Order response = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, LocalDateTime.now(),List.of(orderLineItem));;

        given(orderService.completeDelivery(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));

    }

    @Test
    void complete() throws Exception {

        //given
        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1,new Menu());
        Order order = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, LocalDateTime.now(),List.of(orderLineItem));
        Order response = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.COMPLETED, LocalDateTime.now(),List.of(orderLineItem));;

        given(orderService.complete(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/complete", order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void findAll() throws Exception {

        //given
        Order order1 = OrderTestFixture.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, LocalDateTime.now(),List.of(new OrderLineItem()));
        Order order2 = OrderTestFixture.createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED, LocalDateTime.now(),List.of(new OrderLineItem()));

        given(orderService.findAll())
                .willReturn(Arrays.asList(order1, order2));

        //when then
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order1.getId().toString()))
                .andExpect(jsonPath("$[0].type").value(order1.getType().toString()))
                .andExpect(jsonPath("$[1].id").value(order2.getId().toString()))
                .andExpect(jsonPath("$[1].type").value(order2.getType().toString()));

    }
}