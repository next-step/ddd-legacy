package kitchenpos.ui;

import static org.mockito.ArgumentMatchers.*;
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

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;

@WebMvcTest(OrderTableRestController.class)
class OrderTableRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderTableService orderTableService;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void find_all_order_table() throws Exception {
		OrderTable orderTable1 = new OrderTable("테이블 1", 5);
		OrderTable orderTable2 = new OrderTable("테이블 2");

		when(orderTableService.findAll()).thenReturn(Arrays.asList(orderTable1, orderTable2));

		mockMvc.perform(get("/api/order-tables")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", Matchers.is(2)))
			.andDo(print());
	}

	@Test
	void change_number_of_guests() throws Exception {
		String uuid = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
		OrderTable orderRequest = new OrderTable("테이블", 2);
		OrderTable changedTable = new OrderTable("테이블", 2);

		when(orderTableService.changeNumberOfGuests(any(), any())).thenReturn(changedTable);

		mockMvc.perform(put("/api/order-tables/" + uuid + "/number-of-guests")
			.content(mapper.writeValueAsString(orderRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.numberOfGuests", Matchers.is(2)))
			.andDo(print());
	}

	@Test
	void clear_table() throws Exception {
		String uuid = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
		OrderTable emptyTable = new OrderTable("빈 테이블", 0);

		when(orderTableService.clear(any())).thenReturn(emptyTable);

		mockMvc.perform(put("/api/order-tables/" + uuid + "/clear")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.empty", Matchers.is(true)))
			.andDo(print());
	}

	@Test
	void sit_table() throws Exception {
		String uuid = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
		OrderTable sitTable = new OrderTable("앉은 테이블", 3);

		when(orderTableService.sit(any())).thenReturn(sitTable);

		mockMvc.perform(put("/api/order-tables/" + uuid + "/sit")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.numberOfGuests", Matchers.is(3)))
			.andDo(print());
	}

	@Test
	void create_order_table() throws Exception {
		OrderTable orderTableRequest = new OrderTable("테이블", 0);
		OrderTable result = new OrderTable("테이블", 0);
		ReflectionTestUtils.setField(result, "id", UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a"));

		when(orderTableService.create(any())).thenReturn(result);

		mockMvc.perform(post("/api/order-tables")
			.content(mapper.writeValueAsString(orderTableRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", Matchers.is("2f48f241-9d64-4d16-bf56-70b9d4e0e79a")))
			.andDo(print());
	}
}
