package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FakeOrderTableRepositoryTest {

    // SUT

    private final FakeOrderTableRepository fakeOrderTableRepository = new FakeOrderTableRepository();

    @DisplayName("주문 테이블이 저장되어야 한다.")
    @ValueSource(strings = {
            "limit", "ear", "stage", "influence", "surprise",
            "harvest", "court", "break", "grow", "tour",
    })
    @ParameterizedTest
    void njsxplzb(final String name) {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);

        // when
        final OrderTable savedOrderTable = this.fakeOrderTableRepository.save(orderTable);

        // then
        assertThat(savedOrderTable).isEqualTo(orderTable);
    }

    @DisplayName("저장된 주문 테이블은 ID로 찾을 수 있어야 한다.")
    @Test
    void riapcxmc() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        final OrderTable savedOrderTable = this.fakeOrderTableRepository.save(orderTable);

        // when
        final OrderTable foundOrderTable = this.fakeOrderTableRepository
                .findById(savedOrderTable.getId())
                .orElse(null);

        // then
        assertThat(foundOrderTable).isEqualTo(orderTable);
    }

    @DisplayName("저장되지 않은 주문 테이블은 ID로 찾을 수 없어야 한다.")
    @Test
    void nkjtpobv() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        this.fakeOrderTableRepository.save(orderTable);

        // when
        final OrderTable foundOrderTable = this.fakeOrderTableRepository.findById(UUID.randomUUID())
                .orElse(null);

        // then
        assertThat(foundOrderTable).isNull();
    }

    @DisplayName("빈 상태에서 모두 조회시 빈 List가 반환되어야 한다.")
    @Test
    void kavmvasa() {
        // when
        final List<OrderTable> orderTables = this.fakeOrderTableRepository.findAll();

        // then
        assertThat(orderTables).isEmpty();
    }

    @DisplayName("모두 조회시 저장된 수 만큼 조회되어야 한다.")
    @ValueSource(ints = {
            19, 20, 17, 23, 21,
            26, 4, 11, 24, 7,
    })
    @ParameterizedTest
    void rlqonpkw(int size) {
        // given
        IntStream.range(0, size)
                .forEach(n -> {
                    final OrderTable orderTable = new OrderTable();
                    orderTable.setId(UUID.randomUUID());
                    this.fakeOrderTableRepository.save(orderTable);
                });

        // when
        final List<OrderTable> orderTables = this.fakeOrderTableRepository.findAll();

        // then
        assertThat(orderTables).hasSize(size);
    }
}
