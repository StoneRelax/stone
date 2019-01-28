package stone.dal.pojo.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.annotation.Sequence;
import stone.dal.common.models.annotation.FieldMapper;
import stone.dal.common.models.annotation.Nosql;
import stone.dal.common.models.annotation.UniqueIndex;
import stone.dal.common.models.annotation.UniqueIndices;
import java.util.List;

@Entity

@Table(name = "Transaction")
public class Transaction extends BaseDo {

    private java.lang.Long uuid;
    private java.lang.String bankIdentifier;
    private java.util.Date txDate;
    private java.lang.String label;
    private java.lang.Long amount;
    private java.lang.Long point;


    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Long getUuid(){
        return this.uuid;
    }

    public void setUuid(java.lang.Long uuid){
        this.uuid = uuid;
    }
    @Column(name="bank_identifier", length=100, nullable=false)
    public java.lang.String getBankIdentifier(){
        return this.bankIdentifier;
    }

    public void setBankIdentifier(java.lang.String bankIdentifier){
        this.bankIdentifier = bankIdentifier;
    }
    @Column(name="tx_date", nullable=false)
    public java.util.Date getTxDate(){
        return this.txDate;
    }

    public void setTxDate(java.util.Date txDate){
        this.txDate = txDate;
    }
    @Column(name="label", length=0)
    public java.lang.String getLabel(){
        return this.label;
    }

    public void setLabel(java.lang.String label){
        this.label = label;
    }
    @Column(name="amount", precision=0,scale=0)
    public java.lang.Long getAmount(){
        return this.amount;
    }

    public void setAmount(java.lang.Long amount){
        this.amount = amount;
    }
    @Column(name="point", precision=0,scale=0)
    public java.lang.Long getPoint(){
        return this.point;
    }

    public void setPoint(java.lang.Long point){
        this.point = point;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Transaction obj = (Transaction) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}