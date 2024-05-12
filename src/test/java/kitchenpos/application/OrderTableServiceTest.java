package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.NAME_2번;
import static kitchenpos.fixture.OrderTableFixture.changeNumberOfGuestsRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableResponse;
import static kitchenpos.fixture.OrderTableFixture.이름_1번;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("주문테이블 서비스 테스트")
@ApplicationMockTest
class OrderTableServiceTest {
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderTableService orderTableService;


    @DisplayName("주문 테이블 등록")
    @Nested
    class OrderTableCreate {
        @DisplayName("[성공]")
        @Nested
        class Success {
            @DisplayName("주문테이블을 등록한다.")
            @Test
            void createOrderTable() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 0, false);
                when(orderTableRepository.save(any())).thenReturn(ORDER_TABLE_1번);
                OrderTable request = orderTableCreateRequest(이름_1번);

                // when
                OrderTable result = orderTableService.create(request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                        () -> assertThat(result.getName()).isEqualTo(이름_1번),
                        () -> assertThat(result.getNumberOfGuests()).isZero(),
                        () -> assertThat(result.isOccupied()).isFalse()
                );
            }
        }

        @DisplayName("[실패]")
        @Nested
        class Fail {
            @DisplayName("이름은 필수로 입력해야 한다.")
            @NullAndEmptySource
            @ParameterizedTest
            void createOrderTable_NullOrEmptyNameException(String name) {
                // given
                OrderTable request = orderTableCreateRequest(name);

                // when
                // then
                assertThatThrownBy(() -> orderTableService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @DisplayName("주문 테이블 사용여부")
    @Nested
    class OrderTableOccupied {
        @DisplayName("[성공]")
        @Nested
        class Success {

            @DisplayName("고객이 주문 테이블에 앉는다.")
            @Test
            void sitOrderTable() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 0, false);
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));

                // when
                OrderTable result = orderTableService.sit(ORDER_TABLE_1번.getId());

                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                        () -> assertThat(result.getName()).isEqualTo(이름_1번),
                        () -> assertThat(result.getNumberOfGuests()).isZero(),
                        () -> assertThat(result.isOccupied()).isTrue()
                );
            }

            @DisplayName("해당 주문 테이블이 초기화 된다.")
            @Test
            void clearOrderTable() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, true);
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
                when(orderRepository.existsByOrderTableAndStatusNot(ORDER_TABLE_1번, OrderStatus.COMPLETED)).thenReturn(false);

                // when
                OrderTable result = orderTableService.clear(ORDER_TABLE_1번.getId());

                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                        () -> assertThat(result.getName()).isEqualTo(이름_1번),
                        () -> assertThat(result.getNumberOfGuests()).isZero(),
                        () -> assertThat(result.isOccupied()).isFalse()
                );
            }
        }

        @DisplayName("[실패]")
        @Nested
        class Fail {
            @DisplayName("주문 테이블이 미리 등록되어 있는지 체크한다.")
            @Test
            void exists() {
                // given
                UUID orderTableId = UUID.randomUUID();
                when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

                // when
                // then
                assertAll(
                        () -> assertThatThrownBy(() -> orderTableService.sit(orderTableId))
                                .isInstanceOf(NoSuchElementException.class),
                        () -> assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                                .isInstanceOf(NoSuchElementException.class)
                );
            }

            @DisplayName("`주문`의 주문상태가 **주문종료**이어야 한다.")
            @Test
            void orderStatus() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, true);
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
                when(orderRepository.existsByOrderTableAndStatusNot(ORDER_TABLE_1번, OrderStatus.COMPLETED)).thenReturn(true);
                UUID orderTableId = ORDER_TABLE_1번.getId();

                // when
                // then
                assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @DisplayName("주문테이블 인원 변경")
    @Nested
    class OrderTableNumberChange {
        @DisplayName("[성공]")
        @Nested
        class Success {
            @DisplayName("해당 주문 테이블 고객의 수를 변경한다.")
            @Test
            void changeNumberOfGuestsOrderTable() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, true);
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
                OrderTable request = changeNumberOfGuestsRequest(4);

                // when
                OrderTable result = orderTableService.changeNumberOfGuests(ORDER_TABLE_1번.getId(), request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                        () -> assertThat(result.getName()).isEqualTo(이름_1번),
                        () -> assertThat(result.getNumberOfGuests()).isEqualTo(4),
                        () -> assertThat(result.isOccupied()).isTrue()
                );
            }
        }

        @DisplayName("[실패]")
        @Nested
        class Fail {
            @DisplayName("주문 테이블이 미리 등록되어 있는지 체크한다.")
            @Test
            void exists() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, true);
                UUID orderTableId = ORDER_TABLE_1번.getId();
                when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
                OrderTable request = changeNumberOfGuestsRequest(4);

                // when
                // then
                assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문 테이블은 사용여부가 사용중이어야 한다.")
            @Test
            void occupied() {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, false);
                UUID orderTableId = ORDER_TABLE_1번.getId();
                when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
                OrderTable request = changeNumberOfGuestsRequest(4);

                // when
                // then
                assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                        .isInstanceOf(IllegalStateException.class);
            }

            @DisplayName("고객의 수는 `0`명 이상이다.")
            @ValueSource(ints = {-1, -111, -99999})
            @ParameterizedTest
            void changeNumberOfGuestsOrderTable_negativeException(int numberOfGuests) {
                // given
                OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, true);
                UUID orderTableId = ORDER_TABLE_1번.getId();
                OrderTable request = changeNumberOfGuestsRequest(numberOfGuests);

                // when
                // then
                assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @DisplayName("해당 주문테이블목록을 볼 수 있다.")
    @Test
    void getOrderTables() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(이름_1번, 2, true);
        OrderTable ORDER_TABLE_2번 = orderTableResponse(NAME_2번, 0, false);
        when(orderTableRepository.findAll()).thenReturn(List.of(ORDER_TABLE_1번, ORDER_TABLE_2번));

        // when
        List<OrderTable> result = orderTableService.findAll();

        // then
        assertThat(result).containsExactly(ORDER_TABLE_1번, ORDER_TABLE_2번);
    }
}
