package kitchenpos.application;

import config.UnitTest;
import kitchenpos.OrderFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.IdGenerator;
import kitchenpos.infra.InmemoryOrderRepository;
import kitchenpos.infra.InmemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static kitchenpos.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@DisplayName("주문 테이블 서비스 테스트")
class OrderTableServiceTest {
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService sut;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InmemoryOrderTableRepository();
        orderRepository = new InmemoryOrderRepository();
        sut = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("새로운 매장 테이블 추가")
    class CreateTests {

        @Test
        @DisplayName("성공: 올바른 이름으로 테이블 생성 시 기본 상태는 미사용, 인원수는 0이다.")
        void create_success() {
            // given
            OrderTable request = anOrderTableRequest().build();

            // when
            OrderTable created = sut.create(request);

            // then
            assertAll(
                    () -> assertThat(created).isNotNull(),
                    () -> assertThat(created.getId()).isNotNull(),
                    () -> assertThat(created.getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(created.isOccupied()).isFalse()
            );
        }

        @Test
        @DisplayName("실패: 테이블 이름이 null이면 OrderTableNameException 발생")
        void create_fail_whenNameNull() {
            // given
            OrderTable request = anOrderTableRequest()
                    .name(null)
                    .build();

            // when & then
            assertThrows(OrderTableNameException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 테이블 이름이 빈 문자열이면 OrderTableNameException 발생")
        void create_fail_whenNameEmpty() {
            // given
            OrderTable request = anOrderTableRequest()
                    .name("")
                    .build();

            // when & then
            assertThrows(OrderTableNameException.class, () -> sut.create(request));
        }
    }

    @Nested
    @DisplayName("테이블 착석")
    class SitTests {

        @Test
        @DisplayName("성공: 존재하는 테이블에 대해 착석시 사용중인 상태로 변경")
        void sit_success() {
            // given
            OrderTable request = orderTableRepository.save(anOrderTableRequest().build());
            assertThat(request.isOccupied()).isFalse();

            // when
            OrderTable updated = sut.sit(request.getId());

            // then
            assertThat(updated.isOccupied()).isTrue();
        }


        @Test
        @DisplayName("실패: 존재하지 않는 테이블에 대해 착석 요청 시 OrderTableNotFoundException 발생")
        void sit_fail_tableNotFound() {
            // given
            // when & then
            assertThrows(OrderTableNotFoundException.class, () -> sut.sit(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("매장 테이블 청소")
    class ClearTests {

        @Test
        @DisplayName("성공: 모든 주문이 완료된 테이블은 청소 시 미사용 상태로, 인원 수 0으로 초기화됨")
        void clear_success() {
            // given
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest().occupied(true)
                            .numberOfGuests(3)
                            .build());
            assertThat(orderTable.isOccupied()).isTrue();
            assertThat(orderTable.getNumberOfGuests()).isGreaterThan(0);

            // when
            OrderTable clearedOrderTable = sut.clear(orderTable.getId());

            // then
            assertThat(clearedOrderTable.isOccupied()).isFalse();
            assertThat(clearedOrderTable.getNumberOfGuests()).isEqualTo(0);
        }

        @ParameterizedTest
        @DisplayName("실패: 완료되지 않은 주문이 존재하는 테이블은 청소할 수 없어 IllegalStateException 발생")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotCompleted")
        void clear_fail_whenOrderNotCompleted(OrderStatus status) {
            // given
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest().occupied(true).build());
            Order order = orderRepository.save(OrderFixture.anOrderRequest()
                    .orderTable(orderTable)
                    .status(status)
                    .build());

            // when & then
            assertThrows(OrderNotCompletedException.class, () -> sut.clear(orderTable.getId()));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 테이블에 대해 청소 요청 시 NoSuchElementException 발생")
        void clear_fail_tableNotFound() {
            // given

            // when & then
            assertThrows(OrderTableNotFoundException.class, () -> sut.clear(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("매장 테이블 인원 수 변경")
    class ChangeNumberOfGuestsTests {

        @ParameterizedTest
        @DisplayName("성공: 사용중인 테이블의 인원 수를 유효한 값(1명 이상)으로 변경")
        @ValueSource(ints = {1, 2, 3, 4})
        void changeNumberOfGuests_success(int numberOfGuests) {
            // given
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest().occupied(true).numberOfGuests(3).build());
            OrderTable request = anOrderTableRequest().numberOfGuests(numberOfGuests).build();

            // when
            OrderTable updated = sut.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThat(updated.getNumberOfGuests()).isEqualTo(numberOfGuests);
        }

        @ParameterizedTest
        @DisplayName("실패: 변경 인원 수가 1명 미만이면 IllegalArgumentException 발생")
        @ValueSource(ints = {-1, -100, -1000})
        void changeNumberOfGuests_fail_invalidNumber(int numberOfGuests) {
            // given
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest().occupied(true).numberOfGuests(3).build());
            OrderTable request = anOrderTableRequest().numberOfGuests(numberOfGuests).build();

            // when & then
            assertThrows(OrderTableGuestNegativeException.class, () -> sut.changeNumberOfGuests(orderTable.getId(), request));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 테이블에 대해 인원 수 변경 요청 시 NoSuchElementException 발생")
        void changeNumberOfGuests_fail_tableNotFound() {
            // given
            OrderTable request = anOrderTableRequest().numberOfGuests(3).build();

            // when & then
            assertThrows(OrderTableNotFoundException.class, () -> sut.changeNumberOfGuests(UUID.randomUUID(), request));
        }

        @Test
        @DisplayName("실패: 사용중이지 않은(미착석) 테이블의 인원 수 변경 시 IllegalStateException 발생")
        void changeNumberOfGuests_fail_notOccupied() {
            // given
            OrderTable savedOrderTable = orderTableRepository.save(anOrderTableRequest().occupied(false).build());
            assertThat(savedOrderTable.isOccupied()).isFalse();
            OrderTable request = anOrderTableRequest().numberOfGuests(3).build();

            // when & then
            assertThrows(OrderTableNotOccupiedException.class, () -> sut.changeNumberOfGuests(savedOrderTable.getId(), request));
        }
    }

    @Nested
    @DisplayName("모든 매장 테이블 조회")
    class FindAllTests {

        @Test
        @DisplayName("성공: 등록된 모든 매장 테이블 목록을 조회")
        void findAll_success() {
            // given
            List<OrderTable> orderTables = List.of(
                    anOrderTableRequest().name("테이블 1").build(),
                    anOrderTableRequest().name("테이블 2").build()
            );
            orderTables.forEach(orderTableRepository::save);
            // when
            var allTables = sut.findAll();

            // then
            assertAll(
                    () -> assertThat(allTables).isNotNull(),
                    () -> assertThat(allTables).hasSize(2)
            );
        }
    }
}
