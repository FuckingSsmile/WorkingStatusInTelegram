package ru.work.status.Telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.work.status.Telegram.botCommand.CreateMessage;
import ru.work.status.Telegram.botCommand.GetChatId;
import ru.work.status.Telegram.botCommand.TestCommand;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private HashMap<String, Consumer<Message>> commandMap = new HashMap<>();
    private final List<String> listButton = Arrays.asList("За компьютером", "На телефоне", "Вне зоны доступа", "Ушел");

    //Бот
    @Value("${bot.userName}")
    private String userName;
    @Value("${bot.token}")
    private String token;
    @Value("${chat.prod}")
    public String prodChat;

    public static Message messageForDelete;

    @Autowired
    public TelegramBot(GetChatId getChatId, CreateMessage createMessage, TestCommand testCommand) {
        commandMap.put("/getchatid", getChatId);
        commandMap.put("/createmessage", createMessage);
        commandMap.put("/testcommand", testCommand);
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }


    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();

        if (update.hasMessage()) {

            if (message.hasText()) {

                if (message.hasText() & message.getChatId().toString().equalsIgnoreCase(prodChat)) {

                    String textHasMessage = message.getText().replace(getBotUsername(), "");

                    Consumer<Message> messageConsumer = commandMap.get(textHasMessage);

                    if (messageConsumer != null) {

                        messageConsumer.accept(message);
                    }
                }
            }
        }

        if (update.hasCallbackQuery()) {

            Message messageCallBack = update.getCallbackQuery().getMessage();

            String textMessage = update.getCallbackQuery().getMessage().getText();
            Integer messageId2 = messageCallBack.getMessageId();
            String answerData = update.getCallbackQuery().getData();

            String user = update.getCallbackQuery().getFrom().getUserName();

            String userName = user != null ? "@" + user : update.getCallbackQuery().getFrom().getFirstName();

            if (answerData.equalsIgnoreCase("Ушел")) {
                editTextForMessageGoAway(textMessage, userName, messageId2);
            } else {
                editTextForMessage(textMessage, answerData + ":", userName, messageId2);
            }
        }
    }

    private void editTextForMessageGoAway(String textMessage, String userName, int messageId) {
        textMessage = textMessage.replace("\n" + userName, "");
        editMessageInlineKeyboard(prodChat, messageId, createInlineKeyboardButton(), textMessage);
    }

    private void editTextForMessage(String textMessage, String textStatus, String userName, int messageId) {

        if (textMessage.contains(userName)) {
            textMessage = textMessage.replace("\n" + userName, "").replace(textStatus, textStatus + "\n" + userName);
        } else {
            textMessage = textMessage.replace(textStatus, textStatus + "\n" + userName);
        }
        editMessageInlineKeyboard(prodChat, messageId, createInlineKeyboardButton(), textMessage);
    }

    public void sendMessageText(String chatId, String text) {
        try {

            execute(SendMessage.builder().chatId(chatId).text(text).build());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(Integer messageId, String chatId) {

        try {
            execute(DeleteMessage.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessageInlineKeyboard(String chatId, Integer messageId, InlineKeyboardMarkup replyInlineKeyboard, String text) {

        try {
            execute(DeleteMessage.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .build());

            messageForDelete = execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(replyInlineKeyboard)
                    .build());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public InlineKeyboardMarkup createInlineKeyboardButton() {

        List<InlineKeyboardButton> buttons = listButton.stream()
                .map(text -> InlineKeyboardButton.builder()
                        .text(text)
                        .callbackData(text)
                        .build())
                .collect(Collectors.toList());

        return createInlineKeyboard(buttons, 3);
    }

    public InlineKeyboardMarkup createInlineKeyboard(List<InlineKeyboardButton> buttons, double buttonInLine) {

        double rowsCount = Math.ceil(buttons.size() / buttonInLine);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        Iterator<InlineKeyboardButton> iterator = buttons.iterator();

        for (int i = 0; i < rowsCount; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(row);
            for (int j = 0; j < buttonInLine & iterator.hasNext(); j++) {
                row.add(iterator.next());
            }
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public void createAndSendInlineKeyboard(String chatId, String message, List<String> TextForButtons, double buttonInLine) {

        List<InlineKeyboardButton> buttons = TextForButtons.stream()
                .map(text -> InlineKeyboardButton.builder()
                        .text(text)
                        .callbackData(text)
                        .build())
                .collect(Collectors.toList());

        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboard(buttons, buttonInLine);

        try {
            messageForDelete = execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .replyMarkup(inlineKeyboardMarkup)
                    .build());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
