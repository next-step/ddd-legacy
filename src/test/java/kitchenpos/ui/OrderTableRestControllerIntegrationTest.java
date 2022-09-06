package kitchenpos.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderTableRestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private OrderTableRepository orderTableRepository;

  @Autowired
  private OrderTableService orderTableService;

  @AfterEach
  void tearDown() {
    orderTableRepository.deleteAll();
  }

  @DisplayName("유효한 주문테이블 생성 요청에 HTTP 201 상태값과 함께 생성된 주문테이블을 반환한다")
  @Test
  void givenValidOrderTable_whenCreate_thenStatus201WithCratedOrderTable() throws Exception {
    OrderTable requestOrderTable = new OrderTable();
    requestOrderTable.setName("1번");

    mvc.perform(
            post("/api/order-tables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestOrderTable)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("1번"))
        .andExpect(jsonPath("$.numberOfGuests").value(0))
        .andExpect(jsonPath("$.occupied").value(false))
    ;
  }

  @DisplayName("주문 입석 처리 요청에 HTTP 200과 함께 입석처리된 주문테이블을 반환한다")
  @Test
  void givenValidOrderTable_whenSit_thenStatus200WithOrderTable() throws Exception {
    OrderTable requestOrderTable = new OrderTable();
    requestOrderTable.setName("1번");
    OrderTable orderTable = orderTableService.create(requestOrderTable);

    mvc.perform(
            put("/api/order-tables/{orderTableId}/sit", orderTable.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("1번"))
        .andExpect(jsonPath("$.numberOfGuests").value(0))
        .andExpect(jsonPath("$.occupied").value(true))
    ;
  }

  @DisplayName("주문 테이블 정리 요청에 HTTP 200과 함께 정리된 주문테이블을 반환한다")
  @Test
  void givenValidOrderTable_whenClear_thenStatus200WithOrderTable() throws Exception {
    OrderTable requestOrderTable = new OrderTable();
    requestOrderTable.setName("1번");
    OrderTable orderTable = orderTableService.create(requestOrderTable);
    orderTableService.sit(orderTable.getId());

    mvc.perform(
            put("/api/order-tables/{orderTableId}/clear", orderTable.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("1번"))
        .andExpect(jsonPath("$.numberOfGuests").value(0))
        .andExpect(jsonPath("$.occupied").value(false))
    ;
  }

}
