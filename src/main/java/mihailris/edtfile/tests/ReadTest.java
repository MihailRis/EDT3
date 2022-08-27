package mihailris.edtfile.tests;

import mihailris.edtfile.EDT;
import mihailris.edtfile.EDTGroup;
import mihailris.edtfile.EDTConvert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadTest {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(new File("test.edt").toPath());
        EDTGroup group = (EDTGroup) EDT.read(bytes, 0, bytes.length);
        System.out.println(EDTConvert.toYaml(group));
    }
}
