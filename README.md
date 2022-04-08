# Arcedex
<img width="41" alt="appIcon" src="https://user-images.githubusercontent.com/14002654/159172174-43d57f7e-4002-411a-8b35-cb1b4939bdc4.png">
Arcedex is a fan-made Android companion app to the Pokemon Legends: Arceus (PLA) video game to help players view and keep track of Pokemon research task progression.

## About
[Bulbapedia resource for general information about PLA research tasks](https://bulbapedia.bulbagarden.net/wiki/Research_task_(Legends:_Arceus))

I am learning how to develop Android apps and I was inspired to create Arcedex while playing PLA. 

I mainly wanted an easy way to do 2 things using this app:
1) Quickly find out the research tasks for a Pokemon I was going to add to my team to have a plan for increasing their research level.
2) Find groups of Pokemon with matching research tasks to complete them more efficiently. 
  - Example: You want to add Graveler to your team and will use Rock Slide for one of its tasks. So you can search up research tasks containing "rock-type" and get a list of Pokemon that have the task for being "defeated by Rock-type moves". Then you can work towards completing two birds (Staraptor and Murkrow) with one stone (Graveller). ;D

Those were the main features I wanted, but I also planned and implemented other things for a more complete experience.

## App Screenshots
Example Screenshot 1:

<img src="https://user-images.githubusercontent.com/14002654/162358041-6c4e510b-899e-4901-ad35-3a21188d32fd.jpg" width="250">

Example Screenshot 2:

<img src="https://user-images.githubusercontent.com/14002654/160038866-573b1439-6bd7-4ea4-aae9-6050b27bd24d.jpg" width="250">

Example Screenshot 3:

<img src="https://user-images.githubusercontent.com/14002654/162358137-5e3b0ea3-f928-454c-851e-5a0603982585.jpg" width="250">

Example Screenshot 4:

<img src="https://user-images.githubusercontent.com/14002654/162358313-ee1633f4-0fa2-4d40-b33d-ee458b02fd95.jpg" width="250">

Example Screenshot 5:

<img src="https://user-images.githubusercontent.com/14002654/162358256-d6c220a1-7e96-443c-8139-4f7438d2b02d.jpg" width="250">


## Features
* View and scroll through a list of Pokemon in the game. Each Pokemon is depicted with a summary row displaying research progress for that Pokemon. When a Pokemon has reached research level 10 (minimum 100 research points + 100 bonus points for reaching level 10), a Pokeball will display in their summary row similar to the actual game. A Masterball displays when all research points have been earned for that Pokemon. Examples in screenshot 1.
* View research task list for a Pokemon by tapping on its summary row. (Tapping again hides the list). You can record progress on a research task by tapping the box matching your current progression in that task. You can tap the current progress for a task to clear out all progress for that task.
* Monitor your research rank in the app's top bar and see how many research points you need to rank up.
* Sort the Pokemon list by Hisui (PLA region) numbering, National Pokedex numbering or alphabetical name sort. Screenshot 2 shows the options and the list in National sort.
* Search key words. What you type in will match against a Pokemon name or research task description and case does not matter (so searching "nosepass" would still get you Nosepass). For example, screenshot 3 shows the result of searching "ore deposit" and brings up Pokemon that have the research task for being seen leaping out of ore deposits.
* Supports English and Japanese. Screenshot 4 shows Japanese Dark Mode.
* Tapping a move-type like "FIRE" in Screenshot 1 will do a quick search of that move type to get a list of Pokemon that have a task to be defeated by that move-type as shown in Screenshot 5.

## Project Status
v1.2.0 APK available in releases. 

It adds move-type buttons for damage-dealing moves that can be clicked to quickly get a list of Pokemon that have a task to be defeated by that move-type

Feedback on Japanese translations is welcome as I had to use online translations for some words.

## Android Concepts Used
* Model-View-ViewModel (MVVM) architecture
* Jetpack Compose for implementing UI
* ViewModel to handle UI events and get data from repository
* Room database to keep track of user progress

## Special Thanks
I got various data and image resources to make this app from these sources:
*   [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/Main_Page)
*   [PokeApi Sprites](https://github.com/PokeAPI/sprites)
*   [Serebii](https://www.serebii.net/)

## Contact Info
Feedback can be sent to arcedexdev@gmail.com.


