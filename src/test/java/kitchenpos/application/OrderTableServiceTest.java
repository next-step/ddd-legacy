package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.testfixture.OrderTableFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @TestFactory
    List<DynamicTest> create() {
        doAnswer(args -> args.getArgument(0)).when(orderTableRepository).save(any());

        return List.of(
                dynamicTest("주문 테이블을 생성할 수 있다. 생성된 주문 테이블의 손님 수는 항상 0이다.", () -> {
                    var request = OrderTableFixture.createOrderTable("주문테이블", 1);

                    var result = orderTableService.create(request);

                    assertSoftly(softly -> {
                        softly.assertThat(result.getName()).isEqualTo(request.getName());
                        softly.assertThat(result.getNumberOfGuests()).isZero();
                        softly.assertThat(result.isOccupied()).isFalse();
                    });
                }),

                dynamicTest("주문 테이블의 이름은 null 일 수 없다.", () -> {
                    var request = OrderTableFixture.createOrderTable(null, 1);

                    var throwable = catchThrowable(() -> orderTableService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("주문 테이블의 이름은 빈 값 일 수 없다.", () -> {
                    var request = OrderTableFixture.createOrderTable("", 1);

                    var throwable = catchThrowable(() -> orderTableService.create(request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @TestFactory
    List<DynamicTest> sit() {
        var satTable = OrderTableFixture.createOrderTable("빈_주문_테이블", 0, true);
        var emptyTable = OrderTableFixture.createOrderTable("앉은_주문_테이블", 3, false);

        return List.of(
                dynamicTest("빈 테이블을 점유할 수 있다.", () -> {
                    var orderTableId = emptyTable.getId();
                    doAnswer(args -> Optional.of(emptyTable)).when(orderTableRepository).findById(eq(orderTableId));

                    var result = orderTableService.sit(orderTableId);

                    assertThat(result.isOccupied()).isTrue();
                }),


                dynamicTest("점유된 테이블도 다시 점유 요청할 수 있다.", () -> {
                    var orderTableId = satTable.getId();
                    doAnswer(args -> Optional.of(satTable)).when(orderTableRepository).findById(eq(orderTableId));

                    var result = orderTableService.sit(orderTableId);

                    assertThat(result.isOccupied()).isTrue();
                })
        );
    }

    @TestFactory
    List<DynamicTest> clear() {
        var orderCompletedTable = OrderTableFixture.createOrderTable("주문_완료_테이블", 0, true);
        var orderPendingTable = OrderTableFixture.createOrderTable("주문_대기_테이블", 3, false);

        return List.of(
                dynamicTest("주문이 완료된 상태여야라면 주문테이블을 초기화 할 수 있다", () -> {
                    var orderTableId = orderCompletedTable.getId();
                    doAnswer(args -> Optional.of(orderCompletedTable)).when(orderTableRepository).findById(eq(orderTableId));
                    doAnswer(args -> false).when(orderRepository).existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED));

                    var result = orderTableService.clear(orderCompletedTable.getId());

                    assertThat(result.isOccupied()).isFalse();
                    assertThat(result.getNumberOfGuests()).isZero();
                }),

                dynamicTest("주문이 완료되지 않은 상태에서는 주문테이블을 초기화할 수 없다.", () -> {
                    var orderTableId = orderPendingTable.getId();
                    doAnswer(args -> Optional.of(orderPendingTable)).when(orderTableRepository).findById(eq(orderTableId));
                    doAnswer(args -> true).when(orderRepository).existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED));

                    var throwable = catchThrowable(() -> orderTableService.clear(orderTableId));

                    assertThat(throwable).isInstanceOf(IllegalStateException.class);
                })
        );
    }

    @TestFactory
    List<DynamicTest> changeNumberOfGuests() {
        var satTable = OrderTableFixture.createOrderTable("빈_주문_테이블", 0, true);
        var emptyTable = OrderTableFixture.createOrderTable("앉은_주문_테이블", 3, false);

        return List.of(
                dynamicTest("주문 테이블에 앉은 손님 수를 변경할 수 있다.", () -> {
                    var orderTableId = satTable.getId();
                    doAnswer(args -> Optional.of(satTable)).when(orderTableRepository).findById(eq(orderTableId));
                    var request = OrderTableFixture.createOrderTable(satTable.getId(), satTable.getName(), 10, satTable.isOccupied());

                    var result = orderTableService.changeNumberOfGuests(orderTableId, request);

                    assertThat(result.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
                }),

                dynamicTest("주문 테이블에 앉은 손님 수 이외의 다른 값은 변경할 수 없다.", () -> {
                    var orderTableId = satTable.getId();
                    doAnswer(args -> Optional.of(satTable)).when(orderTableRepository).findById(eq(orderTableId));
                    var request = OrderTableFixture.createOrderTable(UUID.randomUUID(), "메에에륭", satTable.getNumberOfGuests(), !satTable.isOccupied());

                    var result = orderTableService.changeNumberOfGuests(orderTableId, request);

                    assertThat(result.getName()).isEqualTo(satTable.getName());
                    assertThat(result.isOccupied()).isEqualTo(satTable.isOccupied());
                    assertThat(result.getId()).isEqualTo(satTable.getId());
                }),

                dynamicTest("주문 테이블의 변경하는 손님 수는 0일 수 있다.", () -> {
                    var orderTableId = satTable.getId();
                    doAnswer(args -> Optional.of(satTable)).when(orderTableRepository).findById(eq(orderTableId));
                    var request = OrderTableFixture.createOrderTable(satTable.getId(), satTable.getName(), 0, satTable.isOccupied());

                    var result = orderTableService.changeNumberOfGuests(orderTableId, request);

                    assertThat(result.getName()).isEqualTo(satTable.getName());
                    assertThat(result.isOccupied()).isEqualTo(satTable.isOccupied());
                    assertThat(result.getId()).isEqualTo(satTable.getId());
                }),

                dynamicTest("주문 테이블의 변경하는 손님 수는 반드시 0 이상이어야 한다.", () -> {
                    var orderTableId = satTable.getId();
                    var request = OrderTableFixture.createOrderTable(satTable.getId(), satTable.getName(), -1, satTable.isOccupied());

                    var throwable = catchThrowable(() -> orderTableService.changeNumberOfGuests(orderTableId, request));

                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("점유하지 있는 테이블은 손님수를 변경할 수 없다.", () -> {
                    var orderTableId = emptyTable.getId();
                    var request = OrderTableFixture.createOrderTable(emptyTable.getId(), emptyTable.getName(), 5, emptyTable.isOccupied());

                    var throwable = catchThrowable(() -> orderTableService.changeNumberOfGuests(orderTableId, request));

                    assertThat(throwable).isInstanceOf(NoSuchElementException.class);
                })
        );
    }

    @Test
    @DisplayName("모든 주문 테이블을 리스트로 조회할 수 있다.")
    void findAll() {
        doAnswer(args -> List.of(OrderTableFixture.createOrderTable("주문테이블1", 1, true)))
                .when(orderTableRepository).findAll();

        var result = orderTableService.findAll();

        assertThat(result).isNotEmpty();

    }
}
