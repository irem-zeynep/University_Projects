#include "Controller.h"
#include <algorithm>
#include <QMessageBox>

Controller::Controller(std::vector<QPushButton*> &c, QPushButton* &ng, QLabel* &s) :
    cards(c), newGame(ng), scoreLabel(s)
{
    //Delay for showing the cards
    delayTimer = new QTimer(this);

    //Connect buttons to same function
    for(int i = 0; i < 30; i++){
        QObject::connect(cards[i], SIGNAL(released()), this, SLOT(cardPressed()));
    }

    //Connection for new game button
    QObject::connect(newGame, SIGNAL(released()), this, SLOT(newGamePressed()));

    QObject::connect(delayTimer, SIGNAL(timeout()), this, SLOT(delay()));

    //Reset board on a new game
    resetCards();
}
//Resets the cards when new game button is clicked.
void Controller::resetCards() {
    //Shuffle cards randomly
    srand(time(0));
    std::random_shuffle(cardStrings.begin(), cardStrings.end());

    //Flip back all the cards.
    for(QPushButton* b : cards){
        b->setText("?");
    }

    //Reset other variables
    scoreLabel->setText("Score: 0");
    score = 0;
    selectedIndex = -1;
    secondIndex = -1;
    selectedString = "";
    isShowing = false;
    selectionTrue = false;
    if(delayTimer->isActive()) {
        delayTimer->stop();
    }
}
//When a card is pressed during game session this slot is activated.
void Controller::cardPressed(){
    QPushButton *pressedButton = qobject_cast<QPushButton *>(sender());

    //Do nothing if selected button is already matched or picked.
    if(pressedButton->text().compare("?") != 0 || isShowing) {
        return;
    } else {

        //First card selected
        if(selectedIndex == -1) {
            selectedIndex = pressedButton->objectName().toInt();
            selectedString = cardStrings.at(selectedIndex);
            pressedButton->setText(selectedString);
        } else { //Second card selected
            secondIndex = pressedButton->objectName().toInt();
            QString curString = cardStrings.at(secondIndex);

            if(curString.compare(selectedString) == 0) {
                selectionTrue = true;
            } else {
                selectionTrue = false;
            }

            pressedButton->setText(curString);

            //Delay for user to see the cards.
            isShowing = true;
            delayTimer->start(1000);
        }
    }
}
//The delay before cards are flipped back.
void Controller::delay(){
    delayTimer->stop();
    isShowing = false;

    //Set empty or ? for selected cards
    if(selectionTrue){
        cards.at(selectedIndex)->setText("");
        cards.at(secondIndex)->setText("");
        score++;
        scoreLabel->setText("Score: " + QString::number(score));
    } else {
        cards.at(selectedIndex)->setText("?");
        cards.at(secondIndex)->setText("?");
    }

    //Reset variables
    selectedIndex = -1;
    secondIndex = -1;
    selectedString = "";

    //If score reaches max score, the player wins
    if(score == 15) {
        //emit signals to stop timer
        emit stopTimer();
        QMessageBox msgBox;
        msgBox.setText("You Won!");
        msgBox.setWindowTitle("Success");
        //Gives exit and new game options
        QPushButton *newGameButton = msgBox.addButton(tr("New Game"), QMessageBox::ActionRole);
        QPushButton *exitButton = msgBox.addButton(tr("Exit Game"), QMessageBox::ActionRole);
        msgBox.exec();
        if (msgBox.clickedButton() == newGameButton) {
            newGame->click();
        } else if (msgBox.clickedButton() == exitButton) {
            QCoreApplication::quit();
        }
    }
}

//If new game option is selected, resets cards.
void Controller::newGamePressed(){
    resetCards();
}
