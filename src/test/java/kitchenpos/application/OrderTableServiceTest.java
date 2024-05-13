package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.aspectj.weaver.ast.Or;
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

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;


    @Test
    void create() {

        //given
        OrderTable request = new OrderTable();
        request.setName("9번");

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("9번");
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);

        given(orderTableRepository.save(any(OrderTable.class)))
                .willReturn(orderTable);

        //when
        OrderTable response = orderTableService.create(request);

        //then
        assertEquals(orderTable.getId(), response.getId());
        assertEquals(orderTable.getNumberOfGuests(), response.getNumberOfGuests());
        assertEquals(orderTable.getName(), response.getName());


    }

    @Test
    void sit() {

        //given
        UUID request = UUID.randomUUID();
        given(orderTableRepository.findById(request))
                .willReturn(Optional.ofNullable(new OrderTable()));


        //when
        OrderTable response = orderTableService.sit(request);


        //then
        assertEquals(true, response.isOccupied());

    }

    @Test
    void clear() {

        //given
        UUID request = UUID.randomUUID();
        given(orderTableRepository.findById(request))
                .willReturn(Optional.ofNullable(new OrderTable()));


        //when
        OrderTable response = orderTableService.clear(request);


        //then
        assertEquals(false, response.isOccupied());
        assertEquals(0, response.getNumberOfGuests());
    }

    @Test
    void changeNumberOfGuests() {


        //given
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setNumberOfGuests(10);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(request.getId());
        orderTable.setNumberOfGuests(request.getNumberOfGuests());
        orderTable.setOccupied(true);

        given(orderTableRepository.findById(request.getId()))
                .willReturn(Optional.of(orderTable));


        //when
        OrderTable response = orderTableService.changeNumberOfGuests(
                request.getId(), request);


        //then
        assertEquals(request.getNumberOfGuests(), response.getNumberOfGuests());

    }

    @Test
    void findAll() {

        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(0);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(UUID.randomUUID());
        orderTable2.setName("2번");
        orderTable2.setOccupied(true);
        orderTable2.setNumberOfGuests(2);

        given(orderTableRepository.findAll())
                .willReturn(Arrays.asList(orderTable, orderTable2));


        //when
        List<OrderTable> response = orderTableService.findAll();


        //then
        assertThat(response.size()).isEqualTo(2);

    }
}