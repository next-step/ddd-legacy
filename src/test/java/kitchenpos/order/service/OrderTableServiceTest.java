package kitchenpos.order.service;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.order.fixture.OrderTableFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@DisplayName("주문 테이블 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderTableService orderTableService;

    private OrderTableFixture orderTableFixture;

    @BeforeEach
    void setUp() {
        orderTableFixture = new OrderTableFixture();
    }

    @Test
    @DisplayName("새로운 테이블을 추가할 수 있다.")
    void create() {
        OrderTable 주문_테이블 = orderTableFixture.주문_테이블_A;

        mockingOrderTableRepository(OrderTableRepositoryMethod.SAVE, 주문_테이블);

        OrderTable result = orderTableService.create(주문_테이블);
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.isOccupied()).isEqualTo(false);
    }

    @Test
    @DisplayName("새로운 테이블을 추가 시 테이블의 이름은 반드시 존재해야 한다.")
    void create_exception_name() {
        OrderTable 이름_없는_주문_테이블 = orderTableFixture.이름_없는_주문_테이블;

        Assertions.assertThatThrownBy(
                () -> orderTableService.create(이름_없는_주문_테이블)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("손님은 테이블에 앉을 수 있다.")
    void sit() {
        OrderTable 주문_테이블 = orderTableFixture.주문_테이블_A;

        mockingOrderTableRepository(OrderTableRepositoryMethod.FIND, 주문_테이블);

        orderTableService.sit(주문_테이블.getId());
        Assertions.assertThat(주문_테이블.isOccupied()).isEqualTo(true);
    }

    @Test
    @DisplayName("테이블에 앉는 손님의 수는 변경될 수 있다.")
    void changeNumberOfGuests() {
        OrderTable 주문_테이블_A = orderTableFixture.주문_테이블_A;
        OrderTable 주문_테이블_B = orderTableFixture.주문_테이블_B;

        mockingOrderTableRepository(OrderTableRepositoryMethod.FIND, 주문_테이블_A);

        orderTableService.sit(주문_테이블_A.getId());
        orderTableService.changeNumberOfGuests(주문_테이블_A.getId(), 주문_테이블_B);
        Assertions.assertThat(주문_테이블_A.getNumberOfGuests()).isEqualTo(주문_테이블_B.getNumberOfGuests());
    }

    @Test
    @DisplayName("손님은 0명 이상이어야 한다.")
    void changeNumberOfGuests_exception_number() {
        OrderTable 주문_테이블_A = orderTableFixture.주문_테이블_A;
        OrderTable 손님_음수_주문_테이블 = orderTableFixture.손님_음수_주문_테이블;

        Assertions.assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(주문_테이블_A.getId(), 손님_음수_주문_테이블)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private void mockingOrderTableRepository(OrderTableRepositoryMethod method, OrderTable orderTable) {
        if (method == OrderTableRepositoryMethod.SAVE) {
            Mockito.when(orderTableRepository.save(Mockito.any()))
                    .thenReturn(orderTable);
        }
        if (method == OrderTableRepositoryMethod.FIND) {
            Mockito.when(orderTableRepository.findById(orderTable.getId()))
                    .thenReturn(Optional.of(orderTable));
        }
    }

    private enum OrderTableRepositoryMethod {
        SAVE, FIND
    }
}
