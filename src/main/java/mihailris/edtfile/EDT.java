package mihailris.edtfile;

import java.io.IOException;
import java.nio.ByteBuffer;

public class EDT {
    public static final String VERSION_STRING = "3.0";
    public static final int VERSION_MAJOR = 3;
    public static final int VERSION_MINOR = 0;

    public static byte[] write(EDTItem root){
        return write(root, true);
    }

    public static byte[] write(EDTItem root, boolean compression){
        EDTWriter writer = new EDTWriter();
        if (compression) {
            byte[] data = writer.write(root);
            byte[] buffer = new byte[data.length + 5 + 128];
            buffer[0] = -1;
            EDTDataUtil.int2Bytes(data.length, buffer, 1);
            int size = EDTDataUtil.compress(data, 0, data.length, buffer, 5);
            return EDTDataUtil.trim(buffer, size+5);
        }
        return writer.write(root);
    }

    public static EDTItem read(byte[] source) throws IOException {
        return read(source, 0, source.length);
    }

    public static EDTItem read(byte[] source, int sourceOffset) throws IOException {
        return read(source, sourceOffset, source.length);
    }

    public static EDTItem read(byte[] source, int sourceOffset, int sourceSize) throws IOException {
        int initialOffset = sourceOffset;
        boolean compression = source[sourceOffset] == -1;

        if (compression){
            ByteBuffer bbuffer = ByteBuffer.wrap(source, sourceOffset+1, sourceSize-1);
            int size = bbuffer.getInt();
            sourceOffset += 5;
            byte[] decompressed = EDTDataUtil.decompress(source, sourceOffset, sourceSize - 5);
            return read(decompressed, 0, size);
        }
        return EDTReader.read(ByteBuffer.wrap(source, sourceOffset, sourceSize - initialOffset));
    }

    public static EDTItem readEDT2(byte[] source) throws IOException {
        return readEDT2(source, 0, source.length);
    }

    public static EDTItem readEDT2(byte[] source, int sourceOffset, int sourceSize) throws IOException {
        int initialOffset = sourceOffset;
        boolean compression = source[sourceOffset] == -1;

        if (compression){
            ByteBuffer bbuffer = ByteBuffer.wrap(source, sourceOffset+1, sourceSize-1);
            int size = bbuffer.getInt();
            sourceOffset += 5;
            byte[] decompressed = EDTDataUtil.decompress(source, sourceOffset, sourceSize - 5);
            return readEDT2(decompressed, 0, size);
        }
        return EDT2Reader.read(ByteBuffer.wrap(source, sourceOffset, sourceSize - initialOffset));
    }
}
