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

@Table(name = "Permission")
public class Permission extends BaseDo {

    private java.lang.Integer uuid;
    private java.lang.String permissionId;
    private java.lang.String description;


    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Integer getUuid(){
        return this.uuid;
    }

    public void setUuid(java.lang.Integer uuid){
        this.uuid = uuid;
    }
    @Column(name="permission_id", length=100, nullable=false)
    public java.lang.String getPermissionId(){
        return this.permissionId;
    }

    public void setPermissionId(java.lang.String permissionId){
        this.permissionId = permissionId;
    }
    @Column(name="description", length=100)
    public java.lang.String getDescription(){
        return this.description;
    }

    public void setDescription(java.lang.String description){
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Permission obj = (Permission) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}