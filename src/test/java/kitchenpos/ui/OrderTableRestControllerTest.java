package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.application.OrderTableService;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderTableService orderTableService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    @DisplayName("주문 테이블 생성 API")
    void create() throws Exception {
        var request = TestFixture.createOrderTable("테이블", 5);
        var response = TestFixture.copy(request);
        given(orderTableService.create(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.numberOfGuests").value(response.getNumberOfGuests()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()))
                .andReturn();
    }

    @Test
    @DisplayName("주문 테이블 점유 API")
    void sit() throws Exception {
        var orderTableId = UUID.randomUUID();
        var response = TestFixture.createOrderTable(orderTableId, "테이블", 5, true);
        given(orderTableService.sit(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/order-tables/{orderTableId}/sit", orderTableId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.numberOfGuests").value(response.getNumberOfGuests()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()))
                .andReturn();
    }

    @Test
    @DisplayName("주문 테이블 비우기 API")
    void clear() throws Exception {
        var orderTableId = UUID.randomUUID();
        var response = TestFixture.createOrderTable(orderTableId, "테이블2", 3, false);
        given(orderTableService.clear(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/order-tables/{orderTableId}/clear", orderTableId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.numberOfGuests").value(response.getNumberOfGuests()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()))
                .andReturn();
    }

    @Test
    @DisplayName("주문 테이블 인원 변경 API")
    void changeNumberOfGuests() throws Exception {
        var orderTableId = UUID.randomUUID();
        var request = TestFixture.createOrderTable(orderTableId, "테이블2", 5, false);
        var response = TestFixture.copy(request);
        given(orderTableService.changeNumberOfGuests(any(), any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/order-tables/{orderTableId}/number-of-guests", orderTableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.numberOfGuests").value(response.getNumberOfGuests()))
                .andExpect(jsonPath("$.occupied").value(response.isOccupied()))
                .andReturn();
    }

    @Test
    @DisplayName("주문 테이블 목록 조회 API")
    void findAll() throws Exception {
        var response = List.of(
                TestFixture.createOrderTable("테이블1", 5),
                TestFixture.createOrderTable("테이블2", 3)
        );

        given(orderTableService.findAll()).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();
    }
}
