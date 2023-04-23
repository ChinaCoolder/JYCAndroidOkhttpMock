package happy.jyc.mock.app.net

data class User(
    val name: String,
    val age: Int,
    val male: Boolean
) {
    override fun toString(): String {
        return "name:${name},age:${age},male:${if (male) "male" else "female"}"
    }
}