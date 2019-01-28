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

@Table(name = "TransactionLabel")
public class TransactionLabel extends BaseDo {

    private java.lang.Long uuid;
    private java.lang.Integer type;
    private java.lang.String image;
    private java.lang.Boolean status;
    private java.lang.String name;
    private java.lang.String imageUuid;


    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Long getUuid(){
        return this.uuid;
    }

    public void setUuid(java.lang.Long uuid){
        this.uuid = uuid;
    }
    @Column(name="type", precision=0,scale=0, nullable=false)
    public java.lang.Integer getType(){
        return this.type;
    }

    public void setType(java.lang.Integer type){
        this.type = type;
    }
    @Column(name="image", length=0)
    public java.lang.String getImage(){
        return this.image;
    }

    public void setImage(java.lang.String image){
        this.image = image;
    }
    @Column(name="status")
    public java.lang.Boolean getStatus(){
        return this.status;
    }

    public void setStatus(java.lang.Boolean status){
        this.status = status;
    }
    @Column(name="name", length=100)
    public java.lang.String getName(){
        return this.name;
    }

    public void setName(java.lang.String name){
        this.name = name;
    }
    @Column(name="image_uuid", length=64)
    public java.lang.String getImageUuid(){
        return this.imageUuid;
    }

    public void setImageUuid(java.lang.String imageUuid){
        this.imageUuid = imageUuid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        TransactionLabel obj = (TransactionLabel) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}