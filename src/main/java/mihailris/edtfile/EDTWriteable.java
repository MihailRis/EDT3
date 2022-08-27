package mihailris.edtfile;

public interface EDTWriteable {
    void write(EDTGroup root);

    default byte[] asEdtBytes(String tag, boolean compression){
        EDTGroup root = EDTGroup.create(tag);
        write(root);
        return EDT.write(root, compression);
    }
}
