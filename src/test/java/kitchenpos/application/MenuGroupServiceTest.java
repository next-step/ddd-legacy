package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository repository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    public void create(){

        //given
        MenuGroup request = new MenuGroup();
        request.setName("name");

        given(repository.save(any(MenuGroup.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        //when
        MenuGroup response = menuGroupService.create(request);

        //then
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void findAll(){

        //given
        MenuGroup group1 = new MenuGroup();
        group1.setName("name1");
        group1.setId(UUID.randomUUID());

        MenuGroup group2 = new MenuGroup();
        group2.setName("name2");
        group2.setId(UUID.randomUUID());


        given(repository.findAll())
                .willReturn(Arrays.asList(group1, group2));

        //when
        List<MenuGroup> response = menuGroupService.findAll();

        //then
        assertEquals(2, response.size());


    }


}