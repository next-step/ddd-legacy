package kitchenpos.menugroup

import kitchenpos.domain.MenuGroup

class MenuGroupFixture {
    companion object {
        private const val DEFAULT_NAME: String = "테스트 메뉴 그룹"

        fun fixture(
            name: String? = DEFAULT_NAME
        ): MenuGroup {
            val menuGroup = MenuGroup()
            menuGroup.name = name
            return menuGroup
        }
    }
}
