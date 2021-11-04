# Change log

## 21 September 2021 
### Started implementation of menu screens
*Update by: Bernard & Tristan*
1. Created screens for main menu and lobby.

TODO:
1. Transition between activities shows bars temporarily
2. When soft keyboard is shown, it doesn't expand to full width - there's a gap where the bars are shown temporarily.

## 26 September 2021
### Started implementation of basic gameboard layout
*Update by: Eddie & Koon*
1. Created basic layout screen for gameboard using ConstraintLayout with constraints added to foosmen and goal area objects, and groups added to foosmen objects.

TODO:
1. Test layout and ensure that constraints work well.
2. Move each foosmen move together using swipe function via MotionLayout.

## 3 October 2021
### Store text input across activities and started experimenting with Firebase
*Update by: Bernard & Tristan*
1. Stored player name and game code across activities
2. Added connection to firebase and create a firestore document when create game button is pressed
3. Add back button from lobby to main menu

TODO:
1. Generate our own shortened game code and use that to store as the document id
2. Figure out Firestore rules (currently there is no authentication)
3. Figure out how everyone else should handle Firebase credentials

## 6 October 2021
### Change gameboard layout to programmatically implemented
*Update by: Eddie & Koon*
1. Force landscape orientation in manifest file.
2. Replaced gameboard layout with `main.xml`.
3. Added new drawing package with `Gameboard` class of rendering elements.
4. `GameActivity` acts as controller to control positions.
5. Started `models` package but classes are not in use at the moment.

TODO:
1. To clean up Gameboard class and set up Foosman class to generate the foosmen on the layout instead.
2. Plan collision detection implementation for ball with any foosman.

## 8 October 2021
### Implement game engine
*Update by: Eddie & Koon*

## 10 October 2021
### Add networking functionality to create game and join game buttons in menu
*Update by: Bernard and Tristan*
1. Create game button creates a new database entry
2. Join game button checks for existing db entry by game code and adds player id to db entry
3. Implement basic multiplayer functionality (synchronise ball position)

TODO:
1. Handle logic for player1 as the "host" (all updates to the game to be handled through player1 to prevent concurrency issues)
2. Add functionality to pull values from game in database to lobby screen (i.e. player names)
3. Add ping functionality on lobby screen to update player names in real time

### 10 October 2021
*Update by Koon and Eddie*
1. Extracted Gameboard sprite into Foosman class
2. Refactored GameActivity to implement Foosman class

TODO:
1. Continue Refactoring Ball class
2. Refactor GameActivity to use Ball class

## 12 October 2021
*Update by: Tristan*
1. Add functionality to pull player names from db to update playerTextViews in Lobby Activity

### 12 October 2021
*Update by Koon*
1. Modularize Ball class
2. Create interface for BoardItems

### 14 October 2021
*Update by Eddie & Koon*
1. Add control buttons

### 17 October 2021
*Update by Bernard & Tristan*
1. Improve lobby screen - real time update of players and start game button only visible to host

### 27 October 2021
*Update by Bernard & Tristan*
1. Fix multiplayer functionality (synchronise ball position)

### 28 October 2021
*Update by Koon*
1. Further modularization of board objects (Foosman and Background)
2. Added FoosmanTeam class to encapsulate position info to send to the real-time database

### 29 October 2021
*Update by Tristan*
1. Start adding JavaDocs for interfaces, classes and methods 

### 2 November 2021
*Update by Bernard and Tristan*
1. Sync foosman positions with DB
2. Add firebase authentication
3. Add end game screen

### 4 November 2021
*Update by Tristan*
1. Refactor lobby and menu screens to only allow 2 players

### 4 November 2021
*Update by Eddie & Koon*
1. Add goal and scoring functionality