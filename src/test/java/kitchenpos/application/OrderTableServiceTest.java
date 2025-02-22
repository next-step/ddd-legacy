package kitchenpos.application;

import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableChange;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableClear;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableCreate;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableSit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
class OrderTableServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void tearDown() {
        orderTableRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
    }

    @Test
    void 주문_테이블을_등록하면_손님_수는_0명이고_사용_가능_상태여야_한다() {
        // given
        OrderTable expected = orderTableCreate("1번 테이블");

        // when
        OrderTable actual = orderTableService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void 주문_테이블의_이름이_없으면_등록할_수_없다(final String invalidName) {
        // given
        OrderTable expected = new OrderTable();
        expected.setName(invalidName);

        // when & then
        assertThatThrownBy(() -> orderTableService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 테이블 이름이 존재해야 합니다.");
    }

    @Test
    void 주문_테이블에_손님이_앉으면_사용중_상태로_변경된다() {
        // given
        OrderTable orderTable = orderTableSit(UUID.randomUUID(), "1번 테이블", false, 0);
        OrderTable expected = orderTableRepository.save(orderTable);

        // when
        OrderTable actual = orderTableService.sit(expected.getId());

        // then
        assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    void 존재하지_않는_주문_테이블에_손님이_앉으려_하면_예외가_발생한다() {
        // given
        UUID nonExistentTableId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> orderTableService.sit(nonExistentTableId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("주문 테이블이 존재해야 한다.");
    }

    @Test
    void 주문이_완료된_경우_주문_테이블을_정리할_수_있다() {
        // given
        OrderTable orderTable = orderTableClear(UUID.randomUUID(), "1번 테이블", true, 4);
        OrderTable expected = orderTableRepository.save(orderTable);

        // when
        OrderTable actual = orderTableService.clear(expected.getId());

        // then
        assertAll(
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @Test
    void 주문_테이블의_손님_수를_변경할_수_있다() {
        // given
        OrderTable orderTable = orderTableChange(UUID.randomUUID(), "1번 테이블", true, 4);
        orderTableRepository.save(orderTable);

        // when
        orderTable.setNumberOfGuests(5);
        OrderTable updatedTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        // then
        assertThat(updatedTable.getNumberOfGuests()).isEqualTo(5);
    }

    @Test
    void 손님_수가_0_미만이면_변경할_수_없다() {
        // given
        OrderTable orderTable = orderTableChange(UUID.randomUUID(), "1번 테이블", true, 4);
        orderTableRepository.save(orderTable);

        // when & then
        orderTable.setNumberOfGuests(-1);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("손님 수가 0명 이상이어야 합니다.");
    }

    @Test
    void 주문_테이블이_사용중이_아니면_손님_수를_변경할_수_없다() {
        // given
        OrderTable orderTable = orderTableChange(UUID.randomUUID(), "1번 테이블", false, 4);
        orderTableRepository.save(orderTable);

        // when & then
        orderTable.setNumberOfGuests(4);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 테이블이 사용중이어야 합니다.");
    }

    @Test
    void 등록된_주문_테이블을_모두_조회할_수_있다() {
        // given
        OrderTable orderTable1 = orderTableCreate(UUID.randomUUID(),"1번 테이블");
        orderTableRepository.save(orderTable1);
        OrderTable orderTable2 = orderTableCreate(UUID.randomUUID(),"2번 테이블");
        orderTableRepository.save(orderTable2);

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertThat(orderTables).hasSize(2);
        assertAll(
            () -> assertThat(orderTable1).isNotNull(),
            () -> assertThat(orderTable1.getName()).isEqualTo("1번 테이블")
        );
        assertAll(
            () -> assertThat(orderTable2).isNotNull(),
            () -> assertThat(orderTable2.getName()).isEqualTo("2번 테이블")
        );
    }
}
