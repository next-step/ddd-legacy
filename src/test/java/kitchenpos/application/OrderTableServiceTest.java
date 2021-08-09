package kitchenpos.application;

import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_TABLE;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_TABLE_WITH_GUESTS;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE1;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE2;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLES;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE_WITH_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class OrderTableServiceTest extends MockTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @BeforeEach
    public void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("create - 주문 테이블을 추가할 수 있다")
    @Test
    void create() {
        //given
        given(orderTableRepository.save(any())).willReturn(ORDER_TABLE1());

        //when
        final OrderTable sut = orderTableService.create(ORDER_TABLE1());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(ORDER_TABLE1().getId()),
            () -> assertThat(sut.getName()).isEqualTo(ORDER_TABLE1().getName()),
            () -> assertThat(sut.getNumberOfGuests()).isEqualTo(ORDER_TABLE1().getNumberOfGuests()),
            () -> assertThat(sut.isEmpty()).isEqualTo(ORDER_TABLE1().isEmpty())
        );
    }

    @DisplayName("create - 주문 테이블 이름이 한글자 미만이라면 예외를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void cteateWithEmptyName(final String name) {
        //given
        final OrderTable orderTable = ORDER_TABLE_WITH_NAME(name);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("create - 주문 테이블 이름은 중복될 수 있다")
    @Test
    void cteateWithDuplicateName() {
        //given
        final OrderTable orderTable1 = ORDER_TABLE1();
        final OrderTable orderTable2 = ORDER_TABLE1();

        given(orderTableRepository.save(any())).willReturn(orderTable1, orderTable2);

        //when
        final OrderTable createdOrderTable1 = orderTableService.create(orderTable1);
        final OrderTable createdOrderTable2 = orderTableService.create(orderTable2);

        //then
        assertThat(createdOrderTable1.getName()).isEqualTo(createdOrderTable2.getName());
    }

    @DisplayName("sit - 주문 테이블에 손님이 앉을 수 있다")
    @Test
    void sit() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.of(ORDER_TABLE1()));

        //when
        final OrderTable sut = orderTableService.sit(ORDER_TABLE1().getId());

        //then
        assertThat(sut.isEmpty()).isFalse();
    }

    @DisplayName("sit - 테이블이 존재하지 않는다면 예외를 반환한다")
    @Test
    void sitWIthNotExistTable() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderTableService.sit(ORDER_TABLE1().getId()));
    }

    @DisplayName("clear - 주문 테이블을 치울 수 있다")
    @Test
    void clear() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.of(ORDER_TABLE1()));

        //when
        final OrderTable sut = orderTableService.clear(ORDER_TABLE1().getId());

        //then
        assertThat(sut.isEmpty()).isTrue();
    }

    @DisplayName("clear - 테이블이 존재하지 않는다면 예외를 반환한다")
    @Test
    void clearWIthNotExistTable() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderTableService.clear(ORDER_TABLE1().getId()));
    }

    @DisplayName("clear - 테이블의 주문 상태가 모두 완료가 아닐 경우 예외를 반환한다")
    @Test
    void clearStatus() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.of(ORDER_TABLE1()));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderTableService.clear(ORDER_TABLE1().getId()));
    }

    @DisplayName("changeGuestNumber - 테이블에 앉은 손님의 수를 변경할 수 있다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 100, 1000, Integer.MAX_VALUE})
    void changeGuestNumber(final int numberOfGuests) {
        //given
        final OrderTable orderTable = NOT_EMPTY_TABLE();
        final OrderTable orderTableRequest = NOT_EMPTY_TABLE_WITH_GUESTS(numberOfGuests);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        //when
        final OrderTable sut = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest);

        //then
        assertThat(sut.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("changeGuestNumber - 변경할 손님의 수가 음수라면 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4, -5, -100, -1000, Integer.MIN_VALUE})
    void changeGuestNumberNegativeNumber(final int negativeNumber) {
        //given
        final OrderTable orderTable = ORDER_TABLE1();

        orderTable.setNumberOfGuests(negativeNumber);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("changeGuestNumber - 존재하는 테이블이 아니라면 예외를 반환한다")
    @Test
    void changeGuestNumberNotExistTable() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderTableService.changeNumberOfGuests(ORDER_TABLE1().getId(), ORDER_TABLE1()));
    }

    @DisplayName("changeGuestNumberStatus - 테이블이 비어있는 경우 예외를 반환한다")
    @Test
    void changeGuestNumberStatus() {
        //given
        given(orderTableRepository.findById(any())).willReturn(Optional.of(ORDER_TABLE1()));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderTableService.changeNumberOfGuests(ORDER_TABLE1().getId(), ORDER_TABLE1()));
    }

    @DisplayName("findAll - 테이블 리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        given(orderTableRepository.findAll()).willReturn(ORDER_TABLES());

        //when
        final List<OrderTable> sut = orderTableService.findAll();

        //then
        assertAll(
            () -> assertThat(sut.size()).isEqualTo(ORDER_TABLES().size()),
            () -> assertThat(sut.get(ZERO).getId()).isEqualTo(ORDER_TABLE1().getId()),
            () -> assertThat(sut.get(ZERO).getName()).isEqualTo(ORDER_TABLE1().getName()),
            () -> assertThat(sut.get(ONE).getId()).isEqualTo(ORDER_TABLE2().getId()),
            () -> assertThat(sut.get(ONE).getName()).isEqualTo(ORDER_TABLE2().getName())
        );
    }

}
