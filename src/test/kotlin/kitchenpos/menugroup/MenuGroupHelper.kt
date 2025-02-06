package kitchenpos.menugroup

import io.restassured.RestAssured
import kitchenpos.domain.MenuGroup

class MenuGroupHelper {
    companion object {
        fun 메뉴그룹_이름으로_메뉴_그룹_조회(name: String): MenuGroup {
            return RestAssured
                .get("/api/menu-groups")
                .then().log().all().extract().`as`(Array<MenuGroup>::class.java)
                .find { it.name == name }!!
        }
    }
}
