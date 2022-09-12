package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableTest {
    @DisplayName("매장테이블은 이름, 차지여부, 손님수를 가지고 있다.")
    @Test
    void properties() {
        final var table = new OrderTable();
        table.setName("테이블 1");
        table.setOccupied(true);
        table.setNumberOfGuests(4);

        assertAll(
                () -> assertThat(table.getName()).isEqualTo("테이블 1"),
                () -> assertThat(table.isOccupied()).isEqualTo(true),
                () -> assertThat(table.getNumberOfGuests()).isEqualTo(4)
        );
    }
}