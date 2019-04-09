package com.agooday.daterangepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by psinetron on 29/11/2018.
 * http://slybeaver.ru
 */
public class SlyCalendarView extends FrameLayout implements DateSelectListener {

    private SlyCalendarData slyCalendarData;

    private Callback callback = null;

    private DialogCompleteListener completeListener = null;

    private AttributeSet attrs = null;
    private int defStyleAttr = 0;


    public SlyCalendarView(Context context) {
        super(context);
        //Log.d("tien.hien","SlyCalendarView 1");

    }

    public SlyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //Log.d("tien.hien","SlyCalendarView 2");
        init(null, 0);
        this.attrs = attrs;
    }

    public SlyCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // Log.d("tien.hien","SlyCalendarView 3");
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;

    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    public void setCompleteListener(@Nullable DialogCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    /*public void setSlyCalendarData(SlyCalendarData slyCalendarData) {
        this.slyCalendarData = slyCalendarData;
        init(attrs, defStyleAttr);
        showCalendar();
    }*/

    private void init(@Nullable AttributeSet attrs, int defStyle) {
        slyCalendarData = new SlyCalendarData();
        slyCalendarData.setSingle(false);

        inflate(getContext(), R.layout.slycalendar_frame, this);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlyCalendarView, defStyle, 0);

        if (slyCalendarData.getBackgroundColor() == null) {
            slyCalendarData.setBackgroundColor(typedArray.getColor(R.styleable.SlyCalendarView_backgroundColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defBackgroundColor)));
        }
        if (slyCalendarData.getHeaderColor() == null) {
            slyCalendarData.setHeaderColor(typedArray.getColor(R.styleable.SlyCalendarView_headerColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defHeaderColor)));
        }
        if (slyCalendarData.getHeaderTextColor() == null) {
            slyCalendarData.setHeaderTextColor(typedArray.getColor(R.styleable.SlyCalendarView_headerTextColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defHeaderTextColor)));
        }
        if (slyCalendarData.getTextColor() == null) {
            slyCalendarData.setTextColor(typedArray.getColor(R.styleable.SlyCalendarView_textColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defTextColor)));
        }
        if (slyCalendarData.getSelectedColor() == null) {
            slyCalendarData.setSelectedColor(typedArray.getColor(R.styleable.SlyCalendarView_selectedColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defSelectedColor)));
        }
        if (slyCalendarData.getSelectedTextColor() == null) {
            slyCalendarData.setSelectedTextColor(typedArray.getColor(R.styleable.SlyCalendarView_selectedTextColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defSelectedTextColor)));
        }

        typedArray.recycle();



        // TextView txtSave  = findViewById(R.id.txtSave);
        //txtSave.setText(typedArray.getString(R.styleable.SlyCalendarView_confirmTitle));
        //txtSave.setBackgroundColor(typedArray.getColor(R.styleable.SlyCalendarView_confirmBg,ContextCompat.getColor(getContext(), R.color.slycalendar_defConfirmBGColor)));

        final ViewPager vpager = findViewById(R.id.content);
        vpager.setAdapter(new MonthPagerAdapter(slyCalendarData, this));
        vpager.setCurrentItem(vpager.getAdapter().getCount() / 2);

        showCalendar();
    }

    private void showCalendar() {

        paintCalendar();
        showTime();

        findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onCancelled();
                }
                if (completeListener != null) {
                    completeListener.complete();
                }
            }
        });

        findViewById(R.id.txtSave).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    Calendar start = null;
                    Calendar end = null;
                    if (slyCalendarData.getSelectedStartDate() != null) {
                        start = Calendar.getInstance();
                        start.setTime(slyCalendarData.getSelectedStartDate());
                    }
                    if (slyCalendarData.getSelectedEndDate() != null) {
                        end = Calendar.getInstance();
                        end.setTime(slyCalendarData.getSelectedEndDate());
                    }
                    callback.onDataSelected(start, end, slyCalendarData.getSelectedHour(), slyCalendarData.getSelectedMinutes());
                }
                if (completeListener != null) {
                    completeListener.complete();
                }
            }
        });


        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = null;
        if (slyCalendarData.getSelectedStartDate() != null) {
            calendarStart.setTime(slyCalendarData.getSelectedStartDate());
        } else {
            calendarStart.setTime(slyCalendarData.getShowDate());
        }

        if (slyCalendarData.getSelectedEndDate() != null) {
            calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(slyCalendarData.getSelectedEndDate());
        }




        findViewById(R.id.btnMonthPrev).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager vpager = findViewById(R.id.content);
                vpager.setCurrentItem(vpager.getCurrentItem()-1);
            }
        });

        findViewById(R.id.btnMonthNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager vpager = findViewById(R.id.content);
                vpager.setCurrentItem(vpager.getCurrentItem()+1);
            }
        });

        /*findViewById(R.id.txtTime).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int style = R.style.SlyCalendarTimeDialogTheme;
                if (slyCalendarData.getTimeTheme() != null) {
                    style = slyCalendarData.getTimeTheme();
                }

                TimePickerDialog tpd = new TimePickerDialog(getContext(), style, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        slyCalendarData.setSelectedHour(hourOfDay);
                        slyCalendarData.setSelectedMinutes(minute);
                        showTime();
                    }
                }, slyCalendarData.getSelectedHour(), slyCalendarData.getSelectedMinutes(), true);
                tpd.show();
            }
        });*/

        ViewPager vpager = findViewById(R.id.content);
        vpager.getAdapter().notifyDataSetChanged();
        vpager.invalidate();

    }

    @Override
    public void dateSelect(Date selectedDate) {
        if (slyCalendarData.getSelectedStartDate() == null || slyCalendarData.isSingle()) {
            slyCalendarData.setSelectedStartDate(selectedDate);
            showCalendar();
            return;
        }
        if (slyCalendarData.getSelectedEndDate() == null) {
            if (selectedDate.getTime() < slyCalendarData.getSelectedStartDate().getTime()) {
                slyCalendarData.setSelectedEndDate(slyCalendarData.getSelectedStartDate());
                slyCalendarData.setSelectedStartDate(selectedDate);
                showCalendar();
                return;
            } else if (selectedDate.getTime() == slyCalendarData.getSelectedStartDate().getTime()) {
                slyCalendarData.setSelectedEndDate(null);
                slyCalendarData.setSelectedStartDate(selectedDate);
                showCalendar();
                return;
            } else if (selectedDate.getTime() > slyCalendarData.getSelectedStartDate().getTime()) {
                slyCalendarData.setSelectedEndDate(selectedDate);
                showCalendar();
                return;
            }
        }
        if (slyCalendarData.getSelectedEndDate() != null) {
            slyCalendarData.setSelectedEndDate(null);
            slyCalendarData.setSelectedStartDate(selectedDate);
            showCalendar();
        }
    }

    @Override
    public void dateLongSelect(Date selectedDate) {
        slyCalendarData.setSelectedEndDate(null);
        slyCalendarData.setSelectedStartDate(selectedDate);
        showCalendar();
    }

    private void paintCalendar() {
        findViewById(R.id.mainFrame).setBackgroundColor(slyCalendarData.getBackgroundColor());
        /*findViewById(R.id.headerView).setBackgroundColor(slyCalendarData.getHeaderColor());
        ((TextView) findViewById(R.id.txtYear)).setTextColor(slyCalendarData.getHeaderTextColor());
        ((TextView) findViewById(R.id.txtSelectedPeriod)).setTextColor(slyCalendarData.getHeaderTextColor());
        //((TextView) findViewById(R.id.txtTime)).setTextColor(slyCalendarData.getHeaderColor());*/

    }


    private void showTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, slyCalendarData.getSelectedHour());
        calendar.set(Calendar.MINUTE, slyCalendarData.getSelectedMinutes());
        /*((TextView) findViewById(R.id.txtTime)).setText(
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime())
        );*/

    }

}
