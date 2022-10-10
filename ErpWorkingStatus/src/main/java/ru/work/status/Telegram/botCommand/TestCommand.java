package ru.work.status.Telegram.botCommand;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
//import ru.work.status.Telegram.TelegramBot;
//import ru.work.status.Telegram.scheduler.ResetMessage;

import java.util.function.Consumer;

@Component
public class TestCommand implements Consumer<Message> {

//    @Autowired
//    @Lazy
//    private TelegramBot telegramBot;
//    @Autowired
//    private ResetMessage resetMessage;

    @Override
    public void accept(Message message) {
//        resetMessage.recreateMessage();
    }
}