import java.util.Map;
import java.util.HashMap;

public class Table {
    private String name;
    private Map<String, String> fields;
    private Map<String, String> relations;

    public Table(String name) {
        this.name = name;
        this.fields = new HashMap<String, String>();
        this.relations = new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void addField(String name, String type) {
        this.fields.put(name, type);
    }

    public Map<String, String> getRelations() {
        return relations;
    }

    public void addRelation(String name, String cardinality) {
        this.relations.put(name, cardinality);
    }   
}
