package kitchenpos.application;

import kitchenpos.FixtureFactory;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OrderTableServiceTest extends IntegrationTest {

    private static final int INITIAL_NUMBER_OF_GUESTS = 0;
    private static final boolean INITIAL_OCCUPIED = false;


    @Nested
    @DisplayName("주문테이블을 만들 떄")
    class CreateTest {

        @Test
        @DisplayName("성공적으로 주문테이블을 만든다")
        void create() {
            String name = "테이블1";
            OrderTable orderTable = orderTableService.create(FixtureFactory.createOrderTable(name, INITIAL_NUMBER_OF_GUESTS, INITIAL_OCCUPIED));

            //then
            assertAll(
                () -> assertEquals(name, orderTable.getName()),
                () -> assertEquals(INITIAL_NUMBER_OF_GUESTS, orderTable.getNumberOfGuests()),
                () -> assertEquals(INITIAL_OCCUPIED, orderTable.isOccupied())
            );
        }

        @NullAndEmptySource
        @ParameterizedTest
        @DisplayName("이름이 공백이거나 null일 수 없다.")
        void create_InvalidName(String name) {
            OrderTable orderTable = FixtureFactory.createOrderTable(name);

            //when
            assertThrows(IllegalArgumentException.class, () -> orderTableService.create(orderTable));
        }
    }

    @Test
    @DisplayName("주문테이블에 누군가가 앉으면, 좌석을 차지하게 된다")
    void sit() {
        OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", INITIAL_NUMBER_OF_GUESTS, INITIAL_OCCUPIED));

        orderTableService.sit(orderTable.getId());

        //then
        OrderTable occupiedTable = orderTableRepository.findById(orderTable.getId()).orElseThrow(IllegalArgumentException::new);
        assertTrue(occupiedTable.isOccupied());
    }

    @Test
    @DisplayName("주문테이블에 누군가가 앉아있는 상태에서 누군가가 일어나면, 좌석을 비운다")
    void clear() {
        OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", INITIAL_NUMBER_OF_GUESTS, true));

        orderTableService.clear(orderTable.getId());

        //then
        OrderTable clearedTable = orderTableRepository.findById(orderTable.getId()).orElseThrow(IllegalArgumentException::new);
        assertFalse(clearedTable.isOccupied());
    }

    @Test
    @DisplayName("주문 완료되지 않은 테이블은 비울 수 없다")
    void cannot_clear_complete_order_table() {
        MenuGroup menuGroup = menuGroupRepository.save(FixtureFactory.createMenuGroup("추천메뉴"));
        Product seasonedChicken = FixtureFactory.createProduct("양념치킨", BigDecimal.valueOf(16000));
        Product friedChicken = FixtureFactory.createProduct("후라이드치킨", BigDecimal.valueOf(16000));
        productRepository.saveAll(List.of(seasonedChicken, friedChicken));
        List<MenuProduct> menuProducts = toMenuProductList(seasonedChicken, friedChicken);
        Menu menu = menuRepository.save(FixtureFactory.createMenu("양념 + 후라이드", BigDecimal.valueOf(16000), true, menuGroup, menuProducts));

        OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", INITIAL_NUMBER_OF_GUESTS, true));

        List<OrderLineItem> orderLineItem = List.of(FixtureFactory.createOrderLineItem(menu, menu.getPrice(), 1L));
        orderRepository.save(FixtureFactory.createEatInOrder(OrderStatus.SERVED, orderTable, orderLineItem));

        //when
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문이 완료되지 않은 테이블은 빈 테이블로 변경할 수 없습니다.");
    }


    @Test
    @DisplayName("주문테이블을 모두 확인할 수 있다.")
    void findAll() {
        List<OrderTable> orderTables = orderTableRepository.saveAll(List.of(
            FixtureFactory.createOrderTable("테이블1", INITIAL_NUMBER_OF_GUESTS, INITIAL_OCCUPIED),
            FixtureFactory.createOrderTable("테이블2", INITIAL_NUMBER_OF_GUESTS, INITIAL_OCCUPIED),
            FixtureFactory.createOrderTable("테이블3", INITIAL_NUMBER_OF_GUESTS, INITIAL_OCCUPIED)
        ));

        List<OrderTable> foundOrderTables = orderTableService.findAll();

        assertThat(foundOrderTables).usingRecursiveComparison().isEqualTo(orderTables);
    }

    @Nested
    @DisplayName("주문테이블에 앉은 사람의 수를 기록할 때")
    class NumberOfGuestTest {

        @Test
        @DisplayName("성공적으로 기록한다.")
        void change_number_of_guests() {
            OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", INITIAL_NUMBER_OF_GUESTS, true));
            int numberOfGuests = 3;
            orderTable.setNumberOfGuests(numberOfGuests);

            orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

            OrderTable changedOrderTable = orderTableRepository.findById(orderTable.getId()).orElseThrow(IllegalArgumentException::new);
            assertEquals(numberOfGuests, changedOrderTable.getNumberOfGuests());
        }

        @Test
        @DisplayName("앉은 사람이 음수이면 예외를 발생한다.")
        void change_number_of_guest_negative() {
            OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", -1, true));
            assertThrows(IllegalArgumentException.class, () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @Test
        @DisplayName("누군가 앉아 있지 않으면 예외를 발생한다.")
        void change_number_of_guest_fail() {
            OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", INITIAL_NUMBER_OF_GUESTS, INITIAL_OCCUPIED));
            int numberOfGuests = 3;
            orderTable.setNumberOfGuests(numberOfGuests);

            assertThrows(IllegalStateException.class, () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }


    }


}
