package kitchenpos.controller;

import kitchenpos.bo.TableBo;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableRestController.class)
class TableRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableBo tableBo;

    private static OrderTable mockCreated;
    private static OrderTable mockEmptyTable;
    private static OrderTable mockHundredGuestTable;
    private static List<OrderTable> mockOrderTables = new ArrayList<>();

    @BeforeAll
    private static void setup() {
        mockCreated = new OrderTable();
        mockCreated.setId(1L);

        mockEmptyTable = new OrderTable();
        mockEmptyTable.setEmpty(true);

        mockHundredGuestTable = new OrderTable();
        mockHundredGuestTable.setNumberOfGuests(100);

        mockOrderTables.add(new OrderTable());
    }

    @DisplayName("PUT 주문 테이블 시도 성공(201)")
    @Test
    public void putOrderTableSuccess() throws Exception {
        given(tableBo.create(any(OrderTable.class))).willReturn(mockCreated);

        mockMvc.perform(put("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"tableGroupId\": 1,\n" +
                        "  \"numberOfGuests\": 4,\n" +
                        "  \"empty\": false\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/tables/1"))
        ;
    }

    @DisplayName("GET 주문 테이블 시도 성공(200)")
    @Test
    public void getOrderTablesSuccess() throws Exception {
        given(tableBo.list()).willReturn(mockOrderTables);

        mockMvc.perform(get("/api/tables"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("numberOfGuests")))
        ;
    }

    @DisplayName("PUT 주문 테이블 공석으로 변경 시도 성공(200)")
    @Test
    public void changeOrderTableEmptySuccess() throws Exception {
        given(tableBo.changeEmpty(eq(1L), any(OrderTable.class))).willReturn(mockEmptyTable);

        mockMvc.perform(put("/api/tables/1/empty")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"empty\": true\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"empty\":true")))
        ;
    }

    @DisplayName("PUT 주문 테이블 손님수 변경 시도 성공(200)")
    @Test
    public void changeOrderTableGuestsSuccess() throws Exception {
        given(tableBo.changeEmpty(eq(1L), any(OrderTable.class))).willReturn(mockHundredGuestTable);

        mockMvc.perform(put("/api/tables/1/empty")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"numberOfGuests\": 100\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"numberOfGuests\":100")))
        ;
    }
}
