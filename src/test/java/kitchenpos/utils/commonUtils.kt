package kitchenpos.utils

import java.math.BigInteger
import java.util.UUID

fun generateUUIDFrom(uuidWithoutDash: String): UUID {
    val bi1 = BigInteger(uuidWithoutDash.substring(0, 16), 16)
    val bi2 = BigInteger(uuidWithoutDash.substring(16, 32), 16)
    val uuid = UUID(bi1.toLong(), bi2.toLong())
    return uuid
}
