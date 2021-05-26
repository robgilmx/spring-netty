package lighthouse.assignment.service;

import io.netty.channel.Channel;
import lighthouse.assignment.config.exception.ChatException;
import lighthouse.assignment.model.Client;
import lighthouse.assignment.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void sendPublicMessage(String message){
        if (channelRepository.size() > 0){
            channelRepository.sendPublicMessage("> API: " + message);
        }else{
            throw new ChatException("There is no active channels in the Chat.");
        }
    }

    public void sendPrivateMessage(Client client, String message){
        Channel channel = channelRepository.get(client);
        if (channel != null){
            channel.writeAndFlush("> API: " + message);
        }else{
            throw new ChatException("There is no active channels for that User (The user is not logged in).");
        }
    }

    public List<Client> getActiveClients(){
        if (channelRepository.size() > 0){
            return channelRepository.getAllClients();
        }else{
            throw new ChatException("There is no active channels in the Chat.");
        }
    }
}
