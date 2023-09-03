package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.FakeOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    OrderRepository orderRepository;

    OrderTableRepository orderTableRepository;

    OrderTableService orderTableService;

    OrderTable 왼쪽_테이블;
    OrderTable 착석_가능한_손님_2명의_오른쪽_테이블;
    OrderTable 이름없는_테이블;

    @BeforeEach
    void setUp() {
        this.왼쪽_테이블 = 왼쪽_테이블();
        this.착석_가능한_손님_2명의_오른쪽_테이블 = 착석_가능한_손님_2명의_오른쪽_테이블();
        this.이름없는_테이블 = 이름없는_테이블();
        this.orderTableRepository = new FakeOrderTableRepository();
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    class 주문_테이블_전체기능_정상_테스트 {
        @Test
        void 주문_테이블을_생성한다() {
            OrderTable 등록된_테이블 = 테이블을_등록한다(왼쪽_테이블);

            assertThat(등록된_테이블.getId()).isNotNull();
        }

        @Test
        void 주문_테이블에_착석한다() {
            OrderTable 등록된_테이블 = 테이블을_등록한다(왼쪽_테이블);
            OrderTable 앉은_테이블 = 테이블에_착석한다(등록된_테이블.getId());

            assertThat(앉은_테이블.isOccupied()).isTrue();
        }

        @Test
        void 주문_테이블을_비운다() {
            OrderTable 등록된_테이블 = 테이블을_등록한다(왼쪽_테이블);
            OrderTable 청소한_테이블 = 테이블을_청소한다(등록된_테이블.getId());

            assertThat(청소한_테이블.isOccupied()).isFalse();
            assertThat(청소한_테이블.getNumberOfGuests()).isZero();
        }

        @Test
        void 주문_테이블에_손님을_할당한다() {
            OrderTable 등록된_테이블 = 테이블을_등록한다(착석_가능한_손님_2명의_오른쪽_테이블);
            OrderTable 착석_가능한_테이블 = 테이블에_착석한다(등록된_테이블.getId());
            OrderTable 손님_수_변경한_테이블 = 손님수를_변경한다(착석_가능한_테이블.getId(), 착석_가능한_손님_2명의_오른쪽_테이블);

            assertThat(손님_수_변경한_테이블.getNumberOfGuests()).isEqualTo(착석_가능한_손님_2명의_오른쪽_테이블.getNumberOfGuests());
        }

    }

    @Nested
    class 주문_테이블_예외_테스트 {

        @Test
        void 이름이없는_테이블은_등록할_수_없다() {
            assertThatThrownBy(() -> 테이블을_등록한다(이름없는_테이블))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_테이블은_착석할_수_없다() {
            UUID 왼쪽_테이블_아이디 = 왼쪽_테이블.getId();

            assertThatThrownBy(() -> 테이블에_착석한다(왼쪽_테이블_아이디))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 존재하지_않는_테이블은_청소할_수_없다() {
            UUID 왼쪽_테이블_아이디 = 왼쪽_테이블.getId();

            assertThatThrownBy(() -> 테이블을_청소한다(왼쪽_테이블_아이디))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 존재하지_않는_테이블은_손님수_변경_불가능하다() {
            UUID 왼쪽_테이블_아이디 = 왼쪽_테이블.getId();

            assertThatThrownBy(() -> 손님수를_변경한다(왼쪽_테이블_아이디, 왼쪽_테이블))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 착석이_불가능한_테이블의_손님수_변경_불가능하다() {
            OrderTable 등록된_테이블 = orderTableService.create(왼쪽_테이블);
            UUID 왼쪽_테이블_아이디 = 등록된_테이블.getId();

            assertThatThrownBy(() -> 손님수를_변경한다(왼쪽_테이블_아이디, 등록된_테이블))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    private OrderTable 테이블을_등록한다(OrderTable orderTable) {
        return orderTableService.create(orderTable);
    }

    private OrderTable 테이블에_착석한다(UUID id) {
        return orderTableService.sit(id);
    }

    private OrderTable 테이블을_청소한다(UUID id) {
        return orderTableService.clear(id);
    }

    private OrderTable 손님수를_변경한다(UUID id, OrderTable 변경할_테이블_정보) {
        return orderTableService.changeNumberOfGuests(id, 변경할_테이블_정보);
    }



}