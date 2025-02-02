package kitchenpos.menugroup

import kitchenpos.domain.MenuGroup

class MenuGroupFixture {
    companion object {
        fun fixture(
            name: String? = "테스트 메뉴 그룹"
        ): MenuGroup {
            val menuGroup = MenuGroup()
            menuGroup.name = name
            return menuGroup
        }
    }
}
