package stone.dal.tools.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.regex.*;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

/**
 * String Utilites class
 *
 * @author feng.xie
 * @version $Revision: 1.7 $
 */
public class StringUtilities {

//    private static TraceProducer tracer = Log.tracer(StringUtilities.class);

    /**
     * private constructor
     */
    private StringUtilities() {
    }

    /**
     * Trim String, if String is null, return null
     *
     * @param str <code>String</code>
     * @return @see java.lang.String#trim
     */
    public static String trim(String str) {
        String strReturn;
        if (str != null) {
            strReturn = str.trim();
        } else {
            strReturn = str;
        }
        return strReturn;
    }

    /**
     * Return is String empty or blank, object is measured with its' string value.
     * And its' string value with leading and trailing white
     * space removed, or this string if it has no leading or
     * trailing white space.
     *
     * @param o object which is measured with its' string value
     * @return whether string value is empty or blank
     */
    public static boolean isEmptyOrBlankString(Object o) {
        boolean r = true;
        if (!isEmpty(o)) {
            if (o instanceof StringBuffer) {
                r = isEmpty(o.toString().trim());
            } else {
                r = o instanceof String && isEmpty(((String) o).trim());
            }
        }
        return r;
    }

    /**
     * Return is String empty or blank, object is measured with its' string value.
     *
     * @param o object which is measured with its' string value
     * @return whether string value is empty or blank
     */
    public static boolean isEmptyOrBlankStringWithOutTrim(Object o) {
        boolean r = true;
        if (!isEmpty(o)) {
            if (o instanceof StringBuffer) {
                r = isEmpty(o.toString());
            } else {
                r = o instanceof String && isEmpty(o);
            }
        }
        return r;
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
     * @param strInput   string to be left padded
     * @param intLength  left pad the input string to intLength( in bytes )
     * @param padingChar padding char
     * @return the string after left padding
     */
    public static String leftPad(String strInput, int intLength, char padingChar) {
        if (intLength > strInput.length()) {
            byte[] byteResult = new byte[intLength];
            try {
                byte[] byteInput = strInput.getBytes("utf-8");
                System.arraycopy(byteInput, 0, byteResult, intLength - byteInput.length, byteInput.length);
                for (int i = 0; i < (intLength - byteInput.length); i++) {
                    byteResult[i] = (byte) padingChar;
                }
                return new String(byteResult, "utf-8");
            } catch (UnsupportedEncodingException e) {
//                tracer.logError(e);
            }
            return null;
        } else {
            return strInput;
        }
    }

    /**
     * Pad indent of text
     *
     * @param txt    Text
     * @param indent Number of indent
     * @return Text
     */
    public static String padIndent(String txt, int indent) {
        return StringUtilities.leftPad(txt, getLength4Pad(txt) + indent, '\t');
    }

    private static int getLength4Pad(String text) {
        try {
            return text.getBytes("utf-8").length;
        } catch (UnsupportedEncodingException e) {
//            tracer.logError(e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Split String to List
     *
     * @param strInput  String with splitor
     * @param separator Splitor, such as ',' '|'
     * @return String Item in List
     */
    public static List<String> splitString(String strInput, String separator) {
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
                listResult.add(tok.trim());
                start = end;
            } else {
                String tok = strInput.substring(start, separatorIndex);
                listResult.add(tok.trim());
                start = separatorIndex + separator.length();
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
    public static List<String> splitStringWithoutTrim(String strInput, String separator) {
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
    public static String insertStringWithPosition(String src, String strInput, int position) {
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
     * @param strsInput Object array which includes elements to be combined
     * @param strToken  split token
     * @return string combined with <code>strsInput</code>, splited by <code>strToken</code>
     */
    public static String combineString(Object[] strsInput, String strToken) {
        if (strsInput == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        int l = strsInput.length;
        for (int i = 0; i < l; i++) {
            sb.append(replaceNull(strsInput[i]));
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
        String[] strArray = null;
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
            List<String> result = new ArrayList<String>();
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
        StringBuffer sb = new StringBuffer("");
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
        StringBuffer resultSt = new StringBuffer();
        int maxRow = matrix.length;
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if ('\0' == matrix[i][j]) {
                    resultSt.append(" ");
                } else {
                    resultSt.append(matrix[i][j]);
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
        String str = "";
        if (isEmpty(str1) && isEmpty(str2)) {
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
    public static String convertFirstAlphetLowerCase(String str) {
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
    public static String convertFirstAlphetUpperCase(String str) {
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
        List<String> result = new ArrayList<String>();
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
     * Recover an illegal string represents a method name of POJO. Convert it to a legal name.
     *
     * @param methodName illegal property name
     * @return legal method name
     */
    public static String recoverIllegalMethodName(String methodName) {
        String illegalMethodName = methodName;
        if (methodName.startsWith("set") || methodName.startsWith("get")) {
            illegalMethodName = illegalMethodName.substring(3, 4).toUpperCase() + illegalMethodName.substring(4);
        }
        return illegalMethodName;
    }

    /**
     * Recover under score property name
     *
     * @param propertyName Property name
     * @return Property with underscore
     */
    public static String recoverUnderscorePropertyName(String propertyName) {
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
     * Recover an illegal string represents a property name of POJO. Convert it to a legal name.
     *
     * @param illegalPropertyName illegal property name
     * @return legal property name
     */
    public static String recoverIllegalPropertyName(String illegalPropertyName) {
        String _iPropertyName = StringUtilities.replaceNull(illegalPropertyName);
        if (illegalPropertyName.contains(" ")
                || illegalPropertyName.contains("_")
                || illegalPropertyName.contains("-")) {
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
     * Separte a full class name into string array.
     *
     * @param className Class name with full package name
     * @return String[0]=className, String[1]=packageName
     */
    public static String[] getClassFullPath(String className) {
        String[] _segments = StringUtilities.splitString2Array(className, ".");
        String packageName = "";
        for (int i = 0; i < _segments.length - 1; i++) {
            packageName += _segments[i];
            if (i < _segments.length - 2) {
                packageName += ".";
            }
        }
        if (StringUtils.isEmpty(packageName)) {
            packageName = "java.lang";
        }
        String _className = _segments[_segments.length - 1];
        String[] path = new String[2];
        path[0] = _className;
        path[1] = packageName;
        return path;
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
            if (!StringUtils.isEmpty(s) && !uniqueAccounts.contains(s))
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
     * @param _regExp  Regular expression
     * @param srcInput Source input
     * @return Regular expressions
     * @throws MalformedPatternException Runtime expression of regular expression parsing
     */
    public static Set<String> getMatchParams(String _regExp, String srcInput) throws MalformedPatternException {
        Set<String> result = new HashSet<String>();
        PatternCompiler compiler = new Perl5Compiler();
        PatternMatcher matcher = new Perl5Matcher();
        Pattern pattern = compiler.compile(_regExp);
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
            StringBuffer stringbuffer = new StringBuffer();
            int j = plainText.length();
            for (int i = 0; i < j; i++) {
                char c = plainText.charAt(i);
                switch (c) {
                    case 60:
                        stringbuffer.append("&lt; ");
                        break;
                    case 62:
                        stringbuffer.append("&gt; ");
                        break;
                    case 38:
                        stringbuffer.append("&amp; ");
                        break;
                    case 34:
                        stringbuffer.append("&quot; ");
                        break;
                    case 169:
                        stringbuffer.append("&copy; ");
                        break;
                    case 174:
                        stringbuffer.append("&reg; ");
                        break;
                    case 165:
                        stringbuffer.append("&yen; ");
                        break;
                    case 8364:
                        stringbuffer.append("&euro; ");
                        break;
                    case 8482:
                        stringbuffer.append("&#153; ");
                        break;
                    case 13:
                        if (i < j - 1 && plainText.charAt(i + 1) == 10) {
                            stringbuffer.append(" <br> ");
                            i++;
                        }
                        break;
                    case 32:
                        stringbuffer.append(" ");
                        break;

                    default:
                        stringbuffer.append(c);
                        break;
                }
            }
            return stringbuffer.toString();
        }
        return "";
    }

    public static String convertColor2Str(Color color) {
        String r = Integer.toHexString(color.getRed());
        r = r.length() < 2 ? ('0' + r) : r;
        String g = Integer.toHexString(color.getGreen());
        g = g.length() < 2 ? ('0' + g) : g;
        String b = Integer.toHexString(color.getBlue());
        b = b.length() < 2 ? ('0' + b) : b;
        return '#' + r + g + b;
    }
}
/**
 * History:
 *
 * $Log: StringUtilities.java,v $
 * Revision 1.7  2010/03/18 12:07:33  fxie
 * no message
 *
 * Revision 1.6  2009/08/11 13:21:31  fxie
 * no message
 *
 * Revision 1.5  2009/08/11 11:09:54  fxie
 * no message
 *
 * Revision 1.4  2009/06/30 12:56:59  fxie
 * no message
 *
 * Revision 1.3  2009/03/23 17:16:01  fxie
 * no message
 *
 * Revision 1.2  2009/03/23 12:49:02  fxie
 * no message
 *
 * Revision 1.1  2009/02/24 06:09:46  fxie
 * no message
 *
 * Revision 1.1  2008/10/17 10:03:26  fxie
 * *** empty log message ***
 *
 * Revision 1.9  2008/08/21 14:37:42  fxie
 * add new component
 *
 * Revision 1.8  2008/01/23 11:37:49  fxie
 * adapt property editor context
 *
 * Revision 1.7  2008/01/13 07:08:50  fxie
 * adapt property editor context
 *
 * Revision 1.6  2007/09/29 11:23:48  fxie
 * add jdbc query
 *
 * Revision 1.5  2007/09/10 14:02:53  fxie
 * add jdbc query fixture
 *
 * Revision 1.4  2007/09/05 10:47:08  fxie
 * add orm meta
 *
 * Revision 1.3  2007/07/28 15:18:00  fxie
 * no message
 *
 * Revision 1.2  2007/07/02 15:14:41  fxie
 * Failed commit: Default (2)
 *
 * Revision 1.1  2007/06/30 02:21:25  fxie
 * Failed commit: Default (2)
 *
 * Revision 1.1  2007/06/18 11:38:23  fxie
 * no message
 *
 * Revision 1.1  2007/06/16 06:22:22  fxie
 * no message
 *
 * Revision 1.1  2007/06/16 04:22:26  fxie
 * no message
 *
 * Revision 1.1  2007/06/05 14:44:46  fxie
 * no message
 *
 */


