package kitchenpos.ui;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void create_product() throws Exception {
		Product productRequest = new Product("상품", new BigDecimal(10000));
		Product result = new Product("상품", new BigDecimal(10000));
		ReflectionTestUtils.setField(result, "id", UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a"));

		when(productService.create(any())).thenReturn(result);

		mockMvc.perform(post("/api/products")
			.content(mapper.writeValueAsString(productRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", Matchers.is("2f48f241-9d64-4d16-bf56-70b9d4e0e79a")))
			.andDo(print());
	}

	@Test
	void change_price() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Product request = new Product("상품", new BigDecimal(6000));
		Product result = new Product("상품", new BigDecimal(6000));

		when(productService.changePrice(any(), any())).thenReturn(result);

		mockMvc.perform(put("/api/products/" + uuid.toString() + "/price")
			.content(mapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.price", Matchers.is(6000)))
			.andDo(print());
	}

	@Test
	void find_all_menu() throws Exception {
		Product product1 = new Product("상품 1", new BigDecimal(6000));
		Product product2 = new Product("상품 2", new BigDecimal(7000));

		when(productService.findAll()).thenReturn(Arrays.asList(product1, product2));

		mockMvc.perform(get("/api/products")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", Matchers.is(2)))
			.andDo(print());
	}
}
