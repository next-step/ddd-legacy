package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static kitchenpos.fixture.OrderFixture.order;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    void 주문_생성_요청이_성공하면_주문정보를_반환한다() throws Exception {
        final Order order = order();
        when(orderService.create(any(Order.class))).thenReturn(order);
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order(null, order.getType(), order.getStatus(), order.getOrderDateTime(), order.getOrderLineItems(), order.getDeliveryAddress(), order.getOrderTable(), order.getOrderTableId())))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }


    @Test
    void 주문_승인_요청이_성공하면_상태가_변경된다() throws Exception {
        final Order order = order();

        when(orderService.accept(order.getId())).thenReturn(order(order.getId(), order.getType(), OrderStatus.ACCEPTED, order.getOrderDateTime(), order.getOrderLineItems(), order.getDeliveryAddress(), order.getOrderTable(), order.getOrderTableId()));

        mockMvc.perform(put("/api/orders/{orderId}/accept", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.name()));

    }

    @Test
    void 주문_서빙_요청이_성공하면_상태가_변경된다() throws Exception {
        final Order order = order();

        when(orderService.serve(order.getId())).thenReturn(order(order.getId(), order.getType(), OrderStatus.SERVED, order.getOrderDateTime(), order.getOrderLineItems(), order.getDeliveryAddress(), order.getOrderTable(), order.getOrderTableId()));

        mockMvc.perform(put("/api/orders/{orderId}/serve", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.SERVED.name()));
    }

    @Test
    void 주문_배달_시작_요청이_성공하면_배달_상태가_변경된다() throws Exception {
        final Order order = order();

        when(orderService.startDelivery(order.getId())).thenReturn(order(order.getId(), order.getType(), OrderStatus.DELIVERING, order.getOrderDateTime(), order.getOrderLineItems(), order.getDeliveryAddress(), order.getOrderTable(), order.getOrderTableId()));

        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERING.name()));
    }

    @Test
    void 주문_배달됨_요청이_성공하면_상태가_변경된다() throws Exception {
        final Order order = order();

        when(orderService.completeDelivery(order.getId())).thenReturn(order(order.getId(), order.getType(), OrderStatus.DELIVERED, order.getOrderDateTime(), order.getOrderLineItems(), order.getDeliveryAddress(), order.getOrderTable(), order.getOrderTableId()));

        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.name()));
    }

    @Test
    void 주문_배달_완료_요청이_성공하면_상태가_변경된다() throws Exception {
        final Order order = order();

        when(orderService.complete(order.getId())).thenReturn(order(order.getId(), order.getType(), OrderStatus.COMPLETED, order.getOrderDateTime(), order.getOrderLineItems(), order.getDeliveryAddress(), order.getOrderTable(), order.getOrderTableId()));

        mockMvc.perform(put("/api/orders/{orderId}/complete", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.name()));
    }

    @Test
    void 주문_조회_요청이_성공하면_주문정보를_반환한다() throws Exception {
        when(orderService.findAll()).thenReturn(List.of(order()));

        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
