package lighthouse.assignment.controller;


import lighthouse.assignment.config.exception.ChatException;
import lighthouse.assignment.model.Chat;
import lighthouse.assignment.model.PrivateMessageRequest;
import lighthouse.assignment.service.ChannelService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("api/channels")
public class ChannelController {

    private ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(value = "/message/public", method = RequestMethod.POST
        , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> sendPublicMessage(@RequestBody Chat chat){
        try {
            channelService.sendPublicMessage(chat.getMessage());
        }catch (ChatException c){
            return ResponseEntity.badRequest().body(c);
        }
        return ResponseEntity.ok(Collections.singletonMap("response", "Message send."));
    }

    @RequestMapping(value = "/message/private", method = RequestMethod.POST
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> sendPublicMessage(@RequestBody PrivateMessageRequest pmr){
        try {
            channelService.sendPrivateMessage(pmr.getClient(), pmr.getMessage());
        }catch (ChatException c){
            return ResponseEntity.badRequest().body(c);
        }
        return ResponseEntity.ok(Collections.singletonMap("response", "Message send to: "
                + pmr.getClient().getDisplayName() + "#" + pmr.getClient().getId()));
    }

    @RequestMapping(value = "/clients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getChannelClients(){
        try {
            return ResponseEntity.ok(channelService.getActiveClients());
        }catch (ChatException c){
            return ResponseEntity.badRequest().body(c);
        }
    }

}
