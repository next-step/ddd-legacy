package kitchenpos.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.OrderService;
import kitchenpos.config.TestConfig;
import kitchenpos.domain.OrderStatus;
import kitchenpos.util.OrderBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderRestController.class)
@Import(TestConfig.class)
class OrderRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("주문 생성")
    @Nested
    class CreateOrder {

        @DisplayName("주문 생성 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var request = new HashMap<String, Object>() {{
                put("orderTableId", UUID.randomUUID());
                put("orderLineItems", List.of(new HashMap<String, Object>() {{
                    put("menuId", UUID.randomUUID());
                    put("quantity", 0);
                }}));
            }};
            var content = objectMapper.writeValueAsString(request);
            given(orderService.create(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            post("/api/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("주문 생성 성공하면 201 Created를 응답한다.")
        @Test
        void if_succeed_then_responds_201_created() throws Exception {
            // given
            var request = new HashMap<String, Object>() {{
                put("orderTableId", UUID.randomUUID());
                put("orderLineItems", List.of(new HashMap<String, Object>() {{
                    put("menuId", UUID.randomUUID());
                    put("quantity", 1);
                }}));
            }};
            var content = objectMapper.writeValueAsString(request);

            UUID orderId = UUID.randomUUID();
            var order = OrderBuilder.id(orderId)
                    .orderStatus(OrderStatus.WAITING)
                    .build();
            given(orderService.create(any())).willReturn(order);

            // when
            var result = mockMvc.perform(
                            post("/api/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isCreated())
                    .andReturn();

            var uri = result.getResponse().getHeader("Location");
            assertThat(uri).isEqualTo("/api/orders/" + order.getId());
        }
    }

    @DisplayName("주문 접수")
    @Nested
    class AcceptOrder {

        @DisplayName("주문 접수 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.accept(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/accept", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("주문 접수 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeed_then_responds_200_ok() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.accept(any())).willReturn(OrderBuilder.id(orderId)
                                                                 .orderStatus(OrderStatus.ACCEPTED)
                                                                 .build());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/accept", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("주문 서빙")
    @Nested
    class ServeOrder {

        @DisplayName("주문 서빙 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.serve(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/serve", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("주문 서빙 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeed_then_responds_200_ok() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.serve(any())).willReturn(OrderBuilder.id(orderId)
                                                                .orderStatus(OrderStatus.SERVED)
                                                                .build());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/serve", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("주문 배달 시작")
    @Nested
    class StartDeliveryOrder {

        @DisplayName("주문 배달 시작 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.startDelivery(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/start-delivery", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("주문 배달 시작 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeed_then_responds_200_ok() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.startDelivery(any())).willReturn(
                    OrderBuilder.id(orderId)
                            .orderStatus(OrderStatus.DELIVERING)
                            .build());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/start-delivery", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("주문 배달 완료")
    @Nested
    class CompleteDeliveryOrder {

        @DisplayName("주문 배달 완료 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.completeDelivery(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/complete-delivery", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("주문 배달 완료 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeed_then_responds_200_ok() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.completeDelivery(any())).willReturn(
                    OrderBuilder.id(orderId)
                            .orderStatus(OrderStatus.DELIVERED)
                            .build());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/complete-delivery", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("주문 완료")
    @Nested
    class CompleteOrder {

        @DisplayName("주문 완료 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.complete(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/complete", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("주문 완료 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeed_then_responds_200_ok() throws Exception {
            // given
            var orderId = UUID.randomUUID();
            var content = objectMapper.writeValueAsString(new HashMap<>());
            given(orderService.complete(any())).willReturn(
                    OrderBuilder.id(orderId)
                            .orderStatus(OrderStatus.COMPLETED)
                            .build());

            // when
            mockMvc.perform(
                            put("/api/orders/{orderId}/complete", orderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("주문 목록 조회")
    @Nested
    class ListOrders {

        @DisplayName("주문 목록 조회 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            UUID orderId1 = UUID.randomUUID();
            var order1 = OrderBuilder.id(orderId1)
                    .orderStatus(OrderStatus.WAITING)
                    .build();
            UUID orderId = UUID.randomUUID();
            var order2 = OrderBuilder.id(orderId)
                    .orderStatus(OrderStatus.ACCEPTED)
                    .build();
            given(orderService.findAll()).willReturn(List.of(order1, order2));

            // when
            mockMvc.perform(
                            get("/api/orders")
                                    .contentType(MediaType.APPLICATION_JSON))
                    // then
                    .andExpect(status().isOk());
        }
    }
}
