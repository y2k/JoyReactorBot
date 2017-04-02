package cc.joyreactor

import cc.joyreactor.core.Environment
import cc.joyreactor.core.ImageRef
import cc.joyreactor.core.Post
import cc.joyreactor.core.getPosts
import java.lang.System.getenv
import java.net.URLEncoder

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    startBot(getenv("TOKEN")) { message ->
        match(message) {
            test("posts ([а-яёА-ЯЁ\\w\\d_]+)") { handleGetPosts(it.groupValues[1]) }
            test("posts") { handleGetPosts("") }
            test {
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

private fun handleGetPosts(tag: String) =
    getPosts(tag).let { (posts, next) ->
        posts to Markup("Next page", "$next")
    }

private fun getPosts(tag: String): Pair<List<Response>, Int> =
    Environment()
        .getPosts(tag)
        .map {
            val image = it.image
            when (image) {
                null -> HtmlResponse(makePostTitle(it))
                else -> ImageResponse(image.makeRemoteCacheUrl(500, 500), makePostTitle(it))
            }
        }
        .take(3) to 0

private fun makePostTitle(it: Post) = "${url(it)}${getTitle(it)}"
private fun url(post: Post) = "http://joyreactor.cc/post/${post.id}"
private fun getTitle(post: Post) = post.title?.let { "\n$it" } ?: ""

fun ImageRef.makeRemoteCacheUrl(width: Int, height: Int): String =
    URLEncoder
        .encode(url, "UTF-8")
        .let { "$BASE_URL&width=$width&height=$height&url=$it" }

private const val BASE_URL = "https://rc.y2k.work/cache/fit?quality=100&bgColor=ffffff"