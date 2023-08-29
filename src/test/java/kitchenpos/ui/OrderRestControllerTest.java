package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.application.OrderService;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    @DisplayName("주문 생성 API")
    void create() throws Exception {
        var request = TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN);
        var response = TestFixture.copy(request);
        given(orderService.create(any())).willReturn(response);

        var result = mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/" + response.getId()))
                .andExpect(jsonPath("id").value(response.getId().toString()))
                .andExpect(jsonPath("status").value(response.getStatus().name()))
                .andExpect(jsonPath("orderTableId").value(response.getOrderTableId().toString()))
                .andExpect(jsonPath("orderLineItems").isArray());
    }

    @Test
    @DisplayName("주문 접수 API")
    void accept() throws Exception {
        var response = TestFixture.createOrder(OrderStatus.ACCEPTED, OrderType.EAT_IN);
        given(orderService.accept(any())).willReturn(response);

        var result = mockMvc.perform(
                put("/api/orders/{orderId}/accept", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(response.getId().toString()))
                .andExpect(jsonPath("status").value(response.getStatus().name()))
                .andExpect(jsonPath("orderTableId").value(response.getOrderTableId().toString()))
                .andExpect(jsonPath("orderLineItems").isArray());
    }

    @Test
    @DisplayName("주문 서빙 완료 API")
    void serve() throws Exception {
        var response = TestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN);
        given(orderService.serve(any())).willReturn(response);

        var result = mockMvc.perform(
                put("/api/orders/{orderId}/serve", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(response.getId().toString()))
                .andExpect(jsonPath("status").value(response.getStatus().name()))
                .andExpect(jsonPath("orderTableId").value(response.getOrderTableId().toString()))
                .andExpect(jsonPath("orderLineItems").isArray());
    }

    @Test
    @DisplayName("주문 배달 시작 API")
    void startDelivery() throws Exception {
        var response = TestFixture.createOrder(OrderStatus.DELIVERING, OrderType.DELIVERY);
        given(orderService.startDelivery(any())).willReturn(response);

        var result = mockMvc.perform(
                put("/api/orders/{orderId}/start-delivery", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(response.getId().toString()))
                .andExpect(jsonPath("status").value(response.getStatus().name()))
                .andExpect(jsonPath("orderTableId").value(response.getOrderTableId().toString()))
                .andExpect(jsonPath("orderLineItems").isArray());
    }

    @Test
    @DisplayName("주문 배달 완료 API")
    void completeDelivery() throws Exception {
        var response = TestFixture.createOrder(OrderStatus.COMPLETED, OrderType.DELIVERY);
        given(orderService.completeDelivery(any())).willReturn(response);

        var result = mockMvc.perform(
                put("/api/orders/{orderId}/complete-delivery", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(response.getId().toString()))
                .andExpect(jsonPath("status").value(response.getStatus().name()))
                .andExpect(jsonPath("orderTableId").value(response.getOrderTableId().toString()))
                .andExpect(jsonPath("orderLineItems").isArray());
    }

    @Test
    @DisplayName("주문 완료 API")
    void complete() throws Exception {
        var response = TestFixture.createOrder(OrderStatus.COMPLETED, OrderType.EAT_IN);
        given(orderService.complete(any())).willReturn(response);

        var result = mockMvc.perform(
                put("/api/orders/{orderId}/complete", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(response.getId().toString()))
                .andExpect(jsonPath("status").value(response.getStatus().name()))
                .andExpect(jsonPath("orderTableId").value(response.getOrderTableId().toString()))
                .andExpect(jsonPath("orderLineItems").isArray());
    }

    @Test
    @DisplayName("주문 목록 조회 API")
    void findAll() throws Exception {
        var response = List.of(
                TestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN),
                TestFixture.createOrder(OrderStatus.ACCEPTED, OrderType.EAT_IN),
                TestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN),
                TestFixture.createOrder(OrderStatus.COMPLETED, OrderType.EAT_IN)
        );
        given(orderService.findAll()).willReturn(response);

        var result = mockMvc.perform(
                get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();
    }
}
