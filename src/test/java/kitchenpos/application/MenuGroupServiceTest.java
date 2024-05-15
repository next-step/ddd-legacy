package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.testfixture.InMemoryMenuGroupRepository;
import kitchenpos.testfixture.MenuGroupTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp(){
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    public void create(){

        //given
        MenuGroup request = MenuGroupTestFixture.createMenuGroupRequest("name");

        //when
        MenuGroup response = menuGroupService.create(request);

        //then
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void findAll(){

        //given
        MenuGroup group1 = MenuGroupTestFixture.createMenuGroup(UUID.randomUUID(), "name1");
        MenuGroup group2 = MenuGroupTestFixture.createMenuGroup(UUID.randomUUID(), "name2");

        menuGroupRepository.save(group1);
        menuGroupRepository.save(group2);

        //when
        List<MenuGroup> response = menuGroupService.findAll();

        //then
        assertEquals(2, response.size());
        assertThat(response
                .stream()
                .anyMatch(res -> res.getName().contains(group1.getName())))
                .isTrue();
        assertThat(response
                .stream()
                .anyMatch(res -> res.getName().contains(group2.getName())))
                .isTrue();


    }


}