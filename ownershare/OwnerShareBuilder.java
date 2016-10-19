import java.util.ArrayList;
import java.util.Vector;
import java.sql.*;

public class OwnerShareBuilder {
    public final static class OwnerShareItem {
        public final int mRanking;
        public final long mShares;
        public final float mShareRatio;
        public final String mOwnerName;
        public String mDate;

        public OwnerShareItem(String date, int ranking, long shares, float ratio, String ownerName) {
            mDate       = date; 
            mRanking    = ranking;
            mShares     = shares;
            mShareRatio = ratio;
            mOwnerName  = ownerName;
        }

        public OwnerShareItem(OwnerShareItem srcItem) {
            mDate       = srcItem.mDate; 
            mRanking    = srcItem.mRanking;
            mShares     = srcItem.mShares;
            mShareRatio = srcItem.mShareRatio;
            mOwnerName  = srcItem.mOwnerName;
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
            for (int i = 0 ; i < mOwnerShareItems.size(); ++i) {
                 mOwnerShareItems.get(i).dump();
            }
        }

        public void insertElementAt(final OwnerShareItem item, int index) {
            mOwnerShareItems.insertElementAt(item, index);
        }

        public int size() {
            return mOwnerShareItems.size();
        }

        public OwnerShareItem get(int index) {
            if (index < mOwnerShareItems.size()) {
                return mOwnerShareItems.get(index);
            }

            return null;
        }

        public String toSQLFormatString() {
            return null;
        }
    }

    public static OwnerSharesRecord buildOwnerSharesRecordFromQueryResult(OwnerSharesRecord outRecord, final ResultSet queryResult) {
        if (queryResult == null) {
            System.out.println("Try to buildOwnerSharesRecordFromQueryResult with null queryResult");
            return null;
        }
        //TODO: 10 is hard coded
        OwnerShareItem ownerShareItem = null; 

        try {
            while (queryResult.next()) {
                ownerShareItem = buildOwnerShareItemsFromQueryResult(queryResult);
                outRecord.addOwnerShareItem(ownerShareItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outRecord;
    }

    public static OwnerShareItem buildOwnerShareItemsFromQueryResult(final ResultSet queryResult) {
        try {
            OwnerShareItem tmpItem = new OwnerShareItem(queryResult.getString(1), queryResult.getInt(2), queryResult.getLong(3), queryResult.getFloat(4), queryResult.getString(5));
            //tmpItem.dump();
            return tmpItem;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
