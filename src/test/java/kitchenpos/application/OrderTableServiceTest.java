package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.OrderFixture.order;
import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;
    @Autowired
    private OrderRepository orderRepository;

    @Nested
    class 주문_테이블_생성_경우 {
        @Test
        void 유효한_이름을_입력하면_주문_테이블이_정상적으로_생성된다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);

            // when
            OrderTable response = orderTableService.create(request);

            // then
            assertThat(response.getName()).isEqualTo(DEFAULT_ORDER_TABLE_NAME);
            assertThat(response.getNumberOfGuests()).isEqualTo(DEFAULT_NUMBER_OF_GUESTS);
            assertThat(response.isOccupied()).isFalse();
        }

        @Test
        void 이름이_NULL이면_예외가_발생한다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(null);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.create(request));
        }

        @Test
        void 이름이_빈_문자열이면_예외가_발생한다() {
            // given
            OrderTable request = new OrderTable();
            request.setName("");

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.create(request));
        }
    }

    @Nested
    class 주문_테이블_착석_경우 {
        @Test
        void 존재하는_테이블에_착석하면_테이블_상태가_사용중으로_변경된다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);
            OrderTable created = orderTableService.create(request);

            // when
            OrderTable response = orderTableService.sit(created.getId());

            // then
            assertThat(response.isOccupied()).isTrue();
        }

        @Test
        void 존재하지_않는_테이블에_착석_처리를_하면_예외가_발생한다() {
            // given
            UUID nonExistingId = createOrderTableId();

            // when & then
            assertThatThrownBy(() -> orderTableService.sit(nonExistingId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class 주문_테이블_청소_경우 {
        @Test
        void 완료된_주문만_있는_테이블을_청소하면_손님수_0으로_초기화되고_상태가_사용안함으로_변경된다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);
            OrderTable created = orderTableService.create(request);
            orderTableService.sit(created.getId());

            // when
            OrderTable response = orderTableService.clear(created.getId());

            // then
            assertThat(response.getNumberOfGuests()).isEqualTo(DEFAULT_NUMBER_OF_GUESTS);
            assertThat(response.isOccupied()).isFalse();
        }

        @Test
        void 존재하지_않는_테이블을_청소하면_예외가_발생한다() {
            // given
            UUID nonExistingId = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> orderTableService.sit(nonExistingId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 미완료_주문이_있는_테이블을_청소하면_예외가_발생한다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);

            OrderTable created = orderTableService.create(request);
            orderTableService.sit(created.getId());

            orderRepository.save(order(OrderType.EAT_IN, OrderStatus.SERVED, created));

            // when & then
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderTableService.clear(created.getId()));
        }
    }

    @Nested
    class 손님_수_변경_경우 {
        @Test
        void 사용_중_상태인_테이블의_손님_수를_정상적으로_변경할_수_있다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);
            OrderTable created = orderTableService.create(request);
            orderTableService.sit(created.getId());

            // when
            OrderTable updateRequest = new OrderTable();
            updateRequest.setNumberOfGuests(4);
            OrderTable excepted = orderTableService.changeNumberOfGuests(created.getId(), updateRequest);

            // then
            assertThat(excepted.getNumberOfGuests()).isEqualTo(4);
        }

        @Test
        void 손님_수가_음수이면_예외가_발생한다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);
            OrderTable created = orderTableService.create(request);
            orderTableService.sit(created.getId());

            // when
            OrderTable updateRequest = new OrderTable();
            updateRequest.setNumberOfGuests(-4);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(created.getId(), updateRequest));
        }

        @Test
        void 사용_중_상태가_아닌_테이블의_손님_수_변경_시_예외가_발생한다() {
            // given
            OrderTable request = new OrderTable();
            request.setName(DEFAULT_ORDER_TABLE_NAME);
            OrderTable created = orderTableService.create(request);

            // when
            OrderTable updateRequest = new OrderTable();
            updateRequest.setNumberOfGuests(4);

            // then
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(created.getId(), updateRequest));
        }
    }

    @Nested
    class 주문_테이블_전체_조회_경우 {
        @Test
        void 전체_주문_테이블을_조회하면_생성된_모든_주문_테이블이_반환된다() {
            // given
            OrderTable request1 = new OrderTable();
            request1.setName(DEFAULT_ORDER_TABLE_NAME);
            orderTableService.create(request1);

            OrderTable request2 = new OrderTable();
            request2.setName("추가 " + DEFAULT_ORDER_TABLE_NAME);
            orderTableService.create(request2);

            // when
            List<OrderTable> excepted = orderTableService.findAll();

            // then
            assertThat(excepted).hasSize(2);
        }
    }

}
