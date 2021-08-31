package kitchenpos.ui;

import kitchenpos.BaseControllerTest;
import kitchenpos.application.OrderService;
import kitchenpos.application.OrderTableService;
import kitchenpos.commons.*;
import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderRestControllerTest extends BaseControllerTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderGenerator orderGenerator;
    @Autowired
    private MenuGroupGenerator menuGroupGenerator;
    @Autowired
    private MenuGenerator menuGenerator;
    @Autowired
    private OrderTableGenerator orderTableGenerator;
    @Autowired
    private ProductGenerator productGenerator;
    @Autowired
    private MenuProductGenerator menuProductGenerator;


    private OrderTable orderTable;
    private MenuGroup menuGroup;
    private Product product;
    private MenuProduct menuProduct;
    private Menu menu;
    private OrderLineItem orderLineItem;
    private List<OrderLineItem> orderLineItems;
    private Order order;

    @Test
    @DisplayName("주문 등록 - 성공")
    void createOrder() throws Exception {
        // given
        menuGroup = menuGroupGenerator.generate();

        product = productGenerator.generate();

        menuProduct = menuProductGenerator.generateByProduct(product);

        menu = menuGenerator.generateByMenuGroupAndMenuProducts(menuGroup, Collections.singletonList(menuProduct));

        orderTable = orderTableGenerator.generate();
        orderTable = orderTableService.sit(orderTable.getId());

        orderLineItem = generateOrderLineItemByMenu(menu);

        orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setDeliveryAddress("address");
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(orderLineItems);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order))
        ).andDo(print());

        // then
        resultActions.andExpect(status().isCreated());

        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String redirectedUrl = response.getRedirectedUrl();
        int index = redirectedUrl != null ? redirectedUrl.lastIndexOf("/") : 0;
        String stringUUID = redirectedUrl.substring(index + 1);
        UUID uuid = UUID.fromString(stringUUID);

        Order order = orderRepository.findById(uuid).get();
        assertThat(order).isNotNull();

    }

    private OrderLineItem generateOrderLineItemByMenu(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(2L);
        orderLineItem.setPrice(BigDecimal.valueOf(1000));
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    private Order generateOrder(OrderType orderType) {
        menuGroup = menuGroupGenerator.generate();

        product = productGenerator.generate();

        menuProduct = menuProductGenerator.generateByProduct(product);

        menu = menuGenerator.generateByMenuGroupAndMenuProducts(menuGroup, Collections.singletonList(menuProduct));

        orderTable = orderTableGenerator.generate();
        orderTable = orderTableService.sit(orderTable.getId());

        orderLineItem = generateOrderLineItemByMenu(menu);

        orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        order = orderGenerator.generateByOrderTypeAndOrderTableAndOrderLineItems(orderType, orderTable, orderLineItems);
        return order;
    }

    @Test
    @DisplayName("주문 accept")
    void acceptOrder() throws Exception {
        // given
        generateOrder(OrderType.EAT_IN);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/orders/{orderId}/accept", order.getId())
        ).andDo(print());

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 serve")
    void serveOrder() throws Exception {
        // given
        generateOrder(OrderType.EAT_IN);
        order = orderService.accept(order.getId());

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/orders/{orderId}/serve", order.getId())
        ).andDo(print());

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 start delivery")
    void startDeliveryOrder() throws Exception {
        // given
        generateOrder(OrderType.DELIVERY);
        order = orderService.accept(order.getId());
        order = orderService.serve(order.getId());

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/orders/{orderId}/start-delivery", order.getId())
        ).andDo(print());

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 complete delivery")
    void completeDeliveryOrder() throws Exception {
        // given
        generateOrder(OrderType.DELIVERY);
        order = orderService.accept(order.getId());
        order = orderService.serve(order.getId());
        order = orderService.startDelivery(order.getId());

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/orders/{orderId}/complete-delivery", order.getId())
        ).andDo(print());

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 complete")
    void completeOrder() throws Exception {
        // given
        generateOrder(OrderType.DELIVERY);
        order = orderService.accept(order.getId());
        order = orderService.serve(order.getId());
        order = orderService.startDelivery(order.getId());
        order = orderService.completeDelivery(order.getId());

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/orders/{orderId}/complete", order.getId())
        ).andDo(print());

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 주문 조회 - 성공")
    void getListOrder() throws Exception {
        // given
        int size = 10;
        List<Order> orders = IntStream.range(0, size).mapToObj(i -> generateOrder(OrderType.EAT_IN)).collect(Collectors.toList());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/orders")
        ).andDo(print());

        // then
        resultActions.andExpect(status().isOk());
    }
}