package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import kitchenpos.config.InMemoryOrderRepository;
import kitchenpos.config.InMemoryOrderTableRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTableService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    private OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    void 테이블을_생성한다() {
        OrderTable request = OrderTableFixture.createRequest("1번테이블");

        OrderTable actual = orderTableService.create(request);

        assertAll(() -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse());
    }

    @Test
    void 테이블이름이_비어있으면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("");

        assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.create(createRequest));
    }

    @Test
    void 먹고가는_손님이_오면_테이블점유를_체크한다() {

    }

    @Test
    void 주문이_완료된_테이블을_치울_수_있다() {

    }

    @Test
    void 주문이_완료되지_않은_테이블을_치우면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);

        OrderTable actual = orderTableService.sit(saved.getId());

        assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    void 점유되어있는_테이블의_손님수를_변경할_수_있다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        orderTableService.sit(saved.getId());
        OrderTable changeRequest = OrderTableFixture.changeNumberOfGuestsRequest(4);

        OrderTable actual = orderTableService.changeNumberOfGuests(saved.getId(), changeRequest);

        assertThat(actual.getNumberOfGuests()).isEqualTo(4);
    }

    @Test
    void 점유되어있지_않은_테이블의_손님수를_변경하면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        OrderTable changeRequest = OrderTableFixture.changeNumberOfGuestsRequest(4);

        assertThatIllegalStateException().isThrownBy(
                () -> orderTableService.changeNumberOfGuests(saved.getId(), changeRequest));
    }

    @Test
    void 테이블의_손님수를_마이너스로_변경하면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        orderTableService.sit(saved.getId());
        OrderTable changeRequest = OrderTableFixture.changeNumberOfGuestsRequest(-10);

        assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.changeNumberOfGuests(saved.getId(), changeRequest));
    }
}