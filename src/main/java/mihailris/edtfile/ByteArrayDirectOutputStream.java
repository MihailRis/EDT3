package mihailris.edtfile;

import java.io.OutputStream;

/** ByteArrayOutputStream adapted to use with pre-created byte array **/
public class ByteArrayDirectOutputStream extends OutputStream {
    protected final byte[] buf;
    protected int count;

    public ByteArrayDirectOutputStream(byte[] array, int offset) {
        this.buf = array;
        this.count = offset;
    }

    @Override
    public synchronized void write(int var1) {
        this.buf[this.count] = (byte)var1;
        ++this.count;
    }

    @Override
    public synchronized void write(byte[] var1, int var2, int var3) {
        if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 - var1.length <= 0) {
            System.arraycopy(var1, var2, this.buf, this.count, var3);
            this.count += var3;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void close() {
    }
}
