package mihailris.edtfile;

import java.util.List;
import java.util.Map;

class EDTWriter {
    private static final int INITIAL_CAPACITY = 512;
    private byte[] dest;
    private int index;

    public byte[] write(EDTItem root){
        dest = new byte[INITIAL_CAPACITY];
        index = 0;
        writeEDT(root);
        return EDTDataUtil.trim(dest, index);
    }

    private void grow(int size, int min){
        int capacity = dest.length;
        capacity += Math.max(capacity / 2, min);
        byte[] temp = new byte[capacity];
        System.arraycopy(dest, 0, temp, 0, size);
        this.dest = temp;
    }

    private void writeEDT(EDTItem item){
        String itemTag = item.getTag();
        if (itemTag == null)
            itemTag = "";
        byte[] tagBytes = itemTag.getBytes();

        // grow array if needed
        if (index + 3 + tagBytes.length + 16 >= dest.length){
            grow(index, 3 + tagBytes.length + 16);
        }

        EDTType type;
        boolean longtype = false;
        if (item instanceof EDTGroup){
            if (item.size() > 65535){
                type = EDTType.BIGGROUP;
                longtype = true;
            } else {
                type = EDTType.GROUP;
            }
        } else {
            if (item.size() > 65535){
                type = EDTType.LONGLIST;
                longtype = true;
            } else {
                type = EDTType.LIST;
            }
        }
        dest[index++] = (byte) type.ordinal();
        dest[index++] = (byte) tagBytes.length;
        System.arraycopy(tagBytes, 0, dest, index, tagBytes.length);
        index += tagBytes.length;

        if (item instanceof EDTGroup){
            EDTGroup group = (EDTGroup) item;
            int size = group.size();
            if (longtype){
                EDTDataUtil.short2Bytes(size, dest, index);
                index += 2;
            } else {
                dest[index++] = (byte) size;
            }
            for (Map.Entry<String, Object> entry : group.getObjects().entrySet()){
                Object object = entry.getValue();
                String tag = entry.getKey();
                if (object instanceof EDTItem) {
                    writeEDT((EDTItem) object);
                } else {
                    writeNonEDT(tag, object);
                }
            }
        }
        if (item instanceof EDTList){
            EDTList list = (EDTList) item;
            int size = list.size();
            final List<Object> objects = list.getObjects();
            if (longtype){
                EDTDataUtil.short2Bytes(size, dest, index);
                index += 2;
            } else {
                dest[index++] = (byte) size;
            }
            for (int i = 0; i < size; i++) {
                Object object = objects.get(i);
                if (object instanceof EDTItem) {
                    writeEDT((EDTItem) object);
                } else {
                    writeNonEDT(null, object);
                }
            }
        }
    }

    private void writeHead(EDTType type, String tag){
        dest[index++] = (byte) type.ordinal();
        if (tag == null){
            dest[index++] = 0;
        } else {
            byte[] tagBytes = tag.getBytes();
            dest[index++] = (byte) tagBytes.length;
            System.arraycopy(tagBytes, 0, dest, index, tagBytes.length);
            index += tagBytes.length;
        }
    }

    private void writeNonEDT(String tag, Object object){
        if (index + 3 + 16 >= dest.length){
            grow(index, 3 + 16);
        }
        if (object instanceof Long || object instanceof Integer) {
            long value;
            if (object instanceof Integer)
                value = (int)object;
            else
                value = (long) object;
            if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE){
                writeHead(EDTType.INT8, tag);
                dest[index++] = (byte) value;
            } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE){
                writeHead(EDTType.INT16, tag);
                dest[index++] = (byte) ((value >>> 8) & 255);
                dest[index++] = (byte) (value & 255);
            } else if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE){
                writeHead(EDTType.INT32, tag);
                dest[index++] = (byte) ((value >>> 24) & 255);
                dest[index++] = (byte) ((value >>> 16) & 255);
                dest[index++] = (byte) ((value >>> 8) & 255);
                dest[index++] = (byte) (value & 255);
            } else {
                writeHead(EDTType.INT64, tag);
                EDTDataUtil.long2Bytes(value, dest, index);
                index += 8;
            }
        }
        else if (object instanceof Boolean) {
            writeHead(EDTType.BOOL, tag);
            dest[index++] = (byte) (((boolean)object) ? 1 : 0);
        }
        else if (object instanceof Float) {
            long value = Float.floatToIntBits((Float) object);
            writeHead(EDTType.FLOAT32, tag);
            dest[index++] = (byte) ((value >>> 24) & 255);
            dest[index++] = (byte) ((value >>> 16) & 255);
            dest[index++] = (byte) ((value >>> 8) & 255);
            dest[index++] = (byte) (value & 255);
        }
        else if (object instanceof Double) {
            long value = Double.doubleToLongBits((Double) object);
            writeHead(EDTType.FLOAT64, tag);
            EDTDataUtil.long2Bytes(value, dest, index);
            index += 8;
        }
        else if (object instanceof String) {
            String string = (String) object;
            byte[] bytes = string.getBytes();

            // grow array if needed
            if (index + bytes.length + 4 >= dest.length){
                grow(index, index + bytes.length * 2 + 4);
            }

            if (bytes.length > 255) {
                writeHead(EDTType.LONGSTRING, tag);
                EDTDataUtil.int2Bytes(bytes.length, dest, index);
                index += 4;
            } else {
                writeHead(EDTType.STRING, tag);
                dest[index++] = (byte) bytes.length;
            }
            System.arraycopy(bytes, 0, dest, index, bytes.length);
            index += bytes.length;
        }
        else if (object instanceof byte[]) {
            byte[] bytes = (byte[]) object;
            // grow array if needed
            if (index + bytes.length + 4 >= dest.length){
                grow(index, index + bytes.length * 2);
            }
            if (bytes.length > 255) {
                writeHead(EDTType.LONGBYTES, tag);
                EDTDataUtil.int2Bytes(bytes.length, dest, index);
                index += 4;
            } else {
                writeHead(EDTType.BYTES, tag);
                dest[index++] = (byte) bytes.length;
            }

            System.arraycopy(bytes, 0, dest, index+4, bytes.length);
            index += bytes.length;
        }
    }
}
