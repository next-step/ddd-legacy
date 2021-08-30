package kitchenpos.ui;

import kitchenpos.BaseControllerTest;
import kitchenpos.application.OrderTableService;
import kitchenpos.commons.OrderTableGenerator;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderTableRestControllerTest extends BaseControllerTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableGenerator orderTableGenerator;

    @Test
    @DisplayName("주문 테이블 등록 - 성공")
    void createOrderTable() throws Exception {
        // given
        String name = "OrderTable 1";
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/order-tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderTable))
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("numberOfGuests").value(0))
                .andExpect(jsonPath("empty").value(true))
        ;
    }

    @Test
    @DisplayName("주문 테이블 Sit - 성공")
    void sitOrderTable() throws Exception {
        // given
        OrderTable orderTable = orderTableGenerator.generate();

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", orderTable.getId())
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(orderTable.getName()))
                .andExpect(jsonPath("numberOfGuests").value(0))
                .andExpect(jsonPath("empty").value(false))
        ;
    }

    @Test
    @DisplayName("주문 테이블 손님 수 설정 - 성공")
    void numberOfGuestsOrderTable() throws Exception {
        // given
        OrderTable orderTable = orderTableGenerator.generate();
        orderTable = orderTableService.sit(orderTable.getId()); // sit
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(5);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(orderTable.getName()))
                .andExpect(jsonPath("numberOfGuests").value(request.getNumberOfGuests()))
                .andExpect(jsonPath("empty").value(false))
        ;
    }

    @Test
    @DisplayName("모든 주문 테이블 조회 - 성공")
    void getListOrderTable() throws Exception {
        // given
        int size = 10;
        IntStream.range(0, size).mapToObj(i -> orderTableGenerator.generate()).collect(Collectors.toList());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/order-tables")
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['id']").exists())
                .andExpect(jsonPath("$..['name']").exists())
                .andExpect(jsonPath("$..['numberOfGuests']").exists())
                .andExpect(jsonPath("$..['empty']").exists())
        ;
    }
}