package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderTableService orderTableService;

    @Test
    void create() throws Exception {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        given(orderTableService.create(any())).willReturn(orderTable);

        mockMvc.perform(post("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()));
    }

    @Test
    void sit() throws Exception {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        given(orderTableService.sit(any())).willReturn(orderTable);
        orderTable.setOccupied(true);

        mockMvc.perform(put("/api/order-tables/" + orderTable.getId() + "/sit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(orderTable.isOccupied()));
    }

    @Test
    void clear() throws Exception {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(2);

        given(orderTableService.clear(any())).willReturn(orderTable);
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(0);

        mockMvc.perform(put("/api/order-tables/" + orderTable.getId() + "/clear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()))
                .andExpect(jsonPath("$.occupied").value(orderTable.isOccupied()))
                .andExpect(jsonPath("$.numberOfGuests").value(orderTable.getNumberOfGuests()));
    }

    @Test
    void changeNumberOfGuests() throws Exception {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        given(orderTableService.changeNumberOfGuests(any(), any())).willReturn(orderTable);
        orderTable.setNumberOfGuests(2);

        mockMvc.perform(put("/api/order-tables/" + orderTable.getId() + "/number-of-guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderTable.getId().toString()))
                .andExpect(jsonPath("$.numberOfGuests").value(orderTable.getNumberOfGuests()));
    }

    @Test
    void findAll() throws Exception {
        List<OrderTable> orderTables = new ArrayList<>();
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(UUID.randomUUID());
        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(UUID.randomUUID());
        orderTables.add(orderTable1);
        orderTables.add(orderTable2);

        given(orderTableService.findAll()).willReturn(orderTables);

        mockMvc.perform(get("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(orderTable1.getId().toString())))
                .andExpect(jsonPath("$[1].id", is(orderTable2.getId().toString())));
    }
}
