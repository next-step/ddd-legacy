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

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderTableService orderTableService;


    private static Stream<String> provideTableNameForNullAndEmptyString() { // argument source method
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName("테이블 등록 - 테이블은 반드시 이름을 가져야 한다.")
    @MethodSource("provideTableNameForNullAndEmptyString")
    @ParameterizedTest
    void create01(String 등록할_테이블_이름) {
        OrderTable 등록할_테이블 = mock(OrderTable.class);
        given(등록할_테이블.getName()).willReturn(등록할_테이블_이름);
        assertThatThrownBy(() -> orderTableService.create(등록할_테이블))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 등록 - 테이블을 등록 할 수 있다.")
    @Test
    void create02() {
        OrderTable 등록할_테이블 = mock(OrderTable.class);
        String 등록할_테이블_이름 = "오션뷰 루프탑 테이블";
        given(등록할_테이블.getName()).willReturn(등록할_테이블_이름);

        //when
        orderTableService.create(등록할_테이블);

        //then
        verify(orderTableRepository).save(any(OrderTable.class));
    }

    //@TODO 더블부킹이 가능하다 -> 이미 착성한 테이블에 착성 할 수 없도록 해야하지 않을까?
    @DisplayName("테이블 착석 - 테이블에 착성 할 수 있다.")
    @Test
    void sit() {
        //given
        UUID 착석할_테이블_이름 = UUID.randomUUID();
        OrderTable 착석할_테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(착석할_테이블_이름)).willReturn(Optional.of(착석할_테이블));
        //when
        orderTableService.sit(착석할_테이블_이름);
        //then
        verify(착석할_테이블).setEmpty(false);
    }

    @DisplayName("테이블 정리 - 식사가 완료되지 않은 테이블은 정리 할 수 없다.")
    @Test
    void clear01() {
        //given
        UUID 정리할_테이블_이름 = UUID.randomUUID();
        OrderTable 정리할_테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(정리할_테이블_이름)).willReturn(Optional.of(정리할_테이블));
        given(orderRepository.existsByOrderTableAndStatusNot(정리할_테이블, OrderStatus.COMPLETED)).willReturn(true);
        //when & then
        assertThatThrownBy(() -> orderTableService.clear(정리할_테이블_이름))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 정리 - 테이블을 정리(clear) 할 수 있다.")
    @Test
    void clear02() {
        //given
        UUID 정리할_테이블_아이디 = UUID.randomUUID();
        OrderTable 정리할_테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(정리할_테이블_아이디)).willReturn(Optional.of(정리할_테이블));
        given(orderRepository.existsByOrderTableAndStatusNot(정리할_테이블, OrderStatus.COMPLETED)).willReturn(false);
        //when
        orderTableService.clear(정리할_테이블_아이디);
        //then
        verify(정리할_테이블).setNumberOfGuests(0);
        verify(정리할_테이블).setEmpty(true);
    }

//    - [ ] 테이블에 손님 수를 변경 할 수 있다.
//  - [ ] 비어 있지 않은 테이블에만 손님을 지정할 수 있다.
//            - [ ] 테이블에 손님은 반드시 1명 이상이여야 한다.

    //@TODO 0명이 착석할 수 있음. 의도한 동작인지 확인후 개선
    @DisplayName("테이블 인원 변경 - 테이블에 손님은 반드시 0명 이상이여야 한다.")
    @Test
    void changeNumberOfGuests01() {
        //given
        UUID 인원_변경할_테이블_아이디 = UUID.randomUUID();
        OrderTable 인원_변경_요청 = mock(OrderTable.class);
        given(인원_변경_요청.getNumberOfGuests()).willReturn(-1);

        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(인원_변경할_테이블_아이디, 인원_변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 인원 변경 - 비어 있지 않은 테이블에만 손님을 지정할 수 있다.")
    @Test
    void changeNumberOfGuests02() {
        //given
        UUID 인원_변경할_테이블_아이디 = UUID.randomUUID();
        OrderTable 인원_변경_요청 = mock(OrderTable.class);
        given(인원_변경_요청.getNumberOfGuests()).willReturn(1);
        OrderTable 인원_변경할_테이블 = mock(OrderTable.class);
        given(인원_변경할_테이블.isEmpty()).willReturn(true);
        given(orderTableRepository.findById(인원_변경할_테이블_아이디)).willReturn(Optional.of(인원_변경할_테이블));
        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(인원_변경할_테이블_아이디, 인원_변경_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 인원 변경 - 테이블에 손님 수를 변경 할 수 있다.")
    @Test
    void changeNumberOfGuests03() {
        //given
        UUID 인원_변경할_테이블_아이디 = UUID.randomUUID();
        OrderTable 인원_변경_요청 = mock(OrderTable.class);
        int 변경할_인원_수 = 4;
        given(인원_변경_요청.getNumberOfGuests()).willReturn(변경할_인원_수);
        OrderTable 인원_변경할_테이블 = mock(OrderTable.class);
        given(인원_변경할_테이블.isEmpty()).willReturn(false);
        given(orderTableRepository.findById(인원_변경할_테이블_아이디)).willReturn(Optional.of(인원_변경할_테이블));
        //when
        orderTableService.changeNumberOfGuests(인원_변경할_테이블_아이디, 인원_변경_요청);
        //then
        verify(인원_변경할_테이블).setNumberOfGuests(변경할_인원_수);
    }

}