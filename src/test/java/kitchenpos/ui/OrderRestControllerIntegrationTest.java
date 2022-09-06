package kitchenpos.ui;

import static kitchenpos.fixtures.MenuFixtures.createMenu;
import static kitchenpos.fixtures.MenuFixtures.createMenuGroup;
import static kitchenpos.fixtures.MenuFixtures.createMenuProduct;
import static kitchenpos.fixtures.MenuFixtures.createProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderRestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private MenuGroupRepository menuGroupRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderTableRepository orderTableRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderService orderService;

  @AfterEach
  void tearDown() {
  }

  @DisplayName("유효한 주문생성 요청에 HTTP 201 상태값과 함께 생성된 주문건을 반환한다")
  @Test
  void givenValidOrder_whenCreate_thenStatus201WithCratedOrder() throws Exception {
    Order order = createEatInOrder();

    mvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.type").value(OrderType.EAT_IN.name()))
        .andExpect(jsonPath("$.status").value(OrderStatus.WAITING.name()))
        .andExpect(jsonPath("$.orderDateTime").exists())
        .andExpect(jsonPath("$.orderLineItems", hasSize(1)))
        .andExpect(jsonPath("$.orderTable.id").exists())
        .andExpect(jsonPath("$.orderTable.numberOfGuests").value(5))
        .andExpect(jsonPath("$.orderTable.occupied").value(true));
  }

  @DisplayName("주문 접수 요청에 HTTP 200과 함께 접수완료된 주문을 반환한다")
  @Test
  void givenValidOrder_whenAccept_thenStatus200WithAcceptedOrder() throws Exception {
    // given
    Order savedOrder = orderService.create(createEatInOrder());

    mvc.perform(
            put("/api/orders/{orderId}/accept", savedOrder.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.type").value(OrderType.EAT_IN.name()))
        .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.name()));
  }

  private Order createEatInOrder() {
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));

    MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
    Product savedProduct1 = productRepository.save(product1);
    Product savedProduct2 = productRepository.save(product2);

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        savedMenuGroup,
        List.of(
            createMenuProduct(savedProduct1, 1),
            createMenuProduct(savedProduct2, 1)
        )
    );

    Menu savedMenu = menuRepository.save(menu);

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName("주문테이블1");
    orderTable.setOccupied(true);
    orderTable.setNumberOfGuests(5);
    OrderTable savedOrderTable = orderTableRepository.save(orderTable);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(savedMenu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(savedOrderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));
    return order;
  }
}
