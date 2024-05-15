package utils.spec

import kitchenpos.domain.MenuGroup
import java.util.UUID

object MenuGroupSpec {
    fun of(): MenuGroup {
        val menuGroup = MenuGroup()
        menuGroup.id = UUID.randomUUID()
        menuGroup.name = "testGroup"

        return menuGroup
    }
}
