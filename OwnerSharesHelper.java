
public class OwnerSharesHelper {
    public static int ERROR = -1;

    public static int sumOfFlesh(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        int flesh = 0;

        if (newRecord == null || oldRecord == null
            || newRecord.size() != oldRecord.size()
            || newRecord.size() == 0 || oldRecord.size() == 0) {
            //System.out.println("newRecord: " + newRecord + " oldRecord: " + oldRecord);
            return ERROR;
        }

        int size = oldRecord.size();
        boolean markAsDiff = false;
        boolean markAsNew = false;

        OwnerShareBuilder.OwnerShareItem newShareItem = null;
        OwnerShareBuilder.OwnerShareItem oldShareItem = null;

        //TODO: performancd  need refine
        for ( int i = 0; i < size; ++i) {
            newShareItem = newRecord.get(i);
            markAsDiff = false;
            markAsNew = false;
            for (int j = 0; j < size; ++j) {
                oldShareItem = oldRecord.get(j);
                if (newShareItem.mOwnerName.equals(oldShareItem.mOwnerName)) {
                    if ((int)(newShareItem.mShareRatio * 100) != (int)(oldShareItem.mShareRatio * 100)) {
                        markAsDiff = true;
                    }
                    break;
                } else if (j == size - 1) {
                    markAsNew = true;
                }
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

    public int sumOfFlowIn(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord oldRecord) {
        return ERROR;
    } }
