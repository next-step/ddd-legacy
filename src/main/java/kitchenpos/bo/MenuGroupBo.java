package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MenuGroupBo {
    private final MenuGroupDao menuGroupDao;

    public MenuGroupBo(final MenuGroupDao menuGroupDao) {
        this.menuGroupDao = menuGroupDao;
    }

    /**
     * 메뉴그룹 생성
     *
     * @param menuGroup
     * @return
     */
    @Transactional
    public MenuGroup create(final MenuGroup menuGroup) {
        return menuGroupDao.save(menuGroup);
    }

    /**
     * 전체 메뉴그룹 리스트 조회
     *
     * @return
     */
    public List<MenuGroup> list() {
        return menuGroupDao.findAll();
    }
}
