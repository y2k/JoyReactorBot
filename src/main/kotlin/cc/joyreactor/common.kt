package cc.joyreactor

import cc.joyreactor.core.ImageRef
import java.net.URLEncoder

/**
 * Created by y2k on 02/04/2017.
 **/

fun ImageRef.makeRemoteCacheUrl(width: Int, height: Int): String =
    URLEncoder
        .encode(url, "UTF-8")
        .let { "$BASE_URL&width=$width&height=$height&url=$it" }

private const val BASE_URL = "https://rc.y2k.work/cache/fit?quality=100&bgColor=ffffff"

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