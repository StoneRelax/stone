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

@Table(name = "BankUser")
public class BankUser extends BaseDo {

    private java.lang.Long uuid;
    private java.lang.String name;
    private java.lang.String bankIdentifier;


    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Long getUuid(){
        return this.uuid;
    }

    public void setUuid(java.lang.Long uuid){
        this.uuid = uuid;
    }
    @Column(name="name", length=128, nullable=false)
    public java.lang.String getName(){
        return this.name;
    }

    public void setName(java.lang.String name){
        this.name = name;
    }
    @Column(name="bank_identifier", length=100, nullable=false)
    public java.lang.String getBankIdentifier(){
        return this.bankIdentifier;
    }

    public void setBankIdentifier(java.lang.String bankIdentifier){
        this.bankIdentifier = bankIdentifier;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        BankUser obj = (BankUser) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}