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

@Table(name = "AdminRequest")
public class AdminRequest extends BaseDo {

    private java.lang.Long uuid;
    private java.lang.Long userId;
    private java.lang.Integer status;


    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Long getUuid(){
        return this.uuid;
    }

    public void setUuid(java.lang.Long uuid){
        this.uuid = uuid;
    }
    @Column(name="user_id", precision=0,scale=0, nullable=false)
    public java.lang.Long getUserId(){
        return this.userId;
    }

    public void setUserId(java.lang.Long userId){
        this.userId = userId;
    }
    @Column(name="status", precision=0,scale=0)
    public java.lang.Integer getStatus(){
        return this.status;
    }

    public void setStatus(java.lang.Integer status){
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        AdminRequest obj = (AdminRequest) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}