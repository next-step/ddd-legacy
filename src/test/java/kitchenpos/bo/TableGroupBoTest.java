package kitchenpos.bo;

import kitchenpos.bo.mock.TestOrderDao;
import kitchenpos.bo.mock.TestOrderTableDao;
import kitchenpos.bo.mock.TestTableGroupDao;
import kitchenpos.dao.Interface.OrderDao;
import kitchenpos.dao.Interface.OrderTableDao;
import kitchenpos.dao.Interface.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static kitchenpos.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableGroupBoTest {

    private OrderDao orderDao = new TestOrderDao();
    private OrderTableDao orderTableDao = new TestOrderTableDao();
    private TableGroupDao tableGroupDao = new TestTableGroupDao();

    private TableGroupBo tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);

    private TableGroup input;

    @BeforeEach
    void setUp() {
        orderTableDao.save(defaultOrderTable());
        orderTableDao.save(emptyGroupIdOrderTable());
        orderTableDao.save(emptyTrueOrderTable());

        input = new TableGroup();
        input.setId(1L);
    }

    @DisplayName("테이블 그룹 생성 시 테이블이 null 이거나 2개보다 작으면 IllegalArgumentException 발생")
    @ParameterizedTest
    @NullAndEmptySource
    @MethodSource("provideOneSizeList")
    void createLessSize(List<OrderTable> parameter) {
        input.setOrderTables(parameter);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(input));
    }

    @DisplayName("테이블 그룹 생성 시 저장된 테이블 수랑 요청 테이블 수가 다르면 IllegalArgumentException 발생")
    @Test
    void createLessSize() {
        input.setOrderTables(Arrays.asList(defaultOrderTable(), emptyGroupIdOrderTable(), emptyTrueOrderTable()));

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(input));
    }

    @DisplayName("테이블 그룹 생성 시 저장된 테이블이 비어있지 않거나 그룹아이디가 이미 있으면 IllegalArgumentException 발생")
    @ParameterizedTest
    @MethodSource("provideIllegalOrderTables")
    void createSavedTable(List<OrderTable> parameter) {
        input.setOrderTables(parameter);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(input));
    }

    @DisplayName("테이블 그룹 생성")
    @Test
    void create() {
        input.setOrderTables(Arrays.asList(emptyTrueOrderTable(), emptyTrueOrderTable()));

        TableGroup result = tableGroupBo.create(input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderTables().size()).isEqualTo(2);
        assertThat(result.getOrderTables().get(0).getTableGroupId()).isEqualTo(1L);
        assertThat(result.getOrderTables().get(0).isEmpty()).isFalse();
    }

    @DisplayName("테이블 그룹 삭제시 조리중, 식사중일 때는 삭제 불가능")
    @Test
    void deleteTableStatus() {
        orderDao.save(cookingOrder());

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(1L));
    }

    @DisplayName("테이블 그룹 삭제")
    @Test
    void delete() {
        tableGroupBo.delete(2L);
    }

    private static Stream<Arguments> provideOneSizeList() {
        return Stream.of(
                Arguments.of(Collections.singletonList(new OrderTable()))
        );
    }

    private static Stream<Arguments> provideIllegalOrderTables() {
        OrderTable sample2 = new OrderTable();
        sample2.setEmpty(true);
        sample2.setTableGroupId(2L);
        return Stream.of(
                Arguments.of(Arrays.asList(defaultOrderTable(), defaultOrderTable())),
                Arguments.of(Arrays.asList(sample2, sample2))
        );
    }
}
