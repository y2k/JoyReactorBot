package cc.joyreactor

/**
 * Created by y2k on 02/04/2017.
 **/

fun <T : Any> match(x: String, init: Matcher<T>.() -> Unit): T =
    Matcher<T>().apply(init).find(x)

class Matcher<T : Any> {

    private val templates = ArrayList<Pair<Regex, (List<String>) -> T>>()

    fun case(regex: String, action: (List<String>) -> T) = templates.add(Regex(regex) to action)
    fun default(action: (List<String>) -> T) = templates.add(Regex("(.+)") to action)

    fun find(input: String): T = templates
        .asSequence()
        .mapNotNull { (r, f) -> r.find(input)?.let { f(it.groupValues.drop(1)) } }
        .first()
}