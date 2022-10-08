package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql({"/truncate-all.sql", "/insert-order-table-integration.sql"})
@SpringBootTest
class OrderTableServiceTest {
    @Autowired
    private OrderTableService sut;

    @DisplayName("주문 테이블을 생성할 수 있다.")
    @Test
    void create() {
        final String name = "주문-테이블-이름";
        final OrderTable request = new OrderTable(name);

        final OrderTable response = sut.create(request);

        assertAll(
                () -> assertThat(response.getName()).isEqualTo(name),
                () -> assertThat(response.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(response.isOccupied()).isFalse());
    }

    @DisplayName("주문 테이블의 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithEmptyName(final String name) {
        final OrderTable request = new OrderTable(name);

        assertThatThrownBy( () -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블에 손님이 앉을 수 있다.")
    @Test
    void sit() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035520");

        final OrderTable response = sut.sit(orderTableId);

        assertThat(response.isOccupied()).isTrue();
    }

    @DisplayName("주문 테이블을 정리할 수 있다")
    @Test
    void clear() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035521");

        final OrderTable response = sut.clear(orderTableId);

        assertThat(response.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블의 주문 상태가 완료되어야 정리할 수 있다.")
    @Test
    void clearWithNotCompleted() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035522");

        assertThatThrownBy(() -> sut.clear(orderTableId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블의 손님수는 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035521");
        final OrderTable request = new OrderTable(5);

        final OrderTable response = sut.changeNumberOfGuests(orderTableId, request);

        assertThat(response.getNumberOfGuests()).isEqualTo(5);
    }

    @DisplayName("손님의 수는 0 보다 같거나 커야 한다.")
    @Test
    void changeNumberOfGuestsLessThanZero() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035521");
        final OrderTable request = new OrderTable(-1);

        assertThatThrownBy(() -> sut.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 주문 테이블 이여야 한다.")
    @Test
    void changeNumberOfGuestsWithoutOrderTable() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035523");
        final OrderTable request = new OrderTable(5);

        assertThatThrownBy(() -> sut.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("사용되고 있지 않은 주문 테이블 이여야 한다.")
    @Test
    void changeNumberOfGuestsWithOccupiedTable() {
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035520");
        final OrderTable request = new OrderTable(5);

        assertThatThrownBy(() -> sut.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블을 여러개 조회할 수 있다.")
    @Test
    void findAll() {
        final List<OrderTable> response = sut.findAll();

        assertThat(response).hasSize(3);
    }

}
