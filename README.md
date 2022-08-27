# EDT3 - Independent Data Tree (version 3)
('E' - ex. extendable)

EDT is a tags-based binary data notation format supporting compression.
The format was developed for use as part of MIO-engine and zendes2,5 (game).
Designed to be simple, universal and fast.

**EDT base principes:**
- has no header and does not need any context to read
- root is always a group or list
- every subnode if it is group or list may be extracted from bytes the simplest way
- string tags used to make reading implementation-independent and full
- compression is highly recommended for files
- GZIP used for compression (EDT.write uses compression by default, use EDT.write(item, false) to write uncompressed)

# Format Properties:
- byteorder: big-endian
- compression: gzip
- integers: signed
- booleans: 1 byte
- encoding: utf-8

# Version 2
Version 2 is deprecated since version 3.
There is EDT.readEDT2(...) to read old files (for convertation to EDT3).

There is no EDT.writeEDT2(...) in the project.

# The API works with following types:
- int (byte, char, short, int)
- long (long)
- float (float)
- double (double)
- bool (boolean)
- string (String)
- bytes (byte[])

# Examples:
```java
// example of tree creation
EDTGroup root = EDTGroup.create("root");
root.put("name", "TestWorld")
    .put("id", 5102)
    .put("time", System.currentTimeMillis()/1000.0)
    .childList("players")
    .add("Player1")
    .add("world_inspector");

// write to bytes with compression
byte[] bytes = EDT.write(root);

// read EDT from bytes
EDTItem read = EDT.read(bytes);
// cast to group
EDTGroup readGroup = (EDTGroup)read;
// print data tree
System.out.println(EDTConvert.toString(read));
```

<details>
<summary>Context independency demonstration</summary>

(Don't do it this way, it's just demonstration of the format properties) 
```java
EDTGroup root = EDTGroup.create("root");
root.child("subnode").put("test", 42);

int offset = 2 + // 'root' header bytes
4 + // 'root' tag
1; // 'root' group size byte
// write without compression
byte[] rootBytes = EDT.write(root, false);
EDTGroup subnode = (EDTGroup)EDT.read(rootBytes, offset);
System.out.println(EDTConvert.toString(subnode));
```
Console Output:
```
subnode: {
  test: 42
}
```
</details>

EDTConvert class allows to write string representation of tree.
Also to write YAML or JSON.

This `root` creation code will be used in all examples below.
```java
EDTGroup root = EDTGroup.create("root");

EDTGroup external = EDTGroup.create("external");
root.put(external).put("rand", new Random().nextFloat());

root.child("internal").put("number", -3310)
.childList("random-numbers")
.add(4.2f).add(7).add(0.01d).add(-5L).add(111111L).add("1/3").add(9).add(6);

root.get("internal").getList("random-numbers").childList().child()
.put("some-data", new byte[10])
.put("heh", 0L);
long tm = System.currentTimeMillis();
root.put("time", tm / 1000.0)
.put("ftime", tm / 1000.0f)
.put("working", true)
.put("hex", Long.toHexString(System.nanoTime()))
.put("nanos", System.nanoTime());
EDTList list = root.get("internal").getList("random-numbers");
list.add(54235L).add(true).add(new byte[150]);
```
Length of EDT.write(root) in 376 bytes (uncompressed) or 238 bytes (compressed)

**Convert to JSON:**
```java
System.out.println(EDTConvert.toJson(root));
```
<details>
<summary>Console Output</summary>

```json
{
  "rand": 0.21111876,
  "ftime": 1.66160038E9,
  "external": {
  },
  "internal": {
    "number": -3310,
    "random-numbers": [
      4.2,
      7,
      0.01,
      -5,
      111111,
      "1/3",
      9,
      6,
      [
        {
          "some-data": "AAAAAAAAAAAAAA==",
          "heh": 0
        }
      ],
      54235,
      true,
      [
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "AA"
      ]
    ]
  },
  "nanos": 13433321185514,
  "working": true,
  "hex": "c37b055e927",
  "time": 1.661600455885E9
}
```
</details>

**Convert to YAML:**
```java
System.out.println(EDTConvert.toYaml(root));
```
<details>
<summary>Console Output</summary>

```yaml
root:
  rand: 0.12990189
  ftime: 1.66159962E9
  external: {}
  internal:
    number: -3310
    random-numbers:
      - 4.2
      - 7
      - 0.01
      - -5
      - 111111
      - '1/3'
      - 9
      - 6
      -
        - some-data: !!binary "AAAAAAAAAAAAAA=="
          heh: 0
      - 54235
      - true
      - !binary |
        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        AA
  nanos: 12580984306995
  working: true
  hex: 'b713d194cce'
  time: 1.661599603548E9
```
</details>

# Format Description:
Every tree node is called an Item.
**1. Item (tree node) common payload:**
```
int8 type; [0-14]
int8 tagLength; [0-255]
byte[tagLength] utf8encodedTag;
```
when item tag is null common part is 2 bytes long.

**2. Items individual payload (in addition to common payload)**

null - used for lists
```
no payload
```
int8 - 8bit signed integer
```java
int8 value;
```
int16 - 16bit signed integer
```java
int16 value;
```
int32 - 32 bit signed integer
```java
int32 value;
```
int64 - 64 bit signed integer
```java
int64 value;
```
float32 - 32 bit float
```java
int32 floatBits;
```
float64 - 64 bit float
```java
int64 doubleBits;
```
bool - boolean value **1 byte**

string - utf8 string with length range **[0-255]**
```java
int8 length;
byte[length] utf8encoded; 
```
group - unordered map with length range **[0-255]**
```java
int8 size;
item[size] items;
```
bytes - bytes array with Java standard length range 
```java
int32 length;
byte[length] bytes;
```
list - list with length range **[0-255]**
```java
int8 size;
item[size] items;
```

[added in version 3]
biggroup - unordered map with length range **[0-65535]**
```java
int16 size;
item[size] items;
```
longlist - list with length range **[0-65535]**
```java
int16 length;
item[length] items;
```

**3. Compression**

EDT uses GZIP for compression.
If EDT data bytes begin with value 255, it means data is compressed.
Compressed data is not a part of EDT and has next header over GZIP header, to make it easily read:
