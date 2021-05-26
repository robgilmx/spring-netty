package lighthouse.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lighthouse.assignment.config.exception.UserLoggedOutException;
import lighthouse.assignment.repository.ChannelRepository;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client implements Serializable {

    @Transient
    public static final AttributeKey<Client> USER_ATTRIBUTE_KEY = AttributeKey.newInstance("USER");


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private long id;

    @Column(nullable = false)
    private String displayName;

    @Column
    private boolean connected = false;

    @OneToMany(mappedBy="sender")
    @JsonIgnore
    private List<Chat> chatsSend;

    @OneToMany(mappedBy="receiver")
    @JsonIgnore
    private List<Chat> chatsReceived;

    @Transient
    private Channel channel;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<Chat> getChatsSend() {
        return chatsSend;
    }

    public void setChatsSend(List<Chat> chatsSend) {
        this.chatsSend = chatsSend;
    }

    public List<Chat> getChatsReceived() {
        return chatsReceived;
    }

    public void setChatsReceived(List<Chat> chatsReceived) {
        this.chatsReceived = chatsReceived;
    }

    @Transient
    public Channel getChannel() {
        return channel;
    }

    @Transient
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void login(ChannelRepository channelRepository, Channel channel) {
        channel.attr(USER_ATTRIBUTE_KEY).set(this);
        channelRepository.put(this, channel);
        setChannel(channel);
    }

    public void logout(ChannelRepository channelRepository, Channel channel) {
        channel.attr(USER_ATTRIBUTE_KEY).getAndSet(null);
        setChannel(null);
        channelRepository.remove(this);
    }

    public static Client current(Channel channel) {
        Client user = channel.attr(USER_ATTRIBUTE_KEY).get();
        if ( user == null ){
            throw new UserLoggedOutException();
        }
        return user;
    }

    public void tell(Channel targetChannel, @NonNull String username, @NonNull String message) {
        if (targetChannel != null) {
            targetChannel.write(this.displayName);
            targetChannel.write(">");
            targetChannel.writeAndFlush(message + "\n\r");
            this.channel.writeAndFlush("The message was sent to ["+username+"] successfully.\r\n");
        }else{
            this.channel.writeAndFlush("No user named with ["+ username +"].\r\n");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id &&
                displayName.equals(client.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", connected=" + connected +
                '}';
    }
}
