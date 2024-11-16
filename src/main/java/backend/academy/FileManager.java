package backend.academy;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

// TODO:
// - add new statistics
// - add reading from pattern
// - separate GET <resource>
// - filtering
@Log4j2
public class FileManager {
    public void getPathsMatchingPattern(String pattern) throws IOException {
            Path filePath = Paths.get(pattern);
            try {
                Files.lines(filePath).forEach(System.out::println);
            } catch (IOException e) {
                log.error("IOException while reading file{}", filePath, e);
            }
                /*
            don't work
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            Path rootPath = Paths.get(".");

            List<Path> pathStream = Files.walk(rootPath)
                .filter(Files::isRegularFile)
                .peek(path -> System.out.println("После filter: " + path))
                .filter(pathMatcher::matches)
                .peek(path -> System.out.println("После matches: " + path))
                .toList();
            return pathStream;*/
    }
}
