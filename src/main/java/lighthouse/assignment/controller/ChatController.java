package lighthouse.assignment.controller;


import lighthouse.assignment.config.exception.ChatException;
import lighthouse.assignment.model.Chat;
import lighthouse.assignment.service.ChatService;
import lighthouse.assignment.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/chats")
public class ChatController {


    private final static Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final ClientService clientService;

    public ChatController(ChatService chatService, ClientService clientService) {
        this.chatService = chatService;
        this.clientService = clientService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> findAll() {
        List<Chat> findAll = chatService.findAll();
        return ResponseEntity.ok(findAll);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sender")
    public ResponseEntity<?> findAllBySender(@RequestParam("displayName") Optional<String> displayName,
                                             @RequestParam("id") Optional<Long> id) {
         try {
             List<Chat> chats;
             if (!displayName.isPresent() && !id.isPresent()){
                 log.error("Not sender provided");
                 chats = chatService.findAll();
             }else if (displayName.isPresent() && !id.isPresent()){
                 chats = chatService.findAllBySender(clientService.findByDisplayName(displayName.get()));
             }else if (!displayName.isPresent()){
                 chats = chatService.findAllBySender(clientService.findById(id.get()));
             }else{
                 chats = chatService.findAllBySender(clientService.findByIdAndDisplayName(id.get(), displayName.get()));
             }
             return ResponseEntity.ok(chats);
         } catch (NullPointerException | ChatException e) {
             return ResponseEntity.badRequest().body(e);
         }
     }
}
