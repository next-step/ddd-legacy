package kitchenpos.da;

import kitchenpos.dao.DefaultMenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MenuGroupDaoTest {

    @Autowired
    private DefaultMenuGroupDao menuGroupDao;

    @DisplayName("저장 테스트")
    @Test
    void save() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("신메뉴");
        MenuGroup result = menuGroupDao.save(menuGroup);

        assertThat(result.getName()).isEqualTo("신메뉴");
    }

    @DisplayName("리스트 반환테스트")
    @Test
    void findAll() {
        List<MenuGroup> result = menuGroupDao.findAll();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getName()).isEqualTo("두마리메뉴");
    }

    @DisplayName("id로 조회 테스트")
    @Test
    void findById() {
        MenuGroup result = menuGroupDao.findById(1L).get();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("두마리메뉴");
    }

    @DisplayName("id로 DB에 존재하는지 테스트")
    @Test
    void existsById() {
        assertThat(menuGroupDao.existsById(1L)).isTrue();
    }
}
