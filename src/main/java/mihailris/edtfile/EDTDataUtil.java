package mihailris.edtfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;

public class EDTDataUtil {
    public static void short2Bytes(int value, byte[] dest, int offset){
        dest[offset] = (byte) (value >>> 8 & 255);
        dest[offset+1] = (byte) (value & 255);
    }

    public static void int2Bytes(int value, byte[] dest, int offset){
        dest[offset] = (byte) (value >>> 24 & 255);
        dest[offset+1] = (byte) (value >>> 16 & 255);
        dest[offset+2] = (byte) (value >>> 8 & 255);
        dest[offset+3] = (byte) (value & 255);
    }

    public static void long2Bytes(long value, byte[] dest, int offset){
        dest[offset] = (byte) (value >>> 56 & 255);
        dest[offset+1] = (byte) (value >>> 48 & 255);
        dest[offset+2] = (byte) (value >>> 40 & 255);
        dest[offset+3] = (byte) (value >>> 32 & 255);

        dest[offset+4] = (byte) (value >>> 24 & 255);
        dest[offset+5] = (byte) (value >>> 16 & 255);
        dest[offset+6] = (byte) (value >>> 8 & 255);
        dest[offset+7] = (byte) (value & 255);
    }

    public static int compress(byte[] source, int sourceOffset, int sourceSize, byte[] dest, int destOffset){
        try {
            ByteArrayDirectOutputStream baos = new ByteArrayDirectOutputStream(dest, destOffset);
            ExtGZIPOutputStream gzip = new ExtGZIPOutputStream(baos);
            gzip.setLevel(Deflater.BEST_SPEED);
            gzip.write(source, sourceOffset, sourceSize);
            gzip.finish();
            gzip.flush();
            return baos.count;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destOffset;
    }

    @SuppressWarnings("unused")
    public static void decompress(byte[] source, int sourceOffset, int sourceSize, byte[] dest, int destOffset, int destSize){
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(source, sourceOffset, sourceSize);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            new DataInputStream(gzip).readFully(dest, destOffset, destSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] decompress(byte[] source, int sourceOffset, int sourceSize){
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(source, sourceOffset, sourceSize);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true){
                int read = gzip.read(buffer);
                if (read <= 0)
                    break;
                output.write(buffer, 0, read);
            }
            gzip.close();
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] trim(byte[] buffer, int size) {
        byte[] bytes = new byte[size];
        System.arraycopy(buffer, 0, bytes, 0, size);
        return bytes;
    }
}
