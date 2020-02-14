package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.TableGroupBo;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TableGroupRestControllerTests {
    @InjectMocks
    private TableGroupRestController tableGroupRestController;

    @Mock
    private TableGroupBo tableGroupBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private TableGroup mockCreated;
    private List<TableGroup> tableGroups = new ArrayList<>();

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(tableGroupRestController).alwaysDo(print()).build();

        mockCreated = new TableGroup();
        mockCreated.setId(1L);

        tableGroups.add(new TableGroup());
    }

    @DisplayName("POST 테이블 그룹 시도 성공(201)")
    @Test
    public void postTableGroupSuccess() throws Exception {
        TableGroup mockRequestTableGroup = new TableGroup();
        given(tableGroupBo.create(any(TableGroup.class))).willReturn(mockCreated);

        mockMvc.perform(post("/api/table-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockRequestTableGroup)))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/table-groups/1"))
        ;
    }

    @DisplayName("DELETE 테이블 그룹 시도 성공(204)")
    @Test
    public void deleteTableGroupSuccess() throws Exception {
        mockMvc.perform(delete("/api/table-groups/1"))
                .andExpect(status().isNoContent());

        then(tableGroupBo).should().delete(1L);
    }
}
