package mihailris.edtfile;

import java.util.ArrayList;
import java.util.List;

public class EDTList implements EDTItem {
    private final String tag;
    private final List<Object> objects;

    public EDTList(String tag) {
        this.tag = tag;
        this.objects = new ArrayList<>();
    }

    public EDTList(String tag, List<Object> objects) {
        this.tag = tag;
        this.objects = objects;
    }

    @Override
    public String toString() {
        return "EDTList("+size()+")";
    }

    @Override
    public EDTType getType() {
        return EDTType.LIST;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public int size() {
        return objects.size();
    }

    public EDTGroup child(){
        EDTGroup group = new EDTGroup(null);
        add(group);
        return group;
    }

    public EDTGroup child(String tag){
        EDTGroup group = new EDTGroup(tag);
        add(group);
        return group;
    }

    public EDTList childList(){
        EDTList list = new EDTList(null);
        add(list);
        return list;
    }

    public EDTList childList(String tag){
        EDTList list = new EDTList(tag);
        add(list);
        return list;
    }

    public EDTList add(EDTItem value){
        putObject(value);
        return this;
    }

    public EDTList add(String value) {
        putObject(value);
        return this;
    }

    public EDTList add(byte[] value) {
        putObject(value);
        return this;
    }

    public EDTList add(int value) {
        putObject(value);
        return this;
    }

    public EDTList add(long value) {
        putObject(value);
        return this;
    }

    public EDTList add(float value) {
        putObject(value);
        return this;
    }

    public EDTList add(double value) {
        putObject(value);
        return this;
    }

    public EDTList add(boolean value) {
        putObject(value);
        return this;
    }

    public EDTList add(EDTWriteable writeable){
        writeable.write(child(null));
        return this;
    }

    public EDTList add(String tag, EDTWriteable writeable){
        writeable.write(child(tag));
        return this;
    }

    private void putObject(Object value) {
        objects.add(value);
    }

    public EDTGroup get(int index) {
        return (EDTGroup) objects.get(index);
    }

    public boolean getBool(int index){
        return (boolean) objects.get(index);
    }

    public EDTList getList(int index) {
        return (EDTList) objects.get(index);
    }

    public String getString(int index) {
        return (String) objects.get(index);
    }

    public byte[] getBytes(int index) {
        return (byte[]) objects.get(index);
    }

    public int getInt(int index) {
        return ((Number)objects.get(index)).intValue();
    }

    public long getLong(int index) {
        return ((Number)objects.get(index)).longValue();
    }

    public float getFloat(int index){
        return ((Number)objects.get(index)).floatValue();
    }

    public double getDouble(int index){
        return ((Number)objects.get(index)).doubleValue();
    }

    public List<Object> getObjects() {
        return objects;
    }

    public static EDTList create(String tag){
        return new EDTList(tag);
    }

    public static EDTList create(){
        return new EDTList(null);
    }
}
