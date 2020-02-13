package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.TableBo;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TableRestControllerTests {
    @InjectMocks
    private TableRestController tableRestController;

    @Mock
    private TableBo tableBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static OrderTable mockCreated;
    private static OrderTable mockEmptyTable;
    private static OrderTable mockHundredGuestTable;
    private static List<OrderTable> mockOrderTables = new ArrayList<>();

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tableRestController).alwaysDo(print()).build();
    }

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
        OrderTable mockRequestOrderTable = new OrderTable();
        given(tableBo.create(any(OrderTable.class))).willReturn(mockCreated);

        mockMvc.perform(put("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockRequestOrderTable)))
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
        OrderTable mockEmptyTable = new OrderTable();
        mockEmptyTable.setEmpty(true);
        given(tableBo.changeEmpty(eq(1L), any(OrderTable.class))).willReturn(mockEmptyTable);

        mockMvc.perform(put("/api/tables/1/empty")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockEmptyTable)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"empty\":true")))
        ;
    }

    @DisplayName("PUT 주문 테이블 손님수 변경 시도 성공(200)")
    @Test
    public void changeOrderTableGuestsSuccess() throws Exception {
        OrderTable mockHundredGuestsTable = new OrderTable();
        given(tableBo.changeEmpty(eq(1L), any(OrderTable.class))).willReturn(mockHundredGuestTable);

        mockMvc.perform(put("/api/tables/1/empty")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockHundredGuestsTable)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"numberOfGuests\":100")))
        ;
    }
}
