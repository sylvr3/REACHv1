package asu.reach;

/**
 * Created by mandar on 30-09-2015.
 */
public class Weeks {
    private String week_number;
    private boolean isSelected;

    public Weeks(String week_number,boolean isSelected){
        this.week_number=week_number;
        this.isSelected=isSelected;
    }

    public String getWeek_number() {
        return week_number;
    }

    public void setWeek_number(String week_number) {
        this.week_number = week_number;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
