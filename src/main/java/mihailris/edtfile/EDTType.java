package mihailris.edtfile;

public enum EDTType {
    NULL,
    INT8,
    INT16,
    INT32,
    INT64,
    FLOAT32,
    FLOAT64,
    BOOL,
    STRING,
    GROUP,
    BYTES,
    LIST,
    // since version 3
    BIGGROUP,
    LONGLIST,
    LONGSTRING,
    LONGBYTES,
}