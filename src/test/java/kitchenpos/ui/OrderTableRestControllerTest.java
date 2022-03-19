package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static kitchenpos.domain.OrderTableFixture.TABLE_1_EMPTY;
import static kitchenpos.domain.OrderTableFixture.TABLE_1_NOT_EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {

    public static final String BASE_URL = "/api/order-tables/";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrderTableService orderTableService;

    @Test
    @DisplayName("주문 테이블 생성 테스트")
    void createOrderTableTest() throws Exception {
        // given
        given(orderTableService.create(any())).willReturn(TABLE_1_EMPTY);

        // when
        mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(TABLE_1_EMPTY)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(TABLE_1_EMPTY.getId().toString()))
               .andExpect(jsonPath("$.name").value(TABLE_1_EMPTY.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("주문 테이블 착석 테스트")
    void sitTest() throws Exception {
        // given
        given(orderTableService.sit(any())).willReturn(TABLE_1_NOT_EMPTY);

        // when
        mockMvc.perform(put(BASE_URL + TABLE_1_EMPTY.getId().toString() + "/sit"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(TABLE_1_NOT_EMPTY.getId().toString()))
               .andExpect(jsonPath("$.name").value(TABLE_1_NOT_EMPTY.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("주문 테이블 clear 테스트")
    void clearTest() throws Exception {
        // given
        given(orderTableService.clear(any())).willReturn(TABLE_1_EMPTY);

        // when
        mockMvc.perform(put(BASE_URL + TABLE_1_NOT_EMPTY.getId().toString() + "/clear"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(TABLE_1_NOT_EMPTY.getId().toString()))
               .andExpect(jsonPath("$.name").value(TABLE_1_NOT_EMPTY.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("주문 테이블 인원 변경 테스트")
    void numberOfGuestsTest() throws Exception {
        // given
        given(orderTableService.changeNumberOfGuests(any(), any())).willReturn(TABLE_1_NOT_EMPTY);

        // when
        mockMvc.perform(put(BASE_URL + TABLE_1_NOT_EMPTY.getId().toString() + "/number-of-guests")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(TABLE_1_NOT_EMPTY)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(TABLE_1_NOT_EMPTY.getId().toString()))
               .andExpect(jsonPath("$.name").value(TABLE_1_NOT_EMPTY.getName()))
               .andDo(print());
    }
}
