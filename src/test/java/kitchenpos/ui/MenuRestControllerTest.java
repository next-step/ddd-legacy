package kitchenpos.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
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

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MenuService menuService;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void create_menu() throws Exception {
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		ReflectionTestUtils.setField(menu, "id", UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a"));

		when(menuService.create(any())).thenReturn(menu);

		mockMvc.perform(post("/api/menus")
			.content(mapper.writeValueAsString(menuRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", Matchers.is("2f48f241-9d64-4d16-bf56-70b9d4e0e79a")))
			.andDo(print());
	}

	@Test
	void change_price() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Menu menuRequest = new Menu();
		menuRequest.setPrice(new BigDecimal(15000));

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 3L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuService.changePrice(eq(uuid), any())).thenReturn(menu);

		mockMvc.perform(put("/api/menus/2f48f241-9d64-4d16-bf56-70b9d4e0e79a/price")
			.content(mapper.writeValueAsString(menuRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.displayed", Matchers.is(true)))
			.andDo(print());
	}

	@Test
	void display_menu() throws Exception {
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuService.display(any())).thenReturn(menu);

		mockMvc.perform(put("/api/menus/2f48f241-9d64-4d16-bf56-70b9d4e0e79a/display")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.displayed", Matchers.is(true)))
			.andDo(print());
	}

	@Test
	void hide_menu() throws Exception {
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(20000), menuGroup, false, Collections.singletonList(menuProduct));

		when(menuService.hide(any())).thenReturn(menu);

		mockMvc.perform(put("/api/menus/2f48f241-9d64-4d16-bf56-70b9d4e0e79a/hide")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.displayed", Matchers.is(false)))
			.andDo(print());
	}

	@Test
	void find_all_menu() throws Exception {
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuService.findAll()).thenReturn(Arrays.asList(menu));

		mockMvc.perform(get("/api/menus")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", Matchers.is(1)))
			.andDo(print());
	}
}
