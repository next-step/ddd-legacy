package kitchenpos.ui;

import static java.util.Collections.emptyList;
import static kitchenpos.KitchenposTestFixture.숨겨진비싼후라이드치킨;
import static kitchenpos.KitchenposTestFixture.양념치킨_Menu;
import static kitchenpos.KitchenposTestFixture.주문1번;
import static kitchenpos.KitchenposTestFixture.주문4번_먹는중;
import static kitchenpos.KitchenposTestFixture.주문5번_방금주문;
import static kitchenpos.KitchenposTestFixture.테이블1번;
import static kitchenpos.KitchenposTestFixture.테이블9번_먹는중;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.ui.dto.OrderLineItemRequest;
import kitchenpos.ui.dto.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class OrderRestControllerTest extends IntegrationTest {

    OrderLineItemRequest 테이블주문_상세1;
    OrderLineItemRequest 배달주문_상세1;
    OrderRequest 테이블주문;
    OrderRequest 배달주문;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        테이블주문_상세1 = new OrderLineItemRequest(1L, 양념치킨_Menu);
        배달주문_상세1 = new OrderLineItemRequest(1L, 양념치킨_Menu);

        테이블주문 = new OrderRequest(OrderType.EAT_IN, 테이블9번_먹는중, 테이블주문_상세1);
        배달주문 = new OrderRequest(OrderType.DELIVERY, "우리시 우리대로 우리집", 배달주문_상세1);
    }

    @DisplayName("주문을 생성한다")
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(배달주문)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
    }

    @DisplayName("생성 실패 - 주문유형이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyOrderType() throws Exception {
        테이블주문.setType(null);
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 주문내역이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyOrderLineItems() throws Exception {
        테이블주문.setOrderLineItems(emptyList());
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 주문내역에 중복이 허용되지 않는다")
    @Test
    void createFailedByDuplicateOrderLineItems() throws Exception {
        테이블주문.setOrderLineItems(Arrays.asList(테이블주문_상세1, 테이블주문_상세1));
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 매장 취식이 아닐 경우 주문 수량은 음수가 될 수 없다")
    @Test
    void createFailedByNegativeQuantity() throws Exception {
        배달주문_상세1.setQuantity(-1L);
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(배달주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 실제 존재하는 메뉴만 주문할 수 있다")
    @Test
    void createFailedByNoSuchMenu() throws Exception {
        테이블주문_상세1.setMenu(new Menu());
        테이블주문_상세1.setMenuId(UUID.randomUUID());
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 숨겨져 있는 메뉴는 주문할 수 없다")
    @Test
    void createFailedByHiddenMenuOrdered() throws Exception {
        테이블주문_상세1.setMenu(숨겨진비싼후라이드치킨);
        테이블주문_상세1.setMenuId(숨겨진비싼후라이드치킨.getId());
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 실제 메뉴 가격과 주문 항목의 가격이 일치해야 한다")
    @Test
    void createFailedByPriceNotMatched() throws Exception {
        테이블주문_상세1.setPrice(BigDecimal.valueOf(200L));
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 배달 주문일 경우 배달주소가 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyDeliveryAddress() throws Exception {
        배달주문.setDeliveryAddress(null);
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(배달주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 매장 취식 시 실제 존재하는 주문테이블에만 주문할 수 있다")
    @Test
    void createFailedByNoSuchOrderTable() throws Exception {
        테이블주문.setOrderTable(new OrderTable());
        테이블주문.setOrderTableId(UUID.randomUUID());
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 매장 취식 시 주문테이블이 비어있으면 주문할 수 없다")
    @Test
    void createFailedByEmptyOrderTable() throws Exception {
        테이블주문.setOrderTable(테이블1번);
        테이블주문.setOrderTableId(테이블1번.getId());
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(테이블주문)))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문을 수락한다")
    @Test
    void accept() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/accept", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.name()));
    }

    @DisplayName("수락 실패 - 실제 존재하는 주문만 수락할 수 있다")
    @Test
    void acceptFailedByNoSuchOrder() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/accept", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("수락 실패 - 대기상태의 주문이 아니면 수락할 수 없다")
    @Test
    void acceptFailedByNotWaitingState() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/accept", 주문4번_먹는중.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문을 내보낸다")
    @Test
    void serve() throws Exception {
        acceptOrder(주문5번_방금주문);

        mockMvc.perform(put("/api/orders/{orderId}/serve", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.SERVED.name()));
    }

    @DisplayName("내보내기 실패 - 실제 존재하는 주문만 내보낼 수 있다")
    @Test
    void serveFailedByNoSuchOrder() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/serve", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("내보내기 실패 - 수락상태가 아닌 주문은 내보낼 수 없다")
    @Test
    void serveFailedByNotWaitingState() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/serve", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문 배달을 시작한다")
    @Test
    void startDelivery() throws Exception {
        acceptOrder(주문1번);
        serveOrder(주문1번);

        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", 주문1번.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERING.name()));
    }

    @DisplayName("배달 시작 실패 - 실제 존재하는 주문만 배달할 수 있다")
    @Test
    void startDeliveryFailedByNoSuchOrder() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("배달 시작 실패 - 배달 주문이 아니면 배달할 수 없다")
    @Test
    void startDeliveryFailedByInvalidOrderType() throws Exception {
        acceptOrder(주문5번_방금주문);
        serveOrder(주문5번_방금주문);

        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("배달 시작 실패 - 내보내진 주문이 아니면 배달할 수 없다")
    @Test
    void startDeliveryFailedByOnlyServedStatusAllowed() throws Exception {
        acceptOrder(주문1번);

        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", 주문1번.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문 배달을 완료처리한다")
    @Test
    void completeDelivery() throws Exception {
        acceptOrder(주문1번);
        serveOrder(주문1번);
        startDeliverOrder(주문1번);

        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", 주문1번.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.name()));
    }

    @DisplayName("배달 완료처리 실패 - 실제 존재하는 주문만 배달완료 처리할 수 있다")
    @Test
    void completeDeliveryFailedByNoSuchOrder() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("배달 완료처리 실패 - 배달 주문이 아니면 배달완료 처리할 수 없다")
    @Test
    void completeDeliveryFailedByInvalidOrderType() throws Exception {
        acceptOrder(주문5번_방금주문);
        serveOrder(주문5번_방금주문);

        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("배달 완료처리 실패 - 배달중이 아닌 주문은 배달완료 처리할 수 없다")
    @Test
    void completeDeliveryFailedBy() throws Exception {
        acceptOrder(주문1번);
        serveOrder(주문1번);

        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", 주문1번.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문을 완료처리한다")
    @Test
    void complete() throws Exception {
        acceptOrder(주문5번_방금주문);
        serveOrder(주문5번_방금주문);

        mockMvc.perform(put("/api/orders/{orderId}/complete", 주문4번_먹는중.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.name()));

        mockMvc.perform(put("/api/orders/{orderId}/complete", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.name()))
            .andExpect(jsonPath("$.orderTable.empty").value(true));
    }

    @DisplayName("주문 완료처리 실패 - 실제 존재하는 주문만 완료 처리할 수 있다")
    @Test
    void completeFailedByNoSuchOrder() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/complete", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("주문 완료처리 실패 - 배달주문의 경우 배달완료가 아니면 완료 처리할 수 없다")
    @Test
    void completeFailedByOnlyDeliveredStatusAllowed() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/complete", 주문1번.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문 완료처리 실패 - 테이크아웃이나 매장취식일 경우 주문이 나가지 않았으면 완료할 수 없다")
    @Test
    void completeFailedByOnlyServedStatusAllowed() throws Exception {
        acceptOrder(주문5번_방금주문);
        mockMvc.perform(put("/api/orders/{orderId}/complete", 주문5번_방금주문.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/orders"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }

    private void acceptOrder(Order order) throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/accept", order.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.name()));
    }

    private void serveOrder(Order order) throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/serve", order.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.SERVED.name()));
    }

    private void startDeliverOrder(Order order) throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/start-delivery", order.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERING.name()));
    }

    private void completeDeliverOrder(Order order) throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", order.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.name()));
    }
}
