package asu.reach;

/**
 * Created by kadam on 4/4/2017.
 */

public class GlobalCounter {
    GlobalCounter globalObj = null;
    private int globalCount;
    private GlobalCounter() {
        globalCount = 0;
    }
    public GlobalCounter getInstance() {
        if(globalObj == null) {
            globalObj = new GlobalCounter();
        }
        return globalObj;
    }
    public int getGlobalCount() {
        return globalCount;
    }
    public void setGlobalCount(int count) {
        globalCount = count;
    }
    public void increamentGlobalCount() {
        globalCount++;
    }
}
