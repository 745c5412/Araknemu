package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.chat.channel.Channel;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddChatChannels;
import fr.quatrevieux.araknemu.game.event.listener.service.RegisterChatListeners;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service for handle chat system
 */
final public class ChatService implements PreloadableService {
    final private ListenerAggregate dispatcher;
    final private GameConfiguration.ChatConfiguration configuration;

    final private Map<ChannelType, Channel> channels = new EnumMap<>(ChannelType.class);

    public ChatService(ListenerAggregate dispatcher, GameConfiguration.ChatConfiguration configuration, Channel[] channels) {
        this.dispatcher = dispatcher;
        this.configuration = configuration;

        for (Channel channel : channels) {
            register(channel);
        }
    }

    /**
     * Register listeners for ChatService
     */
    @Override
    public void preload(Logger logger) {
        logger.info("Initialize chat...");
        dispatcher.add(new RegisterChatListeners());
        dispatcher.add(new AddChatChannels(
            configuration,
            new ChannelsTransformer()
        ));
    }

    /**
     * Resolve channel and send the message
     */
    public void send(GamePlayer sender, Message message) throws ChatException {
        channels
            .get(message.channel())
            .send(sender, message)
        ;
    }

    private void register(Channel channel) {
        channels.put(channel.type(), channel);
    }
}