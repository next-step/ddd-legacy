package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createWithNullName() {
        // given
        OrderTable request = new OrderTable();
        request.setName(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderTableService.create(request));
    }

    @Test
    void createWithEmptyName() {
        // given
        OrderTable request = new OrderTable();
        request.setName("");

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderTableService.create(request));
    }

    @Test
    void createSuccessfully() {
        // given
        OrderTable request = new OrderTable();
        request.setName("1번");

        // when
        OrderTable created = orderTableService.create(request);

        // then
        assertNotNull(created.getId());
        assertEquals("1번", created.getName());
        assertEquals(0, created.getNumberOfGuests());
        assertFalse(created.isOccupied());
    }

    @Test
    void sitAtNonExistentTable() {
        // when & then
        assertThrows(NoSuchElementException.class,
                () -> orderTableService.sit(UUID.randomUUID()));
    }

    @Test
    void sitSuccessfully() {
        // given
        OrderTable table = createTable("1번");

        // when
        OrderTable occupied = orderTableService.sit(table.getId());

        // then
        assertTrue(occupied.isOccupied());
    }

    @Test
    void clearNonExistentTable() {
        // when & then
        assertThrows(NoSuchElementException.class,
                () -> orderTableService.clear(UUID.randomUUID()));
    }

    @Test
    void clearTableWithIncompleteOrder() {
        // given
        OrderTable table = createTable("1번");
        table.setOccupied(true);
        orderTableRepository.save(table);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderTable(table);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderTableService.clear(table.getId()));
    }

    @Test
    void clearTableSuccessfully() {
        // given
        OrderTable table = createTable("1번");
        table.setOccupied(true);
        table.setNumberOfGuests(4);
        orderTableRepository.save(table);

        // when
        OrderTable cleared = orderTableService.clear(table.getId());

        // then
        assertFalse(cleared.isOccupied());
        assertEquals(0, cleared.getNumberOfGuests());
    }

    @Test
    void changeNumberOfGuestsWithNegativeNumber() {
        // given
        OrderTable table = createTable("1번");
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderTableService.changeNumberOfGuests(table.getId(), request));
    }

    @Test
    void changeNumberOfGuestsAtNonExistentTable() {
        // given
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        // when & then
        assertThrows(NoSuchElementException.class,
                () -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request));
    }

    @Test
    void changeNumberOfGuestsAtUnoccupiedTable() {
        // given
        OrderTable table = createTable("1번");
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderTableService.changeNumberOfGuests(table.getId(), request));
    }

    @Test
    void changeNumberOfGuestsSuccessfully() {
        // given
        OrderTable table = createTable("1번");
        table.setOccupied(true);
        orderTableRepository.save(table);

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        // when
        OrderTable updated = orderTableService.changeNumberOfGuests(table.getId(), request);

        // then
        assertEquals(4, updated.getNumberOfGuests());
    }

    private OrderTable createTable(String name) {
        OrderTable table = new OrderTable();
        table.setName(name);
        return orderTableService.create(table);
    }
}
