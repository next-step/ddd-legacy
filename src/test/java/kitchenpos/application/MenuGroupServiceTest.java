package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @AfterEach
    void tearDown() {
        menuGroupRepository.deleteAll();
    }

    @Test
    void create() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("햄버거");

        //when
        MenuGroup res = menuGroupService.create(menuGroup);

        //then
        assertNotNull(res);
        assertEquals(menuGroup.getName(), res.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createWithEmptyName(String name) {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(menuGroup));
    }

    @Test
    void findAll() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("치킨");
        menuGroupRepository.save(menuGroup);

        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("치킨", result.get(0).getName())
        );
    }

    @Test
    void findAllWithEmptyData() {
        List<MenuGroup> result = menuGroupService.findAll();
        assertTrue(result.isEmpty());
    }
}
