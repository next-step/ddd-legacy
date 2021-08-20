package kitchenpos.order_table.application;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.order_table.fixture.OrderTableFixture.주문_테이블;
import static kitchenpos.order_table.fixture.OrderTableFixture.주문_테이블_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("OrderTable 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    private UUID id;
    private String 테이블_1번 = "1번";
    private String 테이블_2번 = "2번";

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
        id = UUID.randomUUID();
    }

    @DisplayName("테이블 생성 시, 테이블 이름이 없는 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithoutName(final String name) {
        // given
        OrderTable orderTable = 주문_테이블_요청(name);

        // when, then
        assertThatThrownBy(() -> orderTableService.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블을 생성한다")
    @Test
    public void createWithNoCustomerAndEmptyTable() {
        // given
        OrderTable request = 주문_테이블_요청(테이블_1번);

        OrderTable orderTable = 주문_테이블(id, 테이블_1번, true, 0);
        given(orderTableRepository.save(any ())).willReturn(orderTable);

        // when
        OrderTable savedOrderTable = orderTableService.create(request);

        // then
        assertAll(
                () -> assertThat(savedOrderTable.isEmpty()).isTrue(),
                () -> assertThat(savedOrderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(savedOrderTable.getId()).isInstanceOf(UUID.class)
        );
    }

    @DisplayName("고객이 테이블에 앉으면 테이블을 채운다")
    @Test
    public void sitNonEmpty() {
        // given
        OrderTable orderTable = 주문_테이블(id, 테이블_1번, true, 0);
        given(orderTableRepository.findById(id)).willReturn(Optional.of(orderTable));

        // when
        OrderTable filledOrderTable = orderTableService.sit(id);

        // then
        assertThat(filledOrderTable.isEmpty()).isFalse();
    }

    @DisplayName("주문이 완료되지 않은 테이블을 치울 경우, IllegalStateException를 던진다")
    @Test
    public void clearCanNotCleanIfOrderComplete() {
        // given
        OrderTable orderTable = 주문_테이블(id, 테이블_1번, true, 0);
        given(orderTableRepository.findById(id)).willReturn(Optional.of(orderTable));

        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(true);

        // when, then
        assertThatThrownBy(() -> orderTableService.clear(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블을 치우면 테이블의 인원은 0명이고 비어 있도록 한다")
    @Test
    public void clearNoCustomerAndEmpty() {
        // given
        OrderTable orderTable = 주문_테이블(id, 테이블_1번, true, 5);
        given(orderTableRepository.findById(id)).willReturn(Optional.of(orderTable));

        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(false);

        // when
        OrderTable clearedOrderTable = orderTableService.clear(id);

        // then
        assertThat(clearedOrderTable.getNumberOfGuests()).isZero();
        assertThat(clearedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("테이블의 변경하려는 인원이 0명 미만인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10, -100})
    public void changeNumberOfGuestsWithNegativeNumberOfCustomer(int numberOfGuests) {
        // given
        OrderTable orderTable = 주문_테이블_요청(numberOfGuests);

        // when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비어있는 테이블의 인원을 변경시 IllegalStateException을 던진다.")
    @Test
    public void changeNumberOfGuestsWithEmptyTable() {
        // given
        OrderTable orderTable = 주문_테이블(id, 테이블_1번, true, 0);
        given(orderTableRepository.findById(id)).willReturn(Optional.of(orderTable));

        OrderTable request = 주문_테이블_요청(4);

        // when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블의 인원을 변경한다")
    @Test
    public void changeNumberOfGuests() {
        // given
        OrderTable orderTable = 주문_테이블(id, 테이블_1번, false, 3);
        given(orderTableRepository.findById(id)).willReturn(Optional.of(orderTable));

        OrderTable request = 주문_테이블_요청(4);

        // when
        OrderTable 변경된_주문_테이블 = orderTableService.changeNumberOfGuests(id, request);

        // then
        assertAll(
                () -> assertThat(변경된_주문_테이블.getNumberOfGuests()).isEqualTo(4)
        );
    }

    @DisplayName("모든 가게 테이블을 조회한다")
    @Test
    public void findAll() {
        // given
        OrderTable 주문_테이블_1번 = 주문_테이블(UUID.randomUUID(), 테이블_1번, true, 0);
        OrderTable 주문_테이블_2번 = 주문_테이블(UUID.randomUUID(), 테이블_2번, true, 0);
        given(orderTableRepository.findAll()).willReturn(Arrays.asList(
                주문_테이블_1번, 주문_테이블_2번));

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertThat(orderTables.size()).isEqualTo(2);
    }
}
