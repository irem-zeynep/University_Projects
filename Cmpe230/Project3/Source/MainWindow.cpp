#include <QApplication>
#include <QWidget>
#include <QVBoxLayout>
#include <QSpacerItem>
#include <QPushButton>
#include <QLabel>
#include <vector>
#include "Timer.h"
#include "Controller.h"

int main(int argc, char *argv[])
{
    QApplication game(argc, argv);
    QWidget *widget = new QWidget();
    QVBoxLayout *vbox = new QVBoxLayout(widget);
    QGridLayout *grid = new QGridLayout();

    Timer *time = new Timer();
    QLabel *score = new QLabel("Score: 0");
    QPushButton *newGame = new QPushButton("New Game");
    QPushButton *quit = new QPushButton("Quit");

    // Vector that stores the buttons.
    std::vector<QPushButton*> buttonVector;
    // 30 button are created and added into vector and to the grid.
    for(int i = 0; i < 5; i++){
        for(int j = 0; j < 6; j++){
            QPushButton *button = new QPushButton();
            button->setObjectName(QString::number(6*i+j));
            grid->addWidget(button, i+2, j, 1,1);
            buttonVector.push_back(button);
        }
    }
    Controller control(buttonVector, newGame, score);
    // Connects necessary things.
    QObject::connect(quit, SIGNAL(clicked()), &game, SLOT(quit()));
    QObject::connect(newGame, SIGNAL(clicked()), time, SLOT(newGameClicked()));
    QObject::connect(time, SIGNAL(timesUp()), &control, SLOT(newGamePressed()));
    QObject::connect(&control, SIGNAL(stopTimer()), time, SLOT(won()));
    // adds necessary widgets to the grid.
    grid->addWidget(time->label,0, 0);
    grid->addWidget(score,0, 1);
    grid->addWidget(newGame, 0, 4);
    grid->addWidget(quit, 0, 5);
    grid->addWidget(new QLabel(),1,0);

    // Spacer used for arranging widgets on the layout.
    QSpacerItem *spacer = new QSpacerItem(0, 10, QSizePolicy::Expanding, QSizePolicy::Expanding);

    vbox->addLayout(grid);
    vbox->addSpacerItem(spacer);
    widget->setWindowTitle("Card Match Game");
    //Sets the width and height.
    widget->setFixedWidth(640);
    widget->setFixedHeight(240);
    widget->show();

    return game.exec();
}
