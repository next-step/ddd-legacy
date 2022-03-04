package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
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

}