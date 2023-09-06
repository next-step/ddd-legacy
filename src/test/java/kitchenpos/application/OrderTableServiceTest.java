package kitchenpos.application;

import static kitchenpos.application.constant.KitchenposTestConst.TEST_DELIVERY_ADDRESS;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_DATE_TIME;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_TABLE_NAME;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_TABLE_NUMBER_OF_GUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrderTableServiceTest extends OrderTableServiceTestSetup {

    @DisplayName("orderTable이 없으면 예외를 발생시킨다")
    @Test
    void sit_orderTable이_없으면_예외를_발생시킨다() {

        // when & then
        assertThatThrownBy(() -> sut.sit(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("점유상태를 true로 변경하여 반환한다.")
    @Test
    void sit_점유상태를_true로_변경하여_반환한다() {

        // given
        final OrderTable orderTable = orderTableRepository.save(create(false));

        // when
        final OrderTable actual = sut.sit(orderTable.getId());

        // then
        assertThat(actual.isOccupied()).isTrue();
    }

    private OrderTable create(final boolean occupied) {
        return createOrderTableRequest(TEST_ORDER_TABLE_NAME,
            TEST_ORDER_TABLE_NUMBER_OF_GUEST, occupied);
    }

    @DisplayName("orderTable이 없으면 예외를 발생시킨다")
    @Test
    void clear_orderTable이_없으면_예외를_발생시킨다() {

        // when & then
        assertThatThrownBy(() -> sut.sit(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("orderTable에 할당된 주문의 상태가 완료가 아니면 예외를 발생시킨다")
    @Test
    void clear_orderTable에_할당된_주문의_상태가_완료가_아니면_예외를_발생시킨다() {

        // given
        final OrderTable orderTable = orderTableRepository.save(
            createOrderTableRequest(TEST_ORDER_TABLE_NAME,
                TEST_ORDER_TABLE_NUMBER_OF_GUEST, true));
        orderRepository.save(create(orderTable, OrderStatus.SERVED));

        // when & then
        assertThatThrownBy(() -> sut.clear(orderTable.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);

    }

    private Order create(final OrderTable orderTable, final OrderStatus status) {
        return createOrder(OrderType.EAT_IN, status,
            TEST_ORDER_DATE_TIME, ImmutableList.of(), TEST_DELIVERY_ADDRESS, orderTable);
    }

    @DisplayName("orderTable의 손님 수를 0으로 만들고 점유 상태를 false로 변경하여 반환한다")
    @Test
    void clear_orderTable의_손님_수를_0으로_만들고_점유_상태를_false로_변경하여_반환한다() {

        // given
        final OrderTable orderTable = orderTableRepository.save(
            createOrderTableRequest(TEST_ORDER_TABLE_NAME, TEST_ORDER_TABLE_NUMBER_OF_GUEST, true));
        orderRepository.save(create(orderTable, OrderStatus.COMPLETED));

        // when
        final OrderTable act = sut.clear(orderTable.getId());

        // then
        assertThat(act.getNumberOfGuests()).isZero();
        assertThat(act.isOccupied()).isFalse();
    }

    @DisplayName("orderTable을 모두 반환한다.")
    @ParameterizedTest
    @CsvSource("dummyName1,dummyName2,dummyName3")
    void findAll_orderTable을_모두_반환한다(final String dummy1, final String dummy2,
        final String dummy3) {

        // given
        final OrderTable orderTable1 = orderTableRepository.save(create(dummy1));
        final OrderTable orderTable2 = orderTableRepository.save(create(dummy2));
        final OrderTable orderTable3 = orderTableRepository.save(create(dummy3));

        // when
        final List<OrderTable> actual = sut.findAll();

        // then
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .contains(orderTable1, orderTable2, orderTable3);
    }

    private OrderTable create(final String name) {
        return createOrderTableRequest(name, TEST_ORDER_TABLE_NUMBER_OF_GUEST, true);
    }
}