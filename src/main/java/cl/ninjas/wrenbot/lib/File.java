package cl.ninjas.wrenbot.lib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class File {
    /**
     * Creates a new temp file and fill it with the provided content
     * @param content
     * @return Path to the temp file
     * @throws IOException
     */
    public static Path createAndWriteTempFile(String content) throws IOException {
        Path temp = Files.createTempFile(null, null);
        Files.write(temp, content.getBytes(StandardCharsets.UTF_8));
        return temp;
    }
}
