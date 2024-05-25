package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.application.MenuFixture.createMenuRequest;
import static kitchenpos.application.MenuGroupFixture.createMenuGroupRequest;
import static kitchenpos.application.OrderFixture.createOrderRequest;
import static kitchenpos.application.OrderLineItemFixture.createOrderLineItemRequest;
import static kitchenpos.application.OrderTableFixture.createOrderTableRequest;
import static kitchenpos.application.ProductFixture.createProductRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    private OrderService orderService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KitchenridersClient kitchenridersClient;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("주문을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroupRequest);

        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Product savedProduct = productRepository.save(productRequest);

        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), savedMenuGroup, savedProduct);
        final Menu savedMenu = menuRepository.save(menuRequest);

        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(savedMenu);
        final OrderTable orderTableRequest = createOrderTableRequest("테이블1");
        final OrderTable savedOrderTable = orderTableRepository.save(orderTableRequest);

        final Order orderRequest = createOrderRequest(savedOrderTable, OrderType.EAT_IN, orderLineItemRequest);
        final Order savedOrder = orderRepository.save(orderRequest);

        // when
        final Order order = orderService.create(orderRequest);

        // then
        assertThat(order.getOrderTableId()).isEqualTo(savedOrder.getOrderTableId());
    }

    @DisplayName("매장 주문을 수락 받을 수 있다.")
    @Test
    void accept() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final OrderTable orderTableRequest = createOrderTableRequest("테이블1");
        final Order orderRequest = createOrderRequest(orderTableRequest, OrderType.EAT_IN, orderLineItemRequest);

        final Order orderRequestForSetup = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderTableRequest, orderRequest);

        // when
        final Order accept = orderService.accept(orderRequestForSetup.getId());

        // then
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

    }

    @DisplayName("배달 주문을 수락받을 수 있다.")
    @Transactional
    @Test
    void accept2() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.DELIVERY, "서울 강남",orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);

        // when
        final Order accept = orderService.accept(savedOrder.getId());

        // then
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

    }

    @DisplayName("포장 주문을 수락받을 수 있다.")
    @Transactional
    @Test
    void accept3() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.TAKEOUT, orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);

        // when
        final Order accept = orderService.accept(savedOrder.getId());

        // then
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

    }


    @DisplayName("주문 상태를 served로 변경할 수 있다.")
    @Transactional
    @Test
    void serve() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.DELIVERY, "서울 강남",orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);
        final Order accept = orderService.accept(savedOrder.getId());


        // when
        final Order serve = orderService.serve(accept.getId());
        // then
        assertThat(serve.getStatus()).isEqualTo(OrderStatus.SERVED);


    }

    @DisplayName("배달 주문을 시작할 수 있다.")
    @Transactional
    @Test
    void startDelivery() {

        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.DELIVERY, "서울 강남",orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);
        final Order accept = orderService.accept(savedOrder.getId());
        final Order serve = orderService.serve(accept.getId());

        // when
        final Order order = orderService.startDelivery(accept.getId());

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);

    }

    @DisplayName("배달 주문을 배달 완료 상태로 변경할 수 있다.")
    @Transactional
    @Test
    void completeDelivery() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.DELIVERY, "서울 강남",orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);
        final Order accept = orderService.accept(savedOrder.getId());
        final Order serve = orderService.serve(accept.getId());
        final Order deliveringOrder = orderService.startDelivery(accept.getId());

        // when
        final Order completeDelivery = orderService.completeDelivery(deliveringOrder.getId());

        // then
        assertThat(completeDelivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("매장 주문을 완료할 수 있다.")
    @Transactional
    @Test
    void complete() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final OrderTable orderTableRequest = createOrderTableRequest("테이블1");
        final Order orderRequest = createOrderRequest(orderTableRequest, OrderType.EAT_IN, orderLineItemRequest);

        final Order orderRequestForSetup = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderTableRequest, orderRequest);
        final Order acceptOrder = orderService.accept(orderRequestForSetup.getId());
        final Order servedOrder = orderService.serve(acceptOrder.getId());

        // when
        final Order completeOrder = orderService.complete(servedOrder.getId());

        // then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(completeOrder.getOrderTable().getNumberOfGuests()).isZero();
        assertThat(completeOrder.getOrderTable().isOccupied()).isFalse();

    }

    @DisplayName("배달 주문을 완료할 수 있다.")
    @Transactional
    @Test
    void complete2() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.DELIVERY, "서울 강남",orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);
        final Order accept = orderService.accept(savedOrder.getId());
        final Order serve = orderService.serve(accept.getId());
        final Order deliveringOrder = orderService.startDelivery(accept.getId());
        final Order completeDelivery = orderService.completeDelivery(deliveringOrder.getId());

        // when
        final Order completeOrder = orderService.complete(completeDelivery.getId());

        // then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

    }

    @DisplayName("포장 주문을 완료할 수 있다.")
    @Transactional
    @Test
    void complete3() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final Order orderRequest = createOrderRequest(OrderType.TAKEOUT, orderLineItemRequest);

        final Order savedOrder = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderRequest);
        final Order accept = orderService.accept(savedOrder.getId());
        final Order serve = orderService.serve(accept.getId());

        // when
        final Order completeOrder = orderService.complete(serve.getId());

        // then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

    }


    @DisplayName("주문을 조회할 수 있다.")
    @Transactional
    @Test
    void findAll() {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final Menu menuRequest = createMenuRequest("후라이드", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest(menuRequest);
        final OrderTable orderTableRequest = createOrderTableRequest("테이블1");
        final Order orderRequest = createOrderRequest(orderTableRequest, OrderType.EAT_IN, orderLineItemRequest);

        final Order orderRequestForSetup = orderRequestSave(menuGroupRequest, productRequest, menuRequest, orderTableRequest, orderRequest);
        final Order acceptOrder = orderService.accept(orderRequestForSetup.getId());
        final Order servedOrder = orderService.serve(acceptOrder.getId());

        // given
        final MenuGroup menuGroupRequest2 = createMenuGroupRequest("두마리메뉴");
        final Product productRequest2 = createProductRequest("양념치킨", 16_000L);
        final Menu menuRequest2 = createMenuRequest("앙념치킨", BigDecimal.valueOf(16_000L), menuGroupRequest, productRequest);
        final OrderLineItem orderLineItemRequest2 = createOrderLineItemRequest(menuRequest);
        final OrderTable orderTableRequest2 = createOrderTableRequest("테이블2");
        final Order orderRequest2 = createOrderRequest(orderTableRequest, OrderType.EAT_IN, orderLineItemRequest);

        final Order orderRequestForSetup2 = orderRequestSave(menuGroupRequest2, productRequest2, menuRequest2, orderTableRequest2, orderRequest2);
        final Order acceptOrder2 = orderService.accept(orderRequestForSetup2.getId());
        final Order servedOrder2 = orderService.serve(acceptOrder2.getId());

        // when
        final List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).hasSize(2);

    }

    Order orderRequestSave(final MenuGroup menuGroupRequest, final Product productRequest, final Menu menuRequest, final OrderTable orderTableRequest, final Order orderRequest){
        final MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroupRequest);
        final Product savedProduct = productRepository.save(productRequest);
        final Menu savedMenu = menuRepository.save(menuRequest);
        final OrderTable savedOrderTable = orderTableRepository.save(orderTableRequest);

        return orderRepository.save(orderRequest);
    }

    Order orderRequestSave(final MenuGroup menuGroupRequest, final Product productRequest, final Menu menuRequest, final Order orderRequest){
        final MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroupRequest);
        final Product savedProduct = productRepository.save(productRequest);
        final Menu savedMenu = menuRepository.save(menuRequest);

        return orderRepository.save(orderRequest);
    }
}