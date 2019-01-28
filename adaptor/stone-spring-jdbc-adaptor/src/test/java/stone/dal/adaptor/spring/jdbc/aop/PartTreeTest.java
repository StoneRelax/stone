package stone.dal.adaptor.spring.jdbc.aop;

import java.util.Date;
import java.util.Iterator;
import org.junit.Test;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

public class PartTreeTest {

  @Test
  public void testMethod() {
    PartTree tree = new PartTree("findByContactPersonAndAddressOrCreateDate", Contact.class);
//    tree.hasPredicate()
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

  }
}
