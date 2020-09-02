package `fun`.wackloner.marivanna.bot.handlers

import `fun`.wackloner.marivanna.bot.*
import `fun`.wackloner.marivanna.model.Emojis
import `fun`.wackloner.marivanna.bot.commands.*
import `fun`.wackloner.marivanna.model.Operations
import `fun`.wackloner.marivanna.utils.*
import org.telegram.telegrambots.meta.api.objects.CallbackQuery


fun sendInProgress(chatId: Long) =
        Context.bot.sendUpdate(chatId, "${Emojis.SPIRAL} <i><b>4:20</b></i> ${Emojis.SPIRAL}",
                oneLineKeyboard(newButton("Menu", Operations.MENU)))

fun tryProgressAnswer(answer: String, chatId: Long): Boolean {
    val optionNum = answer.toIntOrNull() ?: return false

    val quiz = Context.forChat(chatId).currentQuiz
    if (quiz == null) {
        Context.bot.sendUpdate(chatId, "Sorry, no quizes for the moment... Wanna play, sweetie?${Emojis.WINKING}",
                mainMenuKeyboard(chatId))
        return true
    }

    if (optionNum == quiz.rightOption) {
        Context.bot.sendUpdate(chatId, "Hell yea, right (:", afterQuizKeyboard())
    } else {
        Context.bot.sendUpdate(chatId,
                "Wrong! :(\n\n${formatSingleTranslation(quiz.questionWord, quiz.options[quiz.rightOption].text)}",
                afterQuizKeyboard())
    }

    return true
}

fun defaultButtonHandler(text: String, userId: Int, chatId: Long) {
    if (tryProgressAnswer(text, chatId)) {
        return
    }

    sendInProgress(chatId)
}


fun processCallbackQuery(callbackQuery: CallbackQuery) {
    val text = callbackQuery.data
    val userId = callbackQuery.from.id
    val chatId: Long = userId.toLong()

    when (text) {
        Operations.DICTIONARY -> showDictionary(userId, chatId)
        Operations.ADD_TRANSLATION -> promptTranslation(chatId)
        Operations.MENU -> processMenu(chatId)
        Operations.TRANSLATE -> promptTranslate(chatId)
        Operations.SAVE -> processSave(userId, chatId)
        Operations.SETTINGS -> processSettings(chatId)
        Operations.QUIZ -> processQuiz(userId, chatId)
        else -> defaultButtonHandler(text, userId, chatId)
    }
}