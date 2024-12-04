package backend.academy.logSource;

import java.util.List;
import java.util.stream.Stream;

public interface LogSource {
    Stream<String> getLogs();

    List<String> getName();
}
