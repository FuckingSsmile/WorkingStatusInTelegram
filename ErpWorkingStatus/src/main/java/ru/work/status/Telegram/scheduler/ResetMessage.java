package ru.work.status.Telegram.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.work.status.Telegram.TelegramBot;

import java.util.Arrays;
import java.util.List;

@Component
public class ResetMessage {

    @Autowired
    @Lazy
    private TelegramBot telegramBot;
    @Value("${chat.prod}")
    private String prodChat;

    private final String textForMessage = "За компьютером:\n\nНа телефоне:\n\nВне зоны доступа:\n\n";
    private final List<String> listButton = Arrays.asList("За компьютером", "На телефоне", "Вне зоны доступа", "Ушел");
    private int buttonInLine = 3;

    @Scheduled(cron = "0 0 8 * * *", zone = "Europe/Moscow")
    private void recreateMessage() {

        Integer messageId = TelegramBot.messageForDelete.getMessageId();

        telegramBot.deleteMessage(messageId, prodChat);

        telegramBot.createAndSendInlineKeyboard(prodChat
                , textForMessage
                , listButton
                , buttonInLine);
    }
}