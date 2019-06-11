package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.MoreObjects;

import java.io.IOException;
import java.util.*;

public class SubscribeResponse extends BaseResponse<List<SubscribeResponse.StreamInfo>> {

    private List<StreamInfo> result;

    @Override
    public List<StreamInfo> result() {
        return result;
    }

    public void setResult(List<StreamInfo> result) {
        this.result = result;
    }

    @JsonDeserialize(using = StreamInfoDeserializer.class)
    public static class StreamInfo {
        private String stream;
        private List<ColumnInfo> columns;
        private String subscriptionId;

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public void visitColumns(ColumnInfoVisitor visitor) {
            columns.forEach(c -> c.visit(visitor));
        }

        public int getColumnCount() {
            return columns.size();
        }

        public void setColumns(List<ColumnInfo> columns) {
            this.columns = columns;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("stream", stream)
                    .add("columns", columns)
                    .add("subscriptionId", subscriptionId)
                    .toString();
        }

        public interface ColumnInfo {
            void visit(ColumnInfoVisitor visitor);
        }

        public static class ScalarColumnInfo implements ColumnInfo {
            private final String name;

            public ScalarColumnInfo(String name) {
                this.name = name;
            }

            @Override
            public void visit(ColumnInfoVisitor visitor) {
                visitor.visitScalarColumn(name);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("name", name)
                        .toString();
            }
        }

        public static class ColumnSublistInfo implements ColumnInfo {
            private final List<String> names;

            public ColumnSublistInfo(List<String> names) {
                this.names = names;
            }

            @Override
            public void visit(ColumnInfoVisitor visitor) {
                visitor.visitColumnSublist(names);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("names", names)
                        .toString();
            }
        }

        public interface ColumnInfoVisitor {
            void visitScalarColumn(String name);
            void visitColumnSublist(List<String> names);
        }
    }

    public static class StreamInfoDeserializer extends JsonDeserializer<StreamInfo> {
        @Override
        public StreamInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode tree = p.readValueAsTree();
            Map<String, JsonNode> fields = new HashMap<>((int)(2 / 0.75d + 1));
            tree.fields().forEachRemaining(field -> {
                fields.put(field.getKey(), field.getValue());
            });
            if (fields.size() != 2) {
                throw new IllegalStateException("Expected 2 fields (sid and stream info), but was: " + fields);
            }

            StreamInfo streamInfo = new StreamInfo();

            streamInfo.setSubscriptionId(Objects.requireNonNull(fields.remove("sid").textValue()));

            String stream = fields.keySet().iterator().next();
            streamInfo.setStream(stream);

            ArrayNode columnsNode = (ArrayNode) fields.remove(stream).get("cols");
            List<StreamInfo.ColumnInfo> columns = new ArrayList<>(columnsNode.size() + 1);
            columnsNode.elements().forEachRemaining(columnNode -> {
                if (columnNode.isTextual()) {
                    String columnName = Objects.requireNonNull(columnNode.textValue());
                    columns.add(new StreamInfo.ScalarColumnInfo(columnName));
                } else if (columnNode.isArray()) {
                    ArrayNode columnSublistNode = (ArrayNode) columnNode;
                    List<String> columnNames = new ArrayList<>(columnSublistNode.size() + 1);
                    columnSublistNode.elements().forEachRemaining(sublistNode -> {
                        String columnName = Objects.requireNonNull(sublistNode.textValue());
                        columnNames.add(columnName);
                    });
                    columns.add(new StreamInfo.ColumnSublistInfo(columnNames));
                } else {
                    throw new IllegalStateException("Unexpected column info type: " + columnNode);
                }
            });
            streamInfo.setColumns(columns);

            return streamInfo;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", result)
                .add("jsonrpc", jsonrpc)
                .add("id", id)
                .add("error", error)
                .toString();
    }
}
