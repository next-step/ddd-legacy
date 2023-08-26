package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.OrderTableServiceTest;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @DisplayName("주문 테이블을 생성한다")
    @Test
    void create() throws Exception {
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

    @DisplayName("주문 테이블에 착석 처리를 한다")
    @Test
    void sit() throws Exception {
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

    @DisplayName("주문 테이블에 비움 처리를 한다")
    @Test
    void createOrderTable() throws Exception {
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

    @DisplayName("주문 테이블에 착석한 손님의 숫자를 바꾼다")
    @Test
    void changeNumberOfGuests() throws Exception {
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

    @DisplayName("모든 주문 테이블을 조회할 수 있다")
    @Test
    void findAll() throws Exception {
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
