package kitchenpos.ui;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static kitchenpos.fixture.OrderFixture.TEST_ORDER_DELIVERY;
import static kitchenpos.fixture.OrderFixture.TEST_ORDER_EAT_IN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderRestController.class)
@DisplayName("/api/orders 주문 ui 레이어 테스트")
class OrderRestControllerTest extends BaseRestControllerTest{

    @MockBean
    OrderService orderService;

    public static final String BASE_URL = "/api/orders";

    @Test
    @DisplayName("[POST] 주문를 등록한다.")
    void createTest() throws Exception {
        //given
        Order order = TEST_ORDER_EAT_IN(OrderStatus.WAITING);
        given(orderService.create(any())).willReturn(order);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("type").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("orderDateTime").exists())
                .andExpect(jsonPath("orderLineItems").exists())
                .andExpect(jsonPath("orderTable").exists())
                .andExpect(jsonPath("orderTableId").exists())
        ;
    }

    @Test
    @DisplayName("[PUT] /{orderId}/accept 주문을 수락한다.")
    void acceptTest() throws Exception {
        //given
        Order order = TEST_ORDER_EAT_IN(OrderStatus.ACCEPTED);
        given(orderService.accept(any())).willReturn(order);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + order.getId() + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("type").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("orderDateTime").exists())
                .andExpect(jsonPath("orderLineItems").exists())
                .andExpect(jsonPath("orderTable").exists())
                .andExpect(jsonPath("orderTableId").exists())
        ;
    }
    
    @Test
    @DisplayName("[PUT] /{orderId}/serve 조리를 완료한다.")
    void serveTest() throws Exception {
        //given
        Order order = TEST_ORDER_EAT_IN(OrderStatus.SERVED);
        given(orderService.serve(any())).willReturn(order);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + order.getId() + "/serve")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("type").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("orderDateTime").exists())
                .andExpect(jsonPath("orderLineItems").exists())
                .andExpect(jsonPath("orderTable").exists())
                .andExpect(jsonPath("orderTableId").exists())
        ;
    }
    
    @Test
    @DisplayName("[PUT] /{orderId}/start-delivery 배달을 시작한다")
    void startDeliveryTest() throws Exception {
        //given
        Order order = TEST_ORDER_DELIVERY(OrderStatus.DELIVERING);
        given(orderService.startDelivery(any())).willReturn(order);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + order.getId() + "/start-delivery")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("type").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("orderDateTime").exists())
                .andExpect(jsonPath("orderLineItems").exists())
                .andExpect(jsonPath("deliveryAddress").exists())
        ;
    }
    
    @Test
    @DisplayName("[PUT] /{orderId}/complete-delivery 배달을 완료한다")
    void completeDeliveryTest() throws Exception {
        //given
        Order order = TEST_ORDER_DELIVERY(OrderStatus.DELIVERED);
        given(orderService.completeDelivery(any())).willReturn(order);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + order.getId() + "/complete-delivery")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("type").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("orderDateTime").exists())
                .andExpect(jsonPath("orderLineItems").exists())
                .andExpect(jsonPath("deliveryAddress").exists())
        ;
    }
    
    @Test
    @DisplayName("[PUT] /{orderId}/complete 주문을 완료한다.")
    void hideTest() throws Exception {
        //given
        Order order = TEST_ORDER_DELIVERY(OrderStatus.COMPLETED);
        given(orderService.complete(any())).willReturn(order);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + order.getId() + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("type").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("orderDateTime").exists())
                .andExpect(jsonPath("orderLineItems").exists())
                .andExpect(jsonPath("deliveryAddress").exists())
        ;
    }
}