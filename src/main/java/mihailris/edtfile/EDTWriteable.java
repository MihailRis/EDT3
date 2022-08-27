package mihailris.edtfile;

public interface EDTWriteable {
    void write(EDTGroup root);

    default EDTGroup asEDT(String tag){
        EDTGroup root = EDTGroup.create(tag);
        write(root);
        return root;
    }

    default byte[] asEDTBytes(String tag, boolean compression){
        EDTGroup root = EDTGroup.create(tag);
        write(root);
        return EDT.write(root, compression);
    }
}
