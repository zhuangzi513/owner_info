
public class OwnerSharesHelper {
    public static int ERROR = -1;

    public static int sumOfFlesh(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        int flesh = 0;

        if (newRecord == null || oldRecord == null
            /*|| newRecord.size() != oldRecord.size()*/
            || newRecord.size() == 0 || oldRecord.size() == 0) {
            //System.out.println("newRecord: " + newRecord + " oldRecord: " + oldRecord);
            return ERROR;
        }

        int newSize = newRecord.size();
        int oldSize = oldRecord.size();
        boolean markAsDiff = false;
        boolean markAsNew = false;

        OwnerShareBuilder.OwnerShareItem newShareItem = newRecord.get(0);
        OwnerShareBuilder.OwnerShareItem oldShareItem = oldRecord.get(0);

        long newSumShares = newShareItem.mShares * (int)(100/newShareItem.mShareRatio);
        long oldSumShares = oldShareItem.mShares * (int)(100/oldShareItem.mShareRatio);

        if (newSumShares - oldSumShares > 100
            || newSumShares - oldSumShares < -100) {
            return 0;
        }

        //TODO: performancd  need refine
        for ( int i = 0; i < newSize; ++i) {
            newShareItem = newRecord.get(i);
            markAsDiff = false;
            markAsNew = false;
            int j = 0;
            for (j = 0; j < oldSize; ++j) {
                oldShareItem = oldRecord.get(j);
                if (isSameOwner(oldShareItem.mOwnerName, newShareItem.mOwnerName)) {
                    markAsDiff = true;
                    break;
                }
            }

            if (j == oldSize) {
                markAsNew = true;
            }

            if (markAsNew) {
                flesh = flesh + (int)(newShareItem.mShareRatio * 100);
            }

            if (markAsDiff) {
                //Only positive ?
                if ((int)(newShareItem.mShareRatio * 100) > (int)(oldShareItem.mShareRatio * 100)) {
                    flesh = flesh + ((int)((newShareItem.mShareRatio - oldShareItem.mShareRatio) * 100));
                } else {
                    //TODO:maybe we will count into negtive change in the future
                }
            }
        }

        return flesh;
    }

    public static boolean isSameOwner(String src, String dst) {
        int i = 0, j = 0;
        src = src.replaceAll("\\(", "");
        src = src.replaceAll("\\)", "");

        dst = dst.replaceAll("\\(", "");
        dst = dst.replaceAll("\\)", "");


        if (src.length() <= dst.length()) {
            while (i < src.length() && j < dst.length()) {
                if (src.charAt(i) == dst.charAt(j)) {
                    ++i;
                    ++j;
                } else {
                    ++j;
                }
            }

            if (j == dst.length() && i < src.length()) {
                //System.out.println(src + " 1 NOT equals to: " + dst);
                return false;
            }
        } else {
            while (i < dst.length() && j < src.length()) {
                if (dst.charAt(i) == src.charAt(j)) {
                    ++i;
                    ++j;
                } else {
                    ++j;
                }
            }

            if (j == src.length() && i < dst.length()) {
                //System.out.println(src + "2 NOT equals to: " + dst);
                return false;
            }
        }

        //if (src.length() != dst.length()) {
        //    System.out.println(src + " equals to: " + dst);
        //}

        return true;
    }

    public int sumOfFlowIn(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        return ERROR;
    } }
