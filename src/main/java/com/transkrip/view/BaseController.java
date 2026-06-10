package com.transkrip.view;


public interface BaseController {
    void setMainLayout(MainLayoutController mainLayout);

    default void onNavigatedTo() {
        // default kosong; override di controller yang perlu refresh
    }
}
