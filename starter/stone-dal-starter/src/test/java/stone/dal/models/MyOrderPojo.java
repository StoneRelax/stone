package stone.dal.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author fengxie
 */
public class MyOrderPojo {
  private Long uuid;

  private String orderNo;

  private String orderDesc;

  private Timestamp createDate;

  private Date expireDate;

  private Boolean disabled;

  private BigDecimal chargeAmt;

  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getOrderDesc() {
    return orderDesc;
  }

  public void setOrderDesc(String orderDesc) {
    this.orderDesc = orderDesc;
  }

  public Timestamp getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Timestamp createDate) {
    this.createDate = createDate;
  }

  public Date getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(Date expireDate) {
    this.expireDate = expireDate;
  }

  public Boolean getDisabled() {
    return disabled;
  }

  public void setDisabled(Boolean disabled) {
    this.disabled = disabled;
  }

  public BigDecimal getChargeAmt() {
    return chargeAmt;
  }

  public void setChargeAmt(BigDecimal chargeAmt) {
    this.chargeAmt = chargeAmt;
  }
}
