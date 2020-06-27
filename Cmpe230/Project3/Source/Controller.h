#ifndef CONTROLLER_H
#define CONTROLLER_H

#include "Timer.h"
#include <QLabel>
#include <QObject>
#include <QPushButton>
#include <QTimer>
#include <QCoreApplication>

class Controller : public QObject
{
    Q_OBJECT
public:
    Controller(std::vector<QPushButton*> &c, QPushButton* &ng, QLabel* &s);

    //Get from main
    std::vector<QPushButton*> &cards;
    QPushButton* &newGame;
    QLabel* &scoreLabel;

    //Keeps track of score
    int score;

    //Sets to true if 2 cards are revealed, and showing the result
    bool isShowing;

    //True if correctly matched
    bool selectionTrue;

    //To record first selected card.
    int selectedIndex;
    //To record second selected card.
    int secondIndex;
    //Currently selected string.
    QString selectedString;
    //Card names.
    std::vector<QString> cardStrings{"cat", "dog",  "fish",  "cow",  "bird", "horse", "monkey", "squirrel",
                             "dolphin", "wolf", "lion", "tiger", "hamster", "bear", "bee",
                             "cat", "dog",  "fish",  "cow",  "bird", "horse", "monkey", "squirrel",
                             "dolphin", "wolf", "lion", "tiger", "hamster", "bear", "bee"};

    //For delaying
    QTimer *delayTimer;

public:
    void resetCards();

public slots:
    void cardPressed();
    void newGamePressed();
    void delay();

// This signal is used to stop the timer.
signals:
    void stopTimer();
};

#endif // CONTROLLER_H
