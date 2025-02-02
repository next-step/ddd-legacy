package kitchenpos.fixture

import kitchenpos.domain.MenuGroup

object MenuGroupFixture {
    fun create(
        name: String?,
    ): MenuGroup {
        val menuGroup = MenuGroup()
        return menuGroup.apply {
            this.name = name
        }
    }
}
