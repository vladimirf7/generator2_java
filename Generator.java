import org.yaml.snakeyaml.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Generator {
    private Yaml yaml;
    private String input;
    private String output;
    private LinkedHashMap<String, Object> yamlData;
    private Map<String, Table> tables;
    private Set<Relation> relations;

    @SuppressWarnings("unchecked")
    public Generator(String input, String output) {
        yaml = new Yaml();
        this.input = input;
        this.output = output;
        yamlData = (LinkedHashMap<String, Object>)
            yaml.load(loadYamlFile(this.input));
        tables = new HashMap<String, Table>();
        relations = new HashSet<Relation>();
    }

    private String loadYamlFile(String file) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void createTableObjects() {
        for (Map.Entry<String,Object> entry : yamlData.entrySet()) {
            String tableName = entry.getKey().toLowerCase();
            Table table = new Table(tableName);

            LinkedHashMap<String, Object> tableData = 
                (LinkedHashMap<String, Object>) entry.getValue();
            LinkedHashMap<String, Object> tableFields =
                (LinkedHashMap<String, Object>) tableData.get("fields");
            LinkedHashMap<String, Object> tableRelations =
                (LinkedHashMap<String, Object>) tableData.get("relations");

            // System.out.println(tableName);

            for (Map.Entry<String, Object> field :  tableFields.entrySet()) {
                String fieldName = field.getKey().toLowerCase();
                String fieldType = ((String)field.getValue()).toLowerCase();

                table.addField(fieldName, fieldType);
                // System.out.println(" " + fieldName + " " + fieldType);
            }

            for (Map.Entry<String, Object> relation :  tableRelations.entrySet()) {
                String relationName = relation.getKey().toLowerCase();
                String relationType = ((String)relation.getValue()).toLowerCase();

                table.addRelation(relationName, relationType);
                // System.out.println(" " + relationName + " " + relationType);
            }
            tables.put(tableName, table);
        }
    }

    private void createRelationObjects() throws RelationException {
        for (Table thisTable : tables.values()) {
            String thisTableName = thisTable.getName();

            for (Map.Entry relationLeft : thisTable.getRelations().entrySet()) {
                String leftName = (String)relationLeft.getKey();
                String leftType = (String)relationLeft.getValue();
                String rightName;
                String rightType;
                Table otherTable;
                Relation relation = null;

                // System.out.println("DEBUG: " + leftName);
                if (!tables.containsKey(leftName)) {
                    throw new RelationException();
                }
                otherTable = tables.get(leftName);
                if (!otherTable.getRelations().containsKey(thisTableName)) {
                    throw new RelationException();
                }
                rightName = thisTableName;
                rightType = otherTable.getRelations().get(thisTableName);

                System.out.println("DEBUG: " + rightName + " " + rightType + " "+ leftType + " " + leftName);

                if (rightType.equals("many") && leftType.equals("many")) {
                    relation = new Relation(
                        leftName, rightName, Cardinality.MANY_TO_MANY
                    );                    
                }
                else if (rightType.equals("one") && leftType.equals("many")) {
                    relation = new Relation(
                        rightName, leftName, Cardinality.ONE_TO_MANY
                    );
                }
                else if (rightType.equals("many") && leftType.equals("one")) {
                    relation = new Relation(
                        leftName, rightName, Cardinality.ONE_TO_MANY
                    );
                }
                else if (rightType.equals("one") && leftType.equals("one")) {
                    relation = new Relation(
                        leftName, rightName, Cardinality.ONE_TO_ONE
                    );
                }
                this.relations.add(relation);
            }
        }
        System.out.println("DEBUG: " + relations.size());
        for (Relation r : this.relations) {
            System.out.println("DEBUG: " + r);
            System.out.println("DEBUG: " + r.getName());
        }
    }
    
    public void saveStatements() throws RelationException {
        createTableObjects();
        createRelationObjects();
    }
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("Usage: Generator input.file output.file");
        }
        Generator gen = new Generator(args[0], args[1]);
        
        try {
            gen.saveStatements();
        } catch (RelationException ex) {
            ex.printStackTrace();
        }
    }
}
