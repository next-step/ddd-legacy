package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
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
    void sit_fail_no_order_table() throws Exception {
        //given
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    private static Stream<String> 잘못된_주문테이블명() {
        return Stream.of(
                null,
                ""
        );
    }
}