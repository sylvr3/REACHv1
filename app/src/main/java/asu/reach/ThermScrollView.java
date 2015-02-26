package asu.reach;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ThermScrollView extends HorizontalScrollView{

    private Runnable scrollerTask;
    private int initialPosition;

    private int newCheck = 100;

    public interface OnScrollStoppedListener{
        void onScrollStopped();
    }

    private OnScrollStoppedListener onScrollStoppedListener;

    public ThermScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scrollerTask = new Runnable() {

            public void run() {

                int newPosition = getScrollY();
                if(initialPosition - newPosition == 0){//has stopped

                    if(onScrollStoppedListener!=null){

                        onScrollStoppedListener.onScrollStopped();
                    }
                }else{
                    initialPosition = getScrollY();
                    ThermScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void setOnScrollStoppedListener(ThermScrollView.OnScrollStoppedListener listener){
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask(){

        initialPosition = getScrollY();
        ThermScrollView.this.postDelayed(scrollerTask, newCheck);
    }
}
