package kitchenpos.testsupport

import java.util.UUID
import kitchenpos.domain.MenuGroup

object MenuGroupFixtures {
    fun createMenuGroup(
        id: UUID? = UUID.randomUUID(),
        name: String? = "test-menu-group-name"
    ): MenuGroup {
        return MenuGroup().apply {
            this.id = id
            this.name = name

        }
    }
}