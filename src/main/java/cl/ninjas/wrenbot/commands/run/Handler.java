package cl.ninjas.wrenbot.commands.run;

import cl.ninjas.wrenbot.MessageWrapper;
import cl.ninjas.wrenbot.lib.File;
import cl.ninjas.wrenbot.lib.Wren;
import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

public abstract class Handler {

    private Logger log = LoggerFactory.getLogger(Handler.class);

    @Value("${wren.cli:wrenc}")
    private String wrenCLI;

    public Mono<Void> process(Message eventMessage) {

        MessageWrapper wrap = new MessageWrapper(eventMessage);
        if(!wrap.isHuman()) {
            return Mono.empty();
        }

        try {
            Path temp = File.createAndWriteTempFile(wrap.codeBlock());
            String out = Wren.eval(this.wrenCLI, temp);
            return wrap.write(out).then();
        } catch(IOException ex) {
            log.warn(ex.getMessage());
            return wrap.write("Error creating temp file").then();
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
            return wrap.write("Operation Interrupted").then();
        } catch (TimeoutException ex) {
            log.warn(ex.getMessage());
            return wrap.write("Operation Timed Out").then();
        } catch(NullPointerException ex) {
            log.error(ex.getMessage());
            return wrap.write("Executable not found").then();
        }
    }
}
