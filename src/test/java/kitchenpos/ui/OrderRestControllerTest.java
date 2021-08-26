package kitchenpos.ui;

import static java.util.Collections.emptyList;
import static kitchenpos.KitchenposTestFixture.비싼후라이드치킨;
import static kitchenpos.KitchenposTestFixture.양념치킨_Menu;
import static kitchenpos.KitchenposTestFixture.테이블1번;
import static kitchenpos.KitchenposTestFixture.테이블9번_먹는중;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
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
        테이블주문_상세1.setMenu(비싼후라이드치킨);
        테이블주문_상세1.setMenuId(비싼후라이드치킨.getId());
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

    @DisplayName("생성 실패 - 배달 - 배달 주문일 경우 배달주소가 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyDeliveryAddress() throws Exception {
        배달주문.setDeliveryAddress(null);
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(배달주문)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("생성 실패 - 매장 취식 - 실제 존재하는 주문테이블에만 주문할 수 있다")
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

    @DisplayName("생성 실패 - 매장 취식 - 주문테이블이 비어있으면 주문할 수 없다")
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

    @Test
    void accept() throws Exception {
    }

    @Test
    void serve() throws Exception {
    }

    @Test
    void startDelivery() throws Exception {
    }

    @Test
    void completeDelivery() throws Exception {
    }

    @Test
    void complete() throws Exception {
    }

    @Test
    void findAll() throws Exception {
    }
}
