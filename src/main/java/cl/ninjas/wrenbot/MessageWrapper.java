package cl.ninjas.wrenbot;

import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * A helper class that contains useful methods
 * when working with Discord Messages
 */
@RequiredArgsConstructor
public class MessageWrapper {
    @Getter
    private final Message message;

    public String content() {
        return this.message.getContent();
    }

    /**
     * Extracts the content inside code block tags
     * @return String inside the codeblock
     */
    public String codeBlock() {
        String content = this.content().trim();

        if(content.startsWith("```wren")) {
            content = content.replaceFirst("```wren", "");
        }

        if(content.startsWith("```js")) {
            content = content.replaceFirst("```js", "");
        }

        if(content.startsWith("```")) {
            content = content.replaceFirst("```", "");
        }

        if (content.endsWith("```")) {
            content = content.replaceFirst("```", "");
        }

        return content;
    }

    /**
     * Determines is the message was send by a human or a bot
     * @return true if the message is human made
     */
    public Boolean isHuman() {
        return this.message.getAuthor().map(user -> !user.isBot()).orElse(false);
    }

    /**
     * Writes a String to the Channel
     * @param content
     * @return A Promise
     */
    public Mono<Object> write(String content) {
        return this.message.getChannel().flatMap(channel -> channel.createMessage(content));
    }
}
