package internal;

public class SimpleTimer {

    private long beginTime;
    private long endTime;
    
    public void begin() {
        beginTime = System.currentTimeMillis();
    }
    
    public void end() {
        endTime = System.currentTimeMillis(); 
    }
    
    public long getResult() {
        return endTime - beginTime;
    }
}
