package kitchenpos.ui;

import static kitchenpos.builder.TestFactory.createOrderTable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderTableRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Test
    @DisplayName("주문 테이블을 생성한다")
    void create_orderTable() throws Exception {
        // given
        OrderTable request = createOrderTableRequest(0);

        // when
        ResultActions perform = mockMvc.perform(post("/api/order-tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        perform.andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numberOfGuests").value(0))
                .andExpect(jsonPath("$.occupied").value(false));
    }

    @Test
    @DisplayName("주문 테이블을 착석 상태로 변경한다")
    void sit_orderTable() throws Exception {
        // given
        OrderTable orderTable = createAndSaveOrderTable(4);

        // when
        ResultActions perform = mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", orderTable.getId()));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(true));
    }

    @Test
    @DisplayName("주문 테이블을 빈 상태로 변경한다")
    void clear_orderTable() throws Exception {
        // given
        OrderTable orderTable = createAndSaveOrderTable(4);
        orderTable.setOccupied(true);
        orderTableRepository.save(orderTable);

        // when
        ResultActions perform = mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", orderTable.getId()));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(false))
                .andExpect(jsonPath("$.numberOfGuests").value(0));
    }

    @Test
    @DisplayName("주문 테이블의 손님 수를 변경한다")
    void change_numberOfGuests() throws Exception {
        // given
        OrderTable orderTable = createAndSaveOrderTable(4);
        orderTable.setOccupied(true);
        orderTableRepository.save(orderTable);

        OrderTable request = createOrderTableRequest(6);

        // when
        ResultActions perform = mockMvc.perform(
                put("/api/order-tables/{orderTableId}/number-of-guests", orderTable.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()))
                .andExpect(jsonPath("$.numberOfGuests").value(6));
    }

    @Test
    @DisplayName("전체 주문 테이블을 조회한다")
    void find_allOrderTables() throws Exception {
        // given
        createAndSaveOrderTable(4);
        createAndSaveOrderTable(6);

        // when
        ResultActions perform = mockMvc.perform(get("/api/order-tables"));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private OrderTable createOrderTableRequest(int numberOfGuests) {
        return createOrderTable("1번 테이블", numberOfGuests, false);
    }

    private OrderTable createAndSaveOrderTable(int numberOfGuests) {
        OrderTable orderTable = createOrderTable("1번 테이블", numberOfGuests, false);
        return orderTableRepository.save(orderTable);
    }
}
