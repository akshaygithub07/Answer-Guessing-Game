import java.io.FileNotFoundException;
import java.io.IOException;

public class Source {
    public static void main(String[] args) throws IOException {
        quizle w = new quizle();
        w.setSize(400,600);
        w.setLocationRelativeTo(null);
        w.setVisible(true);
        w.setResizable(false);
    }
}