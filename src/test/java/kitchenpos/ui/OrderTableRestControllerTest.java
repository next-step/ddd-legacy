package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderTableService orderTableService;

    @Test
    void 주문_테이블_생성_요청이_성공하면_테이블이_생성된다() throws Exception {
        final UUID tableId = UUID.fromString("6ab59e81-06eb-4416-84e9-9faabc87c9ca");

        when(orderTableService.create(any(OrderTable.class))).thenReturn(OrderTableFixture.orderTable(tableId, "8번", 0, false));

        mockMvc.perform(post("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderTableFixture.orderTable(null, "8번", 0, false)))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 주문_테이블에_앉는_요청을_처리한다() throws Exception {
        final UUID tableId = UUID.fromString("6ab59e81-06eb-4416-84e9-9faabc87c9ca");

        when(orderTableService.sit(tableId)).thenReturn(OrderTableFixture.orderTable(tableId, "8번", 1, true));

        mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", tableId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tableId.toString()))
                .andExpect(jsonPath("$.name").value("8번"))
                .andExpect(jsonPath("$.numberOfGuests").value(1))
                .andExpect(jsonPath("$.occupied").value(true));
    }

    @Test
    void 주문_테이블_비우기_요청이_성공하면_테이블이_초기화된다() throws Exception {
        final UUID tableId = UUID.fromString("6ab59e81-06eb-4416-84e9-9faabc87c9ca");

        when(orderTableService.clear(tableId)).thenReturn(OrderTableFixture.orderTable(tableId, "8번", 0, false));

        mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", tableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderTableFixture.orderTable(tableId, "8번", 9, true)))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tableId.toString()))
                .andExpect(jsonPath("$.name").value("8번"))
                .andExpect(jsonPath("$.numberOfGuests").value(0))
                .andExpect(jsonPath("$.occupied").value(false));
    }

    @Test
    void 주문_테이블_게스트_숫자_변경_요청이_성공하면_숫자가_변경된다() throws Exception {
        final UUID tableId = UUID.fromString("6ab59e81-06eb-4416-84e9-9faabc87c9ca");

        when(orderTableService.changeNumberOfGuests(eq(tableId), any(OrderTable.class))).thenReturn(OrderTableFixture.orderTable(tableId, "8번", 5, true));

        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", tableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderTableFixture.orderTable(tableId, "8번", 3, true)))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("8번"))
                .andExpect(jsonPath("$.id").value(tableId.toString()))
                .andExpect(jsonPath("$.numberOfGuests").value(5))
                .andExpect(jsonPath("$.occupied").value(true));
    }

    @Test
    void 주문_테이블_전체_조회_요청이_성공하면_테이블_목록을_반환한다() throws Exception {
        final UUID tableId = UUID.fromString("6ab59e81-06eb-4416-84e9-9faabc87c9ca");

        when(orderTableService.findAll()).thenReturn(List.of(OrderTableFixture.orderTable(tableId, "8번", 5, true)));

        mockMvc.perform(get("/api/order-tables"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}