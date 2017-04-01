package cc.joyreactor

import com.pengrad.telegrambot.TelegramBotAdapter
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    startBot(args[0]) {
        when (it.message().text()) {
            "posts" -> getPost()
            else -> "<b>Список команд бота:</b>\n• <b>posts</b> - список постов из ленты"
        }
    }
}

private fun getPost(): String {
    TODO()
}

private fun startBot(token: String, callback: (Update) -> String) {
    val bot = TelegramBotAdapter.build(token)
    bot.setUpdatesListener { updates ->
        updates
            .map { it to callback(it) }
            .forEach { (update, response) ->
                bot.execute(SendMessage(update.message().from().id(), response).parseMode(ParseMode.HTML))
            }
        UpdatesListener.CONFIRMED_UPDATES_ALL
    }
}