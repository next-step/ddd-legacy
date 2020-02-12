package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    private TableGroup input;
    private TableGroup saved;
    private List<OrderTable> savedOrderTables;

    @BeforeEach
    void setUp() {
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(1L);
        orderTable1.setEmpty(true);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(2L);
        orderTable2.setEmpty(true);

        savedOrderTables = new ArrayList<>();
        savedOrderTables.add(orderTable1);
        savedOrderTables.add(orderTable2);

        input = new TableGroup();
        input.setOrderTables(Arrays.asList(orderTable1, orderTable2));

        saved = new TableGroup();
        saved.setId(1L);
        saved.setOrderTables(Arrays.asList(orderTable1, orderTable2));
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
        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(Arrays.asList(new OrderTable()));

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(input));
    }

    @DisplayName("테이블 그룹 생성 시 저장된 테이블이 비어있지 않거나 그룹아이디가 이미 있으면 IllegalArgumentException 발생")
    @ParameterizedTest
    @MethodSource("provideIllegalOrderTables")
    void createSavedTable(List<OrderTable> parameter) {
        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(parameter);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(input));
    }

    @DisplayName("테이블 그룹 생성")
    @Test
    void create() {
        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(savedOrderTables);

        given(tableGroupDao.save(input))
                .willReturn(saved);

        TableGroup result = tableGroupBo.create(input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderTables().size()).isEqualTo(2);
        assertThat(result.getOrderTables().get(0).getTableGroupId()).isEqualTo(1L);
        assertThat(result.getOrderTables().get(0).isEmpty()).isFalse();
    }

    @DisplayName("테이블 그룹 삭제시 조리중, 식사중일 때는 삭제 불가능")
    @Test
    void deleteTableStatus() {
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(savedOrderTables);

        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(1L));
    }

    @DisplayName("테이블 그룹 삭제")
    @Test
    void delete() {
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(savedOrderTables);

        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(false);

        tableGroupBo.delete(1L);
    }

    private static Stream<Arguments> provideOneSizeList() {
        return Stream.of(
                Arguments.of(Collections.singletonList(new OrderTable()))
        );
    }

    private static Stream<Arguments> provideIllegalOrderTables() {
        OrderTable sample1 = new OrderTable();
        sample1.setEmpty(false);

        OrderTable sample2 = new OrderTable();
        sample2.setEmpty(true);
        sample2.setTableGroupId(2L);
        return Stream.of(
                Arguments.of(Arrays.asList(sample1, sample1)),
                Arguments.of(Arrays.asList(sample2, sample2))
        );
    }
}
