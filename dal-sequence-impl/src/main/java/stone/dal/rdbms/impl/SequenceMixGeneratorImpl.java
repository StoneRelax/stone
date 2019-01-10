package stone.dal.rdbms.impl;

import stone.dal.kernel.KernelRuntimeException;
import stone.dal.kernel.StringUtils;
import stone.dal.sequence.api.SequenceGenerator;
import stone.dal.sequence.api.meta.SequenceMeta;

import java.util.*;

import static stone.dal.kernel.KernelUtils.date_2_str_pattern;
import static stone.dal.kernel.KernelUtils.get_v_recursive;
import static stone.dal.kernel.KernelUtils.repl_emp;
import static stone.dal.kernel.KernelUtils.replace;
import static stone.dal.kernel.KernelUtils.str_emp;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * @author fengxie
 */
public class SequenceMixGeneratorImpl implements SequenceGenerator<String> {
	private static final String FIELD_EXP = "\\$F\\{(\\w)*(\\.)?(\\w)+\\}";
	private static final String EXPRESS_EXP = "\\$\\{(\\w)*(\\.)?(\\w)+\\}";
	private HashMap<String, SequenceSeed> registry = new HashMap<>();

	SequenceMixGeneratorImpl(Collection<SequenceMeta> definitions) {
		definitions.stream().filter(meta -> meta.getType().equals("mix")).forEach(meta -> {
			registry.put(meta.getId(), new SequenceSeed(meta));
		});
	}

	@Override
	public String next(String seqId, Object context) {
		SequenceSeed locker = registry.get(seqId);
		if (locker != null) {
			SequenceMeta def = locker.getMeta();
			if (!str_emp(def.getFormat())) {
				Map<String, String> values = readFormat(def, context);
				long newNum = locker.acquire(def.getStart());
				String v = def.getFormat();
				for (String key : values.keySet()) {
					if (key.equals("${sequence}")) {
						String seqStr = StringUtils.leftPad(Long.toString(newNum), def.getLength(), '0');
						v = replace(v, key, seqStr);
					} else {
						v = replace(v, key, repl_emp(values.get(key)));
					}
				}
				return v;
			} else {
				long newNum = locker.acquire(def.getStart());
				if ("java.lang.String".equals(def.getType())
						&& def.getLength() > 0) {
					return StringUtils.leftPad(Long.toString(newNum), def.getLength(), '0');
				}
			}
			throw new KernelRuntimeException(seqId + " invalid definition.Format or length is required!");
		} else {
			throw new KernelRuntimeException(seqId + "'s definition is not found!");
		}
	}

	private Map<String, String> readFormat(SequenceMeta seq, Object context) {
		String format = seq.getFormat();
		HashSet<String> keys = readExpression(format);
		keys.addAll(readFromContext(seq.getFormat()));
		Map<String, String> res = new HashMap<>();
		for (String key : keys) {
			String v = "";
			if (key.equals("${date}")) {
				v = date_2_str_pattern(new Date(), seq.getDatePattern());
				res.put(key, v);
			} else if (key.equals("${sequence}")) {
				res.put(key, key);
			} else if (key.contains("$F{")) {
				String field = replace(key, "$F{", "");
				field = replace(field, "}", "");
				Object objV = get_v_recursive(context, field);
				if (objV instanceof Date) {
					v = date_2_str_pattern((Date) objV, seq.getDatePattern());
				} else {
					v = objV.toString();
				}
				res.put(key, v);
			}
		}
		return res;
	}

	private HashSet<String> readExpression(String format) {
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcher matcher;
		HashSet<String> keys = new HashSet<>();
		try {
			Pattern pattern = compiler.compile(EXPRESS_EXP);
			PatternMatcherInput input = new PatternMatcherInput(format);
			matcher = new Perl5Matcher();
			while (matcher.contains(input, pattern)) {
				MatchResult result = matcher.getMatch();
				String fieldName = result.toString();
				keys.add(fieldName);
			}
			return keys;
		} catch (MalformedPatternException e) {
			throw new KernelRuntimeException(e);
		}
	}

	private HashSet<String> readFromContext(String format) {
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcher matcher;
		try {
			HashSet<String> keys = new HashSet<>();
			Pattern pattern = compiler.compile(FIELD_EXP);
			PatternMatcherInput input = new PatternMatcherInput(format);
			matcher = new Perl5Matcher();
			while (matcher.contains(input, pattern)) {
				MatchResult result = matcher.getMatch();
				String fieldName = result.toString();
				keys.add(fieldName);
			}
			return keys;
		} catch (MalformedPatternException e) {
			throw new KernelRuntimeException(e);
		}
	}
}
