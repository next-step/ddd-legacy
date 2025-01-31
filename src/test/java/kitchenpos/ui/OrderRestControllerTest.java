package kitchenpos.ui;

import static kitchenpos.test.TestFixtureFactory.createEmptyOrderTable;
import static kitchenpos.test.TestFixtureFactory.createMenu;
import static kitchenpos.test.TestFixtureFactory.createMenuGroup;
import static kitchenpos.test.TestFixtureFactory.createOrderLineItem;
import static kitchenpos.test.TestFixtureFactory.createOrderWithDeliveryType;
import static kitchenpos.test.TestFixtureFactory.createProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.JpaMenuRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("주문을 생성한다.")
    void create_success() throws Exception {
        // given
        Order request = createOrderRequestWithDeliveryType();

        // when
        ResultActions result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orderLineItems").isArray())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("주문 상태가 주문 대기 중이라면 주문을 수락할 수 있다.")
    void accept_success() throws Exception {
        // given
        Order savedOrder = createAndSaveOrderWithDeliveryType();

        // when
        ResultActions result = mockMvc.perform(put("/api/orders/{orderId}/accept", savedOrder.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("주문 상태가 접수 완료라면 서빙할 수 있다.")
    void serve_success() throws Exception {
        // given
        Order savedOrder = createAndSaveOrderWithDeliveryType();
        savedOrder.setStatus(OrderStatus.ACCEPTED);

        // when
        ResultActions result = mockMvc.perform(put("/api/orders/{orderId}/serve", savedOrder.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value("SERVED"));
    }

    @Test
    @DisplayName("주문 상태가 서빙 완료라면 배달을 시작할 수 있다.")
    void startDelivery_success() throws Exception {
        // given
        Order savedOrder = createAndSaveOrderWithDeliveryType();
        savedOrder.setStatus(OrderStatus.SERVED);

        // when
        ResultActions result = mockMvc.perform(put("/api/orders/{orderId}/start-delivery", savedOrder.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value("DELIVERING"));
    }

    @Test
    @DisplayName("주문 상태가 배달 중이라면 배달을 완료할 수 있다.")
    void completeDelivery_success() throws Exception {
        // given
        Order savedOrder = createAndSaveOrderWithDeliveryType();
        savedOrder.setStatus(OrderStatus.DELIVERING);

        // when
        ResultActions result = mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", savedOrder.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "DELIVERY, DELIVERED, COMPLETED",
            "TAKEOUT, SERVED, COMPLETED",
            "EAT_IN, SERVED, COMPLETED",
    })
    @DisplayName("주문 종류와 상태에 따라 주문을 완료할 수 있다.")
    void complete_success(OrderType orderType, OrderStatus orderStatus, OrderStatus expected) throws Exception {
        // given
        Order savedOrder = createAndSaveOrderWithDeliveryType();
        savedOrder.setType(orderType);
        savedOrder.setStatus(orderStatus);

        // when
        ResultActions result = mockMvc.perform(put("/api/orders/{orderId}/complete", savedOrder.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value(expected.toString()));
    }

    @Test
    @DisplayName("전체 주문을 조회한다.")
    void findAll_success() throws Exception {
        // given
        createAndSaveOrderWithDeliveryType();
        createAndSaveOrderWithDeliveryType();

        // when
        ResultActions result = mockMvc.perform(get("/api/orders"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private Order createOrderRequestWithDeliveryType() {
        MenuGroup menuGroup = createAndSaveMenuGroup();
        Product product = createAndSaveProduct();
        Menu menu = createAndSaveMenu(menuGroup, product);

        OrderLineItem orderLineItem = createOrderLineItem(menu);
        OrderTable orderTable = createAndSaveOrderWithDeliveryTypeTable();

        return createOrderWithDeliveryType(orderLineItem, orderTable, OrderStatus.WAITING);
    }

    private Order createAndSaveOrderWithDeliveryType() {
        MenuGroup menuGroup = createAndSaveMenuGroup();
        Product product = createAndSaveProduct();
        Menu menu = createAndSaveMenu(menuGroup, product);

        OrderLineItem orderLineItem = createOrderLineItem(menu);
        OrderTable orderTable = createAndSaveOrderWithDeliveryTypeTable();

        Order order = createOrderWithDeliveryType(orderLineItem, orderTable, OrderStatus.WAITING);
        return orderRepository.save(order);
    }

    private OrderTable createAndSaveOrderWithDeliveryTypeTable() {
        OrderTable orderTable = createEmptyOrderTable();
        orderTableRepository.save(orderTable);
        return orderTable;
    }

    private Menu createAndSaveMenu(MenuGroup menuGroup, Product product) {
        Menu menu = createMenu(menuGroup, product);
        menuRepository.save(menu);
        return menu;
    }

    private Product createAndSaveProduct() {
        Product product = createProduct("김치", 4000);
        productRepository.save(product);
        return product;
    }

    private MenuGroup createAndSaveMenuGroup() {
        MenuGroup menuGroup = createMenuGroup();
        menuGroupRepository.save(menuGroup);
        return menuGroup;
    }
}
