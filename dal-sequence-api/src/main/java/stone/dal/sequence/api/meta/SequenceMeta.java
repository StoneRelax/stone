package stone.dal.sequence.api.meta;

/**
 * @author fengxie
 */
public class SequenceMeta {

	private String id;
	private String format;
	private int length;
	private long start = 0;
	private long end = -1;
	private String type;
	private int step = 1;
	private boolean circle;
	private String sequenceOn;
	private String datePattern;

	public String getId() {
		return id;
	}

	public String getFormat() {
		return format;
	}

	public int getLength() {
		return length;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public String getType() {
		return type;
	}

	public int getStep() {
		return step;
	}

	public boolean isCircle() {
		return circle;
	}

	public String getSequenceOn() {
		return sequenceOn;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory {
		private SequenceMeta meta = new SequenceMeta();

		public Factory format(String format) {
			meta.format = format;
			return this;
		}

		public Factory length(int length) {
			meta.length = length;
			return this;
		}

		public Factory start(long start) {
			meta.start = start;
			return this;
		}

		public Factory end(long end) {
			meta.end = end;
			return this;
		}

		public Factory type(String type) {
			meta.type = type;
			return this;
		}

		public Factory step(int step) {
			meta.step = step;
			return this;
		}

		public Factory circle(boolean circle) {
			meta.circle = circle;
			return this;
		}

		public Factory sequenceOn(String sequenceOn) {
			meta.sequenceOn = sequenceOn;
			return this;
		}

		public Factory datePattern(String datePattern) {
			meta.datePattern = datePattern;
			return this;
		}

		public Factory id(String id) {
			meta.id = id;
			return this;
		}

		public SequenceMeta build() {
			return meta;
		}
	}
}
