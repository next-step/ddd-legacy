package kitchenpos.ui;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void find_all_order() throws Exception {
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderService.findAll()).thenReturn(Collections.singletonList(order));

		mockMvc.perform(get("/api/orders/")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", Matchers.is(1)))
			.andDo(print());
	}

	@Test
	void complete_order() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		OrderTable orderTable = new OrderTable("테이블", 3);
		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.COMPLETED);

		when(orderService.complete(any())).thenReturn(order);

		mockMvc.perform(put("/api/orders/" + uuid.toString() + "/complete")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status", Matchers.is("COMPLETED")))
			.andDo(print());
	}

	@Test
	void complete_delivery() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		OrderTable orderTable = new OrderTable("테이블", 3);
		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.DELIVERED);

		when(orderService.completeDelivery(any())).thenReturn(order);

		mockMvc.perform(put("/api/orders/" + uuid.toString() + "/complete-delivery")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status", Matchers.is("DELIVERED")))
			.andDo(print());
	}

	@Test
	void start_delivery() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		OrderTable orderTable = new OrderTable("테이블", 3);
		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.DELIVERING);

		when(orderService.startDelivery(any())).thenReturn(order);

		mockMvc.perform(put("/api/orders/" + uuid.toString() + "/start-delivery")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status", Matchers.is("DELIVERING")))
			.andDo(print());
	}

	@Test
	void serve_order() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		OrderTable orderTable = new OrderTable("테이블", 3);
		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderService.serve(any())).thenReturn(order);

		mockMvc.perform(put("/api/orders/" + uuid.toString() + "/serve")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status", Matchers.is("SERVED")))
			.andDo(print());
	}

	@Test
	void accept_order() throws Exception {
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		OrderTable orderTable = new OrderTable("테이블", 3);
		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.ACCEPTED);

		when(orderService.accept(any())).thenReturn(order);

		mockMvc.perform(put("/api/orders/" + uuid.toString() + "/accept")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status", Matchers.is("ACCEPTED")))
			.andDo(print());
	}

	@Test
	void create_order() throws Exception {
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);
		ReflectionTestUtils.setField(order, "id", UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a"));

		when(orderService.create(any())).thenReturn(order);

		mockMvc.perform(post("/api/orders")
			.content(mapper.writeValueAsString(orderRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", Matchers.is("2f48f241-9d64-4d16-bf56-70b9d4e0e79a")))
			.andDo(print());
	}
}
