package stone.dal.ext.es.models;

public class TxAggregationRecord {
    private String userName;
    private long userTotalCount;
    private String type;
    private long typeTotalCount;
    private double value;
    private double score;

    public TxAggregationRecord(String userName, long userTotalCount, String type, long typeTotalCount, double value,double score) {
        this.userName = userName;
        this.userTotalCount = userTotalCount;
        this.type = type;
        this.typeTotalCount = typeTotalCount;
        this.value = value;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserTotalCount() {
        return userTotalCount;
    }

    public void setUserTotalCount(long userTotalCount) {
        this.userTotalCount = userTotalCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTypeTotalCount() {
        return typeTotalCount;
    }

    public void setTypeTotalCount(long typeTotalCount) {
        this.typeTotalCount = typeTotalCount;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
