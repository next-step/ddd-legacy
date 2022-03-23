package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import kitchenpos.application.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static kitchenpos.domain.MenuFixture.CHICKEN_MENU;
import static kitchenpos.domain.OrderFixture.DELIVERY_CHICKEN_ORDER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    private static final String BASE_URL = "/api/orders/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrderTest() throws Exception {
        // given
        given(orderService.create(any())).willReturn(DELIVERY_CHICKEN_ORDER);

        // when
        mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(CHICKEN_MENU)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(DELIVERY_CHICKEN_ORDER.getId().toString()))
               .andExpect(jsonPath("$.type").value("DELIVERY"))
               .andExpect(jsonPath("$.status").value("WAITING"))
               .andDo(print());
    }

    @Test
    @DisplayName("주문 서브 테스트")
    void serveOrderTest() throws Exception {
        // given
        given(orderService.serve(any())).willReturn(DELIVERY_CHICKEN_ORDER);

        // when
        mockMvc.perform(put(BASE_URL + DELIVERY_CHICKEN_ORDER.getId().toString() + "/serve"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(DELIVERY_CHICKEN_ORDER.getId().toString()))
               .andExpect(jsonPath("$.type").value("DELIVERY"))
               .andExpect(jsonPath("$.status").value("WAITING"))
               .andDo(print());
    }

    @Test
    @DisplayName("배달 시작 테스트")
    void startDeliveryTest() throws Exception {
        // given
        given(orderService.startDelivery(any())).willReturn(DELIVERY_CHICKEN_ORDER);

        // when
        mockMvc.perform(put(BASE_URL + DELIVERY_CHICKEN_ORDER.getId().toString() + "/start-delivery"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(DELIVERY_CHICKEN_ORDER.getId().toString()))
               .andExpect(jsonPath("$.type").value("DELIVERY"))
               .andExpect(jsonPath("$.status").value("WAITING"))
               .andDo(print());
    }

    @Test
    @DisplayName("배달 종료 테스트")
    void completeDeliveryTest() throws Exception {
        // given
        given(orderService.completeDelivery(any())).willReturn(DELIVERY_CHICKEN_ORDER);

        // when
        mockMvc.perform(put(BASE_URL + DELIVERY_CHICKEN_ORDER.getId().toString() + "/complete-delivery"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(DELIVERY_CHICKEN_ORDER.getId().toString()))
               .andExpect(jsonPath("$.type").value("DELIVERY"))
               .andExpect(jsonPath("$.status").value("WAITING"))
               .andDo(print());
    }

    @Test
    @DisplayName("주문 완료 테스트")
    void completeOrderTest() throws Exception {
        // given
        given(orderService.complete(any())).willReturn(DELIVERY_CHICKEN_ORDER);

        // when
        mockMvc.perform(put(BASE_URL + DELIVERY_CHICKEN_ORDER.getId().toString() + "/complete"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(DELIVERY_CHICKEN_ORDER.getId().toString()))
               .andExpect(jsonPath("$.type").value("DELIVERY"))
               .andExpect(jsonPath("$.status").value("WAITING"))
               .andDo(print());
    }

    @Test
    @DisplayName("주문 조회 테스트")
    void findAllTest() throws Exception {
        // given
        given(orderService.findAll()).willReturn(Collections.singletonList(DELIVERY_CHICKEN_ORDER));

        // when
        mockMvc.perform(get(BASE_URL))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(DELIVERY_CHICKEN_ORDER.getId().toString()))
               .andExpect(jsonPath("$[0].type").value("DELIVERY"))
               .andExpect(jsonPath("$[0].status").value("WAITING"))
               .andDo(print());
    }
}
