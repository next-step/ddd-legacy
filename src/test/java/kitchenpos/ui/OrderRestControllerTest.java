package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

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
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드+후라이드");


        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(BigDecimal.valueOf(16000));
        orderLineItem.setQuantity(3L);

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(orderTable.getId());
        request.setOrderLineItems(Arrays.asList(orderLineItem));

        Order response = new Order();
        response.setId(UUID.randomUUID());
        response.setOrderTableId(orderTable.getId());
        response.setOrderLineItems(request.getOrderLineItems());


        given(orderService.create(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.orderTableId").value(response.getOrderTableId().toString()));

    }

    @Test
    void accept() throws Exception {

        //given
        UUID orderId = UUID.randomUUID();
        Order response = new Order();
        response.setId(orderId);
        response.setStatus(OrderStatus.ACCEPTED);

        given(orderService.accept(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/accept", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void serve() throws Exception {

        //given
        UUID orderId = UUID.randomUUID();
        Order response = new Order();
        response.setId(orderId);
        response.setStatus(OrderStatus.SERVED);

        given(orderService.serve(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/serve", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void startDelivery() throws Exception {

        //given
        UUID orderId = UUID.randomUUID();
        Order response = new Order();
        response.setId(orderId);
        response.setStatus(OrderStatus.DELIVERING);

        given(orderService.startDelivery(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void completeDelivery() throws Exception {

        //given
        UUID orderId = UUID.randomUUID();
        Order response = new Order();
        response.setId(orderId);
        response.setStatus(OrderStatus.DELIVERED);

        given(orderService.completeDelivery(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void complete() throws Exception {

        //given
        UUID orderId = UUID.randomUUID();
        Order response = new Order();
        response.setId(orderId);
        response.setStatus(OrderStatus.COMPLETED);

        given(orderService.complete(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/orders/{orderId}/complete", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    void findAll() throws Exception {

        //given
        Order order1 = new Order();
        order1.setId(UUID.randomUUID());
        order1.setType(OrderType.EAT_IN);
        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setType(OrderType.DELIVERY);

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