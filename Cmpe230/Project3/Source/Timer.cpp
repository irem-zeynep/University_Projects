#include "Timer.h"


Timer::Timer()
{
    counter = 0;
    label = new QLabel("Time (secs): 0");
    timer1 = new QTimer(this);

    QObject::connect(timer1, SIGNAL(timeout()), this, SLOT(timerslot()));

    timer1->start(1000);
}
// Increases the counter and check whether time is up.
void Timer:: timerslot(){
    counter += 1;
    label->setText("Time (secs): " + QString::number(this->counter));
    //If timer's counter reaches 180, time is up. The player loses.
    if(this->counter >= 180){
        this->timer1->stop();
        QMessageBox msgBox;
        msgBox.setText("Time is up, you failed!");
        msgBox.setWindowTitle("Failed!");
        //Gives new game and exit options.
        QPushButton *newGameButton = msgBox.addButton(tr("New Game"), QMessageBox::ActionRole);
        QPushButton *exitButton = msgBox.addButton(tr("Exit Game"), QMessageBox::ActionRole);
        msgBox.exec();

        //If new game option is selected, resets timer.
        if (msgBox.clickedButton() == newGameButton) {
            emit timesUp();
            counter = 0;
            label->setText("Time (secs): 0");
            timer1->start(1000);
        } else if (msgBox.clickedButton() == exitButton) {
            QCoreApplication::quit();
        }

    }
}

void Timer:: newGameClicked() {
    this->timer1->stop();
    counter = 0;
    label->setText("Time (secs): 0");
    timer1->start(1000);
}

void Timer::won(){
    this->timer1->stop();
}




