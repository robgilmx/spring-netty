package lighthouse.assignment.service;

import lighthouse.assignment.config.exception.ChatException;
import lighthouse.assignment.model.Client;
import lighthouse.assignment.repository.ChannelRepository;
import lighthouse.assignment.repository.ClientRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ChannelRepository channelRepository;

    public ClientService(ClientRepository clientRepository, ChannelRepository channelRepository) {
        this.clientRepository = clientRepository;
        this.channelRepository = channelRepository;
    }

    public Client findByComposedUsername(String composedUsername){
        String[] parts = composedUsername.split("#");
        return clientRepository.findByIdAndDisplayName(Long.valueOf(parts[1]), parts[0]);
    }

    public Client findByIdAndDisplayName(Long id, String displayName){
        return clientRepository.findByIdAndDisplayName(id, displayName);
    }

    public Client findById(Long id) throws ChatException {
        return clientRepository.findById(id).orElseThrow(() -> new ChatException("Hey"));
    }

    public Client findByDisplayName(String name){
        return clientRepository.findFirstByDisplayName(name);
    }


    public Client save(Client client){
        return clientRepository.save(client);
    }

    public List<Client> findAll(){
        return clientRepository.findAll();
    }

    public List<Client> findAllConnected() {
        for (Client client : channelRepository.getAllClients()){
            save(client);
        }
        return clientRepository.findAllByConnected(true); }


    public Client logout(Client client) throws IllegalArgumentException {
            Client user = clientRepository.findByIdAndDisplayName(client.getId(), client.getDisplayName());
            if (user == null) {
                throw new ChatException("Not user found for: " + client);
            }
            channelRepository.remove(client);
            user.setConnected(false);
            return clientRepository.save(user);
    }
}
