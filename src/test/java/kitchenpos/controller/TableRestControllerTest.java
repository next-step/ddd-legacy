package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.TableBo;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableRestController.class)
class TableRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TableBo tableBo;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("테이블을 생성할 수 있어야 한다.")
    @Test
    void create() throws Exception {
        // given
        OrderTable requestOrderTable = createUnregisteredTable();
        OrderTable responseOrderTable = createRegisteredTableWithId(requestOrderTable, new Random().nextLong());

        given(tableBo.create(any(OrderTable.class)))
                .willReturn(responseOrderTable);

        // when
        ResultActions result = mockMvc.perform(put("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestOrderTable)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/tables/" + responseOrderTable.getId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseOrderTable)));
    }

    @DisplayName("테이블 목록을 볼 수 있어야 한다.")
    @Test
    void list() throws Exception {
        // given
        OrderTable orderTable1 = createRegisteredTableWithId(1L);
        OrderTable orderTable2 = createRegisteredTableWithId(2L);

        given(tableBo.list())
                .willReturn(Arrays.asList(orderTable1, orderTable2));

        // when
        ResultActions result = mockMvc.perform(get("/api/tables"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(String.valueOf(orderTable1.getId()))))
                .andExpect(content().string(containsString(String.valueOf(orderTable2.getId()))));
    }

    @DisplayName("테이블을 비어있는 상태로 변경할 수 있어야 한다.")
    @Test
    void changeEmpty() throws Exception {
        // given
        Long tableId = new Random().nextLong();
        OrderTable orderTable = createRegisteredTableWithId(tableId);

        given(tableBo.changeEmpty(eq(tableId), any(OrderTable.class)))
                .willReturn(orderTable);

        // when
        ResultActions result = mockMvc.perform(put("/api/tables/{orderTableId}/empty", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("5")));
    }

    @DisplayName("테이블의 인원을 변경할 수 있어야 한다.")
    @Test
    void changeNumberOfGuests() throws Exception {
        // given
        Long tableId = new Random().nextLong();
        OrderTable orderTable = createRegisteredTableWithId(tableId);

        given(tableBo.changeNumberOfGuests(eq(tableId), any(OrderTable.class)))
                .willReturn(orderTable);

        // when
        ResultActions result = mockMvc.perform(put("/api/tables/{orderTableId}/number-of-guests", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("5")));
    }

    private OrderTable createUnregisteredTable() {
        return new OrderTable() {{
            setTableGroupId(1L);
            setEmpty(false);
            setNumberOfGuests(5);
        }};
    }

    private OrderTable createRegisteredTableWithId(Long tableId) {
        OrderTable orderTable = createUnregisteredTable();
        orderTable.setId(tableId);

        return orderTable;
    }

    private OrderTable createRegisteredTableWithId(OrderTable unregisteredOrderTable, Long tableId) {
        return new OrderTable() {{
            setId(tableId);
            setTableGroupId(unregisteredOrderTable.getTableGroupId());
            setEmpty(unregisteredOrderTable.isEmpty());
            setNumberOfGuests(unregisteredOrderTable.getNumberOfGuests());
        }};
    }
}
