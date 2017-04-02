package cc.joyreactor

import cc.joyreactor.core.*
import java.lang.System.getenv
import java.net.URLEncoder

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    startBot(getenv("TOKEN")) { message ->
        match(message) {
            case("posts +([а-яёА-ЯЁ\\w\\d_ ]+) +(\\d+)") { (tag, page) -> handleGetPosts(tag, page.toInt()) }
            case("posts +(\\d+)") { (page) -> handleGetPosts(page = page.toInt()) }
            case("posts +([а-яёА-ЯЁ\\w\\d_ ]+)") { (tag) -> handleGetPosts(tag) }
            case("posts") { handleGetPosts() }
            default {
                """<b>Список команд бота:</b>
• <b>posts</b> - список постов из ленты
• <b>posts [tag]</b> - список постов тега

Исходный код (MIT):
https://github.com/y2k/JoyReactorBot - бот
https://github.com/y2k/JoyReactorCore - парсер"""
                    .let(::HtmlResponse)
                    .let(::listOf) to null
            }
        }
    }
}

private fun handleGetPosts(tag: String? = null, page: Int? = null): Pair<List<Response>, Markup> {
    val source = when (tag) {
        null -> FeaturedSource
        else -> TagSource(tag)
    }
    val result = getPosts(source, page)
    return result.first to Markup("Next page", "posts ${tag ?: ""} ${result.second}")
}

private fun getPosts(source: Source, page: Int?): Pair<List<Response>, Int?> {
    val response = Environment().get(source, page)
    return response
        .posts
        .map {
            val image = it.image
            when (image) {
                null -> HtmlResponse(makePostTitle(it))
                else -> ImageResponse(image.makeRemoteCacheUrl(500, 500), makePostTitle(it))
            }
        }
        .take(5) to response.nextPage
}

private fun makePostTitle(it: Post) = "${url(it)}${getTitle(it)}"
private fun url(post: Post) = "http://joyreactor.cc/post/${post.id}"
private fun getTitle(post: Post) = post.title?.let { "\n$it" } ?: ""

private fun ImageRef.makeRemoteCacheUrl(width: Int, height: Int): String =
    URLEncoder
        .encode(url, "UTF-8")
        .let { "$BASE_URL&width=$width&height=$height&url=$it" }

private const val BASE_URL = "https://rc.y2k.work/cache/fit?quality=100&bgColor=ffffff"