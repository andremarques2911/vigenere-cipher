import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static String readFile(String fileName) {
        String lines = null;
        try {
            Path path = Paths.get(fileName);
            List<String> fileLines = null;
            fileLines = Files.lines(path).collect(Collectors.toList());
            lines = fileLines.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeFile(String fileName, String message) {
        Path p = Paths.get(fileName);
        try {
            Files.write(p, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
