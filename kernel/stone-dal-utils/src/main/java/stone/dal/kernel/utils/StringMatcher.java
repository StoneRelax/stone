package stone.dal.kernel.utils;

import java.util.Vector;

public class StringMatcher {

  private String fPattern = "";

  private boolean fIgnoreWildCards = false;

  private int fLength = fPattern.length();

  private boolean fIgnoreCase = true;

  private boolean fHasLeadingStar;

  private boolean fHasTrailingStar;

  private int fBound = 0;

  private static final char fSingleWildCard = '\u0000';

  private String fSegments[];

  /**
   * @param pattern:         Input text
   * @param ignoreCase:      Whether ignore case
   * @param ignoreWildCards: Whether ignore special char like "*"
   * @param prePostWildAuto: Whether add "*" at the last location of pattern
   */
  public StringMatcher(String pattern, boolean ignoreCase, boolean ignoreWildCards, boolean prePostWildAuto) {
    if (pattern == null)
      throw new IllegalArgumentException();
    fIgnoreCase = ignoreCase;
    fIgnoreWildCards = ignoreWildCards;
    if (prePostWildAuto) {
      pattern = pattern + "*";
    }
    fPattern = pattern;
    fLength = pattern.length();

    parseWildCards();
  }

  public boolean match(String text, int start, int end) {
    if (null == text)
      throw new IllegalArgumentException();
    if (start > end)
      return false;
    if (fIgnoreWildCards)
      return (end - start == fLength) && fPattern.regionMatches(fIgnoreCase, 0, text, start, fLength);
    int segCount = fSegments.length;
    if (segCount == 0 && (fHasLeadingStar || fHasTrailingStar))
      return true;
    if (start == end)
      return fLength == 0;
    if (fLength == 0)
      return false;
    int tlen = text.length();
    if (start < 0)
      start = 0;
    if (end > tlen)
      end = tlen;
    int tCurPos = start;
    int bound = end - fBound;
    if (bound < 0)
      return false;
    int i = 0;
    String current = fSegments[i];
    int segLength = current.length();
    if (!fHasLeadingStar) {
      if (!regExpRegionMatches(text, start, current, 0, segLength)) {
        return false;
      } else {
        ++i;
        tCurPos = tCurPos + segLength;
      }
    }
    if ((fSegments.length == 1) && (!fHasLeadingStar) && (!fHasTrailingStar)) {
      return tCurPos == end;
    }
    while (i < segCount) {
      current = fSegments[i];
      int currentMatch;
      int k = current.indexOf(fSingleWildCard);
      if (k < 0) {
        currentMatch = textPosIn(text, tCurPos, end, current);
        if (currentMatch < 0)
          return false;
      } else {
        currentMatch = regExpPosIn(text, tCurPos, end, current);
        if (currentMatch < 0)
          return false;
      }
      tCurPos = currentMatch + current.length();
      i++;
    }
    if (!fHasTrailingStar && tCurPos != end) {
      int clen = current.length();
      return regExpRegionMatches(text, end - clen, current, 0, clen);
    }
    return i == segCount;
  }

  private void parseWildCards() {
    if (fPattern.startsWith("*"))
      fHasLeadingStar = true;
    if (fPattern.endsWith("*")) {
      if (fLength > 1 && fPattern.charAt(fLength - 2) != '\\') {
        fHasTrailingStar = true;
      }
    }
    Vector temp = new Vector();
    int pos = 0;
    StringBuilder buf = new StringBuilder();
    while (pos < fLength) {
      char c = fPattern.charAt(pos++);
      switch (c) {
      case '\\':
        if (pos >= fLength) {
          buf.append(c);
        } else {
          char next = fPattern.charAt(pos++);
          if (next == '*' || next == '?' || next == '\\') {
            buf.append(next);
          } else {
            buf.append(c);
            buf.append(next);
          }
        }
        break;
      case '*':
        if (buf.length() > 0) {
          temp.addElement(buf.toString());
          fBound += buf.length();
          buf.setLength(0);
        }
        break;
      case '?':
        buf.append(fSingleWildCard);
        break;
      default:
        buf.append(c);
      }
    }
    if (buf.length() > 0) {
      temp.addElement(buf.toString());
      fBound += buf.length();
    }
    fSegments = new String[temp.size()];
    temp.copyInto(fSegments);
  }

  protected boolean regExpRegionMatches(String text, int tStart, String p, int pStart, int plen) {
    while (plen-- > 0) {
      char tchar = text.charAt(tStart++);
      char pchar = p.charAt(pStart++);
      if (!fIgnoreWildCards) {
        if (pchar == fSingleWildCard) {
          continue;
        }
      }
      if (pchar == tchar)
        continue;
      if (fIgnoreCase) {
        if (Character.toUpperCase(tchar) == Character.toUpperCase(pchar))
          continue;
        if (Character.toLowerCase(tchar) == Character.toLowerCase(pchar))
          continue;
      }
      return false;
    }
    return true;
  }

  protected int textPosIn(String text, int start, int end, String p) {
    int plen = p.length();
    int max = end - plen;
    if (!fIgnoreCase) {
      int i = text.indexOf(p, start);
      if (i == -1 || i > max)
        return -1;
      return i;
    }
    for (int i = start; i <= max; ++i) {
      if (text.regionMatches(true, i, p, 0, plen))
        return i;
    }
    return -1;
  }

  protected int regExpPosIn(String text, int start, int end, String p) {
    int plen = p.length();
    int max = end - plen;
    for (int i = start; i <= max; ++i) {
      if (regExpRegionMatches(text, i, p, 0, plen))
        return i;
    }
    return -1;
  }
}
