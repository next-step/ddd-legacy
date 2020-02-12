package kitchenpos.controller;

import kitchenpos.bo.TableGroupBo;
import kitchenpos.model.TableGroup;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TableGroupRestController.class)
class TableGroupRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableGroupBo tableGroupBo;

    private static TableGroup mockCreated;
    private static List<TableGroup> tableGroups = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        mockCreated = new TableGroup();
        mockCreated.setId(1L);

        tableGroups.add(new TableGroup());
    }

    @DisplayName("POST 테이블 그룹 시도 성공(201)")
    @Test
    public void postTableGroupSuccess() throws Exception {
        given(tableGroupBo.create(any(TableGroup.class))).willReturn(mockCreated);

        mockMvc.perform(post("/api/table-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"orderTables\": [\n" +
                        "    {\n" +
                        "      \"id\": 1\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": 2\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/table-groups/1"))
        ;
    }

    @DisplayName("DELETE 테이블 그룹 시도 성공(204)")
    @Test
    public void deleteTableGroupSuccess() throws Exception {
        mockMvc.perform(delete("/api/table-groups/1"))
                .andExpect(status().isNoContent());

        verify(tableGroupBo).delete(1L);
    }
}
