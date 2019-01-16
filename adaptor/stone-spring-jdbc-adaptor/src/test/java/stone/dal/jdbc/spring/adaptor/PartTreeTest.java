package stone.dal.jdbc.spring.adaptor;

import java.util.Date;
import java.util.Iterator;
import org.junit.Test;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.util.Streamable;

public class PartTreeTest {

  @Test
  public void testMethod() {
    PartTree tree = new PartTree("findByContactPersonOrAddressOrCreateDate", Contact.class);
//    tree.hasPredicate()
    Streamable<Part> parts = tree.getParts();
    Iterator<PartTree.OrPart> orPartIterator = tree.iterator();
    while (orPartIterator.hasNext()) {
      PartTree.OrPart orPart = orPartIterator.next();
      Iterator<Part> partIterator = orPart.iterator();
      while (partIterator.hasNext()) {
        System.out.println(partIterator.next());
      }
//      System.out.println(orPart);
    }
  }

  class Condition {
    private String contactPerson;

    private String address;

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public String getContactPerson() {
      return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
      this.contactPerson = contactPerson;
    }
  }

  class Contact {
    private String contactPerson;

    private String address;

    private Date createDate;

    public String getContactPerson() {
      return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
      this.contactPerson = contactPerson;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public Date getCreateDate() {
      return createDate;
    }

    public void setCreateDate(Date createDate) {
      this.createDate = createDate;
    }
  }
}
