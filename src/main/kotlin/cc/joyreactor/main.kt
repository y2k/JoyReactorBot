package cc.joyreactor

import cc.joyreactor.core.Environment
import cc.joyreactor.core.ImageRef
import cc.joyreactor.core.Post
import cc.joyreactor.core.getPosts
import java.net.URLEncoder

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    startBot(args[0]) { message ->
        when (message) {
            "/posts" -> getPost() to Markup("Next page", "/posts")
            else ->
                "<b>Список команд бота:</b>\n• <b>/posts</b> - список постов из ленты\n\nИсходный код (MIT): https://github.com/y2k/JoyReactorBot"
                    .let(::HtmlResponse)
                    .let(::listOf) to null
        }
    }
}

private fun getPost(): List<Response> =
    Environment()
        .getPosts()
        .map {
            val image = it.image
            when (image) {
                null -> HtmlResponse(makePostTitle(it))
                else -> ImageResponse(image.makeRemoteCacheUrl(500, 500), makePostTitle(it))
            }
        }
        .filterIsInstance<ImageResponse>()
        .take(2)

private fun makePostTitle(it: Post) = "${it.url()} - ${getTitle(it)}"
private fun getTitle(it: Post) = (it.title ?: "")
private fun Post.url() = "http://joyreactor.cc/post/$id"

fun ImageRef.makeRemoteCacheUrl(width: Int, height: Int): String =
    URLEncoder
        .encode(url, "UTF-8")
        .let { "$BASE_URL&width=$width&height=$height&url=$it" }

private const val BASE_URL = "https://rc.y2k.work/cache/fit?quality=100&bgColor=ffffff"