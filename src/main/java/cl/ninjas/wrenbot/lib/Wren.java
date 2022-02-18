package cl.ninjas.wrenbot.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class Wren {

    Logger log = LoggerFactory.getLogger(Wren.class);

    public static int defaultTimeout = 3;

    @Getter
    private Path file;

    // default seconds for timeout
    @Getter
    private int timeout = 3;

    @Getter
    private String wrenCLI;

    public Wren(String cli, Path file, int timeout) {
        this.wrenCLI = cli;
        this.file = file;
        this.timeout = timeout;
    }

    public String eval() throws IOException, InterruptedException, TimeoutException, NullPointerException {

        log.info("Begin evaluating Wren Code");

        List<String> out = new ArrayList<>();

        new ProcessExecutor()
                .command(this.wrenCLI, this.file.toString())
                .timeout(this.timeout, TimeUnit.SECONDS)
                .readOutput(true)
                .destroyOnExit()
                // Direct output gets wrongly parsed by the lib
                // at least here is joined by spaces instead
                .redirectOutput(new LogOutputStream() {
                    @Override
                    protected void processLine(String line) {
                        out.add(line);
                    }
                })
                .execute();

        // TODO: Figure out how to properly output the stdio and stderr to Discord
        return String.join(" ", out);
    }

    public static String eval(@Value("${wren.cli:wrenc}") String cli, Path file, int timeout) throws IOException, InterruptedException, TimeoutException, NullPointerException {
        Wren wren = new Wren(cli, file, timeout);
        return wren.eval();
    }

    public static String eval(@Value("${wren.cli:wrenc}") String cli, Path file) throws IOException, InterruptedException, TimeoutException, NullPointerException {
        return Wren.eval(cli, file, Wren.defaultTimeout);
    }
}
