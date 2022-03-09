package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @InjectMocks
    OrderTableService orderTableService;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    OrderRepository orderRepository;

    @DisplayName(value = "주문테이블을 등록할 수 있다")
    @Test
    void create_success() throws Exception {
        //given
        OrderTable 등록할_주문테이블 = mock(OrderTable.class);
        given(등록할_주문테이블.getName()).willReturn("1번");

        //when
        orderTableService.create(등록할_주문테이블);

        //then
        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
    }

    @DisplayName(value = "주문테이블은 반드시 한글자 이상의 이름을 가진다")
    @ParameterizedTest
    @MethodSource("잘못된_주문테이블명")
    void create_fail_invalid_name(final String 주문테이블명) {
        //given
        OrderTable 등록할_주문테이블 = mock(OrderTable.class);
        given(등록할_주문테이블.getName()).willReturn(주문테이블명);

        //when, then
        assertThatThrownBy(() -> orderTableService.create(등록할_주문테이블))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "테이블의 착석여부를 착석으로 변경할 수 있다")
    @Test
    void sit_success() throws Exception {
        //given
        OrderTable 주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(주문테이블));

        //when
        orderTableService.sit(UUID.randomUUID());

        //then
        verify(주문테이블, times(1)).setEmpty(false);
    }

    @DisplayName(value = "존재하는 테이블만 착석으로 변경할 수 있다")
    @Test
    void sit_fail_no_order_table () throws Exception {
        //given
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "테이블의 착석여부를 공석으로 변경할 수 있다")
    @Test
    void clear_success() throws Exception {
        //given
        OrderTable 주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(주문테이블));

        //when
        orderTableService.clear(UUID.randomUUID());

        //then
        verify(주문테이블, times(1)).setNumberOfGuests(0);
        verify(주문테이블, times(1)).setEmpty(true);
    }

    @DisplayName(value = "존재하는 테이블만 공석으로 변경할 수 있다")
    @Test
    void clear_fail_table_not_exists () throws Exception {
        //given
        OrderTable 주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "주문의 상태가 주문종결인 경우만 공석으로 변경할 수 있다")
    @Test
    void clear_fail_no_complete () throws Exception {
        //given
        OrderTable 주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(주문테이블));
        given(orderRepository.existsByOrderTableAndStatusNot(주문테이블, OrderStatus.COMPLETED)).willReturn(true);

        //when, then
        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문테이블의 착석인원을 변경한다")
    @Test
    void changeNumberOfGuests_success() throws Exception {
        //given
        OrderTable 착석인원_변경요청          = mock(OrderTable.class);
        int 변경_착석인원 = 3;
        given(착석인원_변경요청.getNumberOfGuests()).willReturn(변경_착석인원);

        OrderTable 변경할_주문테이블 = mock(OrderTable.class);
        given(변경할_주문테이블.isEmpty()).willReturn(false);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(변경할_주문테이블));

        //when
        orderTableService.changeNumberOfGuests(UUID.randomUUID(), 착석인원_변경요청);

        //then
        verify(변경할_주문테이블, times(1)).setNumberOfGuests(변경_착석인원);
    }

    @DisplayName(value = "착석인원은 최소 0명 이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_착석인원")
    void changeNumberOfGuests_fail_invalid_numberOfGuests(final int 변경_착석인원) throws Exception {
        //given
        OrderTable 착석인원_변경요청          = mock(OrderTable.class);
        given(착석인원_변경요청.getNumberOfGuests()).willReturn(변경_착석인원);

        //when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), 착석인원_변경요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "존재하는 테이블만 착석인원을 변경할 수 있다")
    @Test
    void changeNumberOfGuests_fail_table_not_exist() throws Exception {
        //given
        OrderTable 착석인원_변경요청          = mock(OrderTable.class);
        int 변경_착석인원 = 3;
        given(착석인원_변경요청.getNumberOfGuests()).willReturn(변경_착석인원);

        OrderTable 변경할_주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), 착석인원_변경요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "테이블이 공석일때는 착석인원을 변경할 수 없다")
    @Test
    void changeNumberOfGuest_fail_table_not_empty() throws Exception {
        //given
        OrderTable 착석인원_변경요청          = mock(OrderTable.class);
        int 변경_착석인원 = 3;
        given(착석인원_변경요청.getNumberOfGuests()).willReturn(변경_착석인원);

        OrderTable 변경할_주문테이블 = mock(OrderTable.class);
        given(변경할_주문테이블.isEmpty()).willReturn(true);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(변경할_주문테이블));

        //when,then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), 착석인원_변경요청))
                .isInstanceOf(IllegalStateException.class);
    }

    private static Stream<String> 잘못된_주문테이블명() {
        return Stream.of(
                null,
                ""
        );
    }

    private static Stream<Integer> 잘못된_착석인원() {
        return Stream.of(
                -1
        );
    }
}