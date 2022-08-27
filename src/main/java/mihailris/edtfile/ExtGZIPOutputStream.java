package mihailris.edtfile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class ExtGZIPOutputStream extends GZIPOutputStream {
    public ExtGZIPOutputStream(OutputStream outputStream) throws IOException {
        super(outputStream);
    }

    public void setLevel(int level){
        def.setLevel(level);
    }
}
