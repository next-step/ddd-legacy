package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {OrderTableService.class})
class OrderTableServiceTest extends ObjectCreator {
    @MockBean
    private OrderTableRepository orderTableRepository;
    @MockBean
    private OrderRepository orderRepository;
    @Autowired
    private OrderTableService orderTableService;

    private String name;
    private UUID id;
    private int changedNumberOfGuest;
    private OrderTable defaultOrderTable;

    @BeforeEach
    void setUp() {
        name = "테이블";
        id = UUID.randomUUID();
        changedNumberOfGuest = 3;
        defaultOrderTable = defaultOrderTable(id, name);
        saveOrderTable();
    }

    @Test
    @DisplayName("테이블을 생성한다.")
    void create() {
        OrderTable orderTable = saveOrderTable();

        assertThat(orderTable.getName()).isEqualTo(name);
    }

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("테이블 명은 비어있으면 안된다.")
    void create_valid_name(String name) {
        OrderTable request = createRequest(name);

        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블 착석처리 한다.")
    void sit() {
        given(orderTableRepository.findById(any())).willReturn(Optional.of(defaultOrderTable));

        OrderTable orderTable = orderTableService.sit(id);

        assertThat(orderTable.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("존재하는 테이블만 착석처리 가능하다.")
    void sit_exit_orderTable() {
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("테이블 비움처리 한다.")
    void clear() {
        OrderTable exitTable = getExitTable();
        given(orderTableRepository.findById(any())).willReturn(Optional.of(exitTable));

        OrderTable orderTable = orderTableService.clear(id);

        assertAll(
                () -> assertEquals(orderTable.getNumberOfGuests(), 0),
                () -> assertTrue(orderTable.isEmpty())
        );
    }

    @Test
    @DisplayName("존재하는 테이블만 비움 가능하다.")
    void clear_exit_orderTable() {
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("테이블에 할당된 주문이 모두 완료되어야 한다.")
    void clear_order_allCompleted() {
        OrderTable exitTable = getExitTable();
        given(orderTableRepository.findById(any())).willReturn(Optional.of(exitTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블 착석 인원수를 변경한다.")
    void changeNumberOfGuests() {
        OrderTable request = getChangeNumberOfGuestsRequest(changedNumberOfGuest);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(getExitTable()));

        OrderTable orderTable = orderTableService.changeNumberOfGuests(id, request);

        assertAll(
                () -> assertEquals(orderTable.getId(), id),
                () -> assertEquals(orderTable.getNumberOfGuests(), changedNumberOfGuest)
        );
    }

    @Test
    @DisplayName("테이블 착석 인원수는 0 이상이다.")
    void changeNumberOfGuests_valid_numberOfGuests() {
        OrderTable request = getChangeNumberOfGuestsRequest(-1);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(getExitTable()));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하는 테이블만 인원수 변경 가능하다.")
    void changeNumberOfGuests_exit_orderTable() {
        OrderTable request = getChangeNumberOfGuestsRequest(changedNumberOfGuest);
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("착삭된 테이블만 인원수 변경 가능하다.")
    void changeNumberOfGuests_valid_empty() {
        OrderTable request = getChangeNumberOfGuestsRequest(changedNumberOfGuest);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(defaultOrderTable));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, request))
                .isInstanceOf(IllegalStateException.class);
    }

    public OrderTable saveOrderTable() {
        OrderTable request = createRequest(name);
        given(orderTableRepository.save(any())).willReturn(defaultOrderTable);

        return orderTableService.create(request);
    }

    public OrderTable getExitTable() {
        return getExitTable(defaultOrderTable);
    }
}
