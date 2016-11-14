# Visual Tiles Together

**Visual Tiles Together** turns your mobile phone or tablet into a VJ platform to allow realtime visual performance.

A user can add "Visual Tiles" to the main tile canvas. Each visual tile can have a simple drawn graphic and can have saved transformations. The program will have a process running which can fire events, and each tile can listen for events and start a transformation when an event is fired. The process can run according to a tempo (such as beats per minute) which can be entered or calculated by tapping on the screen. This can allow syncing of events with the beat of music played at the event, if desired.

Another feature allows users at the same event to log in and create a visual tile, allowing them to draw and share a creation of their own for everyone at the event to see. The tile they create will be added to a queue from which the moderator user can select and add to the main visual canvas.


## User Stories

### **Required** functionality:

_Login and Creating/Joining Parties_

* [ ] User can create a new party and own it as the "moderator"
  * [ ] Select grid size of the main presentation canvas ("stage")
* [ ] User can join in on a party
* [ ] Implement options for joining/signing in
  * [ ] No approval, QR code, Single click, code
  * [ ] Sign in using Google sign in api


_Visual Canvas_

* [ ] Device can be the presenter and remain in "Presentation mode," playing the main visual canvas
* [ ] Moderator can edit the visual canvas
  * [ ] Add or Swap a visual tile from the "upcomming" queue
* [ ] Presenting device can connect and clone display to external screens and/or cast using Chromecast


_Creting a Visual Tile_

* [ ] User can click on create tile FAB to start Create Visual Tile mode
  * [ ] User can draw using touch
  * [ ] User can enter text
  * [ ] User can subscribe to visual effects
  * [ ] User can submit tile to "upcomming" queue


_Selecting Visual Tiles_

* [ ] Users can see a list of "Upcomming" Visual Tiles
* [ ] Users can see the list of "Now Playing" Visual Tiles
* [ ] Users can vote on tiles in the "Upcomming" and "Now Playing" tabs
* [ ] Moderator can add or swap out a tile from "Upcomming" to "Now Playing" (i.e., add visual tile to main presenting canvas)
* [ ] Moderator can delete any visual tile


_Visual Effects and Timeline_

* [ ] Moderator can pick a visual effect from a list of available effects
  * [ ] Moderator can set interval of visual effect event to fire
* [ ] Moderator can pick period length (# of beats)
* [ ] Moderator can assign tempo (beats per minute)


_Backend, Models, Persistence, Architecture_

* [x] Research and Design
  * [ ] Models
* [ ] Create Firebase account


**Optional** functionality:

_Visual Effects and Timeline_

* [ ] Show a visual timeline of events
  * [ ] Implement drag/drop editing functionality of events on the timeline


_Visual Tile Templates_

* [ ] Twitter feed template


_Creting Visual Tile_

* [ ] User can customize an effect: user define motion attributes: color(s) & color animation speed (length each color stays on, transition time to the next color, etc), and movement for each axis x y and z.  Movement parameters for each axis are speed, angle limits, and what happens at the end of the cycle (repeat, ping-pong, stop).  We could also animate the size.  After a user defines a shape and possibly its animation, they can send it to the stage.


_Chat_

* [ ] Implement chat functionality so users can chat in a chatroom for this event
* [ ] See a list of users
* [ ] Direct message


## Wireframes

![screenshot](https://github.com/VisualTiles/VisualTilesTogether/blob/master/art/wf_01.jpg)

![screenshot](https://github.com/VisualTiles/VisualTilesTogether/blob/master/art/wf_02.jpg)

![screenshot](https://github.com/VisualTiles/VisualTilesTogether/blob/master/art/wf_03.jpg)

## License

    Copyright 2016 Chris Spack, George Cohn, Javier Arboleda
