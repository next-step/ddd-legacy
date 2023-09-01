package kitchenpos.application;

import kitchenpos.application.fixture.OrderTableTestFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {
    OrderTableService orderTableService;
    OrderTableTestFixture orderTableTestFixture;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
        this.orderTableTestFixture = new OrderTableTestFixture();
    }

    @DisplayName("정상동작")
    @Test
    void createOk() {
        OrderTable orderTable = orderTableTestFixture.createOrderTable("test", true, 10000);
        given(orderTableRepository.save(any())).willReturn(orderTable);
        OrderTable result = orderTableService.create(orderTable);

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.isOccupied()).isTrue();
        assertThat(result.getNumberOfGuests()).isEqualTo(10000);
    }


    @Nested
    @DisplayName("테이블 신규 생성 시")
    class OrderTable_create {
        @DisplayName("테이블 명이 없으면 예외를 반환한다.")
        @Test
        void createTableName() {
            OrderTable orderTable = orderTableTestFixture.createOrderTable(null, true, 1);

            assertThatThrownBy(() -> orderTableService.create(orderTable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("테이블명 오류");

        }
    }

    @Nested
    @DisplayName("손님 착석 시")
    class Guest_sit {

        @DisplayName("테이블이 차지됨으로 설정.")
        @Test
        void createTableStatus() {
            OrderTable orderTable = orderTableTestFixture.createOrderTable(false, 2);
            given(orderTableRepository.findById(any())).willReturn(Optional.ofNullable(orderTable));

            assertThat(orderTableService.sit(orderTable.getId()).isOccupied()).isTrue();
        }
    }

    @Nested
    @DisplayName("테이블 변경")
    class OrderTable_clear {

        @DisplayName("주문 완료시 테이블을 비어있는 상태로 변경한다.")
        @Test
        void createTableClean() {
            OrderTable orderTable = orderTableTestFixture.createOrderTable(true, 2);
            given(orderTableRepository.findById(any())).willReturn(Optional.ofNullable(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            OrderTable result = orderTableService.clear(orderTable.getId());

            assertThat(result.isOccupied()).isFalse();
            assertThat(result.getNumberOfGuests()).isEqualTo(0);

        }

        @DisplayName("주문이 완료되지 않은 경우 테이블을 비어있는 상태로 변경하면 예외를 반환한다.")
        @Test
        void createTableUsing() {
            OrderTable orderTable = orderTableTestFixture.createOrderTable(true, 2);
            given(orderTableRepository.findById(any())).willReturn(Optional.ofNullable(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("테이블 상태 오류");

        }
    }

    @Nested
    @DisplayName("손님수 변경")
    class OrderTable_GuestNum_change {

        @DisplayName("손님 수가 0 이상이 아니면 예외를 반환한다.")
        @Test
        void GuestNum() {
            OrderTable orderTable1 = orderTableTestFixture.createOrderTable(2);
            OrderTable orderTable2 = orderTableTestFixture.createOrderTable(-1);

            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable1.getId(), orderTable2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("손님수 오류");
        }

        @DisplayName("테이블에 기존 손님이 있던 경우가 아니라면 손님수 변경 시 예외를 반환한다.")
        @Test
        void changeGuestNum() {
            OrderTable orderTable1 = orderTableTestFixture.createOrderTable("test", false, 0);
            OrderTable orderTable2 = orderTableTestFixture.createOrderTable("test2", true, 100);
            given(orderTableRepository.findById(any())).willReturn(Optional.ofNullable(orderTable1));

            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable1.getId(), orderTable2))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("손님수 변경 오류");

        }

    }
}
