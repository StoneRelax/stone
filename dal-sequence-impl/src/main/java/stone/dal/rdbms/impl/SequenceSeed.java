package stone.dal.rdbms.impl;

import stone.dal.kernel.FileUtils;
import stone.dal.sequence.api.meta.SequenceMeta;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fengxie
 */
class SequenceSeed {
	private ReentrantLock lock = new ReentrantLock();
	private ReentrantLock inc2Startlock = new ReentrantLock();
	private AtomicLong sequence;
	private AtomicLong acquiredSeq;
	private long acquireTimes = 100; //todo:read from config
	private AtomicLong modCount = new AtomicLong(0);
	private AtomicLong inc2StartModCount = new AtomicLong(0);
	private String seedFileName;
	private SequenceMeta meta;

	SequenceSeed(SequenceMeta seqMeta) {
		this.seedFileName = "sequence/" + seqMeta.getId() + ".seed";
		this.meta = seqMeta;
		init();
	}

	private void acquirePersistenceFile() {
		long newAcq = acquiredSeq.get() + acquireTimes * meta.getStep();
		FileUtils.writeFile(seedFileName, String.valueOf(newAcq).getBytes());
		acquiredSeq.set(newAcq);
	}

	SequenceMeta getMeta() {
		return meta;
	}

	long acquire(long start) {
		long seq = sequence.addAndGet(meta.getStep());
		if (seq < start) {
			try {
				long currentMod = inc2StartModCount.get();
				inc2Startlock.lock();
				if (currentMod == inc2StartModCount.get()) {
					acquirePersistenceFile();
					inc2StartModCount.incrementAndGet();
				}
			} finally {
				inc2Startlock.unlock();
			}
		}
		if (seq > acquiredSeq.get()) {
			try {
				long currentMod = modCount.get();
				lock.lock();
				if (currentMod == modCount.get()) {
					acquirePersistenceFile();
					modCount.incrementAndGet();
				}
			} finally {
				lock.unlock();
			}
		}
		if (meta.getEnd() != -1) {
			if (seq > meta.getEnd() && meta.isCircle()) {
				resetSeq();
				return acquire(meta.getStart());
			}
		}
		return seq;
	}

	private synchronized void resetSeq() {
		long startNumber = meta.getStart();
		sequence = new AtomicLong(startNumber);
		long initNum = startNumber + (acquireTimes * meta.getStep());
		acquiredSeq = new AtomicLong(initNum);
		FileUtils.writeFile(seedFileName, String.valueOf(initNum).getBytes());
	}

	private void init() {
		byte[] prevSeedContent = FileUtils.readFile(seedFileName);
		long initNum = 0;
		if (prevSeedContent == null) {
			long startNumber = meta.getStart();
			sequence = new AtomicLong(startNumber);
			initNum = startNumber + (acquireTimes * meta.getStep());
			acquiredSeq = new AtomicLong(initNum);
		} else {
			long point = Long.parseLong(new String(prevSeedContent));
			sequence = new AtomicLong(point + meta.getStep());
			initNum = point + (acquireTimes * meta.getStep());
			acquiredSeq = new AtomicLong(initNum);
		}
		FileUtils.writeFile(seedFileName, String.valueOf(initNum).getBytes());
	}

}
