package mihailris.edtfile.tests;

import mihailris.edtfile.EDT;
import mihailris.edtfile.EDTConvert;
import mihailris.edtfile.EDTGroup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class OtherTest {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(new File("scopyn.edt").toPath());
        EDTGroup group = (EDTGroup) EDT.readEDT2(bytes, 0, bytes.length);
        System.out.println(EDTConvert.toYaml(group));
    }
}
