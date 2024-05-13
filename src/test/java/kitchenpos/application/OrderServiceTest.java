package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void create() {

        // given
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(20000));
        menu.setDisplayed(true);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(true);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setQuantity(1);
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(20000));
        orderLineItem.setMenuId(menu.getId());

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(Arrays.asList(orderLineItem));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(request.getType());
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTable(orderTable);

        given(menuRepository.findAllByIdIn(Arrays.asList(orderLineItem.getMenuId())))
                .willReturn(Arrays.asList(menu));
        given(menuRepository.findById(orderLineItem.getMenuId()))
                .willReturn(Optional.of(menu));
        given(orderTableRepository.findById(request.getOrderTableId()))
                .willReturn(Optional.of(orderTable));

        given(orderRepository.save(any())).willReturn(order);

        //when
        Order response  = orderService.create(request);

        //then
        assertEquals(orderTable, response.getOrderTable());

    }

    @Test
    void accept() {

        //given
        UUID request = UUID.randomUUID();
        Order order = new Order();
        order.setStatus(OrderStatus.WAITING);

        given(orderRepository.findById(request))
                .willReturn(Optional.of(order));

        //when
        Order response = orderService.accept(request);

        //then
        assertEquals(OrderStatus.ACCEPTED, response.getStatus());

    }

    @Test
    void serve() {

        //given
        UUID request = UUID.randomUUID();
        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);

        given(orderRepository.findById(request))
                .willReturn(Optional.of(order));

        //when
        Order response = orderService.serve(request);

        //then
        assertEquals(OrderStatus.SERVED, response.getStatus());
    }

    @Test
    void startDelivery() {

        //given
        UUID request = UUID.randomUUID();
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);

        given(orderRepository.findById(request))
                .willReturn(Optional.of(order));

        //when
        Order response = orderService.startDelivery(request);


        //then
        assertEquals(OrderStatus.DELIVERING, response.getStatus());

    }

    @Test
    void completeDelivery() {

        //given
        UUID request = UUID.randomUUID();
        Order order = new Order();
        order.setStatus(OrderStatus.DELIVERING);

        given(orderRepository.findById(request))
                .willReturn(Optional.of(order));


        //when
        Order response = orderService.completeDelivery(request);


        //then
        assertEquals(OrderStatus.DELIVERED, response.getStatus());

    }

    @Test
    void complete() {

        //given
        UUID request= UUID.randomUUID();

        OrderTable orderTable = new OrderTable();

        Order order = new Order();
        order.setId(request);
        order.setOrderTable(orderTable);

        given(orderRepository.findById(request))
                .willReturn(Optional.of(order));


        //when
        Order response = orderService.complete(request);

        //then
        assertEquals(OrderStatus.COMPLETED, response.getStatus());
        assertEquals(0, response.getOrderTable().getNumberOfGuests());
        assertEquals(false, response.getOrderTable().isOccupied());

    }

    @Test
    void findAll() {

        // given
        Order order1 = new Order();
        Order order2 = new Order();

        given(orderRepository.findAll())
                .willReturn(Arrays.asList(order1, order2));

        // when
        List<Order> response = orderService.findAll();

        // then
        assertEquals(2, response.size());
        assertEquals(Arrays.asList(order1, order2), response);

    }
}