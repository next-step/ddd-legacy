package kitchenpos.application;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.test.UnitTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("주문 테이블")
class OrderTableServiceTest extends UnitTestCase {

    @InjectMocks
    private OrderTableService service;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    private UUID orderTableId;

    @BeforeEach
    void setUp() {
        orderTableId = UUID.randomUUID();
    }

    @DisplayName("등록")
    @Nested
    class CreateTest {

        @DisplayName("이름으로 주문 테이블을 등록한다. "
                + "인원수는 0명, 비어있는 테이블 상태로 등록된다.")
        @Test
        void success() {
            // given
            String name = "5번";
            OrderTable request = new OrderTable();
            request.setName(name);

            given(orderTableRepository.save(any()))
                    .willReturn(request);

            // when then
            assertThat(service.create(request))
                    .hasFieldOrPropertyWithValue("name", name)
                    .hasFieldOrPropertyWithValue("numberOfGuests", 0)
                    .hasFieldOrPropertyWithValue("occupied", Boolean.FALSE);
        }

        @DisplayName("이름은 비어 있을 수 없다.")
        @ParameterizedTest
        @NullAndEmptySource
        void error(String actual) {
            // given
            OrderTable request = new OrderTable();
            request.setName(actual);

            // when then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.create(request));
        }
    }

    @DisplayName("배정")
    @Nested
    class SitTest {

        @DisplayName("테이블을 배정한다.")
        @Test
        void success() {
            // when
            OrderTable orderTable = new OrderTable();
            orderTable.setOccupied(false);
            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));

            // when then
            assertThat(service.sit(orderTableId))
                    .hasFieldOrPropertyWithValue("occupied", Boolean.TRUE);
        }

        @DisplayName("등록된 테이블이 없을 경우 배정할 수 없다.")
        @Test
        void error() {
            // when
            when(orderTableRepository.findById(orderTableId))
                    .thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> service.sit(orderTableId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("빈 테이블 등록")
    @Nested
    class ClearTest {

        @DisplayName("모든 주문이 완료된 경우에만 빈 테이블로 변경할 수 있다. "
                + "인원수는 0명, 비어있는 테이블 상태로 등록된다.")
        @Test
        void success() {
            // given
            OrderTable orderTable = new OrderTable();
            orderTable.setOccupied(true);
            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));

            // when
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .thenReturn(Boolean.FALSE);

            // then
            assertThat(service.clear(orderTableId))
                    .hasFieldOrPropertyWithValue("numberOfGuests", 0)
                    .hasFieldOrPropertyWithValue("occupied", Boolean.FALSE);
        }

        @DisplayName("모든 주문이 완료된 경우에만 빈 테이블로 변경할 수 있다.")
        @Test
        void error1() {
            // given
            OrderTable orderTable = new OrderTable();
            orderTable.setOccupied(true);
            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));

            // when
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .thenReturn(Boolean.TRUE);

            // then
            assertThatThrownBy(() -> service.clear(orderTableId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("테이블이 존재하지 않을 경우 빈 테이블로 변경할 수 없다.")
        @Test
        void error2() {
            // when
            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> service.clear(orderTableId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("인원수 수정")
    @Nested
    class ChangeNumberOfGuestsTest {

        private OrderTable request;

        @BeforeEach
        void setUp() {
            request = new OrderTable();
            request.setNumberOfGuests(0);
        }

        @DisplayName("인원수를 수정할 수 있다.")
        @Test
        void success() {
            // when
            OrderTable orderTable = new OrderTable();
            orderTable.setOccupied(true);
            orderTable.setNumberOfGuests(request.getNumberOfGuests());

            when(orderTableRepository.findById(orderTableId))
                    .thenReturn(Optional.of(orderTable));

            // then
            assertThat(service.changeNumberOfGuests(orderTableId, request))
                    .hasFieldOrPropertyWithValue("numberOfGuests", 0);
        }

        @DisplayName("테이블 인원수는 0명 이상이어야 한다.")
        @Test
        void error1() {
            // when
            request.setNumberOfGuests(-1);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.changeNumberOfGuests(orderTableId, request));
        }

        @DisplayName("테이블이 존재하지 않을 경우 빈 테이블로 변경할 수 없다.")
        @Test
        void error2() {
            // when
            when(orderTableRepository.findById(any()))
                    .thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> service.changeNumberOfGuests(orderTableId, request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("배정된 테이블만 인원 수정 가능하다.")
        @Test
        void error3() {
            // when
            OrderTable orderTable = new OrderTable();
            orderTable.setOccupied(false);
            when(orderTableRepository.findById(any()))
                    .thenReturn(Optional.of(orderTable));

            // then
            assertThatThrownBy(() -> service.changeNumberOfGuests(orderTableId, request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("등록된 주문 테이블을 조회할 수 있다.")
    @Test
    void findAll() {
        assertThatCode(() -> service.findAll())
                .doesNotThrowAnyException();
    }
}
