package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.TableGroupBo;
import kitchenpos.model.TableGroup;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TableGroupRestControllerTests {
    @InjectMocks
    private TableGroupRestController tableGroupRestController;

    @Mock
    private TableGroupBo tableGroupBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static TableGroup mockCreated;
    private static List<TableGroup> tableGroups = new ArrayList<>();

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tableGroupRestController).alwaysDo(print()).build();
    }

    @BeforeAll
    public static void setup() {
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

        verify(tableGroupBo).delete(1L);
    }
}
