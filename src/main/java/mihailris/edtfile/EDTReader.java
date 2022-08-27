package mihailris.edtfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EDTReader {
    static String readTag(ByteBuffer buffer) {
        int tagLength = buffer.get() & 0xFF;
        byte[] tagBytes = new byte[tagLength];
        buffer.get(tagBytes, 0, tagLength);
        return new String(tagBytes, 0, tagLength);
    }

    static void skipTag(ByteBuffer buffer) {
        int tagLength = buffer.get() & 0xFF;
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
            case BIGGROUP: return readGroup(buffer, tag, true);
            case LONGLIST: return readList(buffer, tag, true);
            case GROUP: return readGroup(buffer, tag, false);
            case LIST: return readList(buffer, tag, false);
            default:
                throw new IllegalStateException(type.name()+" as root item");
        }
    }

    static EDTList readList(ByteBuffer buffer, String tag, boolean longtype) throws IOException {
        int size;
        if (longtype){
            size = buffer.getShort() & 0xFFFF;
        } else {
            size = buffer.get() & 0xFF;
        }
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int position = buffer.position();
            EDTType type = EDTType.values()[buffer.get()];
            switch (type){
                case GROUP:
                case BIGGROUP:
                case LIST:
                case LONGLIST:
                {
                    buffer.position(position);
                    EDTItem item = EDTReader.read(buffer);
                    objects.add(item);
                    break;
                }
                default: {
                    EDTReader.skipTag(buffer);
                    Object primitive = readNonEDT(buffer, type);
                    objects.add(primitive);
                    break;
                }
            }
        }
        return new EDTList(tag, objects);
    }

    static Object readNonEDT(ByteBuffer buffer, EDTType type) {
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
            case STRING:
            case LONGSTRING: {
                int length = (type == EDTType.LONGSTRING) ? (buffer.getInt()) : (buffer.get() & 0xFF);
                byte[] stringBytes = new byte[length];
                buffer.get(stringBytes, 0, length);
                return new String(stringBytes, 0, length);
            }
            case BYTES:
            case LONGBYTES: {
                int length = (type == EDTType.LONGBYTES) ? buffer.getInt() : (buffer.get() & 0xFF);
                byte[] bytes = new byte[length];
                buffer.get(bytes, 0, length);
                return bytes;
            }
            case NULL:
                return null;
        }
        return null;
    }

    static EDTGroup readGroup(ByteBuffer buffer, String tag, boolean longtype) throws IOException {
        int size;
        if (longtype){
            size = buffer.getShort() & 0xFFFF;
        } else {
            size = buffer.get() & 0xFF;
        }
        Map<String, Object> objects = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int position = buffer.position();
            EDTType type = EDTType.values()[buffer.get()];
            switch (type){
                case GROUP:
                case BIGGROUP:
                case LIST:
                case LONGLIST: {
                    buffer.position(position);
                    EDTItem item = EDTReader.read(buffer);
                    objects.put(item.getTag(), item);
                    break;
                }
                default: {
                    String subTag = EDTReader.readTag(buffer);
                    Object primitive = readNonEDT(buffer, type);
                    objects.put(subTag, primitive);
                    break;
                }
            }
        }
        return new EDTGroup(tag, objects);
    }
}
