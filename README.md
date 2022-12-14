# EDT3 - Context-Independent Data Tree (version 3)
('E' - ex. extendable)

EDT is a tags-based binary data notation format supporting compression.
The format was developed for use as part of MIO-engine and [Zendes2,5 (game)](https://mihailris.itch.io/zendes25).
Designed to be simple, universal and fast.

## Table of contents
- [EDT base principles](#edt-base-principles)
- [Format properties](#format-properties)
- [Limitations](#limitations)
- [Version 2](#version-2)
- [Supported types](#supported-types)
- [Usage](#usage)
  - [Convert to JSON](#convert-to-json)
  - [Convert to YAML](#convert-to-yaml)
- [Examples](#examples)
  - [Example 1](#example-1)
  - [Example 2](#example-2)
- [Format description](#format-description)
  - [Item common payload](#1-item-common-payload)
  - [Items individual payload](#2-items-individual-payload)
  - [Compression](#3-compression)

## EDT base principles
- has no header and does not need any context to read
- only types are tree nodes: group and list
- any tree node may be root (but group is more preferred as root)
- string tags used to make reading implementation-independent and full
- serialized tree node is still context independed for reading (unless it's in compressed tree bytes)
- compression is highly recommended for files

## Format Properties
- byteorder: big-endian
- compression: gzip
- integers: signed
- booleans: 1 byte
- encoding: utf-8

## Limitations
- max tag length: 255 bytes (intentional limitation)

## Version 2
Version 2 is deprecated since version 3.
There is EDT.readEDT2(...) to read old files (for convertation to EDT3).

There is no EDT.writeEDT2(...) in the project.

## Supported types
### The API works with following types:
- int (byte, char, short, int)
- long (long)
- float (float)
- double (double)
- bool (boolean)
- string (String)
- bytes (byte[])
### And tree node types:
- list (EDTList - list)
- group (EDTGroup - map)

## Usage:
Create new group:
```java
EDTGroup group = EDTGroup.create("tag_name");
// or subgroup
EDTGroup subgroup = group.child("subgroup_tag_name");
// or
EDTGroup subgroup = list.child();
```

Create new list:
```java
EDTList list = EDTList.create("tag_name");
// or sublist
EDTList sublist = list.childList();
// or
EDTList sublist = group.childList("subgroup_tag_name");
```

Read group from bytes:
```java
byte[] bytes = ...;
EDTGroup group = EDT.read(bytes);
```

Read list from bytes:
```java
byte[] bytes = ...;
EDTList list = EDT.readList(bytes);
```

Convert EDTItem (EDTGroup or EDTList) to bytes:
```java
byte[] bytes = EDT.write(item); // with compression
// or
byte[] bytes = EDT.write(item, false); // without compression
```

Put values to EDTGroup:
```java
group.put("a", 53123)
     .put("b", 0.1f)
     .put("c", true)
     .put("d", bytes)
     .put("e", "text");
```

Add values to EDTList:
```java
list.add(53123)
    .add(0.1f)
    .add(true)
    .add(bytes)
    .add("text");
```

Getting values:
```java
int a = group.getInt("a");
float b = group.getFloat("b");
// default value available on all getX methods
boolean c = group.getBool("c", false);
```

Get subgroup/sublist:
```java
EDTGroup subgroup = group.get("subgroup_tag_name");
EDTList sublist = group.getList("sublist_tag_name");
// or
EDTGroup subgroup = list.get(0);
EDTList sublist = list.getList(1);
```

Add null to EDTList:
```java
list.addNull();
```

Also the library contains next interfaces:
```java
public interface EDTReadable {
    void read(EDTGroup root);
}
```
```java
public interface EDTWriteable {
    void write(EDTGroup root);
    
    default EDTGroup asEDT(String tag){
        ...
    }

    default byte[] asEDTBytes(String tag, boolean compression){
        ...
    }
}
```

And `EDTSerializable` that just combines interfaces above.

Also you can put any EDT tree into another as bytes to store compressed sub-trees in memory:
```java
EDTGroup internalRoot = ...;
EDTGroup externalRoot = ...;

byte[] internalBytes = EDT.write(internalRoot);
externalRoot.put("internal", internalBytes);
```
And easily read it:
```java
EDTGroup internalRoot = EDT.read(externalRoot.getBytes("internal"))
```
It may be also used to prevent tree from reading all sub-trees at once.

EDTConvert class allows to write string representation of tree.
Also to write YAML or JSON.
In next usage examples used 'root' generated in [Example 2](#example-2).

### Convert to JSON
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

### Convert to YAML
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

## Examples:
### Example 1
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

### Example 2
This `root` creation code used in EDTConvert usage examples.
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

## Format description:
Every tree node is called an Item.
### 1. Item common payload:
```
int8 type; [0-14]
int8 tagLength; [0-255]
byte[tagLength] utf8encodedTag;
```
when item tag is null common part is 2 bytes long.

### 2. Items individual payload

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

### 3. Compression

EDT uses GZIP for compression.
If EDT data bytes begin with value 255 (-1 signed byte), it means data is compressed.
Compressed data is not a part of EDT and has next structure, to make it easily read:
```java
int8 compressionFlag; // (always -1 signed byte value)
int32 uncompressedLength;
byte[] compressedData; // GZIP data
```
