package kitchenpos.ui;

import kitchenpos.DummyData;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest extends DummyData {

    @Autowired
    private MockMvc webMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private OrderService orderService;

    @BeforeEach
    void utf8Filter() {
        this.webMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @DisplayName("주문 생성")
    @Test
    void createOrder() throws Exception {
        Order create = orders.get(0);

        Order order = new Order();
        order.setType(create.getType());
        order.setOrderTableId(create.getOrderTable().getId());
        order.setOrderLineItems(orderLineItems);

        given(orderService.create(any())).willReturn(create);

        ResultActions perform = webMvc.perform(
                post("/api/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order))
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("주문 접수")
    @Test
    void accept() throws Exception {
        Order accept = orders.get(0);
        accept.setStatus(OrderStatus.ACCEPTED);

        given(orderService.accept(any())).willReturn(accept);

        ResultActions perform = webMvc.perform(
                put("/api/orders/{orderId}/accept", accept.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.toString()));
    }

    @DisplayName("주문 서빙")
    @Test
    void serve() throws Exception {
        Order serve = orders.get(0);
        serve.setStatus(OrderStatus.SERVED);

        given(orderService.accept(any())).willReturn(serve);

        ResultActions perform = webMvc.perform(
                put("/api/orders/{orderId}/accept", serve.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.SERVED.toString()));
    }

    @DisplayName("배달 시작")
    @Test
    void startDelivery() throws Exception {
        Order startDelivery = orders.get(1);
        startDelivery.setStatus(OrderStatus.DELIVERING);

        given(orderService.startDelivery(any())).willReturn(startDelivery);

        ResultActions perform = webMvc.perform(
                put("/api/orders/{orderId}/start-delivery", startDelivery.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(OrderType.DELIVERY.toString()))
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERING.toString()));
    }

    @DisplayName("배달 종료")
    @Test
    void completeDelivery() throws Exception {
        Order completeDelivery = orders.get(1);
        completeDelivery.setStatus(OrderStatus.DELIVERED);

        given(orderService.completeDelivery(any())).willReturn(completeDelivery);

        ResultActions perform = webMvc.perform(
                put("/api/orders/{orderId}/complete-delivery", completeDelivery.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(OrderType.DELIVERY.toString()))
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.toString()));
    }

    @DisplayName("주문 완료")
    @Test
    void complete() throws Exception {
        Order complete = orders.get(1);
        complete.setStatus(OrderStatus.COMPLETED);

        given(orderService.complete(any())).willReturn(complete);

        ResultActions perform = webMvc.perform(
                put("/api/orders/{orderId}/complete", complete.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.toString()));
    }

    @DisplayName("주문 현황")
    @Test
    void findAll() throws Exception {
        given(orderService.findAll()).willReturn(orders);

        ResultActions perform = webMvc.perform(
                get("/api/orders")
        );

        perform
                .andDo(print())
                .andExpect(status().isOk());
    }
}