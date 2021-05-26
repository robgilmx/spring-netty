package lighthouse.assignment.netty.handlers;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lighthouse.assignment.config.exception.ChatException;
import lighthouse.assignment.model.Client;
import lighthouse.assignment.repository.ChannelRepository;
import lighthouse.assignment.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private final ChannelRepository channelRepository;
    private final ClientService clientService;


    public ClientHandler(ChannelRepository channelRepository, ClientService clientService) {
        this.channelRepository = channelRepository;
        this.clientService = clientService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (!(msg instanceof String) || (!((String) msg).startsWith("login ")
            && !((String) msg).startsWith("logout "))) {
            log.info("Skipped :: ClientHandler");
            ctx.fireChannelRead(msg);
            return;
        }

        String stringMessage = (String) msg;
        if (stringMessage.toLowerCase().startsWith("login ")){
            log.info("Login start");
            Client user;
            String username = stringMessage.trim().substring("login ".length());
            if (stringMessage.contains("#")){
                user = clientService.findByComposedUsername(username.toLowerCase());
                if (user == null){
                    ctx.channel().writeAndFlush("User not found: " + username);
                    throw new ChatException("Not user found for: " + stringMessage);
                }
                user.login(channelRepository, ctx.channel());
            } else {
                user = new Client();
                user.setDisplayName(username.toLowerCase());
                user = clientService.save(user);
                user.login(channelRepository, ctx.channel());
            }
            ctx.writeAndFlush("Successfully logged in as " + user.getDisplayName() + "#" + user.getId()
                    + ". \r\n");
            user.setConnected(true);
            clientService.save(user);
        } else

        if (log.isDebugEnabled()) {
            log.debug(stringMessage);
        }
        if (stringMessage.startsWith("logout ")){
            Client user;
            String username = stringMessage.trim().substring("logout ".length());
            if (stringMessage.contains("#")) {
                user = clientService.findByComposedUsername(username.toLowerCase());
                if (user == null) {
                    throw new ChatException("Not user found for: " + stringMessage);
                }
                user.logout(channelRepository, ctx.channel());
                user.setConnected(false);
                clientService.save(user);
                ctx.writeAndFlush("Successfully logged out as " + user.getDisplayName() + "#" + user.getId());
            }else {
                throw new ChatException("Please provide user name and id. Ex: Username#0000");
            }
        }

    }
}
