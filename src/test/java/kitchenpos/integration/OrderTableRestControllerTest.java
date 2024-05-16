package kitchenpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.config.IntegrationTest;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.util.MockMvcUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.fixture.OrderTableFixture.createOrderTableWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@IntegrationTest
class OrderTableRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Test
    void create() throws Exception {
        final MvcResult result = mockMvc.perform(post("/api/order-tables")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createOrderTable("1번테이블"))))
                .andReturn();

        final OrderTable orderTable = MockMvcUtil.readValue(objectMapper, result, OrderTable.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(result.getResponse().getHeader("Location"))
                        .isEqualTo("/api/order-tables/" + orderTable.getId()),
                () -> assertThat(orderTable.getId()).isNotNull()
        );
    }

    @Test
    void sit() throws Exception {
        final OrderTable changedOrderTable = orderTableRepository.save(
                createOrderTableWithId("1번테이블", false, 0)
        );
        final MvcResult result = mockMvc.perform(put("/api/order-tables/" + changedOrderTable.getId() + "/sit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        final OrderTable orderTable = MockMvcUtil.readValue(objectMapper, result, OrderTable.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(orderTable.isOccupied()).isTrue()
        );
    }

    @Test
    void clear() throws Exception {
        final OrderTable changedOrderTable = orderTableRepository.save(
                createOrderTableWithId("1번테이블", true, 0)
        );
        final MvcResult result = mockMvc.perform(put("/api/order-tables/" + changedOrderTable.getId() + "/clear")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        final OrderTable orderTable = MockMvcUtil.readValue(objectMapper, result, OrderTable.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }

    @Test
    void changeNumberOfGuests() throws Exception {
        final OrderTable changedOrderTable = orderTableRepository.save(
                createOrderTableWithId("1번테이블", true, 0)
        );
        changedOrderTable.setNumberOfGuests(4);

        final MvcResult result = mockMvc.perform(put("/api/order-tables/" + changedOrderTable.getId() + "/number-of-guests")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(changedOrderTable)))
                .andReturn();

        final OrderTable orderTable = MockMvcUtil.readValue(objectMapper, result, OrderTable.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(4)
        );
    }

    @Test
    void findAll() throws Exception {
        final OrderTable orderTable1 = orderTableRepository.save(createOrderTableWithId("1번테이블"));
        final OrderTable orderTable2 = orderTableRepository.save(createOrderTableWithId("2번테이블"));

        final MvcResult result = mockMvc.perform(get("/api/order-tables"))
                .andReturn();

        final List<OrderTable> orderTables = MockMvcUtil.readListValue(objectMapper, result, OrderTable.class);
        final List<UUID> orderTableIds = orderTables.stream().map(OrderTable::getId).toList();

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(orderTables).hasSize(2),
                () -> assertThat(orderTableIds).contains(orderTable1.getId(), orderTable2.getId())
        );
    }
}
