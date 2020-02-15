package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.TestMenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        MenuGroup actual = 야식();

        //when
        MenuGroup expected = menuGroupBo.create(actual);

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.getId()).isEqualTo(actual.getId()),
                () -> assertThat(expected.getId()).isEqualTo(actual.getId())
        );
    }

    @Test
    @DisplayName(value = "전체 메뉴 그룹 리스트를 조회 할 수 있다.")
    void listTest() {
        //given
        MenuGroup actual = menuGroupBo.create(야식());

        //when
        List<MenuGroup> expected = menuGroupBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.stream().anyMatch(i -> {
                    Long expectedId = i.getId();
                    Long actualId = actual.getId();

                    return expectedId.equals(actualId);
                }))
        );
    }
}
