package lighthouse.assignment.controller;

import lighthouse.assignment.config.exception.ChatException;
import lighthouse.assignment.model.Chat;
import lighthouse.assignment.model.Client;
import lighthouse.assignment.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> findAll() {
        List<Client> findAll = clientService.findAll();
        return ResponseEntity.ok(findAll);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/connected")
    public ResponseEntity<?> findAllConnected(){
        List<Client> clients = clientService.findAllConnected();
        return ResponseEntity.ok(clients);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<?> restLogout(@RequestBody Client client) {
        try {
            Client result = clientService.logout(client);
            return ResponseEntity.ok(result.toString());
        }catch (ChatException e){
            return ResponseEntity.badRequest().body(e);
        }
    }

}
