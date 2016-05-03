import java.util.ArrayList;

public class OwnerShareBuilder {
    public final static class OwnerShareItem {
        private int mRanking;
        private long mShares;
        private float mShareRatio;
        private String mOwnerName;
        private String mDate;

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
        private ArrayList mOwnerShareItems; 

        public OwnerSharesRecord(int size) {
            mOwnerShareItems = new ArrayList(size);
        }

        public void addOwnerShareItem(final OwnerShareItem item) {
            mOwnerShareItems.add(item);
        }

        public void dump() {
        }

        public String toSQLFormatString() {
            return null;
        }

        public OwnerSharesRecord fromSQLResult() {
            return null;
        }
    }

    public static OwnerSharesRecord buildOwnerSharesRecord(final OwnerShareItem[] items) {
        return null; 
    }

    public static OwnerShareItem buildOwnerShareItem(final String[] info) {
        //XXX: KEEP AN EYE ON THE ORDER
        OwnerShareItem ret = new OwnerShareItem(info[0], Integer.parseInt(info[1]), Long.parseLong(info[3]), Float.parseFloat(info[4]), info[2]);
        ret.dump();
        return ret;
    }
}
