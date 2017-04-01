package cc.joyreactor

import cc.joyreactor.core.Environment
import cc.joyreactor.core.ImageRef
import cc.joyreactor.core.Post
import cc.joyreactor.core.getPosts
import com.pengrad.telegrambot.TelegramBotAdapter
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.request.SendPhoto
import java.net.URLEncoder

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    startBot(args[0]) { message ->
        when (message) {
            "posts" -> getPost()
            else ->
                "<b>Список команд бота:</b>\n• <b>posts</b> - список постов из ленты\n\nИсходный код (MIT): https://github.com/y2k/JoyReactorBot"
                    .let(::HtmlResponse)
                    .let(::listOf)
        }
    }
}

private fun getPost(): List<Response> =
    Environment()
        .getPosts()
        .map {
            val image = it.image
            when (image) {
                null -> HtmlResponse("${it.url()} - ${getTitle(it)}")
                else -> ImageResponse(image.makeRemoteCacheUrl(200, 200), "${it.url()} - ${getTitle(it)}")
            }
        }
        .filterIsInstance<ImageResponse>()
        .take(3)

private fun getTitle(it: Post): String {
    return "<b>" + (it.title ?: "") + "</b>"
}

private fun Post.url() = "http://joyreactor.cc/post/$id"

private fun startBot(token: String, callback: (String) -> List<Response>) {
    val bot = TelegramBotAdapter.build(token)
    bot.setUpdatesListener { updates ->
        updates
            .flatMap { x -> callback(x.message().text()).map { x to it } }
            .map { (update, response) ->
                val chat = update.message().chat().id()
                when (response) {
                    is HtmlResponse ->
                        SendMessage(chat, response.html).parseMode(ParseMode.HTML)
                    is ImageResponse ->
                        SendPhoto(chat, response.image).caption(response.title)
                }
            }
            .forEach { bot.execute(it) }
        UpdatesListener.CONFIRMED_UPDATES_ALL
    }
}

sealed class Response
class HtmlResponse(val html: String) : Response()
class ImageResponse(val image: String, val title: String) : Response()

fun ImageRef.makeRemoteCacheUrl(width: Int, height: Int): String {
    if (width > 200) { // FIXME:
        val h = (200 / (width.toFloat() / height)).toInt()
        return URLEncoder
            .encode(url, "UTF-8")
            .let { "$BASE_URL&width=200&height=$h&url=$it" }
    }
    return URLEncoder
        .encode(url, "UTF-8")
        .let { "$BASE_URL&width=$width&height=$height&url=$it" }
}

private const val BASE_URL = "https://rc.y2k.work/cache/fit?quality=70&bgColor=ffffff"