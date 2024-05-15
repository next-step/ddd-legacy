package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.testfixture.InMemoryMenuRepository;
import kitchenpos.testfixture.InMemoryOrderRepository;
import kitchenpos.testfixture.InMemoryOrderTableRepository;
import kitchenpos.testfixture.OrderTableTestFixture;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    private OrderRepository orderRepository = new InMemoryOrderRepository();

    @InjectMocks
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp(){
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }


    @Test
    void create() {

        //given
        OrderTable request = OrderTableTestFixture.createOrderTableRequest();

        OrderTable orderTable = OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(),request.getName(), request.isOccupied(), request.getNumberOfGuests());

        //when
        OrderTable response = orderTableService.create(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertEquals(orderTable.getNumberOfGuests(), response.getNumberOfGuests());
        assertEquals(orderTable.getName(), response.getName());


    }

    @Test
    void sit() {

        //given
        OrderTable request = OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(), "2번", false, 10);
        orderTableRepository.save(request);

        //when
        OrderTable response = orderTableService.sit(request.getId());

        //then
        assertEquals(true, response.isOccupied());

    }

    @Test
    void clear() {

        //given
        OrderTable request = OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(), "2번", true, 10);
        orderTableRepository.save(request);

        //when
        OrderTable response = orderTableService.clear(request.getId());

        //then
        assertEquals(false, response.isOccupied());
        assertEquals(0, response.getNumberOfGuests());
    }

    @Test
    void changeNumberOfGuests() {


        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(), "3번", true, 10);
        orderTableRepository.save(orderTable);

        OrderTable request = orderTable;
        request.setNumberOfGuests(5);

        //when
        OrderTable response = orderTableService.changeNumberOfGuests(
                request.getId(), request);


        //then
        assertEquals(request.getNumberOfGuests(), response.getNumberOfGuests());

    }

    @Test
    void findAll() {

        //given
        OrderTable orderTable1 = OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(), "1번", false, 0
        );
        OrderTable orderTable2 =  OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(), "2번", true, 2
        );
        orderTableRepository.save(orderTable1);
        orderTableRepository.save(orderTable2);

        //when
        List<OrderTable> response = orderTableService.findAll();

        //then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response
                .stream()
                .anyMatch(res ->res.getName().contains(orderTable1.getName())))
                .isTrue();
        assertThat(response
                .stream()
                .anyMatch(res ->res.getName().contains(orderTable2.getName())))
                .isTrue();

    }
}