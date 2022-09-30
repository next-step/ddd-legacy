package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import kitchenpos.domain.FakeOrderRepository;
import kitchenpos.domain.FakeOrderTableRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();

    private final OrderRepository orderRepository = new FakeOrderRepository();

    // SUT

    private final OrderTableService orderTableService = new OrderTableService(
            orderTableRepository,
            orderRepository
    );

    @DisplayName("생성")
    @Nested
    class Sazeorfa {

        @DisplayName("유효한 이름으로 주문 테이블을 생성할 수 있다.")
        @ValueSource(strings = {
                "time", "large", "bottom", "tidy", "left",
                "poem", "tremble", "poverty", "great", "urge",
        })
        @ParameterizedTest
        void mmgcjzyv(final String name) {
            // given
            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setName(name);

            // when
            final OrderTable orderTable = orderTableService.create(requestOrderTable);

            // then
            assertThat(orderTable.getName()).isEqualTo(name);
        }

        @DisplayName("이름은 null이거나 빈 문자열일 수 없다.")
        @NullAndEmptySource
        @ParameterizedTest
        void oegekftx(final String name) {
            // given
            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setName(name);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                    orderTableService.create(requestOrderTable));
        }

        @DisplayName("주문 테이블이 생성되었을 때 손님 수는 0명이어야 한다.")
        @Test
        void qzicoyjx() {
            // given
            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setName("failure");

            // when
            final OrderTable orderTable = orderTableService.create(requestOrderTable);

            // then
            assertThat(orderTable.getNumberOfGuests()).isZero();
        }

        @DisplayName("주문 테이블이 생성되었을 때 점유되지 않은 상태여야 한다.")
        @Test
        void wwmxxxhf() {
            // given
            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setName("fasten");

            // when
            final OrderTable orderTable = orderTableService.create(requestOrderTable);

            // then
            assertThat(orderTable.isOccupied()).isFalse();
        }
    }

    @DisplayName("점유 상태 변경(sit, clear)")
    @Nested
    class Tcgahrho {

        @DisplayName("점유 상태가 아닌 주문 테이블을 점유 상태로 변경할 수 있다.")
        @Test
        void ihouvpbq() {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("light");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(false);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            // when
            final OrderTable sitOrderTable = orderTableService.sit(savedOrderTable.getId());

            // then
            assertThat(sitOrderTable.isOccupied()).isTrue();

            final OrderTable foundOrderTable = orderTableRepository.findById(
                            savedOrderTable.getId()
                    )
                    .orElse(null);
            assertThat(foundOrderTable).isNotNull();
            assertThat(foundOrderTable.isOccupied()).isTrue();
        }

        @DisplayName("이미 점유 상태인 테이블을 다시 점유 상태로 변경할 수 있다.")
        @Test
        void vxmedctb() {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("slow");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(true);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            // when
            final OrderTable sitOrderTable = orderTableService.sit(savedOrderTable.getId());

            // then
            assertThat(sitOrderTable.isOccupied()).isTrue();

            final OrderTable foundOrderTable = orderTableRepository.findById(
                            savedOrderTable.getId()
                    )
                    .orElse(null);
            assertThat(foundOrderTable).isNotNull();
            assertThat(foundOrderTable.isOccupied()).isTrue();
        }

        @DisplayName("점유 상태인 주문 테이블을 점유중이지 않은 상태로 변경할 수 있다.")
        @Test
        void ysezkswd() {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("department");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(true);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            // when
            final OrderTable clearedOrderTable = orderTableService.clear(savedOrderTable.getId());

            // then
            assertThat(clearedOrderTable.isOccupied()).isFalse();

            final OrderTable foundOrderTable = orderTableRepository.findById(
                            savedOrderTable.getId()
                    )
                    .orElse(null);
            assertThat(foundOrderTable).isNotNull();
            assertThat(foundOrderTable.isOccupied()).isFalse();
        }

        @DisplayName("이미 점유중이지 않은 상태인 테이블을 다시 점유중이지 않은 상태로 변경할 수 있다.")
        @Test
        void yacmeoxn() {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("taxi");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(false);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            // when
            final OrderTable clearedOrderTable = orderTableService.clear(savedOrderTable.getId());

            // then
            assertThat(clearedOrderTable.isOccupied()).isFalse();

            final OrderTable foundOrderTable = orderTableRepository.findById(
                            savedOrderTable.getId()
                    )
                    .orElse(null);
            assertThat(foundOrderTable).isNotNull();
            assertThat(foundOrderTable.isOccupied()).isFalse();
        }

        @DisplayName("주문 테이블에 주문이 있는 경우 완료된 경우에만 주문 테이블을 점유중이지 않은 상태로 변경할 수 있다.")
        @EnumSource(OrderStatus.class)
        @ParameterizedTest
        void haibddrx(final OrderStatus orderStatus) {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("practical");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(false);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            final Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setOrderTable(savedOrderTable);
            order.setOrderTableId(savedOrderTable.getId());
            order.setStatus(orderStatus);
            orderRepository.save(order);

            // when / then
            if (orderStatus == OrderStatus.COMPLETED) {
                final OrderTable clearedOrderTable = orderTableService.clear(
                        savedOrderTable.getId());

                assertThat(clearedOrderTable.isOccupied()).isFalse();

                final OrderTable foundOrderTable = orderTableRepository.findById(
                                savedOrderTable.getId()
                        )
                        .orElse(null);
                assertThat(foundOrderTable).isNotNull();
                assertThat(foundOrderTable.isOccupied()).isFalse();
            } else {
                assertThatIllegalStateException().isThrownBy(()
                        -> orderTableService.clear(savedOrderTable.getId()));
            }
        }

        @DisplayName("주문 테이블이 점유중이지 않은 상태로 변경되면 그 주문 테이블의 손님 수는 0명으로 변경되어야 한다.")
        @Test
        void ynraeyop() {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("wheel");
            orderTable.setNumberOfGuests(16);
            orderTable.setOccupied(true);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            // when
            final OrderTable clearedOrderTable = orderTableService.clear(savedOrderTable.getId());

            // then
            assertThat(clearedOrderTable.getNumberOfGuests()).isZero();

            final OrderTable foundOrderTable = orderTableRepository.findById(
                            savedOrderTable.getId()
                    )
                    .orElse(null);
            assertThat(foundOrderTable).isNotNull();
            assertThat(foundOrderTable.getNumberOfGuests()).isZero();
        }
    }

    @DisplayName("손님 수 변경")
    @Nested
    class Pfbzzywc {

        @DisplayName("주문 테이블이 점유중이면 손님 수를 변경할 수 있다.")
        @ValueSource(ints = {
                21, 31, 25, 1, 2,
                32, 3, 22, 27, 4,
        })
        @ParameterizedTest
        void nnmzyebr(final int numberOfGuest) {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("nursery");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(true);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setNumberOfGuests(numberOfGuest);

            // when
            final OrderTable updatedOrderTable = orderTableService.changeNumberOfGuests(
                    savedOrderTable.getId(),
                    requestOrderTable
            );

            // then
            assertThat(updatedOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuest);

            final OrderTable foundOrderTable = orderTableRepository.findById(
                            savedOrderTable.getId()
                    )
                    .orElse(null);
            assertThat(foundOrderTable).isNotNull();
            assertThat(foundOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuest);
        }

        @DisplayName("주문 테이블이 점유중이지 않으면 손님 수를 변경할 수 없다.")
        @Test
        void lvpdwjzi() {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("middle");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(false);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setNumberOfGuests(11);

            // when / then
            assertThatIllegalStateException().isThrownBy(() ->
                    orderTableService.changeNumberOfGuests(
                            savedOrderTable.getId(),
                            requestOrderTable
                    )
            );
        }

        @DisplayName("손님 수를 음수로 변경할 수 없다.")
        @ValueSource(ints = {
                -32, -19, -22, -1, -4,
                -29, -10, -28, -5, -9,
        })
        @ParameterizedTest
        void dmheokln(final int numberOfGuest) {
            // given
            final OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("redden");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(true);
            final OrderTable savedOrderTable = orderTableRepository.save(orderTable);

            final OrderTable requestOrderTable = new OrderTable();
            requestOrderTable.setNumberOfGuests(numberOfGuest);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                    orderTableService.changeNumberOfGuests(
                            savedOrderTable.getId(),
                            requestOrderTable
                    )
            );
        }
    }

    @DisplayName("목록 조회")
    @Nested
    class Uqrxxtrc {

        @DisplayName("주문 테이블을 생성한 후 모두 조회할 수 있다.")
        @ValueSource(ints = {
                22, 25, 3, 28, 32,
                21, 31, 4, 15, 28,
        })
        @ParameterizedTest
        void lcuceevi(final int size) {
            // given
            IntStream.range(0, size)
                    .forEach(n -> {
                        final OrderTable orderTable = new OrderTable();
                        orderTable.setName(String.valueOf(n));
                        orderTableService.create(orderTable);
                    });

            // when
            final List<OrderTable> orderTables = orderTableService.findAll();

            // then
            assertThat(orderTables).hasSize(size);
        }

        @DisplayName("주문 테이블이 없는 상태에서 모두 조회시 빈 list가 반환되어야 한다.")
        @Test
        void orrxrgex() {
            // when
            final List<OrderTable> orderTables = orderTableService.findAll();

            // then
            assertThat(orderTables).isEmpty();
        }
    }
}
