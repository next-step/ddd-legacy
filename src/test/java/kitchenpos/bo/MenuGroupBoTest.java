package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.TestMenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static kitchenpos.bo.Fixture.야식;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuGroupBoTest {

    private final MenuGroupDao menuGroupDao = new TestMenuGroupDao();
    private MenuGroupBo menuGroupBo;

    @BeforeEach
    void init() {
        menuGroupBo = new MenuGroupBo(menuGroupDao);
    }

    @Test
    @DisplayName(value = "새로운 메뉴 그룹을 생성 할 수 있다..")
    void createTest() {
        //given
        MenuGroup expected = 야식();

        //when
        MenuGroup actual = menuGroupBo.create(expected);

        //then
        Assertions.assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.getId()).isEqualTo(expected.getId())
        );
    }

    @Test
    @DisplayName(value = "전체 메뉴 그룹 리스트를 조회 할 수 있다.")
    void listTest() {
        //given
        MenuGroup expected = menuGroupBo.create(야식());

        //when
        List<MenuGroup> actual = menuGroupBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(Collections.singletonList(expected))
        );
    }
}