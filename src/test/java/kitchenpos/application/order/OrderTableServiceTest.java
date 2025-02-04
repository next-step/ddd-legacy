package kitchenpos.application.order;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;

import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderTableServiceTest {
    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        this.orderTableRepository = new InMemoryOrderTableRepository();
        this.orderRepository = mock(OrderRepository.class);
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }



    @Nested
    @DisplayName("주문 테이블을 생성한다")
    class CreateOrderTable {
        @Test
        void create() {
            // given
            OrderTable request = OrderTableFixture.orderTable("테이블", 0, false);

            // when
            OrderTable created = orderTableService.create(request);

            // then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("테이블");
            assertThat(created.getNumberOfGuests()).isEqualTo(0);
            assertThat(created.isOccupied()).isFalse();
        }

        @Test
        void failWithEmptyName() {
            // given
            OrderTable request = OrderTableFixture.orderTable("", 0, false);

            // when & then
            assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("주문 테이블에 손님을 입장시킨다")
    class SitOrderTable {
        @Test
        void sitSuccess() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.orderTable("테이블", 0, false));

            // when
            OrderTable sitOrderTable = orderTableService.sit(orderTable.getId());

            // then
            assertThat(sitOrderTable.isOccupied()).isTrue();
        }
        @Test
        void sitFailWithNoSuchElementException() {
            // given
            OrderTable orderTable = OrderTableFixture.orderTable("테이블", 0, false);

            // when & then
            assertThatThrownBy(() -> orderTableService.sit(orderTable.getId()))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("주문 테이블을 비운다")
    class ClearOrderTable {
        @Test
        void clearSuccess() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.orderTable("테이블", 0, true));

            // when
            OrderTable clearOrderTable = orderTableService.clear(orderTable.getId());

            // then
            assertThat(clearOrderTable.getNumberOfGuests()).isEqualTo(0);
            assertThat(clearOrderTable.isOccupied()).isFalse();
        }

        @Test
        void clearFailWithNoSuchElementException() {
            // given
            OrderTable orderTable = OrderTableFixture.orderTable("테이블", 3, false);

            // when & then
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void failWithStatusNotCompleted() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.orderTable("테이블", 0, true));

            // when
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(true);

            // then
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("테이블에 손님을 추가한다")
    class AddClientOrderTable {
        @Test
        void successAddClient(){
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.orderTable("테이블", 0, true));
            OrderTable request = OrderTableFixture.orderTable("테이블", 3, true);

            // when
            OrderTable updated = orderTableService.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThat(updated.getNumberOfGuests()).isEqualTo(3);
        }

        @Test
        void failWithNegativeNumberOfGuests() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.orderTable("테이블", 0, true));
            OrderTable request = OrderTableFixture.orderTable("테이블", -1, true);

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void failWithNotOccupied() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.orderTable("테이블", 0, false));
            OrderTable request = OrderTableFixture.orderTable("테이블", 3, false);

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalStateException.class);
        }
    }
}