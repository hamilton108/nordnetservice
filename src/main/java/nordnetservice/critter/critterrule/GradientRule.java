package nordnetservice.critter.critterrule;

public class GradientRule {
    /*
    oid     | integer      | not null default nextval('gradient_rules_oid_seq'::regclass)
    rtyp    | integer      | not null
    value_1 | price        | not null
    value_2 | price        | not null
    level_1 | price        | not null
    level_2 | price        | not null
    active  | bool_type    | default 'y'::bpchar
    name    | critter_name |
    cid     | integer      |
    */

    private int oid;
    private int rtyp = 9;
    private double value1;
    private double value2;
    private double level1;
    private double level2;
    private String active = "y";
    private int cid;

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getRtyp() {
        return rtyp;
    }

    public void setRtyp(int rtyp) {
        this.rtyp = rtyp;
    }

    public double getValue1() {
        return value1;
    }

    public void setValue1(double value1) {
        this.value1 = value1;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(double value2) {
        this.value2 = value2;
    }

    public double getLevel1() {
        return level1;
    }

    public void setLevel1(double level1) {
        this.level1 = level1;
    }

    public double getLevel2() {
        return level2;
    }

    public void setLevel2(double level2) {
        this.level2 = level2;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
