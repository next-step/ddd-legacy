package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("주문테이블")
public class OrderTableServiceTest extends ApplicationTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    private static final String DEFAULT_ORDERTABLE_NAME = "주문테이블";

    @DisplayName("만들기")
    @Nested
    class Create {
        @DisplayName("[성공] 주문 테이블을 만든다.")
        @Test
        void createTest1() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, false, 0);
            when(orderTableRepository.save(any())).thenReturn(orderTable);
            //when
            OrderTable created = orderTableService.create(orderTable);
            //then
            assertAll(
                    () -> assertThat(created.isOccupied()).isFalse()
                    , () -> assertThat(created.getNumberOfGuests()).isZero()
            );
        }

        @DisplayName("[예외] 주문 테이블의 이름은 공백일 수 없다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createTest2(String name) {
            //when
            OrderTable orderTable = OrderTableFixture.create(name, false, 0);
            //then
            assertThatThrownBy(() -> orderTableService.create(orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("앉기")
    @Nested
    class Sit {
        @DisplayName("[성공] 주문 테이블에 앉는다.")
        @Test
        void sitTest1() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, false, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            //when
            OrderTable sit = orderTableService.sit(orderTable.getId());
            //then
            assertThat(sit.isOccupied()).isTrue();
        }

        @DisplayName("[예외] 등록되지 않은 주문테이블엔 앉을 수 없다.")
        @Test
        void sitTest2() {
            //then
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, false, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
            //when
            assertThatThrownBy(() -> orderTableService.sit(orderTable.getId()))
                    .isInstanceOf(NoSuchElementException.class);

        }
    }

    @DisplayName("치우기")
    @Nested
    class Clear {
        @DisplayName("[성공] 주문 테이블을 치운다.")
        @Test
        void clearTest1() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, true, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            // 주문 상태 =  주문완료
            when(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED)))
                    .thenReturn(false);
            //when
            OrderTable clear = orderTableService.clear(orderTable.getId());
            //then
            assertAll(
                    () -> assertThat(clear.isOccupied()).isFalse()
                    , () -> assertThat(clear.getNumberOfGuests()).isZero()
            );
        }

        @DisplayName("[예외] 등록되지 않은 주문테이블은 치울 수 없다.")
        @Test
        void clearTest2() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, true, 1);
            //when
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 주문이 완료되지 않은 주문테이블은 치울 수 없다.")
        @Test
        void clearTest3() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, true, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            //when
            // 주문 상태 =  주문 미완료
            when(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED)))
                    .thenReturn(true);
            //then
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("인원수 바꾸기")
    @Nested
    class ChangeNumberOfGuests {
        @DisplayName("[성공] 주문 테이블의 인원수를 바꾼다.")
        @Test
        void changeTest1() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, true, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            //when
            orderTable.setNumberOfGuests(5);
            OrderTable changeNumberOfGuests
                    = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);
            //then
            assertThat(changeNumberOfGuests.getNumberOfGuests())
                    .isEqualTo(5);
        }

        @DisplayName("[예외] 주문 테이블의 인원 수는 0명 이상이다.")
        @ParameterizedTest
        @ValueSource(ints = {-1, -100})
        void changeTest2(int numberOfGuests) {
            //when
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, true, 1);
            //when
            orderTable.setNumberOfGuests(numberOfGuests);
            //then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 등록하지 않은 주문테이블의 인원수는 바꿀 수 없다.")
        @Test
        void changeTest3() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, true, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
            //when
            orderTable.setNumberOfGuests(5);
            //then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 사용중이 아닌 주문테이블의 인원수는 바꿀 수 없다.")
        @Test
        void changeTest4() {
            //given
            OrderTable orderTable = OrderTableFixture.create(DEFAULT_ORDERTABLE_NAME, false, 1);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            //when
            assertThat(orderTable.isOccupied()).isFalse();
            //then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
    
}
