package kitchenpos.ui;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
		MenuGroup menuGroupResult = getMenuGroup("메뉴 그룹");
		ReflectionTestUtils.setField(menuGroupResult, "id", UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a"));
		when(menuGroupService.create(any())).thenReturn(menuGroupResult);

		mockMvc.perform(post("/api/menu-groups")
			.content(mapper.writeValueAsString(getMenuGroup("메뉴 그룹")))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", Matchers.is("2f48f241-9d64-4d16-bf56-70b9d4e0e79a")))
			.andDo(print());
	}

	@Test
	void find_all_menu_group() throws Exception {
		when(menuGroupService.findAll()).thenReturn(Arrays.asList(getMenuGroup("메뉴 그룹 1"), getMenuGroup("메뉴 그룹 2")));

		mockMvc.perform(get("/api/menu-groups")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", Matchers.is(2)))
			.andDo(print());
	}

	private MenuGroup getMenuGroup(String name) {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName(name);

		return menuGroup;
	}
}
