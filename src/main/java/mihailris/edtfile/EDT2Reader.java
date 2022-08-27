package mihailris.edtfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EDT2Reader {
    static String readTag(ByteBuffer buffer) {
        int tagLength = buffer.getShort() & 0xFFFF;
        byte[] tagBytes = new byte[tagLength];
        buffer.get(tagBytes, 0, tagLength);
        return new String(tagBytes, 0, tagLength);
    }

    static void skipTag(ByteBuffer buffer) {
        int tagLength = buffer.getShort() & 0xFFFF;
        buffer.position(buffer.position() + tagLength);
    }

    static EDTItem read(ByteBuffer buffer) throws IOException {
        int typeid = buffer.get() & 0xFF;
        if (typeid >= EDTType.values().length){
            throw new IOException("unknown item type with id "+typeid);
        }

        String tag = readTag(buffer);

        EDTType type = EDTType.values()[typeid];
        switch (type) {
            case GROUP: return readGroup(buffer, tag);
            case LIST: return readList(buffer, tag);
            default:
                throw new IllegalStateException(type.name()+" as root item");
        }
    }

    static Object readPrimitive(EDTType type, ByteBuffer buffer) {
        switch (type){
            case BOOL:
                return buffer.get() != 0;
            case INT8:
                return (int)buffer.get();
            case INT16:
                return (int)buffer.getShort();
            case INT32:
                return buffer.getInt();
            case INT64:
                return buffer.getLong();
            case FLOAT32:
                return Float.intBitsToFloat(buffer.getInt());
            case FLOAT64:
                return Double.longBitsToDouble(buffer.getLong());
        }
        throw new IllegalStateException();
    }

    static EDTList readList(ByteBuffer buffer, String tag) throws IOException {
        int size = buffer.getShort() & 0xFFFF;
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int position = buffer.position();
            EDTType type = EDTType.values()[buffer.get()];
            switch (type){
                case GROUP:
                case LIST:
                {
                    buffer.position(position);
                    EDTItem item = read(buffer);
                    objects.add(item);
                    break;
                }
                case BOOL:
                case INT8:
                case INT16:
                case INT32:
                case INT64:
                case FLOAT32:
                case FLOAT64:
                {
                    skipTag(buffer);
                    Object primitive = readPrimitive(type, buffer);
                    objects.add(primitive);
                    break;
                }
                case STRING: {
                    skipTag(buffer);
                    int length = buffer.getShort() & 0xFFFF;
                    byte[] stringBytes = new byte[length];
                    buffer.get(stringBytes, 0, length);
                    String string = new String(stringBytes, 0, length);
                    objects.add(string);
                    break;
                }
                case BYTES: {
                    skipTag(buffer);
                    int length = buffer.getInt();
                    byte[] bytes = new byte[length];
                    buffer.get(bytes, 0, length);
                    objects.add(bytes);
                    break;
                }
                case NULL: {
                    skipTag(buffer);
                    objects.add(null);
                    break;
                }
            }
        }
        return new EDTList(tag, objects);
    }

    static EDTGroup readGroup(ByteBuffer buffer, String tag) throws IOException {
        int size = buffer.getShort() & 0xFFFF;
        Map<String, Object> objects = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int position = buffer.position();
            EDTType type = EDTType.values()[buffer.get()];
            switch (type){
                case GROUP:
                case LIST:
                {
                    buffer.position(position);
                    EDTItem item = read(buffer);
                    objects.put(item.getTag(), item);
                    break;
                }
                case BOOL:
                case INT8:
                case INT16:
                case INT32:
                case INT64:
                case FLOAT32:
                case FLOAT64:
                {
                    String subTag = readTag(buffer);
                    Object primitive = readPrimitive(type, buffer);
                    objects.put(subTag, primitive);
                    break;
                }
                case STRING: {
                    String subTag = readTag(buffer);
                    int length = buffer.getShort() & 0xFFFF;
                    byte[] stringBytes = new byte[length];
                    buffer.get(stringBytes, 0, length);
                    String string = new String(stringBytes, 0, length);
                    objects.put(subTag, string);
                    break;
                }
                case BYTES: {
                    String subTag = readTag(buffer);
                    int length = buffer.getInt();
                    byte[] bytes = new byte[length];
                    buffer.get(bytes, 0, length);
                    objects.put(subTag, bytes);
                    break;
                }
                case NULL: {
                    skipTag(buffer);
                    break;
                }
            }
        }
        return new EDTGroup(tag, objects);
    }
}
