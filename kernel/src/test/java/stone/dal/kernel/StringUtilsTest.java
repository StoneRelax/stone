package stone.dal.kernel;

import org.apache.oro.text.regex.MalformedPatternException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fengxie
 */
public class StringUtilsTest {

  @Test
  public void testTrim() {
    Assert.assertEquals("test", StringUtils.trim("test"));
    Assert.assertEquals("test", StringUtils.trim("test "));
    Assert.assertEquals("test", StringUtils.trim(" test "));
  }

  @Test
  public void testIsEmpty() {
    Assert.assertTrue(StringUtils.isEmpty(null));
  }

  @Test
  public void testSplitFirst() {
    Assert.assertEquals("test", StringUtils.splitFirst("test,a", ','));
  }

  @Test
  public void testReplaceNull() {
    Assert.assertEquals("test", StringUtils.replaceNull("test"));
    Assert.assertEquals("", StringUtils.replaceNull(null));
  }

  @Test
  public void testReplaceNull_ByReplaceAs() {
    Assert.assertEquals("test", StringUtils.replaceNull("test", "a"));
    Assert.assertEquals("a", StringUtils.replaceNull(null, "a"));
  }

  public void subString() {
  }

  public void subString_withinRange() {
  }

  public void subString_length() {
  }

  public void convertString2Map() {
  }

  public void leftPad() {
  }

  public void padIndent() {
  }

  public void splitString() {
  }

  public void splitStrNoTrim() {
  }

  public void insertStrAtPosition() {
  }

  public void combineString() {
  }

  public void combineString_ByToken() {
  }

  public void string2List() {
  }

  public void combineStringWithQuotation() {
  }

  public void printMatrix() {
  }

  public void concatStringWithSeparator() {
  }

  public void convertFirstLetterLowerCase(String str) {

  }

  public void convertFirstLetterUpperCase(String str) {

  }

  public void splitContent(StringBuffer sb, int numPreRow) {

  }

  public void splitContent(String src, int numPreRow) {

  }

  public void recoverIllegalMethodName(String methodName) {

  }

  public void recoverUnderscorePropertyName(String propertyName) {

  }

  public void recoverIllegalPropertyName(String illegalPropertyName) {

  }

  public void getClassFullPath(String className) {

  }

  public void deleteDuplicatedString(String[] stringArray) {

  }

  public void getMatchParams() throws MalformedPatternException {

  }

  public void htmEncode() {

  }

  public void convertColor2Str() {
  }
}