
public class OwnerSharesHelper {
    public static int ERROR = -1;

    private static boolean checkValidateInputsRecords(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        if (newRecord == null || oldRecord == null
            /*|| newRecord.size() != oldRecord.size()*/
            || newRecord.size() == 0 || oldRecord.size() == 0) {
            //System.out.println("newRecord: " + newRecord + " oldRecord: " + oldRecord);
            return false;
        }


        long newSumShares = newRecord.get(0).mShares * (int)(100/newRecord.get(0).mShareRatio);
        long oldSumShares = oldRecord.get(0).mShares * (int)(100/oldRecord.get(0).mShareRatio);

        if (newSumShares - oldSumShares > 100
            || newSumShares - oldSumShares < -100) {
            return false;
        }

        return true;
    }

    /**
     * Compute the shares of NEW comers and the increased shares of the OLD owners
    */
    public static int sumOfFlesh(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        if (!checkValidateInputsRecords(newRecord, oldRecord)) {
            return -1;
        }

        int flesh = 0;
        int newSize = newRecord.size();
        int oldSize = oldRecord.size();
        boolean markAsDiff = false;
        boolean markAsNew = false;

        OwnerShareBuilder.OwnerShareItem newShareItem = newRecord.get(0);
        OwnerShareBuilder.OwnerShareItem oldShareItem = oldRecord.get(0);

        int newSharesVolume = (int)( (newShareItem.mShares * 10000)/(int)(newShareItem.mShareRatio*10000) );
        int oldSharesVolume = (int)( (oldShareItem.mShares * 10000)/(int)(oldShareItem.mShareRatio*10000) );

        //discard of new shares
        if (newSharesVolume - oldSharesVolume > 10000
            || newSharesVolume - oldSharesVolume < -10000) {
            return -1;
        }

        //TODO: performancd  need refine
        for ( int i = 0; i < newSize; ++i) {
            newShareItem = newRecord.get(i);
            markAsDiff = false;
            markAsNew = false;
            int j = 0;
            for (j = 0; j < oldSize; ++j) {
                oldShareItem = oldRecord.get(j);
                //System.out.println("Compare : " + newShareItem.mOwnerName + " & " + oldShareItem.mOwnerName);
                if (isSameOwner(newShareItem.mOwnerName, oldShareItem.mOwnerName)) {
                    if (oldShareItem.mShareRatio != oldShareItem.mShareRatio) {
                        markAsDiff = true;
                    }
                    //System.out.println(oldShareItem.mOwnerName + " == " + newShareItem.mOwnerName);
                    break;
                }

                //Just the owner renamed
                if (newShareItem.mRanking == oldShareItem.mRanking
                    && newShareItem.mShares == oldShareItem.mShares) {
                    break;
                }
            }

            if (j == oldSize) {
                markAsNew = true;
            }

            if (markAsNew) {
                //System.out.println("markAsNew:" + newShareItem.mOwnerName);
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

    /**
     * Compute the decreased shares of the OLD owners
    */
    public static int sumOfDecreased(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        if (!checkValidateInputsRecords(newRecord, oldRecord)) {
            return -1;
        }

        int decreased = 0;
        int newSize = newRecord.size();
        int oldSize = oldRecord.size();
        boolean markAsDiff = false;
        boolean markAsQuit = false;

        OwnerShareBuilder.OwnerShareItem newShareItem = newRecord.get(0);
        OwnerShareBuilder.OwnerShareItem oldShareItem = oldRecord.get(0);

        //TODO: performancd  need refine
        for ( int i = 0; i < oldSize; ++i) {
            oldShareItem = oldRecord.get(i);
            markAsDiff = false;
            markAsQuit = false;
            int j = 0;
            for (j = 0; j < newSize; ++j) {
                newShareItem = newRecord.get(j);
                if (!isSameOwner(oldShareItem.mOwnerName, newShareItem.mOwnerName)) {
                    markAsDiff = true;
                    break;
                }
            }

            if (j == newSize) {
                markAsQuit = true;
            }

            if (markAsQuit) {
                decreased = decreased + (int)(newShareItem.mShareRatio * 100);
            }

            if (markAsDiff) {
                //Only positive ?
                if ((int)(newShareItem.mShareRatio * 100) < (int)(oldShareItem.mShareRatio * 100)) {
                    decreased = decreased + ((int)((oldShareItem.mShareRatio - newShareItem.mShareRatio) * 100));
                } else {
                    //TODO:maybe we will count into negtive change in the future
                }
            }
        }

        return decreased;

    }

    public static boolean isSameOwner(String src, String dst) {
        int i = 0, j = 0;
        src = src.replaceAll("\\(", "");
        src = src.replaceAll("\\)", "");
        src = src.replaceAll(" ", "");
        src = src.replaceAll("-", "");

        dst = dst.replaceAll("\\(", "");
        dst = dst.replaceAll("\\)", "");
        dst = dst.replaceAll(" ", "");
        dst = dst.replaceAll("-", "");

        if (src.length() <= dst.length()) {
            while (i < src.length() && j < dst.length()) {
                if (src.charAt(i) == dst.charAt(j)) {
                    ++i;
                    ++j;
                } else {
                    ++j;
                }
            }

            //if 80%  same, then same
            if (j == dst.length() && i > (int)((src.length() * 8)/10)) {
                //System.out.println(src + " equals to: " + dst);
                return true;
            } else if (i == src.length() && j <= dst.length()) {
                return true;
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

            //if 80%  same, then same
            if (j == src.length() && i > (int)((dst.length()*8)/10)) {
                //System.out.println(src + "equals to: " + dst);
                return true;
            } else if (i == dst.length() && j <= src.length()) {
                return true;
            }
        }

        //System.out.println(src + " equals to: " + dst);

        return false;
    }

    public int sumOfFlowIn(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        return ERROR;
    } }
