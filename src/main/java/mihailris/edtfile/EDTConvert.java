package mihailris.edtfile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class EDTConvert {
    private static final int MIN_BASE64_LINE_LENGTH = 20;
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\033[0;0m";

    private static void toString(StringBuilder builder, Object object, int depth, boolean colored){
        if (object instanceof EDTItem){
            if (object instanceof EDTList)
                builder.append('[');
            else
                builder.append('{');
            if (colored) builder.append(GREEN);
            builder.append('\n').append(toString((EDTItem)object, depth+1, colored));
            for (int i = 0; i < depth; i++) {
                builder.append("  ");
            }
            if (object instanceof EDTList)
                builder.append(']');
            else
                builder.append('}');
        }
        if (colored) builder.append(GREEN);
        if (object instanceof Long || object instanceof Integer){
            builder.append(object);
        }
        else if (object instanceof Float){
            builder.append(object).append('f');
        }
        else if (object instanceof Double){
            builder.append(object).append('d');
        }
        else if (object instanceof Boolean){
            builder.append(object);
        }
        else if (object instanceof String){
            builder.append("'").append(object).append("'");
        }
        else if (object instanceof byte[]){
            builder.append("byte[").append(((byte[]) object).length).append(']');
        }
    }

    public static String toString(EDTItem node){
        return node.getTag()+": {\n"+ toString(node, 1, false)+'}';
    }

    public static String toString(EDTItem node, boolean colored){
        return node.getTag()+": {\n"+ toString(node, 1, colored)+'}';
    }

    public static String toString(EDTItem node, int depth, boolean colored){
        StringBuilder builder = new StringBuilder();

        switch (node.getType()){
            case GROUP: {
                EDTGroup group = (EDTGroup) node;
                for (Map.Entry<String, Object> entry : group.getObjects().entrySet()){
                    Object object = entry.getValue();
                    String tag = entry.getKey();

                    if (colored) builder.append(RESET);
                    for (int j = 0; j < depth; j++) {
                        builder.append("  ");
                    }
                    builder.append(tag);
                    builder.append(": ");
                    toString(builder, object, depth, colored);
                    if (builder.charAt(builder.length()-1) != '\n')
                        builder.append('\n');
                }
                break;
            }
            case LIST: {
                EDTList list = (EDTList) node;
                List<Object> objects = list.getObjects();
                for (int i = 0; i < list.size(); i++) {
                    Object object = objects.get(i);

                    if (colored) builder.append(RESET);
                    for (int j = 0; j < depth; j++) {
                        builder.append("  ");
                    }
                    toString(builder, object, depth, colored);
                    if (colored) builder.append(RESET);
                    builder.append(",");
                    if (builder.charAt(builder.length()-1) != '\n')
                        builder.append('\n');
                }
                break;
            }
            default:
                throw new IllegalStateException(node.getType().name());
        }
        if (colored) builder.append(RESET);
        return builder.toString();
    }

    public static String toYaml(EDTItem node){
        return node.getTag()+':'+ toYaml(node, 1, !(node instanceof EDTList));
    }

    public static String toYaml(EDTItem node, int depth, boolean head){
        StringBuilder builder = new StringBuilder();
        switch (node.getType()){
            case GROUP: {
                EDTGroup group = (EDTGroup) node;
                if (group.size() == 0){
                    builder.append("{}");
                } else {
                    if (head)
                        builder.append('\n');
                    int i = 0;
                    for (Map.Entry<String, Object> entry : group.getObjects().entrySet()){
                        Object object = entry.getValue();
                        String tag = entry.getKey();

                        if (head || i > 0)
                            for (int j = 0; j < depth; j++) {
                                builder.append("  ");
                            }
                        builder.append(tag);
                        builder.append(": ");
                        toYaml(builder, object, depth, true);
                        if (builder.charAt(builder.length()-1) != '\n')
                            builder.append('\n');
                        i++;
                    }
                }
                break;
            }
            case LIST: {
                EDTList list = (EDTList) node;
                List<Object> objects = list.getObjects();
                if (list.size() == 0){
                    builder.append("[]");
                } else {
                    builder.append('\n');
                    for (int i = 0; i < list.size(); i++) {
                        Object object = objects.get(i);

                        for (int j = 0; j < depth; j++) {
                            builder.append("  ");
                        }
                        builder.append("- ");
                        toYaml(builder, object, depth, false);
                        if (builder.charAt(builder.length()-1) != '\n')
                            builder.append('\n');
                    }
                }
                break;
            }
            default:
                throw new IllegalStateException(node.getType().name());
        }
        return builder.toString();
    }

    private static void toYaml(StringBuilder builder, Object object, int depth, boolean head){
        if (object instanceof EDTItem){
            builder.append(toYaml((EDTItem)object, depth+1, head));
        }
        if (object instanceof Long || object instanceof Integer){
            builder.append(object);
        }
        else if (object instanceof Float){
            builder.append(object);
        }
        else if (object instanceof Double){
            builder.append(object);
        }
        else if (object instanceof Boolean){
            builder.append(object);
        }
        else if (object instanceof String){
            builder.append("'").append(object).append("'");
        }
        else if (object instanceof byte[]){
            Base64.Encoder encoder = Base64.getEncoder();
            int chunk = Math.max(MIN_BASE64_LINE_LENGTH, 80 - (depth * 2 + 8));

            final String encoded = encoder.encodeToString((byte[]) object);
            int length = encoded.length();
            if (length <= chunk){
                builder.append("!!binary \"").append(encoded).append('"');
            } else {
                builder.append("!binary |\n");
                int pos = 0;
                while (pos < length) {
                    for (int i = 0; i <= depth; i++) {
                        builder.append("  ");
                    }
                    for (int i = 0; i < chunk && pos < length; i++, pos++) {
                        builder.append(encoded.charAt(i));
                    }
                    if (pos < length) {
                        builder.append('\n');
                    }
                }
            }
        }
    }

    private static void toJson(StringBuilder builder, Object object, int depth){
        if (object instanceof EDTItem){
            if (object instanceof EDTList)
                builder.append('[');
            else
                builder.append('{');
            builder.append('\n').append(toJson((EDTItem)object, depth+1));
            for (int i = 0; i < depth; i++) {
                builder.append("  ");
            }
            if (object instanceof EDTList)
                builder.append(']');
            else
                builder.append('}');
        }
        if (object instanceof Number){
            builder.append(object);
        }
        else if (object instanceof Boolean){
            builder.append(object);
        }
        else if (object instanceof String){
            builder.append('"').append(object).append('"');
        }
        else if (object instanceof byte[]){
            Base64.Encoder encoder = Base64.getEncoder();
            int chunk = Math.max(MIN_BASE64_LINE_LENGTH, 80 - (depth * 2 + 8));

            final String encoded = encoder.encodeToString((byte[]) object);
            int length = encoded.length();
            if (length <= chunk){
                builder.append("\"").append(encoded).append('"');
            } else {
                builder.append("[\n");
                int pos = 0;
                while (pos < length) {
                    for (int i = 0; i <= depth; i++) {
                        builder.append("  ");
                    }
                    builder.append('"');
                    for (int i = 0; i < chunk && pos < length; i++, pos++) {
                        builder.append(encoded.charAt(i));
                    }
                    builder.append('"');
                    if (pos < length) {
                        builder.append(",\n");
                    }
                }
                builder.append('\n');
                for (int i = 0; i < depth; i++) {
                    builder.append("  ");
                }
                builder.append("]");
            }
        }
    }

    public static String toJson(EDTItem node){
        return "{\n"+ toJson(node, 1)+'}';
    }

    public static String toJson(EDTItem node, int depth){
        StringBuilder builder = new StringBuilder();

        switch (node.getType()){
            case GROUP: {
                EDTGroup group = (EDTGroup) node;
                int size = group.size();
                int i = 0;
                for (Map.Entry<String, Object> entry : group.getObjects().entrySet()){
                    Object object = entry.getValue();
                    String tag = entry.getKey();

                    for (int j = 0; j < depth; j++) {
                        builder.append("  ");
                    }
                    builder.append('"');
                    builder.append(tag);
                    builder.append("\": ");
                    toJson(builder, object, depth);
                    if (i + 1 < size)
                        builder.append(',');
                    if (builder.charAt(builder.length()-1) != '\n')
                        builder.append('\n');
                    i++;
                }
                break;
            }
            case LIST: {
                EDTList list = (EDTList) node;
                List<Object> objects = list.getObjects();
                int size = list.size();
                for (int i = 0; i < list.size(); i++) {
                    Object object = objects.get(i);

                    for (int j = 0; j < depth; j++) {
                        builder.append("  ");
                    }
                    toJson(builder, object, depth);
                    if (i + 1 < size)
                        builder.append(',');
                    if (builder.charAt(builder.length()-1) != '\n')
                        builder.append('\n');
                }
                break;
            }
            default:
                throw new IllegalStateException(node.getType().name());
        }
        return builder.toString();
    }
}
