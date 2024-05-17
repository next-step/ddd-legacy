package kitchenpos.testsupport

import kitchenpos.domain.MenuGroup

object MenuGroupFixtures {
    fun createMenuGroup(
        name: String? = "test-menu-gruop-name"
    ): MenuGroup {
        return MenuGroup().apply {
            this.id = id
            this.name = name

        }
    }
}