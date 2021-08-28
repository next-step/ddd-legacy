package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Spy
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    @Spy
    private final OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블 생성")
    @Test
    void create() {
        OrderTable expected = orderTableService.create(new OrderTable("10번"));

        assertAll(
                () -> assertThat(expected.getId()).isNotNull(),

                // 테이블 생성시점의 해당 테이블의 이용객 수는 0 명으로 처리해야 한다.
                () -> assertThat(expected.getNumberOfGuests()).isEqualTo(0),

                // 테이블 생성시점의 해당 테이블의 이용 여부는 '비어있음' 상태로 처리해야 한다.
                () -> assertThat(expected.isEmpty()).isTrue()
        );
    }

    @DisplayName("테이블 착석 - 테이블 착석 처리 시점에 테이블의 이용 여부가 '비어있지 않음' 상태로 처리되어야 한다.")
    @Test
    void sit() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));
        OrderTable expected = orderTableService.sit(actual.getId());
        assertThat(expected.isEmpty()).isFalse();
    }

    @DisplayName("테이블 착석 - 존재하지 않는 테이블을 착석 처리할 수 없다.")
    @Test
    void sitValidation() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));
        when(orderTableRepository.findById(actual.getId())).thenThrow(NoSuchElementException.class);

        assertThatThrownBy(() -> orderTableService.sit(actual.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블 정리")
    @Test
    void clear() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));
        OrderTable expected = orderTableService.clear(actual.getId());
        assertAll(
                // 테이블 정리 시점에 해당 테이블의 이용객 수는 0 명으로 변경되어야 한다.
                () -> assertThat(expected.getNumberOfGuests()).isEqualTo(0),

                // 테이블 정리 시점에 해당 테이블의 이용 여부는 '비어있음' 상태로 변경 되어야 한다.
                () -> assertThat(expected.isEmpty()).isTrue()
        );
    }

    @DisplayName("테이블 정리 - 정리처리를 하는 시점에 해당 테이블에서 발생된 주문의 상태가 COMPLETED(완료됨) 이어야 한다.")
    @Test
    void clearValidation() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));
        when(orderRepository.existsByOrderTableAndStatusNot(actual, OrderStatus.COMPLETED)).thenReturn(true);
        assertThatThrownBy(() -> orderTableService.clear(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 인원 수를 변경")
    @Test
    void changeNumberOfGuests() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));
        actual.setEmpty(false);
        orderTableRepository.save(actual);
        OrderTable expected = orderTableService.changeNumberOfGuests(actual.getId(), new OrderTable(10));

        assertThat(expected.getNumberOfGuests()).isEqualTo(10);
    }

    @DisplayName("테이블 인원 수를 변경 - 음수의 인원수로 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsValidationNumber() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));
        actual.setEmpty(false);
        orderTableRepository.save(actual);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(actual.getId(), new OrderTable(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 인원 수를 변경 - 비어있는 상태의 테이블의 인원수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsValidationEmpty() {
        OrderTable actual = orderTableService.create(new OrderTable("10번"));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(actual.getId(), new OrderTable(10)))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("모든 테이블 조회")
    @Test
    void findAll() {
        orderTableRepository.save(new OrderTable());
        orderTableRepository.save(new OrderTable());
        assertThat(orderTableService.findAll().size()).isEqualTo(orderTableRepository.findAll().size());
    }

}

