package stone.dal.kernel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * String Utils class
 *
 * @author feng.xie
 * @version $Revision: 1.7 $
 */
public class StringUtils {

  /**
   * private constructor
   */
  private StringUtils() {
  }

  /**
   * Trim String, if String is null, return null
   *
   * @param str <code>String</code>
   * @return @see java.lang.String#trim
   */
  public static String trim(String str) {
    String strReturn = null;
    if (str != null) {
      strReturn = str.trim();
    }
    return strReturn;
  }

  /**
   * Return is boolean which is indicated whether object is empty or its' string value is ""
   *
   * @param o object which is measured with its' string value
   * @return boolean value indicated whether object is empty or its' string value is ""
   */
  public static boolean isEmpty(Object o) {
    return o == null || "".equals(o);
  }

  /**
   * Find first element, Split by char
   *
   * @param src string to be split
   * @param ch  which is used to split <code>src</code>
   * @return string after split with first char
   */
  public static String splitFirst(String src, char ch) {
    return src.substring(0, src.indexOf(ch));
  }

  /**
   * Get Object.toString, if null return ""  else return object.toString()
   *
   * @param obj object to be replaced with ""
   * @return obj.toString or ""
   */
  public static String replaceNull(Object obj) {
    if (obj == null) {
      return "";
    } else {
      return obj.toString();
    }
  }

  /**
   * Use specific String replace null;
   *
   * @param src       object to be replaced with replaceAs
   * @param replaceAs string used to replace
   * @return src or replaceAs
   */
  public static String replaceNull(String src, String replaceAs) {
    if (src == null) {
      return replaceAs;
    } else {
      return src;
    }
  }

  /**
   * Find substring start by one string end by another string
   *
   * @param src   source string to be split
   * @param start start string
   * @param end   end string
   * @return string between start and end
   */
  public static String subString(String src, String start, String end) {
    int iStart = src.indexOf(start);
    int iEnd = src.indexOf(end);
    if ((iStart == -1) || (iEnd == -1)) {
      return null;
    }
    iEnd += end.length();
    return src.substring(iStart, iEnd);
  }

  /**
   * Find substring start by one index end by index
   *
   * @param src        string to be splited
   * @param startIndex start index
   * @param endIndex   end index
   * @return string between start and end
   */
  public static String subString(String src, int startIndex, int endIndex) {
    if ((startIndex == -1) || (endIndex == -1)) {
      return null;
    }
    return src.substring(startIndex, endIndex);
  }

  /**
   * Find substring start by first index and end by length
   *
   * @param src    source string to be split
   * @param length end index
   * @return string between start and end
   */
  public static String subString(String src, int length) {
    if (src == null) {
      return "";
    }
    if (src.length() <= length) {
      return src;
    } else {
      return src.substring(0, length);
    }
  }

  /**
   * Convert String to Map, "A=B&C=D" -> [A=B, C=D]
   *
   * @param srcStr    string to convert
   * @param separator separator for split <code>srcStr</code>
   * @return <code>Map</code>
   */
  public static Map<String, String> convertString2Map(String srcStr, String separator) {
    StringTokenizer st1 = new StringTokenizer(srcStr, separator);
    Map<String, String> result = new HashMap<String, String>();
    while (st1.hasMoreTokens()) {
      String field = st1.nextToken();
      StringTokenizer st2 = new StringTokenizer(field, "=");
      String key;
      String value = null;
      if (st2.countTokens() == 2) {
        key = st2.nextToken();
        value = st2.nextToken();
      } else {
        key = st2.nextToken();
      }
      result.put(key, value);
    }
    return result;
  }

  /**
   * Pad specific string to string left side
   *
   * @param strInput    string to be left padded
   * @param intLength   left pad the input string to intLength( in bytes )
   * @param paddingChar padding char
   * @return the string after left padding
   */
  public static String leftPad(String strInput, int intLength, char paddingChar) {
    try {
      if (intLength > strInput.length()) {
        byte[] byteResult = new byte[intLength];
        byte[] byteInput = strInput.getBytes("utf-8");
        System.arraycopy(byteInput, 0, byteResult, intLength - byteInput.length, byteInput.length);
        for (int i = 0; i < (intLength - byteInput.length); i++) {
          byteResult[i] = (byte) paddingChar;
        }
        return new String(byteResult, "utf-8");
      } else {
        return strInput;
      }
    } catch (Exception ex) {
      throw new KernelRuntimeException(ex);
    }
  }

