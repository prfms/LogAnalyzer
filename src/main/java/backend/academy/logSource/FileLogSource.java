package backend.academy.logSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class FileLogSource implements LogSource {
    private final List<Path> filePaths;

    public FileLogSource(String globPattern) {
        this.filePaths = findFilesByGlob(globPattern);
    }

    private List<Path> findFilesByGlob(String globPattern) {
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
        try {
            return Files.walk(Paths.get("."))
                .filter(pathMatcher::matches)
                .onClose(() -> log.info("Stream for glob {} has been closed", globPattern)).toList();
        } catch (IOException e) {
            log.error("Error while parsing glob {}", globPattern, e);
            return List.of();
        }
    }

    @Override
    public Stream<String> getLogs() {
    return filePaths.stream()
            .flatMap(filePath -> {
                log.info("Reading logs from file: {}", filePath);
                try {
                    return Files.lines(filePath, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @Override
    public List<String> getName() {
        List<String> names = new ArrayList<>();
        if (filePaths != null) {

            for (Path filePath : filePaths) {
                names.add(filePath.getFileName().toString());
            }
            return names;
        }
        return List.of();
    }
}

