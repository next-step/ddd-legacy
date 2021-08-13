package kitchenpos.ui;

import kitchenpos.FixtureData;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest extends FixtureData {

    @Autowired
    private MockMvc webMvc;

    @MockBean
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        fixtureOrderTables();

        this.webMvc = ofUtf8MockMvc();
    }

    @DisplayName("주문 테이블 생성")
    @Test
    void createOrderTable() throws Exception {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("10번");

        OrderTable create = orderTables.get(0);
        create.setName(orderTable.getName());

        given(orderTableService.create(any())).willReturn(create);

        ResultActions perform = webMvc.perform(
                post("/api/order-tables")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(orderTable))
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("테이블 활성화")
    @Test
    void sit() throws Exception {
        OrderTable sitTable = orderTables.get(0);
        sitTable.setEmpty(TABLE_SIT);

        given(orderTableService.sit(any())).willReturn(sitTable);

        ResultActions perform = webMvc.perform(
                put("/api/order-tables/{orderTableId}/sit", sitTable.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.empty").value(TABLE_SIT));
    }

    @DisplayName("테이블 정리")
    @Test
    void clear() throws Exception {
        OrderTable clearTable = orderTables.get(0);
        clearTable.setEmpty(TABLE_CLEAR);

        given(orderTableService.clear(any())).willReturn(clearTable);

        ResultActions perform = webMvc.perform(
                put("/api/order-tables/{orderTableId}/clear", clearTable.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.empty").value(TABLE_CLEAR));
    }

    @DisplayName("테이블 손님 수 변경")
    @Test
    void changeNumberOfGuests() throws Exception {
        OrderTable changeTable = orderTables.get(0);
        changeTable.setEmpty(TABLE_SIT);
        changeTable.setNumberOfGuests(2);

        given(orderTableService.changeNumberOfGuests(any(), any())).willReturn(changeTable);

        ResultActions perform = webMvc.perform(
                put("/api/order-tables/{orderTableId}/number-of-guests", changeTable.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(changeTable))
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.numberOfGuests").value(changeTable.getNumberOfGuests()));
    }

    @DisplayName("테이블 현황")
    @Test
    void findAll() throws Exception {
        given(orderTableService.findAll()).willReturn(orderTables);

        ResultActions perform = webMvc.perform(
                get("/api/order-tables/")
        );

        perform
                .andDo(print())
                .andExpect(status().isOk());
    }
}