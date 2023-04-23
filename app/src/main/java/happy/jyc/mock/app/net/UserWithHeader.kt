package happy.jyc.mock.app.net

data class UserWithHeader(
    val user: User,
    val header: List<Pair<String, String>>
) {
    override fun toString(): String =
        StringBuilder(user.toString()).apply {
            this.append("\n")
            header.forEach {
                this.append("${it.first}:${it.second}\n")
            }
        }.toString()
}