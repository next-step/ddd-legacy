package kitchenpos.controller;

import kitchenpos.bo.OrderBo;
import kitchenpos.model.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderBo orderBo;

    private static Order mockCreated;
    private static Order mockChanged;
    private static List<Order> mockOrders = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        mockCreated = new Order();
        mockCreated.setId(1L);
        mockChanged = new Order();
        mockChanged.setId(1L);
        mockChanged.setOrderTableId(2L);

        mockOrders.add(new Order());
    }

    @DisplayName("POST 주문 정상 성공(201)")
    @Test
    public void postOrderSuccess() throws Exception {
        given(orderBo.create(any(Order.class))).willReturn(mockCreated);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"orderTableId\": 1,\n" +
                        "  \"orderLineItems\": [\n" +
                        "    {\n" +
                        "      \"menuId\": 1,\n" +
                        "      \"quantity\": 1\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/orders/1"))
                .andExpect(content().string(containsString("\"id\":1")))
        ;
    }

    @DisplayName("GET 주문 콜렉션 성공(200)")
    @Test
    public void getOrdersSuccess() throws Exception {
        given(orderBo.list()).willReturn(mockOrders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("orderLineItems")))
        ;
    }

    @DisplayName("PUT 주문 정상 성공(200)")
    @Test
    public void putOrderSuccess() throws Exception {
        given(orderBo.changeOrderStatus(eq(1L), any(Order.class))).willReturn(mockChanged);

        mockMvc.perform(put("/api/orders/1/order-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"orderTableId\": 2,\n" +
                        "  \"orderLineItems\": [\n" +
                        "    {\n" +
                        "      \"menuId\": 1,\n" +
                        "      \"quantity\": 1\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"orderTableId\":2")))
        ;
    }
}