  /**
   * Pad indent of text
   *
   * @param txt    Text
   * @param indent Number of indent
   * @return Text
   */
  public static String padIndent(String txt, int indent) throws Exception {
    return StringUtils.leftPad(txt, getLength4Pad(txt) + indent, '\t');
  }

  private static int getLength4Pad(String text) throws UnsupportedEncodingException {
    return text.getBytes("utf-8").length;
  }

  /**
   * Split String to List
   *
   * @param strInput  String with splitor
   * @param separator Splitor, such as ',' '|'
   * @return String Item in List
   */
  public static List<String> splitString(String strInput, String separator) {
    List<String> listResult = new ArrayList<>();
    if (strInput.contains(separator)) {
      int start = 0;
      int end = strInput.length();
      while (start < end) {
        int separatorIndex = strInput.indexOf(separator, start);
        if (separatorIndex < 0) {
          String tok = strInput.substring(start);
          listResult.add(tok.trim());
          start = end;
        } else if (separatorIndex > 0) {
          String tok = strInput.substring(start, separatorIndex);
          listResult.add(tok.trim());
          start = separatorIndex + separator.length();
        } else {
          start = separator.length();
        }
      }
    }
    return listResult;
  }

  /**
   * Split String to List
   *
   * @param strInput  String with Separator
   * @param separator Separator, such as ',' '|'
   * @return String Item in List
   */
  public static List<String> splitStrNoTrim(String strInput, String separator) {
    List<String> listResult = new ArrayList<String>();
    if (strInput == null) {
      return null;
    }
    int start = 0;
    int end = strInput.length();
    while (start < end) {
      int separatorIndex = strInput.indexOf(separator, start);
      if (separatorIndex < 0) {
        String tok = strInput.substring(start);
        listResult.add(tok);
        start = end;
      } else {
        String tok = strInput.substring(start, separatorIndex);
        listResult.add(tok);
        start = separatorIndex + separator.length();
      }
    }
    return listResult;
  }

  /**
   * Insert string at specified position of <code>src</code>
   *
   * @param src      string to be inserted
   * @param strInput insert content
   * @param position index
   * @return new string with insert content
   */
  public static String insertStrAtPosition(String src, String strInput, int position) {
    String result = src;
    if (src.length() > position) {
      String firstSection = subString(src, position);
      String secondSection = subString(src, position, src.length());
      result = firstSection + strInput + secondSection;
    }
    return result;
  }

