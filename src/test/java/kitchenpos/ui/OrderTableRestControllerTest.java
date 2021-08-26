package kitchenpos.ui;

import static kitchenpos.KitchenposTestFixture.테이블1번;
import static kitchenpos.KitchenposTestFixture.테이블9번_먹는중;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import kitchenpos.IntegrationTest;
import kitchenpos.ui.dto.OrderTableRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class OrderTableRestControllerTest extends IntegrationTest {

    @DisplayName("주문 테이블을 생성한다")
    @Test
    void create() throws Exception {
        OrderTableRequest request = new OrderTableRequest("9번");
        mockMvc.perform(post("/api/order-tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.empty").value(true));
    }

    @DisplayName("생성 실패 - 주문테이블명이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyName() throws Exception {
        OrderTableRequest request = new OrderTableRequest();
        mockMvc.perform(post("/api/order-tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("주문 테이블을 채운다")
    @Test
    void sit() throws Exception {
        mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", 테이블1번.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.empty").value(false));
    }

    @DisplayName("채우기 실패 - 실제 존재하는 테이블만 채워질 수 있다")
    @Test
    void sitFailedByNoSuchOrderTable() throws Exception {
        mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("주문 테이블을 비운다")
    @Test
    void clear() throws Exception {
        mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", 테이블1번.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.empty").value(true));
    }

    @DisplayName("비우기 실패 - 실제 존재하는 테이블만 비워질 수 있다")
    @Test
    void clearFailedByNoSuchOrderTable() throws Exception {
        mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("비우기 실패 - 완료되지 않은 주문이 존재하면 비울 수 없다")
    @Test
    void clearFailedByOrderNotCompleted() throws Exception {
        mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", 테이블9번_먹는중.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문 테이블의 인원 수를 변경한다")
    @Test
    void changeNumberOfGuests() throws Exception {
        OrderTableRequest request = new OrderTableRequest(4);
        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", 테이블9번_먹는중.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numberOfGuests").value(request.getNumberOfGuests()));
    }

    @DisplayName("인원 수 변경 실패 - 인원 수는 음수가 될 수 없다")
    @Test
    void changeNumberOfGuestsFailedByNegativeNumberOfGuests() throws Exception {
        OrderTableRequest request = new OrderTableRequest(-1);
        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", 테이블9번_먹는중.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("인원 수 변경 실패 - 실제 존재하는 테이블만 인원수를 변경할 수 있다")
    @Test
    void changeNumberOfGuestsFailedByNoSuchOrderTable() throws Exception {
        OrderTableRequest request = new OrderTableRequest(4);
        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("인원 수 변경 실패 - 비어있는 테이블의 인원수는 변경할 수 없다")
    @Test
    void changeNumberOfGuestsFailedByEmptyState() throws Exception {
        OrderTableRequest request = new OrderTableRequest(4);
        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", 테이블1번.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("주문 테이블 목록을 조회한다")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/order-tables"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }
}
