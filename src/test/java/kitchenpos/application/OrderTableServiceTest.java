package kitchenpos.application;

import static kitchenpos.domain.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderTableServiceTest extends IntegrationTest {

    @Autowired
    private OrderTableService orderTableService;

    @DisplayName("새로운 주문테이블을 생성할 수 있다.")
    @Nested
    class Create {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            OrderTable request = OrderTable("1번 테이블");

            // when
            OrderTable result = orderTableService.create(request);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("1번 테이블");
            assertThat(result.getNumberOfGuests()).isEqualTo(0);
            assertThat(result.isOccupied()).isFalse();
        }

        @DisplayName("주문테이블의 이름은 null 일 수 없다.")
        @Test
        void nameNullException() {
            // given
            OrderTable request = OrderTable(null);

            // when, then
            assertThatThrownBy(() -> orderTableService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문테이블의 이름은 공백 일 수 없다.")
        @Test
        void nameBlankException() {
            // given
            OrderTable request = OrderTable("");

            // when, then
            assertThatThrownBy(() -> orderTableService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("주문테이블의 자리상태를 채워짐으로 바꿀 수 있다.")
    @Nested
    class Sit {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableWithUUID("1번 테이블"));

            // when
            OrderTable result = orderTableService.sit(orderTable.getId());

            // then
            assertThat(result.isOccupied()).isTrue();
        }

        @DisplayName("주문테이블이 우선 존재해야 한다.")
        @Test
        void orderTableNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderTableService.sit(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("주문테이블의 자리상태를 비어있음으로 바꿀 수 있다.")
    @Nested
    class Clear {

        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            orderTable = orderTableRepository.save(OrderTableWithUUID("1번 테이블"));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // when
            OrderTable result = orderTableService.clear(orderTable.getId());

            // then
            assertThat(result.isOccupied()).isFalse();
            assertThat(result.getNumberOfGuests()).isEqualTo(0);
        }

        @DisplayName("주문테이블이 우선 존재해야 한다.")
        @Test
        void orderTableNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderTableService.clear(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문테이블의 주문이 처리완료 상태가 아닐 경우 자리상태를 비어있음으로 바꿀 수 없다.")
        @Test
        void invalidOrderStatusException() {
            // given
            // TODO: 2022/09/05 주문 테스트 먼저 작성하고 완성

            // when, then
//            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
//                .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문테이블의 손님 수를 변경할 수 있다.")
    @Nested
    class ChangeNumberOfGuests {

        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            orderTable = OrderTableWithUUID("1번 테이블");
            orderTable.setOccupied(true);
            orderTable = orderTableRepository.save(orderTable);
        }

        @DisplayName("성공")
        @Test
        void success() {
            // given
            OrderTable request = OrderTable("1번 테이블");
            request.setNumberOfGuests(2);

            // when
            OrderTable result = orderTableService.changeNumberOfGuests(
                orderTable.getId(),
                request
            );

            // then
            assertThat(result.getId()).isEqualTo(orderTable.getId());
            assertThat(result.getNumberOfGuests()).isEqualTo(2);
        }

        @DisplayName("주문테이블이 우선 존재해야한다.")
        @Test
        void orderTableNotFoundException() {
            // given
            OrderTable request = OrderTable("1번 테이블");
            request.setNumberOfGuests(2);

            // when, then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                request
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("변경하려는 손님 수는 음수일 수 없다.")
        @Test
        void negativeException() {
            // given
            OrderTable request = OrderTable("1번 테이블");
            request.setNumberOfGuests(-1);

            // when, then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(
                orderTable.getId(),
                request
            )).isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문테이블의 상태가 우선 채워짐이어야 한다.")
        @Test
        void notOccupiedException() {
            // given
            orderTable.setOccupied(false);
            orderTableRepository.save(orderTable);
            OrderTable request = OrderTable("1번 테이블");
            request.setNumberOfGuests(2);

            // when, then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(
                orderTable.getId(),
                request
            )).isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("전체 주문테이블을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        OrderTable 테이블_1번 = orderTableRepository.save(OrderTableWithUUID("1번 테이블"));
        OrderTable 테이블_2번 = orderTableRepository.save(OrderTableWithUUID("2번 테이블"));

        // when
        List<OrderTable> result = orderTableService.findAll();

        // then
        assertThat(result).usingRecursiveFieldByFieldElementComparator()
            .containsExactly(테이블_1번, 테이블_2번);
    }
}
