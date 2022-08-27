package mihailris.edtfile.tests;

import mihailris.edtfile.EDT;
import mihailris.edtfile.EDTGroup;
import mihailris.edtfile.EDTList;
import mihailris.edtfile.EDTConvert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Test {
    private static EDTGroup create(){
        EDTGroup root = EDTGroup.create("root");

        EDTGroup external = EDTGroup.create("external");
        root.put(external).put("rand", new Random().nextFloat());

        root.child("internal").put("number", -3310)
                .childList("random-numbers")
                .add(4.2f).add(7).add(0.01d).add(-5L).add(11164256111L).add("1/3").add(9).add(6);
        root.get("internal").childList("list").child();
        root.get("internal").getList("random-numbers").childList().child()
                .put("some-data", new byte[10])
                .put("heh", 0L);
        long tm = System.currentTimeMillis();
        root.put("time", tm / 1000.0)
                .put("ftime", tm / 1000.0f)
                .put("working", true)
                .put("hex", Long.toHexString(System.nanoTime()))
                .put("nanos", System.nanoTime())
                .put("something", 1561164256111L);
        EDTList list = root.get("internal").getList("random-numbers");
        list.add(54235L).add(true).add(new byte[150]).add('c');
        list.childList().add(555).add(EDTGroup.create("test")).add(999);

        root.childList("other-list").add(new byte[600]);
        return root;
    }
    public static void main(String[] args) throws IOException {
        EDTGroup root = create();
        EDTList list = root.get("internal").getList("random-numbers");
        System.out.println("0: "+list.getFloat(0));
        System.out.println("1: "+list.getInt(1));
        System.out.println("2: "+list.getDouble(2));
        System.out.println("3: "+list.getInt(3));
        System.out.println("4: "+list.getLong(4));
        System.out.println("5: "+list.getString(5));
        System.out.println("8: "+list.getList(8));
        System.out.println("10: "+list.getBool(10));
        System.out.println("working "+root.getBool("working"));
        System.out.println("hex "+root.getString("hex"));
        System.out.println("nanos "+root.getLong("nanos"));
        System.out.println("time "+root.getDouble("time"));
        System.out.println("ftime "+root.getFloat("ftime"));
        System.out.println("internal.number"+root.get("internal").getInt("number"));
        System.out.println("internal.list[0]"+root.get("internal").getList("list").get(0));
        System.out.println(root.getList("other-list").getBytes(0).length);

        System.out.println("version "+EDT.VERSION_MAJOR+"."+EDT.VERSION_MINOR+" ("+EDT.VERSION_STRING+")");
        System.out.println("root has internal "+root.has("internal"));

        System.out.println(EDTConvert.toYaml(root));
        Path path = new File("test.edt").toPath();
        Files.write(path, EDT.write(root, true));

        System.out.println(EDTConvert.toString(EDT.read(Files.readAllBytes(path)), true));
        System.out.println(EDTConvert.toJson(EDT.read(Files.readAllBytes(path))));

        System.out.println(((byte)(-1)) & 0xFF);
    }
}
