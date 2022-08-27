package mihailris.edtfile;

import java.util.HashMap;
import java.util.Map;

public class EDTGroup implements EDTItem {
    private final Map<String, Object> objects;
    private final String tag;

    public EDTGroup(String tag) {
        this.tag = tag;
        this.objects = new HashMap<>();
    }

    public EDTGroup(String tag, Map<String, Object> objects) {
        this.tag = tag;
        this.objects = objects;
    }

    public EDTGroup child(String tag){
        EDTGroup group = new EDTGroup(tag);
        objects.put(tag, group);
        return group;
    }

    public EDTList childList(String tag){
        EDTList list = new EDTList(tag);
        objects.put(tag, list);
        return list;
    }

    public EDTGroup put(EDTGroup group){
        objects.put(group.getTag(), group);
        return this;
    }

    public EDTGroup put(String tag, boolean value){
        objects.put(tag, value);
        return this;
    }

    public EDTGroup put(String tag, long value){
        objects.put(tag, value);
        return this;
    }

    public EDTGroup put(String tag, int value){
        objects.put(tag, value);
        return this;
    }

    public EDTGroup put(String tag, float value){
        objects.put(tag, value);
        return this;
    }

    public EDTGroup put(String tag, double value){
        objects.put(tag, value);
        return this;
    }

    public EDTGroup put(String tag, String value){
        objects.put(tag, value);
        return this;
    }

    public EDTGroup put(String tag, byte[] value){
        objects.put(tag, value);
        return this;
    }

    public boolean has(String tag){
        return objects.containsKey(tag);
    }

    public EDTGroup get(String tag){
        Object value = objects.get(tag);
        if (value == null)
            return null;
        return (EDTGroup) value;
    }

    public EDTList getList(String tag) {
        Object value = objects.get(tag);
        if (value == null)
            return null;
        return (EDTList) value;
    }

    public String getString(String tag, String def) {
        Object value = objects.get(tag);
        if (value == null)
            return def;
        return (String) value;
    }

    public boolean getBool(String tag, boolean def){
        Boolean value = (Boolean) objects.get(tag);
        if (value == null)
            return def;
        return value;
    }

    public int getInt(String tag, int def){
        Number value = (Number) objects.get(tag);
        if (value == null)
            return def;
        return value.intValue();
    }

    public long getLong(String tag, long def){
        Number value = (Number) objects.get(tag);
        if (value == null)
            return def;
        return value.longValue();
    }

    public float getFloat(String tag, float def){
        Number value = (Number) objects.get(tag);
        if (value == null)
            return def;
        return value.floatValue();
    }

    public double getDouble(String tag, double def){
        Number value = (Number) objects.get(tag);
        if (value == null)
            return def;
        return value.doubleValue();
    }

    public String getString(String tag) {
        Object value = objects.get(tag);
        if (value == null)
            return null;
        return (String) value;
    }

    public boolean getBool(String tag){
        return (boolean) objects.get(tag);
    }

    public int getInt(String tag){
        return ((Number)objects.get(tag)).intValue();
    }

    public long getLong(String tag){
        return ((Number)objects.get(tag)).longValue();
    }

    public float getFloat(String tag){
        return ((Number)objects.get(tag)).floatValue();
    }

    public double getDouble(String tag){
        return ((Number)objects.get(tag)).doubleValue();
    }

    @Override
    public EDTType getType() {
        return EDTType.GROUP;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public int size() {
        return objects.size();
    }

    public static EDTGroup create(String tag){
        return new EDTGroup(tag);
    }

    public Map<String, Object> getObjects() {
        return objects;
    }
}
