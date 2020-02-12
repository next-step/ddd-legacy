package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.OrderBo;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderBo orderBo;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("주문을 생성할 수 있어야 한다.")
    @Test
    void create() throws Exception {
        // given
        Order requestOrder = createUnregisteredOrderWithStatus("COOKING");
        Order responseOrder = createRegisteredOrderWithId(requestOrder, new Random().nextLong());

        given(orderBo.create(any(Order.class)))
                .willReturn(responseOrder);

        // when
        ResultActions result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestOrder)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/orders/" + responseOrder.getId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseOrder)));
    }

    @DisplayName("주문 목록을 볼 수 있어야 한다.")
    @Test
    void list() throws Exception {
        // given
        Order order1 = createRegisteredOrderWithId(1L);
        Order order2 = createRegisteredOrderWithId(2L);
        Order order3 = createRegisteredOrderWithId(3L);

        given(orderBo.list())
                .willReturn(Arrays.asList(order1, order2, order3));

        // when
        ResultActions result = mockMvc.perform(get("/api/orders"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(order1.getOrderStatus())))
                .andExpect(content().string(containsString(order2.getOrderStatus())))
                .andExpect(content().string(containsString(order3.getOrderStatus())));
    }

    @DisplayName("주문 상태를 변경할 수 있어야 한다.")
    @Test
    void changeOrderStatus() throws Exception {
        // given
        Order requestOrder = createUnregisteredOrderWithStatus("MEAL");

        Long orderId = new Random().nextLong();
        Order responseOrder = createRegisteredOrderWithId(requestOrder, orderId);

        given(orderBo.changeOrderStatus(eq(orderId), any(Order.class)))
                .willReturn(responseOrder);

        // when
        ResultActions result = mockMvc.perform(put("/api/orders/{orderId}/order-status", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestOrder)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseOrder)));
    }

    private Order createUnregisteredOrderWithStatus(String orderStatus) {
        return new Order() {{
            setOrderTableId(1L);
            setOrderStatus(orderStatus);
            setOrderLineItems(Arrays.asList(
                    new OrderLineItem() {{
                        setSeq(0L);
                        setMenuId(1L);
                        setQuantity(1);
                    }},
                    new OrderLineItem() {{
                        setSeq(1L);
                        setMenuId(2L);
                        setQuantity(4);
                    }}
            ));
        }};
    }

    private Order createRegisteredOrderWithId(Long orderId) {
        Order order = createUnregisteredOrderWithStatus(orderId % 2 == 1 ? "COOKING" : "MEAL");
        order.setId(orderId);

        return order;
    }

    private Order createRegisteredOrderWithId(Order unregisteredOrder, Long orderId) {
        return new Order() {{
            setId(orderId);
            setOrderTableId(unregisteredOrder.getOrderTableId());
            setOrderStatus(unregisteredOrder.getOrderStatus());
            setOrderLineItems(Arrays.asList(
                    new OrderLineItem() {{
                        setSeq(unregisteredOrder.getOrderLineItems().get(0).getSeq());
                        setOrderId(orderId);
                        setMenuId(unregisteredOrder.getOrderLineItems().get(0).getMenuId());
                        setQuantity(unregisteredOrder.getOrderLineItems().get(0).getQuantity());
                    }},
                    new OrderLineItem() {{
                        setSeq(unregisteredOrder.getOrderLineItems().get(1).getSeq());
                        setOrderId(orderId);
                        setMenuId(unregisteredOrder.getOrderLineItems().get(1).getMenuId());
                        setQuantity(unregisteredOrder.getOrderLineItems().get(1).getQuantity());
                    }}
            ));
        }};
    }
}
