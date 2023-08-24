package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.OrderTableServiceTest;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private static final String BASE_URL = "/api/order-tables";

    @Test
    void 주문_테이블을_생성한다() throws Exception {
        // given
        OrderTable orderTable = createOrderTable("테이블", 1, null);

        given(orderTableService.create(any())).willReturn(orderTable);

        // when
        ResultActions result = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(orderTable))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(orderTable.getName()))
                .andExpect(jsonPath("$.numberOfGuests").value(orderTable.getNumberOfGuests()))
                .andExpect(jsonPath("$.occupied").value(orderTable.isOccupied()));
    }

    @Test
    void 주문_테이블에_착석_처리를_한다() throws Exception {
        // given
        OrderTable orderTable = createOrderTable("테이블", 1, true);

        given(orderTableService.sit(any())).willReturn(orderTable);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderTableId}/sit", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(true));
    }

    @Test
    void 주문_테이블에_비움_처리를_한다() throws Exception {
        // given
        OrderTable orderTable = createOrderTable("테이블", 1, false);

        given(orderTableService.clear(any())).willReturn(orderTable);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderTableId}/clear", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(false));
    }

    @Test
    void 주문_테이블에_착석한_손님의_숫자를_바꾼다() throws Exception {
        // given
        OrderTable orderTable = createOrderTable("테이블", 3, false);

        given(orderTableService.changeNumberOfGuests(any(), any())).willReturn(orderTable);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{orderTableId}/number-of-guests", orderTable.getId())
                .content(objectMapper.writeValueAsString(orderTable))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfGuests").value(orderTable.getNumberOfGuests()));
    }

    @Test
    void 모든_주문_테이블을_조회할_수_있다() throws Exception {
        // given
        OrderTable orderTable1 = createOrderTable("테이블1", 1, true);
        OrderTable orderTable2 = createOrderTable("테이블2", 2, false);

        given(orderTableService.findAll()).willReturn(List.of(orderTable1, orderTable2));

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value(orderTable1.getName()))
                .andExpect(jsonPath("$[1].name").value(orderTable2.getName()))
                .andExpect(jsonPath("$[0].numberOfGuests").value(orderTable1.getNumberOfGuests()))
                .andExpect(jsonPath("$[1].numberOfGuests").value(orderTable2.getNumberOfGuests()))
                .andExpect(jsonPath("$[0].occupied").value(orderTable1.isOccupied()))
                .andExpect(jsonPath("$[1].occupied").value(orderTable2.isOccupied()));
    }

    private OrderTable createOrderTable(
            String name,
            int numberOfGuests,
            Boolean occupied
    ) {
        OrderTable orderTable = OrderTableServiceTest.createOrderTable(name, numberOfGuests);
        if (occupied != null) {
            orderTable.setOccupied(occupied);
        }
        return orderTable;
    }
}