  /**
   * Combine string array as string, split by token string
   *
   * @param strs     Object array which includes elements to be combined
   * @param strToken split token
   * @return string combined with <code>strsInput</code>, splited by <code>strToken</code>
   */
  public static String combineString(Object[] strs, String strToken) {
    if (strs == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int l = strs.length;
    for (int i = 0; i < l; i++) {
      sb.append(replaceNull(strs[i]));
      if (i < l - 1) {
        sb.append(strToken);
      }
    }
    return sb.toString();
  }

  /**
   * Combine list as string with given token
   *
   * @param inputList list which includes elements requiring to be combined
   * @param token     Token
   * @return String combined with <code>inputList</code> with <code>token</code>
   */
  public static String combineString(Collection inputList, String token) {
    if (inputList == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Iterator iterator = inputList.iterator(); iterator.hasNext(); ) {
      sb.append(iterator.next().toString());
      if (iterator.hasNext()) {
        sb.append(token);
      }
    }
    return sb.toString();
  }

  /**
   * Split String to Array
   *
   * @param strInput  Input String with splitor
   * @param separator String Splitor
   * @return String Item in Array
   */
  public static String[] splitString2Array(String strInput, String separator) {
    String[] strArray = new String[0];
    List lstResult = splitString(strInput, separator);
    if ((lstResult != null) && (lstResult.size() > 0)) {
      strArray = new String[lstResult.size()];
      int size = lstResult.size();
      for (int i = 0; i < size; i++) {
        strArray[i] = (String) lstResult.get(i);
      }
    }
    return strArray;
  }

  /**
   * Convert string to list, every char of string would be convert to string and added to list
   *
   * @param source string to be converted
   * @return <code>List</code>
   */
  public static List<String> string2List(String source) {
    if (source != null && source.length() > 0) {
      List<String> result = new ArrayList<>();
      for (int i = 0; i < source.length(); i++) {
        result.add(String.valueOf(source.charAt(i)));
      }

      return result;
    } else {
      return null;
    }
  }

  /**
   * Combine a list to a string of the list's meta with a separator
   * if the element is a string then surround with "'".
   * It is frequently used in sql statement!
   *
   * @param list      <code>List</code>
   * @param separator specified separator
   * @return converted string with <code>separator</code>
   */
  public static String combineStringWithQuotation(List list, String separator) {
    StringBuilder sb = new StringBuilder();
    if (list != null && list.size() > 0) {
      for (Object item : list) {
        if (sb.length() != 0) {
          sb.append(separator);
        }
        if (item instanceof String) {
          String strItem = (String) item;
          sb.append("'");
          sb.append(strItem);
          sb.append("'");
        } else {
          sb.append(item);
        }
      }
    }
    return sb.toString();
  }

  /**
   * Combine matrix to string
   *
   * @param matrix char array to be combined
   * @return string corresponded to matrix
   */
  public static String printMatrix(char[][] matrix) {
    StringBuilder resultSt = new StringBuilder();
    for (char[] row : matrix) {
      for (char column : row) {
        if ('\0' == column) {
          resultSt.append(" ");
        } else {
          resultSt.append(column);
        }
      }
    }
    return resultSt.toString();
  }

  /**
   * Concatenate  string with separator, skip string with null or "" value
   *
   * @param str1      <code>String</code>
   * @param str2      <code>String</code>
   * @param separator <code>String</code>
   * @return string after concatenated str1,str2
   */
  public static String concatStringWithSeparator(String str1, String str2, String separator) {
    String str = null;
    if (isEmpty(str1) && isEmpty(str2)) {
      str = "";
    } else if (isEmpty(str1)) {
      str = str2;
    } else if (isEmpty(str2)) {
      str = str1;
    } else {
      str = str1 + separator + str2;
    }
    return str;
  }

  /**
   * Convert object to string, if object is null, return "".
   *
   * @param str Object to be converted.
   * @return if object is null, return "". else object.toString();
   */
  public static String firstChar2LowerCase(String str) {
    if (isEmpty(str)) {
      return null;
    } else {
      String firstLetter = str.substring(0, 1).toLowerCase();
      return firstLetter + str.substring(1);
    }
  }

  /**
   * Convert object to string, if object is null, return "".
   *
   * @param str Object to be converted.
   * @return if object is null, return "". else object.toString();
   */
  public static String firstChar2UpperCase(String str) {
    if (isEmpty(str)) {
      return null;
    } else {
      String firstLetter = str.substring(0, 1).toUpperCase();
      return firstLetter + str.substring(1);
    }
  }

  /**
   * Split <code>sb</code> to <code>numPreRow</code>
   *
   * @param sb        string buffer to be splited
   * @param numPreRow row number
   * @return <code>ArrayList</code>
   */
  public static List<String> splitContent(StringBuffer sb, int numPreRow) {
    return splitContent(sb.toString(), numPreRow);
  }

  /**
   * Split string to list which includes elements with numPreRow length
   *
   * @param src       source string
   * @param numPreRow row number
   * @return <code>ArrayList</code>
   */
  public static List<String> splitContent(String src, int numPreRow) {
    List<String> result = new ArrayList<>();
    if (src == null || "".equals(src)) {
      return null;
    }
    if (numPreRow == 0) {
      result.add(src);
      return result;
    }
    for (int i = 0; i < src.length(); i++) {
      if ((i + 1) * numPreRow <= src.length()) {
        String temStr = src.substring(i * numPreRow, i * numPreRow + numPreRow);
        result.add(temStr);
      } else if (i * numPreRow <= src.length() && (i + 1) * numPreRow > src.length()) {
        result.add(src.substring(i * numPreRow, src.length()));
      }
    }
    return result;
  }

  /**
   * Convert method name (setter, getter) to a property name.
   *
   * @param method Method name
   * @return Property Name
   */
  public static String getPropertyNameByMethod(Method method) {
    String methodName = method.getName();
    String validName = methodName;
    if (methodName.startsWith("set") || methodName.startsWith("get")) {
      validName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }
    return validName;
  }

  /**
   * Recover under score property name
   *
   * @param propertyName Property name
   * @return Property with underscore
   */
  public static String canonicalPropertyName2DBField(String propertyName) {
    char[] propertyArray = propertyName.toCharArray();
    StringBuilder sb = new StringBuilder();
    int i = 0;
    char lc = 0;
    for (char c : propertyArray) {
      if (i != 0) {
        if (Character.isUpperCase(c) && Character.isLowerCase(lc)) {
          sb.append('_');
          sb.append(Character.toLowerCase(c));
        } else {
          sb.append(c);
        }
      } else {
        sb.append(Character.toLowerCase(c));
      }
      lc = c;
      i++;
    }
    return sb.toString();
  }

  /**
   * Convert db name to a property name following java bean standard.
   *
   * @param dbName illegal property name
   * @return legal property name
   */
  public static String dbFieldName2BeanProperty(String dbName) {
    String _iPropertyName = StringUtils.replaceNull(dbName);
    if (dbName.contains(" ")
        || dbName.contains("_")
        || dbName.contains("-")) {
      char[] charArray = _iPropertyName.toCharArray();
      char[] legalCharArray = new char[charArray.length];
      int emptyCount = 0;
      for (int i = 0; i < charArray.length; i++) {
        char c = charArray[i];
        if (c == '_' || c == '-' || c == ' ') {
          if (i < charArray.length - 1) {
            legalCharArray[i] = ' ';
            legalCharArray[i + 1] = Character.toUpperCase(charArray[i + 1]);
            i++;
            emptyCount++;
          }
        } else {
          legalCharArray[i] = c;
        }
      }
      char[] _result = new char[legalCharArray.length - emptyCount];
      for (int i = 0, index = 0; i < legalCharArray.length; i++) {
        char c = legalCharArray[i];
        if (c != ' ') {
          _result[index] = c;
          index++;
        }
      }
      _iPropertyName = String.valueOf(_result);
    }
    return _iPropertyName;
  }

  /**
   * Filter duplicated string value
   *
   * @param stringArray String array
   * @return Array after filter.
   */
  public static String[] deleteDuplicatedString(String[] stringArray) {
    if (stringArray == null) {
      return null;
    }
    List<String> uniqueAccounts = new ArrayList<String>();
    for (String s : stringArray) {
      if (!org.apache.commons.lang.StringUtils.isEmpty(s) && !uniqueAccounts.contains(s))
        uniqueAccounts.add(s);
    }
    if (uniqueAccounts.size() == 0) {
      return null;
    } else {
      return uniqueAccounts.toArray(new String[uniqueAccounts.size()]);
    }
  }

  /**
   * Get match 's parameters
   *
   * @param regExp   Regular expression
   * @param srcInput Source input
   * @return Regular expressions
   * @throws org.apache.oro.text.regex.MalformedPatternException Runtime expression of regular expression parsing
   */
  public static Set<String> getMatchParams(String regExp, String srcInput) throws MalformedPatternException {
    Set<String> result = new HashSet<>();
    PatternCompiler compiler = new Perl5Compiler();
    PatternMatcher matcher = new Perl5Matcher();
    Pattern pattern = compiler.compile(regExp);
    PatternMatcherInput input = new PatternMatcherInput(srcInput);
    while (matcher.contains(input, pattern)) {
      MatchResult resultMatch = matcher.getMatch();
      result.add(resultMatch.group(1));
    }
    return result;
  }

  /**
   * HTML encoding
   *
   * @param plainText Plain text
   * @return HTML encode
   */
  public static String htmEncode(String plainText) {
    if (!isEmpty(plainText)) {
      StringBuilder sb = new StringBuilder();
      int j = plainText.length();
      for (int i = 0; i < j; i++) {
        char c = plainText.charAt(i);
        switch (c) {
        case 60:
          sb.append("&lt; ");
          break;
        case 62:
          sb.append("&gt; ");
          break;
        case 38:
          sb.append("&amp; ");
          break;
        case 34:
          sb.append("&quot; ");
          break;
        case 169:
          sb.append("&copy; ");
          break;
        case 174:
          sb.append("&reg; ");
          break;
        case 165:
          sb.append("&yen; ");
          break;
        case 8364:
          sb.append("&euro; ");
          break;
        case 8482:
          sb.append("&#153; ");
          break;
        case 13:
          if (i < j - 1 && plainText.charAt(i + 1) == 10) {
            sb.append(" <br> ");
            i++;
          }
          break;
        case 32:
          sb.append(" ");
          break;

        default:
          sb.append(c);
          break;
        }
      }
      return sb.toString();
    }
    return "";
  }

  public static String readInputStream(InputStream input) throws Exception {
    byte[] byt = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int b = 0;
    b = input.read();
    while (b != -1) {
      baos.write(b);
      b = input.read();
    }
    byt = baos.toByteArray();
    baos.close();
    input.close();
    return new String(byt, "utf-8");
  }
}


