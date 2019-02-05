package stone.dal.common.models;

import org.springframework.data.elasticsearch.annotations.Document;
import stone.dal.adaptor.es.example.SampleEntityListener;
import stone.dal.common.models.annotation.Sequence;
import stone.dal.common.models.data.BaseDo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Document(indexName = "bank_transaction")
@Table(name = "bank_transaction", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@EntityListeners(value = SampleEntityListener.class)
public class BankTransaction  extends BaseDo {
    private Long uuid;
    private String user;
    private int type;
    private long amount;
    private long score;

    @Id
    @Column(name = "uuid", precision = 18, scale = 0)
    @Sequence(generator="sequence")
    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    @Column(name = "user_name", length = 50)
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name = "type", length = 4, nullable = false)
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Column(name = "amount", length = 50, nullable = false)
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Column(name = "score", length = 50, nullable = false)
    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
