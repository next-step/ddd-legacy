package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import kitchenpos.testfixture.OrderTableTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderTableService orderTableService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        //given
        OrderTable request = OrderTableTestFixture.createOrderTableRequest("1번", false, 0);
        OrderTable response = OrderTableTestFixture.createOrderTable("1번", false, 0);

        given(orderTableService.create(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/order-tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()));

    }

    @Test
    void sit() throws Exception {

        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable("1번", false, 0);
        OrderTable response = OrderTableTestFixture.createOrderTable("1번", true, 10);

        given(orderTableService.sit(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", orderTable.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()));

    }

    @Test
    void clear() throws Exception {

        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable("1번", true, 10);
        OrderTable response = OrderTableTestFixture.createOrderTable("1번", false, 0);

        given(orderTableService.clear(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", orderTable.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()));

    }

    @Test
    void changeNumberOfGuests() throws Exception {

        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable("1번", true, 10);
        OrderTable response = OrderTableTestFixture.createOrderTable("1번", true, 5);;

        given(orderTableService.changeNumberOfGuests(any(), any()))
                .willReturn(response);
        orderTable.setNumberOfGuests(5);

        //when then
        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", orderTable.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable)))
                .andDo(print())
                .andExpect(jsonPath("$.numberOfGuests").value(response.getNumberOfGuests()));

    }

    @Test
    void findAll() throws Exception {

        //given
        OrderTable orderTable1 = OrderTableTestFixture.createOrderTable("1번", true, 10);
        OrderTable orderTable2 = OrderTableTestFixture.createOrderTable("4번", false, 0);

        given(orderTableService.findAll())
                .willReturn(Arrays.asList(orderTable1, orderTable2));

        //when then
        mockMvc.perform(get("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderTable1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(orderTable1.getName()))
                .andExpect(jsonPath("$[1].id").value(orderTable2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(orderTable2.getName()));

    }
}