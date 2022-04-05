package kitchenpos.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MenuGroupService menuGroupService;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void create_menu_group() throws Exception {
		MenuGroup menuGroupRequest = new MenuGroup();
		menuGroupRequest.setName("메뉴 그룹");
		MenuGroup menuGroupResult = new MenuGroup();
		menuGroupResult.setName("메뉴 그룹");
		ReflectionTestUtils.setField(menuGroupResult, "id", UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a"));
		when(menuGroupService.create(any())).thenReturn(menuGroupResult);

		mockMvc.perform(post("/api/menu-groups")
			.content(mapper.writeValueAsString(menuGroupRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", Matchers.is("2f48f241-9d64-4d16-bf56-70b9d4e0e79a")))
			.andDo(print());
	}

	@Test
	void find_all_menu_group() throws Exception {
		MenuGroup menuGroup1 = new MenuGroup();
		menuGroup1.setName("메뉴 그룹 1");
		MenuGroup menuGroup2 = new MenuGroup();
		menuGroup2.setName("메뉴 그룹 2");
		when(menuGroupService.findAll()).thenReturn(Arrays.asList(menuGroup1, menuGroup2));

		mockMvc.perform(get("/api/menu-groups")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", Matchers.is(2)))
			.andDo(print());
	}
}
