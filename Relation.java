public class Relation {
    private String left;
    private String right;
    private Cardinality cardinality;

    public Relation(String left, String right, Cardinality cardinality) {
        this.left = left;
        this.right = right;
        this.cardinality = cardinality;
    }

    public String getName() {
        return this.left + "__" + this.right;
    }

    @Override
    public String toString() {
        String result = left + " %1$s----%2$s " + right;

        if (cardinality == Cardinality.MANY_TO_MANY) {
            result = String.format(result, "*", "*");
        }
        if (cardinality == Cardinality.ONE_TO_MANY) {
            result = String.format(result, "1", "*");
        }
        if (cardinality == Cardinality.ONE_TO_ONE) {
            result = String.format(result, "1", "1");
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        Relation other = (Relation)obj;

        if (this.getName().equals(other.getName())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
