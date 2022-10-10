package ru.work.status.Telegram.botCommand;

import ru.work.status.Telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Component
public class CreateMessage implements Consumer<Message> {

    private final String textForMessage = "За компьютером:\n\nНа телефоне:\n\nВне зоны доступа:\n\n";
    private final List<String> listButton = Arrays.asList("За компьютером", "На телефоне", "Вне зоны доступа", "Ушел");
    private int buttonInLine = 3;

    @Autowired
    @Lazy
    private TelegramBot telegramBot;

    @Override
    public void accept(Message message) {
        telegramBot.createAndSendInlineKeyboard(message.getChatId().toString()
                , textForMessage
                , listButton
                , buttonInLine);
    }
}
