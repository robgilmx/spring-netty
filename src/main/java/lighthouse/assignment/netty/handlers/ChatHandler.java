package lighthouse.assignment.netty.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lighthouse.assignment.model.Chat;
import lighthouse.assignment.model.Client;
import lighthouse.assignment.repository.ChannelRepository;
import lighthouse.assignment.service.ChatService;
import lighthouse.assignment.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
@ChannelHandler.Sharable
public class ChatHandler
        extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);


    private ChatService chatService;
    private ClientService clientService;
    private final ChannelRepository channelRepository;


    public ChatHandler(ChatService chatService, ClientService clientService, ChannelRepository channelRepository) {
        this.chatService = chatService;
        this.clientService = clientService;
        this.channelRepository = channelRepository;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");

        ctx.fireChannelActive();
        if (log.isDebugEnabled()) {
            log.debug(ctx.channel().remoteAddress() + "");
        }
        String remoteAddress = ctx.channel().remoteAddress().toString();
        ctx.writeAndFlush("Connected to the chat server.\r\n");
        ctx.writeAndFlush("Your remote address is " + remoteAddress + ".\r\n");

        if (log.isDebugEnabled()) {
            log.debug("Bound Channel Count is {}", this.channelRepository.size());
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String stringMessage = ((String) msg).trim();
        if (log.isDebugEnabled()) {
            log.debug(stringMessage);
        }

        if ( stringMessage.toLowerCase().startsWith("login ") || stringMessage.toLowerCase().startsWith("logout ")) {
            log.info("Skipped :: ChatHandler");
            ctx.fireChannelRead(msg);
            return;
        }

        String[] splitMessage = stringMessage.split("::");
        Client current = Client.current(ctx.channel());
        if (splitMessage.length != 2) {
            Chat chat = new Chat();
            chat.setSender(current);
            chat.setMessage(stringMessage);
            channelRepository.sendPublicMessage(stringMessage);
            Chat persistentChat = chatService.save(chat);
            if (log.isDebugEnabled()){
                log.debug("Chat: " + persistentChat);
            }
            return;
        }
        Client found = clientService.findByComposedUsername(splitMessage[0]);
        Chat chat = new Chat();
        chat.setSender(current);
        chat.setMessage(splitMessage[1]);
        chat.setReceiver(found);
        current.tell(channelRepository.get(found), splitMessage[0], splitMessage[1]);
        Chat persistentChat = chatService.save(chat);
        if (log.isDebugEnabled()){
            log.debug("Chat: " + persistentChat);
        }
        log.info("Message send: " + splitMessage[0]);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");

        Client currentUser = Client.current(ctx.channel());
        currentUser.logout(this.channelRepository, ctx.channel());
        currentUser.setConnected(false);
        Client disconnected = clientService.save(currentUser);

        if (log.isDebugEnabled()) {
            log.debug("Disconnected Client: " + disconnected);
            log.debug("Channel Count is " + this.channelRepository.size());
        }
    }


}