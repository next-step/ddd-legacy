package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.OrderBo;
import kitchenpos.model.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderRestControllerTests {
    @InjectMocks
    private OrderRestController orderRestController;

    @Mock
    private OrderBo orderBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static Order mockCreated;
    private static Order mockChanged;
    private static List<Order> mockOrders = new ArrayList<>();

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderRestController).alwaysDo(print()).build();
    }

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
        Order requestOrder = new Order();
        given(orderBo.create(any(Order.class))).willReturn(mockCreated);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestOrder)))
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
        Order requestOrder = new Order();
        given(orderBo.changeOrderStatus(eq(1L), any(Order.class))).willReturn(mockChanged);

        mockMvc.perform(put("/api/orders/1/order-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestOrder)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"orderTableId\":2")))
        ;
    }
}
