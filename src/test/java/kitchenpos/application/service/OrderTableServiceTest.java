package kitchenpos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = OrderTableFixture.init().create();
    }

    @Nested
    @DisplayName("주문 테이블 조회")
    class 주문_테이블_조회 {

        @Test
        @DisplayName("성공 : 특정 조건 없이 상품의 모든 목록을 조회할 수 있다.")
        void 주문테이블_목록_조회() {
            when(orderTableRepository.findAll()).thenReturn(List.of(orderTable));
            List<OrderTable> result = orderTableRepository.findAll();

            assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertEquals(result.size(), 1)
            );
        }
    }

    @Nested
    @DisplayName("주문 테이블 등록")
    class 주문_테이블_등록 {

        @ParameterizedTest
        @DisplayName("성공")
        @ValueSource(strings = {"1번 테이블", "2번 테이블"})
        void 주문_테이블_등록성공(final String name) {
            orderTable = OrderTableFixture.test(name, 0, false).create();

            assertThatCode(() -> {
                orderTableService.create(orderTable);
            }).doesNotThrowAnyException();

        }

        @ParameterizedTest
        @DisplayName("테이블명은 공란일 수 없다.")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        void 테이블명_공란_검사(final String name) {
            orderTable = OrderTableFixture.test(name, 0, false).create();
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(orderTable));

        }
    }

    @Nested
    @DisplayName("테이블 착석")
    class 테이블_착석 {

        @Test
        @DisplayName("성공")
        void 주문_테이블_착석성공() {
            mockFindByOrderTable();

            assertThatCode(() -> {
                orderTableService.sit(orderTable.getId());
            }).doesNotThrowAnyException();

        }

        @Test
        @DisplayName("테이블 사용중 처리한다.")
        void 테이블_사용처리() {
            mockFindByOrderTable();

            var result = orderTableService.sit(orderTable.getId());

            assertThat(result.isOccupied()).isTrue();

        }
    }

    @Nested
    @DisplayName("테이블 정리")
    class 테이블_정리 {

        @Test
        @DisplayName("성공")
        void 주문_테이블_정리성공() {

            mockFindByOrderTable();

            mockExistsByOrderTable(false);

            assertThatCode(() -> {
                orderTableService.clear(orderTable.getId());
            }).doesNotThrowAnyException();

        }

        @Test
        @DisplayName("주문 테이블이 있으면 주문상태가 **완료**이어야 한다.")
        void 주문상태가_완료가아니면_정리불가() {
            mockFindByOrderTable();

            mockExistsByOrderTable(true);

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
        }

        @Test
        @DisplayName("빈 테이블로 설정한다.")
        void 빈테이블_처리() {
            mockFindByOrderTable();
            mockExistsByOrderTable(false);

            var result = orderTableService.clear(orderTable.getId());

            assertThat(result.isOccupied()).isFalse();
        }
    }

    @Nested
    @DisplayName("테이블 인원 변경")
    class 테이블_인원_변경 {

        @Test
        @DisplayName("성공")
        void 주문_테이블_인원변경_성공() {
            orderTable = OrderTableFixture.test("1번 테이블", 3, true).create();

            mockFindByOrderTable();

            assertThatCode(() -> {
                orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);
            }).doesNotThrowAnyException();

        }

        @Test
        @DisplayName("테이블 사용중인 상태여야 한다.")
        void 테이블_사용여부_검사() {
            mockFindByOrderTable();

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(
                    () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @Test
        @DisplayName("테이블 인원 수는 0명 이상이어야 한다.")
        void 테이블_인원수_허용범위_검사() {
            orderTable = OrderTableFixture.test("test", -1, false).create();
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                    () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));

        }
    }

    private void mockFindByOrderTable() {
        when(orderTableRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(orderTable));
    }

    private void mockExistsByOrderTable(boolean status) {
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
            .thenReturn(status);
    }
}
