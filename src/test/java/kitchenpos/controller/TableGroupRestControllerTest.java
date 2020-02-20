package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.TableGroupBo;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableGroupRestController.class)
class TableGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableGroupBo tableGroupBo;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("테이블 그룹을 생성할 수 있어야 한다.")
    @Test
    void createTableGroup() throws Exception {
        // given
        TableGroup requestTableGroup = createUnregisteredTableGroup();
        TableGroup responseTableGroup = createRegisteredTableGroupWithId(requestTableGroup, new Random().nextLong());

        given(tableGroupBo.create(any(TableGroup.class)))
                .willReturn(responseTableGroup);

        // when
        ResultActions result = mockMvc.perform(post("/api/table-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTableGroup)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/table-groups/" + responseTableGroup.getId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseTableGroup)));
    }

    @DisplayName("테이블 그룹을 삭제할 수 있어야 한다.")
    @Test
    void deleteTableGroup() throws Exception {
        // given
        Long tableGroupId = new Random().nextLong();

        // when
        ResultActions result = mockMvc.perform(delete("/api/table-groups/{tableGroupId}", tableGroupId));

        // then
        result.andDo(print())
                .andExpect(status().isNoContent());
    }

    private TableGroup createUnregisteredTableGroup() {
        return new TableGroup() {{
            setOrderTables(Arrays.asList(
                    new OrderTable() {{
                        setId(1L);
                        setEmpty(false);
                        setNumberOfGuests(5);
                    }},
                    new OrderTable() {{
                        setId(2L);
                        setEmpty(false);
                        setNumberOfGuests(3);
                    }}
            ));
            setCreatedDate(LocalDateTime.now());
        }};
    }

    private TableGroup createRegisteredTableGroupWithId(TableGroup unregisteredTableGroup, Long tableGroupId) {
        return new TableGroup() {{
            setId(tableGroupId);
            setOrderTables(Arrays.asList(
                    new OrderTable() {{
                        setTableGroupId(tableGroupId);
                        setId(unregisteredTableGroup.getOrderTables().get(0).getId());
                        setEmpty(unregisteredTableGroup.getOrderTables().get(0).isEmpty());
                        setNumberOfGuests(unregisteredTableGroup.getOrderTables().get(0).getNumberOfGuests());
                    }},
                    new OrderTable() {{
                        setTableGroupId(tableGroupId);
                        setId(unregisteredTableGroup.getOrderTables().get(1).getId());
                        setEmpty(unregisteredTableGroup.getOrderTables().get(1).isEmpty());
                        setNumberOfGuests(unregisteredTableGroup.getOrderTables().get(1).getNumberOfGuests());
                    }}
            ));
            setCreatedDate(unregisteredTableGroup.getCreatedDate());
        }};
    }
}
