import java.util.ArrayList;
import java.util.Vector;

public class OwnerShareBuilder {
    public final static class OwnerShareItem {
        public final int mRanking;
        public final long mShares;
        public final float mShareRatio;
        public final String mOwnerName;
        public final String mDate;

        public OwnerShareItem(String date, int ranking, long shares, float ratio, String ownerName) {
            mDate = date; 
            mRanking = ranking;
            mShares  = shares;
            mShareRatio = ratio;
            mOwnerName = ownerName;
        }

        public void dump() {
            String content = "";
            content += " Date: ";
            content += mDate;
            content += " ranking: ";
            content += mRanking;
            content += " shares: ";
            content += mShares;
            content += " ratio: ";
            content += mShareRatio;
            content += " ownername: ";
            content += mOwnerName;
            System.out.println(content);
        }
    }

    public final static class OwnerSharesRecord {
        private Vector<OwnerShareItem> mOwnerShareItems; 
        private final int mSize;

        public OwnerSharesRecord(int size) {
            mSize = size;
            mOwnerShareItems = new Vector<OwnerShareItem>(size);
        }

        public OwnerSharesRecord(Vector<OwnerShareItem> items) {
            mSize = items.size();
            mOwnerShareItems = items;
        }

        public void addOwnerShareItem(final OwnerShareItem item) {
            mOwnerShareItems.add(item);
        }

        public void dump() {
        }

        public int size() {
            return mSize;
        }

        public OwnerShareItem get(int index) {
            return mOwnerShareItems.get(index);
        }

        public String toSQLFormatString() {
            return null;
        }

        public OwnerSharesRecord fromSQLResult() {
            return null;
        }
    }

    public static OwnerSharesRecord buildOwnerSharesRecord(final Vector<OwnerShareItem> items) {
        return new OwnerSharesRecord(items);
    }

    public static OwnerShareItem buildOwnerShareItem(final String[] info) {
        //XXX: KEEP AN EYE ON THE ORDER
        OwnerShareItem ret = new OwnerShareItem(info[0], Integer.parseInt(info[1]), Long.parseLong(info[3]), Float.parseFloat(info[4]), info[2]);
        //ret.dump();
        return ret;
    }
}
