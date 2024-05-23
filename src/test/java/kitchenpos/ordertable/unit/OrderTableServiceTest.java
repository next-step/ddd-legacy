package kitchenpos.ordertable.unit;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.ordertable.fixture.OrderTableFixture.A_테이블;
import static kitchenpos.ordertable.fixture.OrderTableFixture.비어있는_테이블;
import static kitchenpos.ordertable.fixture.OrderTableFixture.빈문자이름_테이블;
import static kitchenpos.ordertable.fixture.OrderTableFixture.이름미존재_테이블;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        this.orderTableService = new OrderTableService(
                orderTableRepository, orderRepository
        );
    }

    @Nested
    class 등록 {

        @Test
        @DisplayName("[성공] 테이블을 등록한다.")
        void create() {
            // given
            given(orderTableRepository.save(any())).willReturn(A_테이블);

            // when
            var saved = orderTableService.create(A_테이블);

            // then
            assertAll(
                    () -> then(orderTableRepository).should(times(1)).save(any()),
                    () -> assertThat(saved.getName()).isEqualTo(A_테이블.getName())
            );
        }

        @Nested
        class 이름검증 {

            @Test
            @DisplayName("[실패] 테이블의 이름을 입력하지 않으면 등록이 되지 않는다.")
            void 테이블_이름_null() {
                // when & then
                assertThatThrownBy(() -> orderTableService.create(이름미존재_테이블))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 메뉴 그룹의 이름이 빈문자열이면 등록이 되지 않는다.")
            void 테이블_이름_빈문자열() {
                // when & then
                assertThatThrownBy(() -> orderTableService.create(빈문자이름_테이블))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

    }

    @Nested
    class 점유 {

        @Test
        @DisplayName("[성공] 테이블에 앉는다.")
        void sit() {
            // given
            given(orderTableRepository.findById(any())).willReturn(Optional.of(비어있는_테이블));

            // when
            var target = orderTableService.sit(UUID.randomUUID());

            // then
            assertThat(target.isOccupied()).isTrue();
        }

        @Nested
        class 테이블등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 테이블인 경우 앉을 수 없다.")
            void 테이블_미등록() {
                // given
                given(orderTableRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

    }

    @Nested
    class 정리 {

        @Test
        @DisplayName("[성공] 테이블을 정리한다.")
        void clear() {
            // given
            given(orderTableRepository.findById(any())).willReturn(Optional.of(점유하고있는_테이블));
            given(orderRepository.existsByOrderTableAndStatusNot(점유하고있는_테이블, OrderStatus.COMPLETED)).willReturn(false);

            // when
            var target = orderTableService.clear(UUID.randomUUID());

            // then
            assertAll(
                    () -> assertThat(target.isOccupied()).isFalse(),
                    () -> assertThat(target.getNumberOfGuests()).isEqualTo(0)
            );
        }

        @Nested
        class 테이블등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 테이블인 경우 정리할 수 없다.")
            void 테이블_미등록() {
                // given
                given(orderTableRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        @Nested
        class 주문상태검증 {

            @Test
            @DisplayName("[실패] 해당 테이블의 완료되지 않은 상태의 주문이 존재하는 경우 정리할 수 없다.")
            void 미완료상태_주문_존재() {
                // given
                given(orderTableRepository.findById(any())).willReturn(Optional.of(점유하고있는_테이블));
                given(orderRepository.existsByOrderTableAndStatusNot(점유하고있는_테이블, OrderStatus.COMPLETED)).willReturn(true);

                // when & then
                assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

    }

    @Nested
    class 손님_수_변경 {

        @Test
        @DisplayName("[성공] 손님 수를 변경한다.")
        void changeNumberOfGuests() {

        }

        @Nested
        class 손님수검증 {

            @Test
            @DisplayName("[실패] 손님의 수가 0명 미만인 경우 변경할 수 없다.")
            void 음수_손님수() {

            }

        }

        @Nested
        class 테이블등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 테이블인 경우 손님 수를 변경할 수 없다.")
            void 테이블_미등록() {

            }

        }

        @Nested
        class 테이블점유상태검증 {

            @Test
            @DisplayName("[실패] 빈 테이블인 경우 손님 수를 변경할 수 없다.")
            void 비어있는_테이블() {

            }

        }

    }

}
