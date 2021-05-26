package lighthouse.assignment.service;

import lighthouse.assignment.model.Chat;
import lighthouse.assignment.model.Client;
import lighthouse.assignment.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat save(Chat chat){
        return chatRepository.save(chat);
    }

    public List<Chat> findAll(){
        return chatRepository.findAll();
    }

    public List<Chat> findAllBySender(Client sender){
        return chatRepository.findAllBySender(sender);
    }
}
