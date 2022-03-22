package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import kitchenpos.DefaultIntegrationTestConfig;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class MenuGroupServiceTest extends DefaultIntegrationTestConfig {

    private static final String NAME1 = "testMenuName1";
    private static final String NAME2 = "testMenuName2";

    @Autowired
    private MenuGroupRepository repository;

    @Autowired
    private MenuGroupService service;

    private MenuGroup create(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(UUID.randomUUID());

        return menuGroup;
    }

    private void assertReturnMenuGroup(final MenuGroup result,
        final MenuGroup request) {

        assertThat(result).usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isNotEqualTo(request.getId());
    }

    /*
    강의 진행되어 도메인에 equals()를 정의하면 이 테스트 코드도 수정한다.
     */
    private void assertContainsByIdAndName(final List<MenuGroup> results,
        final MenuGroup expResult1, final MenuGroup expResult2) {

        assertThat(results).hasSize(2);

        final MenuGroup result1;
        final MenuGroup result2;
        if (results.get(0).getId().equals(expResult1.getId())) {
            result1 = results.get(0);
            result2 = results.get(1);
        } else {
            result1 = results.get(1);
            result2 = results.get(0);
        }

        assertThat(result1).usingRecursiveComparison().isEqualTo(expResult1);
        assertThat(result2).usingRecursiveComparison().isEqualTo(expResult2);
    }

    @DisplayName("MenuGruop의 name이 null 혹은 empty이면 예외를 발생시킨다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_when_null_or_empty_name(final String name) {
        // given

        // when & then
        assertThatThrownBy(() -> service.create(create(name)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("MenuGroup을 추가할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup alreadyExistMenuGroup = repository.save(create(NAME1));

        // when
        final MenuGroup result = service.create(create(NAME2));

        // then
        final List<MenuGroup> menuGroups = repository.findAll();

        assertContainsByIdAndName(menuGroups, alreadyExistMenuGroup, result);
    }

    @DisplayName("MenuGroup을 추가하면 식별자가 부여된 결과를 반환한다.")
    @Test
    void create_return() {
        // given
        final MenuGroup request = create(NAME2);

        // when
        final MenuGroup result = service.create(request);

        // then
        assertReturnMenuGroup(result, request);
    }

    @DisplayName("name이 동일한 MenuGroup을 추가할 수 있다.")
    @Test
    void create_when_equal_name() {
        // given
        final MenuGroup alreadyExistEqualNameMenuGroup = repository.save(create(NAME1));

        // when
        final MenuGroup result = service.create(create(NAME1));

        // then
        final List<MenuGroup> menuGroups = repository.findAll();

        assertContainsByIdAndName(menuGroups, alreadyExistEqualNameMenuGroup, result);
    }

    @DisplayName("MenuGroup이 아무것도 없다면 빈 리스트를 반환한다.")
    @Test
    void findAll_when_empty() {
        // given

        // when
        final List<MenuGroup> result = service.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("MenuGroup이 있다면 그것을 반환한다.")
    @Test
    void findAll_when_exist() {
        // given
        final MenuGroup group1 = repository.save(create(NAME1));
        final MenuGroup group2 = repository.save(create(NAME2));

        // when
        final List<MenuGroup> result = service.findAll();

        // then
        assertContainsByIdAndName(result, group1, group2);
    }
}
