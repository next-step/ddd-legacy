package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderTableService orderTableService;

    @Test
    void 주문테이블을_생성한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 2);
        given(orderTableService.create(any()))
                .willReturn(orderTable);

        //when
        ResultActions result = mvc.perform(post("/api/order-tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(orderTable.getName()))
                .andExpect(jsonPath("$.occupied").value(orderTable.isOccupied()))
                .andExpect(jsonPath("$.numberOfGuests").value(orderTable.getNumberOfGuests()));
    }

    @Test
    void 매장테이블을_점유한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", true, 2);
        given(orderTableService.sit(any()))
                .willReturn(orderTable);

        //when
        ResultActions result = mvc.perform(put("/api/order-tables/{orderTableId}/sit", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(true));
    }

    @Test
    void 매장테이블을_치운다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 2);
        given(orderTableService.clear(any()))
                .willReturn(orderTable);

        //when
        ResultActions result = mvc.perform(put("/api/order-tables/{orderTableId}/clear", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(false));
    }

    @Test
    void 매장테이블을_인원을_변경한다() throws Exception {
        //given
        OrderTable orderTable = createOrderTable("주문테이블1", false, 2);
        given(orderTableService.changeNumberOfGuests(any(), any()))
                .willReturn(orderTable);

        //when
        ResultActions result = mvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfGuests").value(orderTable.getNumberOfGuests()));
    }

    @Test
    void 모든_매장테이블을_조회한다() throws Exception {
        //given
        OrderTable orderTable1 = createOrderTable("주문테이블1", false, 2);
        OrderTable orderTable2 = createOrderTable("주문테이블2", false, 2);

        List<OrderTable> orderTables = List.of(orderTable1, orderTable2);

        given(orderTableService.findAll())
                .willReturn(orderTables);

        //when
        ResultActions result = mvc.perform(get("/api/order-tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTables)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderTables.size())));
    }
}