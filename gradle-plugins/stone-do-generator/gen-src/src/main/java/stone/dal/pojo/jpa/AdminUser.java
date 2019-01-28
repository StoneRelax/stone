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

@Table(name = "AdminUser")
public class AdminUser extends BaseDo {

    private java.lang.Long uuid;
    private java.lang.String name;
    private java.lang.String phone;
    private java.lang.String mail;
    private java.lang.String password;
    private java.lang.String remarks;

    private List<stone.dal.pojo.jpa.AdminRole> roles;

    @javax.persistence.Id
@Column(name="uuid", precision=0,scale=0, nullable=false)
@Sequence(generator = "sequence")
    public java.lang.Long getUuid(){
        return this.uuid;
    }

        public void setUuid(java.lang.Long uuid){
        this.uuid = uuid;
    }
    @Column(name="name", length=64, nullable=false)
    public java.lang.String getName(){
        return this.name;
    }

    public void setName(java.lang.String name){
        this.name = name;
    }
    @Column(name="phone", length=100)
    public java.lang.String getPhone(){
        return this.phone;
    }

    public void setPhone(java.lang.String phone){
        this.phone = phone;
    }
    @Column(name="mail", length=128)
    public java.lang.String getMail(){
        return this.mail;
    }

    public void setMail(java.lang.String mail){
        this.mail = mail;
    }
    @Column(name="password", length=32, nullable=false)
    public java.lang.String getPassword(){
        return this.password;
    }

    public void setPassword(java.lang.String password){
        this.password = password;
    }
    @Column(name="remarks", length=256)
    public java.lang.String getRemarks(){
        return this.remarks;
    }

    public void setRemarks(java.lang.String remarks){
        this.remarks = remarks;
    }

    @javax.persistence.ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @javax.persistence.JoinTable(name = "ADMINUSER_ADMINROLE", joinColumns = {@JoinColumn(name = "adminuser_uuid", referencedColumnName = "uuid")},
    inverseJoinColumns = {@JoinColumn(name = "adminrole_uuid", referencedColumnName = "uuid")})
    public java.util.List<stone.dal.pojo.jpa.AdminRole> getRoles(){
        return this.roles;
    }

    public void setRoles(java.util.List<stone.dal.pojo.jpa.AdminRole> roles){
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        AdminUser obj = (AdminUser) o;
        if (getUuid() != null ? !getUuid().equals(obj.getUuid()) : obj.getUuid() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getUuid() != null ? getUuid().hashCode() : 0;
    }

}