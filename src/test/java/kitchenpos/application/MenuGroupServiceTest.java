package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import javax.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹 등록 - 등록 성공")
    @Test
    void createMenuGroupSuccess() {
        // Give
        MenuGroup request = new MenuGroup();
        request.setName("오늘 추천 메뉴");
        // When
        MenuGroup result = menuGroupService.create(request);

        // Then
        MenuGroup data  = menuGroupRepository.findById(result.getId()).orElseThrow(NoSuchElementException::new);

        assertThat(data.getId()).isEqualTo(result.getId());
    }

    @DisplayName("메뉴 그룹 등록 - 등록 실패")
    @ParameterizedTest
    @NullSource @EmptySource
    void createMenuGroupFail(String name) {
        // Give
        MenuGroup request = new MenuGroup();
        request.setName(name);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @DisplayName("메뉴 그룹 조회")
    @Test
    void findAllMenuGroup() {
        // Give
        MenuGroup request1 = new MenuGroup();
        request1.setName("오후 2시 잘팔리는 메뉴");

        MenuGroup request2 = new MenuGroup();
        request2.setName("추천 메뉴");

        menuGroupService.create(request1);
        menuGroupService.create(request2);

        // When
        List<MenuGroup> resultList = menuGroupService.findAll();

        // Then
        assertThat(resultList).hasSize(6);
    }
}
