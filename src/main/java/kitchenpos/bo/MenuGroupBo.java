package kitchenpos.bo;

import kitchenpos.dao.DefaultMenuGroupDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MenuGroupBo {
    private final DefaultMenuGroupDao menuGroupDao;

    public MenuGroupBo(final DefaultMenuGroupDao menuGroupDao) {
        this.menuGroupDao = menuGroupDao;
    }

    @Transactional
    public MenuGroup create(final MenuGroup menuGroup) {
        return menuGroupDao.save(menuGroup);
    }

    public List<MenuGroup> list() {
        return menuGroupDao.findAll();
    }
}
