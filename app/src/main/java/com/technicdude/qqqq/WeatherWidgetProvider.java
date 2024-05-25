package com.technicdude.qqqq;

import android.appwidget.AppWidgetManager;
        import android.appwidget.AppWidgetProvider;
        import android.content.Context;

public class WeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Обновление виджета
    }

    @Override
    public void onEnabled(Context context) {
        // Вызывается при создании первого экземпляра виджета
    }

    @Override
    public void onDisabled(Context context) {
        // Вызывается при удалении последнего экземпляра виджета
    }
}
