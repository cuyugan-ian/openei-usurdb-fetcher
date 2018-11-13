/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 */
package net.modsolar.pojo;

public class ZipCount {
    private String state;
    private int count = 0;

    public ZipCount() {
    }
    
    public ZipCount(String state, int count) {
        this.state = state;
        this.count = count;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        if (count <= 0) {
            return;
        }
        this.count = count;
    }
    
    public void incrementCount() {
        this.count++;
    }
    
}
