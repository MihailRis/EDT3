package mihailris.edtfile.tests;

import mihailris.edtfile.EDT;
import mihailris.edtfile.EDTConvert;
import mihailris.edtfile.EDTGroup;

import java.io.IOException;

public class DemonstrationTest {
    public static void main(String[] args) throws IOException {
        EDTGroup root = EDTGroup.create("root");
        root.child("subnode").put("test", 42);

        int offset = 2 + // 'root' header bytes
                4 + // 'root' tag
                1; // 'root' group size byte
        // write without compression
        byte[] rootBytes = EDT.write(root, false);
        EDTGroup subnode = (EDTGroup)EDT.read(rootBytes, offset);
        System.out.println(EDTConvert.toString(subnode));
    }
}
