package kitchenpos.ui;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static kitchenpos.fixture.OrderTableFixture.TEST_ORDER_TABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderTableRestController.class)
@DisplayName("/api/order-tables 주문 테이블 ui 레이어 테스트")
class OrderTableRestControllerTest extends BaseRestControllerTest{

    @MockBean
    private OrderTableService orderTableService;

    private static final String BASE_URL = "/api/order-tables";

    @Test
    @DisplayName("[POST] 주문 테이블을 등록한다.")
    void createTest() throws Exception {
        //given
        OrderTable orderTable = TEST_ORDER_TABLE();
        given(orderTableService.create(any())).willReturn(orderTable);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("numberOfGuests").exists())
                .andExpect(jsonPath("occupied").exists())
        ;
    }

    @Test
    @DisplayName("[PUT] /{orderTableId}/sit 주문 테이블에 손님을 지정한다.")
    void sitTest() throws Exception {
        //given
        OrderTable orderTable = TEST_ORDER_TABLE();
        given(orderTableService.sit(any())).willReturn(orderTable);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + orderTable.getId() + "/sit")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("numberOfGuests").exists())
                .andExpect(jsonPath("occupied").exists())
        ;
    }

    @Test
    @DisplayName("[PUT] /{orderTableId}/clear 주문 테이블을 비운다.")
    void clearTest() throws Exception {
        //given
        OrderTable orderTable = TEST_ORDER_TABLE();
        given(orderTableService.clear(any())).willReturn(orderTable);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + orderTable.getId() + "/clear")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("numberOfGuests").exists())
                .andExpect(jsonPath("occupied").exists())
        ;
    }

    @Test
    @DisplayName("[PUT] /{orderTableId}/number-of-guests  주문 테이블의 손님 수를 변경한다.")
    void changeNumberOfGuestTest() throws Exception {
        //given
        OrderTable orderTable = TEST_ORDER_TABLE();
        given(orderTableService.changeNumberOfGuests(any(), any(OrderTable.class))).willReturn(orderTable);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + orderTable.getId() + "/number-of-guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("numberOfGuests").exists())
                .andExpect(jsonPath("occupied").exists())
        ;
    }
}
