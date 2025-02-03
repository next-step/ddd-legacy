package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void create() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("address");
        order.setStatus(OrderStatus.WAITING);

        given(orderService.create(any())).willReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.type").value(order.getType().toString()))
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void accept() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("address");

        given(orderService.accept(any())).willReturn(order);
        order.setStatus(OrderStatus.ACCEPTED);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.type").value(order.getType().toString()))
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void serve() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("address");

        given(orderService.serve(any())).willReturn(order);
        order.setStatus(OrderStatus.SERVED);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/serve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.type").value(order.getType().toString()))
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void startDelivery() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("address");

        given(orderService.startDelivery(any())).willReturn(order);
        order.setStatus(OrderStatus.DELIVERING);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/start-delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.type").value(order.getType().toString()))
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void completeDelivery() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("address");

        given(orderService.completeDelivery(any())).willReturn(order);
        order.setStatus(OrderStatus.DELIVERED);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/complete-delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.type").value(order.getType().toString()))
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void complete() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("address");

        given(orderService.complete(any())).willReturn(order);
        order.setStatus(OrderStatus.COMPLETED);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.type").value(order.getType().toString()))
                .andExpect(jsonPath("$.deliveryAddress").value(order.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void findAll() throws Exception {
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setId(UUID.randomUUID());
        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        orders.add(order1);
        orders.add(order2);

        given(orderService.findAll()).willReturn(orders);

        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(order1.getId().toString())))
                .andExpect(jsonPath("$[1].id", is(order2.getId().toString())));
    }
}
