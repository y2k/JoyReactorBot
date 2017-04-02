package cc.joyreactor

/**
 * Created by y2k on 02/04/2017.
 **/

fun <T : Any> match(x: String, init: Matcher<T>.() -> Unit): T =
    Matcher<T>().apply(init).find(x)

class Matcher<T : Any> {

    private val templates = ArrayList<Pair<Regex, (MatchResult) -> T>>()

    fun test(regex: String, action: (MatchResult) -> T) = templates.add(Regex(regex) to action)
    fun test(action: (MatchResult) -> T) = templates.add(Regex("(.+)") to action)
    fun find(input: String): T = templates
        .mapNotNull { (r, f) -> r.find(input)?.let { f(it) } }
        .first()
}