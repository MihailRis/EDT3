package mihailris.edtfile.tests;

import mihailris.edtfile.EDT;
import mihailris.edtfile.EDTItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EDT2Test {
    public static void main(String[] args) throws IOException {
        Path path = new File("human.edt").toPath();
        Path path2 = new File("human.edt3").toPath();
        byte[] bytes = Files.readAllBytes(path);
        EDTItem item = EDT.readEDT2(bytes);
        Files.write(path2, EDT.write(item, false));
    }
}
