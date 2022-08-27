package mihailris.edtfile.tests;

import mihailris.edtfile.EDT;
import mihailris.edtfile.EDTGroup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WriteTest {
    public static void main(String[] args) throws IOException {
        EDTGroup root = EDTGroup.create("root");
        root.child("sub")
                .put("name", "test")
                .put("time", 100.0125f)
                .childList("items")
                .add("pickaxe")
                .child()
                .put("name", "pickaxe2")
                .put("value", 5.42f);

        byte[] bytes = EDT.write(root);
        Files.write(new File("test.edt").toPath(), bytes);
    }
}
