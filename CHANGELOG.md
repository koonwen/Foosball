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
