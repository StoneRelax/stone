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

@Table(name = "PointRuleGrade")
public class PointRuleGrade extends BaseDo {

    private java.lang.Long uuid;
    private java.lang.Integer grade;
    private java.lang.Long points;
    private java.lang.String otherReq;


    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Long getUuid(){
        return this.uuid;
    }

    public void setUuid(java.lang.Long uuid){
        this.uuid = uuid;
    }
    @Column(name="grade", precision=0,scale=0)
    public java.lang.Integer getGrade(){
        return this.grade;
    }

    public void setGrade(java.lang.Integer grade){
        this.grade = grade;
    }
    @Column(name="points", precision=0,scale=0)
    public java.lang.Long getPoints(){
        return this.points;
    }

    public void setPoints(java.lang.Long points){
        this.points = points;
    }
    @Column(name="other_req", length=512)
    public java.lang.String getOtherReq(){
        return this.otherReq;
    }

    public void setOtherReq(java.lang.String otherReq){
        this.otherReq = otherReq;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        PointRuleGrade obj = (PointRuleGrade) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}