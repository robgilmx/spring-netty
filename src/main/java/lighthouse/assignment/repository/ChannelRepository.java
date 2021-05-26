package lighthouse.assignment.repository;

import io.netty.channel.Channel;
import lighthouse.assignment.model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChannelRepository {
    private ConcurrentMap<Client, Channel> channelCache = new ConcurrentHashMap<>();

    public void put(Client key, Channel value) {
        channelCache.put(key, value);
    }

    public Channel get(Client key) {
        for (Map.Entry<Client, Channel> cm : channelCache.entrySet()){
            if (cm.getKey().equals(key)){
                return cm.getValue();
            }
        }
        return null;
    }

    public void remove(Client key) { this.channelCache.remove(key); }

    public int size() {
        return this.channelCache.size();
    }

    public List<Channel> getAllChannels(){
        return new ArrayList<>(channelCache.values());
    }

    public List<Client> getAllClients(){
        return new ArrayList<>(channelCache.keySet());
    }

    public void sendPublicMessage(String message){
        for (Channel channel : getAllChannels()){
            channel.writeAndFlush(message + "\n\r");
        }
    }

}
