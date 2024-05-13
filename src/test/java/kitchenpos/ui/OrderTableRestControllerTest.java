package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderService;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

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
        OrderTable request = new OrderTable();
        request.setName("9번");

        OrderTable response = new OrderTable();
        response.setId(UUID.randomUUID());
        response.setName(request.getName());

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
        UUID orderTableId = UUID.randomUUID();
        OrderTable response = new OrderTable();
        response.setId(orderTableId);
        response.setOccupied(true);

        given(orderTableService.sit(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/order-tables/{orderTableId}/sit", orderTableId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()));


    }

    @Test
    void clear() throws Exception {

        //given
        UUID orderTableId = UUID.randomUUID();
        OrderTable response = new OrderTable();
        response.setId(orderTableId);
        response.setOccupied(false);

        given(orderTableService.clear(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/order-tables/{orderTableId}/clear", orderTableId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()));
    }

    @Test
    void changeNumberOfGuests() throws Exception {
        //given
        UUID orderTableId = UUID.randomUUID();
        OrderTable request = new OrderTable();
        request.setId(orderTableId);
        request.setNumberOfGuests(4);
        OrderTable response = new OrderTable();
        response.setId(orderTableId);
        response.setOccupied(true);
        response.setNumberOfGuests(4);

        given(orderTableService.changeNumberOfGuests(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/order-tables/{orderTableId}/number-of-guests", orderTableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.numberOfGuests").value(response.getNumberOfGuests()));
    }

    @Test
    void findAll() throws Exception {

        //given
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(UUID.randomUUID());
        orderTable1.setName("1번");
        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(UUID.randomUUID());
        orderTable2.setName("4번");

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