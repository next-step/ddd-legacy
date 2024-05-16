package kitchenpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.config.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.util.MockMvcUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static kitchenpos.fixture.MenuFixture.createMenuWithId;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupWithId;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderFixture.createOrderWithId;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTableWithId;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@IntegrationTest
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Nested
    class createTest {
        @DisplayName("먹고가기 주문인 경우 생성한다.")
        @Test
        void createWhenEatInTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));
            MvcResult result = mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(createOrder(orderTable.getId(), List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.EAT_IN, null, null))))
                    .andReturn();

            Order order = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(result.getResponse().getHeader("Location")).isNotNull(),
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(order.getOrderDateTime()).isNotNull()
            );
        }

        @DisplayName("포장하기 주문인 경우 생성한다.")
        @Test
        void createWhenTakeOutTest() throws Exception {
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));
            MvcResult result = mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(createOrder(null, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.TAKEOUT, null, null))))
                    .andReturn();

            Order order = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(result.getResponse().getHeader("Location")).isNotNull(),
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(order.getOrderDateTime()).isNotNull()
            );
        }

        @DisplayName("배달하기인 경우 생성한다.")
        @Test
        void createWhenDeliveryTest() throws Exception {
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));
            MvcResult result = mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(createOrder(null, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.DELIVERY, null, "서울시 송파구 위례성대로 2"))))
                    .andReturn();

            Order order = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(result.getResponse().getHeader("Location")).isNotNull(),
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(order.getOrderDateTime()).isNotNull()
            );
        }
    }

    @Nested
    class acceptTest {
        @DisplayName("먹고가기 주문을 접수한다.")
        @Test
        void acceptSuccessWhenEatInTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.EAT_IN, OrderStatus.WAITING, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/accept"))
                    .andReturn();

            Order acceptedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
            );
        }

        @DisplayName("포장하기 주문을 접수한다.")
        @Test
        void acceptSuccessWhenTakeoutTest() throws Exception {
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(null, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.TAKEOUT, OrderStatus.WAITING, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/accept"))
                    .andReturn();

            Order acceptedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
            );
        }

        @DisplayName("배달하기 주문을 접수한다.")
        @Test
        void acceptSuccessWhenDeliveryTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.DELIVERY, OrderStatus.WAITING, "서울시 송파구 위례성대로 2", LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/accept"))
                    .andReturn();

            Order acceptedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
            );
        }

    }

    @Nested
    class serveTest {
        @DisplayName("먹고가기인 경우 서빙한다.")
        @Test
        void serveSuccessWhenEatInTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.EAT_IN, OrderStatus.ACCEPTED, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/serve"))
                    .andReturn();

            Order servedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED)
            );
        }

        @DisplayName("포장하기인 경우 서빙한다.")
        @Test
        void serveSuccessWhenTakeoutTest() throws Exception {
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(null, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.TAKEOUT, OrderStatus.ACCEPTED, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/serve"))
                    .andReturn();

            Order servedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED)
            );
        }

        @DisplayName("배달하기인 경우 서빙한다.")
        @Test
        void serveSuccessWhenDeliveryTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.DELIVERY, OrderStatus.ACCEPTED, "서울시 송파구 위례성대로 2", LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/serve"))
                    .andReturn();

            Order servedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED)
            );
        }
    }

    @Nested
    class startDeliveryTest {
        @DisplayName("배달하기인 경우 배달 시작을 한다.")
        @Test
        void startDeliverySuccessWhenDeliveryTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.DELIVERY, OrderStatus.SERVED, "서울시 송파구 위례성대로 2", LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/start-delivery"))
                    .andReturn();

            Order startedDeliveryOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(startedDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING)
            );
        }
    }

    @Nested
    class completeDeliveryTest {
        @DisplayName("배달하기인 경우 배달 종료를 한다.")
        @Test
        void completeDeliverySuccessWhenDeliveryTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.DELIVERY, OrderStatus.DELIVERING, "서울시 송파구 위례성대로 2", LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/complete-delivery"))
                    .andReturn();

            Order completedDeliveryOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(completedDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED)
            );
        }
    }

    @Nested
    class completeTest {
        @DisplayName("먹고가기인 경우 주문 완료한다.")
        @Test
        void completeSuccessWhenEatInTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.EAT_IN, OrderStatus.SERVED, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/complete"))
                    .andReturn();

            Order completedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED)
            );
        }

        @DisplayName("포장하기인 경우 주문 완료한다.")
        @Test
        void completeSuccessWhenTakeoutTest() throws Exception {
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(null, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.TAKEOUT, OrderStatus.SERVED, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/complete"))
                    .andReturn();

            Order completedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED)
            );
        }

        @DisplayName("배달하기인 경우 주문 완료한다.")
        @Test
        void completeSuccessWhenDeliveryTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            Order order = orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.DELIVERY, OrderStatus.DELIVERED, "서울시 송파구 위례성대로 2", LocalDateTime.now()));

            MvcResult result = mockMvc.perform(put("/api/orders/" + order.getId() + "/complete"))
                    .andReturn();

            Order completedOrder = MockMvcUtil.readValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED)
            );
        }

    }

    @Nested
    class findAllTest {
        @DisplayName("주문을 전체 조회 한다.")
        @Test
        void findAllTest() throws Exception {
            OrderTable orderTable = orderTableRepository.save(createOrderTableWithId("1번테이블", true, 4));
            Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
            MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
            Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1))));

            orderRepository.save(createOrderWithId(orderTable, List.of(createOrderLineItem(menu, BigDecimal.valueOf(16000), 1)), OrderType.EAT_IN, OrderStatus.WAITING, null, LocalDateTime.now()));

            MvcResult result = mockMvc.perform(get("/api/orders"))
                    .andReturn();

            List<Order> orders = MockMvcUtil.readListValue(objectMapper, result, Order.class);

            assertAll(
                    () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(orders).hasSize(1)
            );
        }
    }
}
