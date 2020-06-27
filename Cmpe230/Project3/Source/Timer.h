#ifndef TIMER_H
#define TIMER_H

#include <QTimer>
#include <QLabel>
#include <QMessageBox>
#include <QPushButton>
#include "Controller.h"

class Timer : public QObject
{
    Q_OBJECT

public:
    Timer();
    QTimer *timer1;
    //The displayed label.
    QLabel *label;
    //Stores the time in seconds.
    int counter;


public slots:
    void timerslot();
    void newGameClicked();
    void won();

// Emitted when 180 seconds time limit is up.
signals:
    void timesUp();
};

#endif // TIMER_H

