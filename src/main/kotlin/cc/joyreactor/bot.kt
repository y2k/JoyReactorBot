package cc.joyreactor

import com.pengrad.telegrambot.TelegramBotAdapter
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.request.SendPhoto

/**
 * Created by y2k on 02/04/2017.
 **/

fun startBot(token: String, onUpdate: (String) -> Pair<List<Response>, Markup?>) {
    val bot = TelegramBotAdapter.build(token)
    bot.setUpdatesListener { updates ->
        try {
            updates
                .flatMap { x ->
                    val (text, id) = when {
                        x.callbackQuery() != null -> {
                            bot.execute(AnswerCallbackQuery(x.callbackQuery().id()))
                            x.callbackQuery().data() to x.callbackQuery().from().id()
                        }
                        else -> x.message().text() to x.message().from().id()
                    }

                    println("text = $text")

                    val (items, callback) = onUpdate(text)
                    items.mapIndexed { i, x ->
                        if (i == items.size - 1) Request(x, id, callback)
                        else Request(x, id, null)
                    }
                }
                .map { (data, chat, markup) ->
                    val request = when (data) {
                        is HtmlResponse ->
                            SendMessage(chat, data.html).parseMode(ParseMode.HTML)
                        is ImageResponse ->
                            SendPhoto(chat, data.image).caption(data.title)
                    }
                    if (markup != null)
                        request.replyMarkup(InlineKeyboardMarkup(
                            arrayOf(InlineKeyboardButton(markup.title).callbackData(markup.id))
                        ))
                    request
                }
                .forEach {
                    try {
                        bot.execute(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        UpdatesListener.CONFIRMED_UPDATES_ALL
    }
}

sealed class Response
class HtmlResponse(val html: String) : Response()
class ImageResponse(val image: String, val title: String) : Response()

data class Request(val response: Response, val chat: Int, val markup: Markup?)
class Markup(val title: String, val id: String)