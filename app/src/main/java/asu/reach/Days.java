package asu.reach;

/**
 * Created by mandar on 30-09-2015.
 */
public class Days {
    private String dayName;
    private boolean isSelected;

    public Days(String dayName,boolean isSelected){
        this.dayName=dayName;
        this.isSelected=isSelected;
    }
    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
