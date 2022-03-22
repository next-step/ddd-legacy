package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.DefaultIntegrationTestConfig;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class OrderTableServiceTest extends DefaultIntegrationTestConfig {

    private static final UUID NOT_EXIST_ORDER_TABLE_ID = UUID.randomUUID();

    private static final boolean EMPTY = true;
    private static final boolean NOT_EMPTY = false;

    private static final boolean EXIST = true;
    private static final boolean NOT_EXIST = false;

    private static final String NAME_1 = "testName1";
    private static final String NAME_2 = "testName2";

    @MockBean
    private OrderRepository mockOrderRepository;

    @Autowired
    private OrderTableRepository repository;

    @Autowired
    private OrderTableService service;

    private OrderTable createRequest(final boolean empty,
        final int numOfGuests, final String name) {

        final OrderTable table = new OrderTable();
        table.setEmpty(empty);
        table.setNumberOfGuests(numOfGuests);
        table.setName(name);

        return table;
    }

    private OrderTable create(final boolean empty,
        final int numOfGuests, final String name) {

        final OrderTable table = new OrderTable();
        table.setId(UUID.randomUUID());
        table.setEmpty(empty);
        table.setNumberOfGuests(numOfGuests);
        table.setName(name);

        return table;
    }

    private void assertContainsForFindAll(final List<OrderTable> results,
        final OrderTable expResult1, final OrderTable expResult2) {

        assertThat(results).hasSize(2);

        final OrderTable result1;
        final OrderTable result2;
        if (results.get(0).getId().equals(expResult1.getId())) {
            result1 = expResult1;
            result2 = expResult2;
        } else {
            result1 = expResult2;
            result2 = expResult1;
        }

        assertThat(result1).usingRecursiveComparison().isEqualTo(expResult1);
        assertThat(result2).usingRecursiveComparison().isEqualTo(expResult2);
    }

    private void assertSavedOrderTableForCreate(final OrderTable orderTable,
        final UUID id, final String expName) {

        assertThat(orderTable.getId()).isEqualTo(id);
        assertThat(orderTable.getName()).isEqualTo(expName);
        assertThat(orderTable.isEmpty()).isTrue();
        assertThat(orderTable.getNumberOfGuests()).isZero();
    }

    private void assertReturnOrderTableForCreate(final OrderTable orderTable,
        final String expName) {

        assertThat(orderTable.getId()).isNotNull();
        assertThat(orderTable.getName()).isEqualTo(expName);
        assertThat(orderTable.isEmpty()).isTrue();
        assertThat(orderTable.getNumberOfGuests()).isZero();
    }

    private void assertOrderTableForSit(final OrderTable result, final OrderTable expResult) {
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("empty")
            .isEqualTo(expResult);
    }

    private void assertOrderTableForChangeNumOfGuests(final OrderTable result,
        final OrderTable expResult, final int expNumberOfGuests) {

        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("numberOfGuests", "empty")
            .isEqualTo(expResult);

        assertThat(result.getNumberOfGuests()).isEqualTo(expNumberOfGuests);
        assertThat(result.isEmpty()).isFalse();
    }

    private void assertOrderTableForClear(final OrderTable result, final OrderTable expResult) {
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("empty", "numberOfGuests")
            .isEqualTo(expResult);

        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getNumberOfGuests()).isZero();
    }

    private void configHasNotCompletedOrderScenario(final boolean exist) {
        doReturn(exist)
            .when(mockOrderRepository)
            .existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class));
    }

    @DisplayName("테이블의 이름이 null 혹은 empty이면 예외를 발생시킨다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_when_null_or_empty_name(final String name) {
        // given
        final OrderTable request = createRequest(true, 0, name);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블을 생성한다")
    @Test
    void create() {
        // given
        final OrderTable request = createRequest(true, 0, NAME_1);

        // when
        final OrderTable result = service.create(request);

        final OrderTable savedOrderTable = repository.getById(result.getId());

        // then
        assertReturnOrderTableForCreate(result, NAME_1);
        assertSavedOrderTableForCreate(savedOrderTable, result.getId(), NAME_1);
    }

    @DisplayName("테이블 생성 요청의 정보 중 name을 제외한 다른 정보는 모두 무시하고 디폴트로 설정한다")
    @Test
    void create_when_using_default_value() {
        // given
        final OrderTable request = createRequest(false, 5, NAME_1);

        // when
        final OrderTable result = service.create(request);

        final OrderTable savedOrderTable = repository.getById(result.getId());

        // then
        assertReturnOrderTableForCreate(result, NAME_1);
        assertSavedOrderTableForCreate(savedOrderTable, result.getId(), NAME_1);
    }

    @DisplayName("테이블이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void sit_when_not_exist_orderTable() {
        // given

        // when & then
        assertThatThrownBy(() -> service.sit(NOT_EXIST_ORDER_TABLE_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블이 빈 상태라면 비지 않은 상태로 변경한다")
    @Test
    void sit_when_empty() {
        // given
        final OrderTable orderTable = repository.save(create(true, 0, NAME_1));

        // when
        final OrderTable result = service.sit(orderTable.getId());

        final OrderTable savedOrderTable = repository.getById(orderTable.getId());

        // then
        assertOrderTableForSit(result, orderTable);
        assertOrderTableForSit(savedOrderTable, orderTable);
    }

    @DisplayName("테이블이 비지 않은 상태라면 그 상태를 유지한다")
    @Test
    void sit_when_already_not_empty() {
        // given
        final OrderTable orderTable = repository.save(create(false, 1, NAME_1));

        // when
        final OrderTable result = service.sit(orderTable.getId());

        final OrderTable savedOrderTable = repository.getById(orderTable.getId());

        // then
        assertOrderTableForSit(result, orderTable);
        assertOrderTableForSit(savedOrderTable, orderTable);
    }

    @DisplayName("존재하지 않는 테이블이라면 예외를 발생시킨다")
    @Test
    void clear_when_not_exist_orderTable() {
        // given

        // when & then
        assertThatThrownBy(() -> service.clear(NOT_EXIST_ORDER_TABLE_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블에 완료 되지 않은 주문이 있다면 예외를 발생시킨다")
    @Test
    void clear_when_has_not_completed_order() {
        // given
        configHasNotCompletedOrderScenario(EXIST);

        final OrderTable orderTable = repository.save(create(false, 1, NAME_1));

        // when & then
        assertThatThrownBy(() -> service.clear(orderTable.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블의 주문이 모두 완료 되었다면 손님의 수를 0으로 만들고, 빈 테이블로 변경한다")
    @Test
    void clear() {
        // given
        configHasNotCompletedOrderScenario(NOT_EXIST);

        final OrderTable orderTable = repository.save(create(false, 1, NAME_1));

        // when
        final OrderTable result = service.clear(orderTable.getId());

        final OrderTable savedOrderTable = repository.getById(orderTable.getId());

        // then
        assertOrderTableForClear(result, orderTable);
        assertOrderTableForClear(savedOrderTable, orderTable);
    }

    @DisplayName("손님수가 음수인 경우 예외를 발생시킨다")
    @Test
    void changeNumberOfGuests_when_negative_numberOfGuests() {
        // given
        final UUID id = repository.save(create(false, 0, NAME_1)).getId();
        final OrderTable request = createRequest(false, -1, NAME_1);

        // when & then
        assertThatThrownBy(() -> service.changeNumberOfGuests(id, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void changeNumberOfGuests_when_not_exist_orderTable() {
        // given
        final OrderTable request = createRequest(false, 1, NAME_1);

        // when & then
        assertThatThrownBy(() -> service.changeNumberOfGuests(NOT_EXIST_ORDER_TABLE_ID, request))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블이 비어있다면 손님의 수를 변경할 수 없다")
    @Test
    void changeNumberOfGuests_when_empty() {
        // given
        final UUID id = repository.save(create(true, 0, NAME_1)).getId();
        final OrderTable request = createRequest(false, 1, NAME_1);

        // when & then
        assertThatThrownBy(() -> service.changeNumberOfGuests(id, request))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블의 손님수를 변경한다")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = create(false, 0, NAME_1);
        final UUID id = repository.save(orderTable).getId();
        final OrderTable request = createRequest(false, 3, NAME_1);

        // when
        final OrderTable result = service.changeNumberOfGuests(id, request);

        final OrderTable savedOrderTable = repository.getById(id);

        // then
        assertOrderTableForChangeNumOfGuests(result, orderTable, 3);
        assertOrderTableForChangeNumOfGuests(savedOrderTable, orderTable, 3);
    }

    @DisplayName("테이블의 손님수는 0으로도 변경 가능하고, 손님수가 0이 되더라도 빈테이블로 만들지 않는다")
    @Test
    void changeNumberOfGuests_when_zero_numberOfGuests() {
        // given
        final OrderTable orderTable = create(false, 3, NAME_1);
        final UUID id = repository.save(orderTable).getId();
        final OrderTable request = createRequest(false, 0, NAME_1);

        // when
        final OrderTable result = service.changeNumberOfGuests(id, request);

        final OrderTable savedOrderTable = repository.getById(id);

        // then
        assertOrderTableForChangeNumOfGuests(result, orderTable, 0);
        assertOrderTableForChangeNumOfGuests(savedOrderTable, orderTable, 0);
    }

    @DisplayName("테이블이 없다면 빈 리스트를 반환한다")
    @Test
    void findAll_when_empty() {
        // given

        // when
        final List<OrderTable> result = service.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("테이블이 있다면 그것을 담은 리스트를 반환한다")
    @Test
    void findAll() {
        // given
        final OrderTable table1 = repository.save(create(EMPTY, 0, NAME_1));
        final OrderTable table2 = repository.save(create(NOT_EMPTY, 1, NAME_2));

        // when
        final List<OrderTable> result = service.findAll();

        // then
        assertContainsForFindAll(result, table1, table2);
    }
}
