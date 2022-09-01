package mihailris.edtfile;

public interface EDTItem {
    /**
     * @return type of the item
     */
    EDTType getType();

    /**
     * @return internal tag text
     */
    String getTag();

    /**
     * @return size of group / length of list
     */
    int size();

    /**
     * Changes internal node tag, still has same key in group
     * @param tag new tag text
     */
    void setTag(String tag);
}
