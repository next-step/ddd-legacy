package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.TableGroup;
import kitchenpos.model.TableGroupBuilder;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Collections;

import static kitchenpos.bo.Fixture.단체테이블2;
import static kitchenpos.bo.Fixture.만석인_일번테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class TableGroupBoTest {

    private final OrderDao orderDao = new TestOrderDao();
    private final OrderTableDao orderTableDao = new TestOrderTableDao();
    private final TableGroupDao tableGroupDao = new TestTableGroupDao();

    private TableGroupBo tableGroupBo;

    @BeforeEach
    void setUp() {
        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);
    }

    @Nested
    @DisplayName("테이블 그룹 생성 테스트")
    class TableGroupCreateTest {

        @Test
        @DisplayName("새로운 테이블 그룹을 생성 할 수 있다.")
        void create() {
            //given
            TableGroup expected = 단체테이블2();

            //when
            TableGroup actual = tableGroupBo.create(expected);

            //then
            Assertions.assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getCreatedDate()).isEqualTo(expected.getCreatedDate())
            );
        }

        @Test
        @DisplayName("테이블 그룹 생성 시 1개 이상의 주문 테이블이 있어야 한다.")
        void create2() {
            //given
            TableGroup expected = TableGroupBuilder
                    .aTableGroup()
                    .withId(1L)
                    .withOrderTables(Collections.emptyList())
                    .withCreatedDate(LocalDateTime.now())
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.create(expected));
        }

        @Test
        @DisplayName("공석인 테이블만 테이블 그룹에 추가 가능하다")
        void create3() {
            //given
            TableGroup expected = TableGroupBuilder
                    .aTableGroup()
                    .withId(1L)
                    .withOrderTables(Collections.singletonList(만석인_일번테이블()))
                    .withCreatedDate(LocalDateTime.now())
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.create(expected));
        }
    }

    @Test
    @DisplayName("테이블 그룹을 삭제 할 수 있다.")
    void delete() {
        //given
        TableGroup expected = tableGroupBo.create(단체테이블2());

        //when
        tableGroupBo.delete(expected.getId());

        //then
        assertThat(orderTableDao.findAllByTableGroupId(expected.getId())).isEmpty();
    }
}