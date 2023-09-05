package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kitchenpos.application.fakerepository.OrderFakeRepository;
import kitchenpos.application.fakerepository.OrderTableFakeRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


@Nested
@DisplayName("OrderTableService 클래스")
class OrderTableServiceCreateTest extends OrderTestSetup {

    private static final String TEST_NAME = "tableName";
    private static final int TEST_NUMBER_OF_GUEST = 2;

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    private OrderTableService sut;

    @BeforeEach
    void setUp() {

        orderTableRepository = new OrderTableFakeRepository();
        orderRepository = new OrderFakeRepository();

        sut = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class create_메소드는 {

        @DisplayName("request의 이름이 null이거나 비어있으면 예외를 발생시킨다")
        @ParameterizedTest
        @NullAndEmptySource
        void request의_이름이_null이거나_비어있으면_예외를_발생시킨다(final String name) {

            assertThatThrownBy(() -> sut.create(create(name)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }


        @DisplayName("orderTable을 생성 후 반환한다")
        @Test
        void orderTable을_생성_후_반환한다() {

            // when
            final OrderTable actual = sut.create(
                createOrderTableRequest(TEST_NAME, TEST_NUMBER_OF_GUEST, true));

            // then
            assertThat(actual.getId()).isNotNull();
        }

        private OrderTable create(final String name) {
            return createOrderTableRequest(name, TEST_NUMBER_OF_GUEST, true);
        }
    }
}