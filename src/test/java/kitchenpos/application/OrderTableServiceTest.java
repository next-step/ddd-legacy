package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.Fixtures.망가진_테이블;
import static kitchenpos.Fixtures.주문테이블_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;

    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 등록할 수 있다.")
    @Test
    void create() {
        final OrderTable expected = 주문테이블_생성("창가쪽 테이블");
        final OrderTable actual = orderTableService.create(expected);
        assertThat(actual).isNotNull();
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @DisplayName("주문 테이블 이름은 비어 있거나 공백일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createException(String name) {
        final OrderTable expected = 주문테이블_생성(name);
        assertThatThrownBy(() -> orderTableService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블을 착석 상태로 수정한다.")
    @Test
    void changeEmptyToOccupied() {
        OrderTable expected = orderTableRepository.save(주문테이블_생성("창가쪽 테이블"));
        OrderTable sitTable = orderTableService.sit(expected.getId());
        assertThat(sitTable.isOccupied()).isTrue();
    }

    @DisplayName("주문 테이블을 비어있는 상태로 수정한다.")
    @Test
    void changeOccupiedToEmpty() {
        OrderTable expected = orderTableRepository.save(주문테이블_생성("창가쪽 테이블"));

        OrderTable emptyTable = orderTableService.clear(expected.getId());
        assertAll(
                () -> assertThat(emptyTable.isOccupied()).isFalse(),
                () -> assertThat(emptyTable.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("주문 테이블의 인원정보를 수정 할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable expected = orderTableRepository.save(주문테이블_생성("창가쪽 테이블", 5, true));
        OrderTable changeTable = orderTableService.changeNumberOfGuests(expected.getId(), expected);
        assertThat(changeTable.getNumberOfGuests()).isEqualTo(5);
    }

    @DisplayName("주문 테이블의 인원정보는 0명 이상이어야 한다.")
    @Test
    void changeNumberOfGuestsException() {
        OrderTable expected = orderTableRepository.save(주문테이블_생성("창가쪽 테이블", 5, true));
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(expected.getId(), 주문테이블_생성("창가쪽 테이블", -1, true)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블은 존재하는 테이블이여야 한다.")
    @Test
    void changeNumberOfGuestsException2() {
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(),망가진_테이블()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 테이블 목록을 조회한다.")
    @Test
    void findAll() {
        List<OrderTable> expected = 주문_테이블_조회();
        orderTableRepository.saveAll(expected);
        List<OrderTable> actual = orderTableService.findAll();
        assertThat(actual).hasSize(expected.size());
    }

    public List<OrderTable> 주문_테이블_조회() {
        return new ArrayList<>(List.of(주문테이블_생성("1"), 주문테이블_생성("2")));
    }

}