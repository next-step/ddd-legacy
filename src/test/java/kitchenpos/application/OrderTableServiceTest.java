package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.application.fixture.MenuFixture.SHOW_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_ONE_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.EAT_IN_ORDER_STATUS_COMPLETED_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.EAT_IN_ORDER_STATUS_SERVE_REQUEST;
import static kitchenpos.application.fixture.OrderTableFixture.EMPTY_ORDER_TABLE_REQUEST;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_ORDER_TABLE_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT_ONE_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("주문 테이블 등록 - 성공")
    @Test
    void createOrderTableSuccess() {
        // Given
        final OrderTable request = new OrderTable();
        request.setName("최상의 테이블");

        // When
        final OrderTable data = orderTableService.create(request);

        // Then
        final OrderTable orderTable = orderTableRepository.findById(data.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(orderTable.getId()).isEqualTo(data.getId()),
            () -> assertThat(orderTable.getName()).isEqualTo(data.getName())
        );
    }

    @DisplayName("주문 테이블 등록 - 등록 실패")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createMenuGroupFail(final String name) {
        // Give
        final OrderTable request = new OrderTable();
        request.setName(name);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(request));
    }

    @DisplayName("주문 테이블 - 착석 처리")
    @Test
    void orderTableSit() {
        // Given
        final OrderTable request = orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        // When
        final OrderTable result = orderTableService.sit(request.getId());

        // Then
        assertAll(
            () -> assertThat(result.getId()).isEqualTo(request.getId()),
            () -> assertThat(result.isEmpty()).isFalse()
        );
    }

    @DisplayName("주문 테이블 - 테이블 정리 성공")
    @Test
    void orderTableClearSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());
        orderRepository.save(EAT_IN_ORDER_STATUS_COMPLETED_REQUEST());

        UUID expectedId = EMPTY_ORDER_TABLE_REQUEST().getId();

        // When
        final OrderTable actual = orderTableService.clear(expectedId);

        // Then
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(expectedId),
            () -> assertThat(actual.isEmpty()).isTrue(),
            () -> assertThat(actual.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("주문 테이블 - 테이블 정리 실패, 주문 상태가 주문 완료가 아님")
    @Test
    void orderTableClearFail() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());
        orderRepository.save(EAT_IN_ORDER_STATUS_SERVE_REQUEST());

        // When, Then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(EMPTY_ORDER_TABLE_REQUEST().getId()));
    }

    @DisplayName("주문 테이블 전체 조회")
    @Test
    void findAll() {
        // Given
        orderTableRepository.save(NOT_EMPTY_ORDER_TABLE_REQUEST());

        // When
        final List<OrderTable> list = orderTableService.findAll();
        final String actualTableName = NOT_EMPTY_ORDER_TABLE_REQUEST().getName();

        // Then
        assertAll(
            () -> assertThat(list).isNotEmpty(),
            () -> assertThat(list.stream().filter(x -> (actualTableName.equals(x.getName()))).count()).isEqualTo(1)
        );
    }
}
