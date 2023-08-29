package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTableTest {
    @DisplayName("테이블은 이름과 인원수, 착석 여부로 구성되어 있다")
    @Test
    void test1() {
        final UUID id = UUID.randomUUID();
        final String name = "테이블1번";
        final int numberOfGuests = 5;
        final boolean occupied = true;

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);

        assertThat(orderTable.getId()).isEqualTo(id);
        assertThat(orderTable.getName()).isEqualTo(name);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
        assertThat(orderTable.isOccupied()).isEqualTo(occupied);
    }

}