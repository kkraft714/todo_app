**To-Do/Notes Application Specification**

* **QUICK SPEC**: I want an app where I can quickly view 2 things:
  * What are my priorities?
  * What’s on my schedule?

Driftboard link: [https://v0-no-content-ten-topaz.vercel.app/board/2iQ8zKBa0bMP8XbELjcW](https://v0-no-content-ten-topaz.vercel.app/board/2iQ8zKBa0bMP8XbELjcW)
Initial interface will be a browser app (with a stretch goal of being a mobile app)

### **UI Specification**

The current plan is to start by supporting a browser app, and then later potentially add support for a mobile app.

* We need to display a few different things on the UI:
  * A list of `Note` items (probably on the left panel)
  * A detail view of the currently-selected `Note` item (on the right or maybe middle panel)
  * The child `Note` items of the currently-selected `Note` (probably at the bottom of the detail view)
  * When a child-`Note` is selected the tree view will "Scroll" one level to the right
    * So the original list of `Note` children will be displayed on the left panel
    * And the newly-selected `Note` will be displayed on the right (or middle) detail view panel
  * Note that we might have room for _three_ panels in a browser app but it won't work for mobile
  * We will also need to support other UI functionality (possibly at the top of the UI screen or in a menu):
    * Tree navigation (e.g. breadcrumb display and a Back button)
      * Will need to be able to navigate arbitrary levels of nested lists
      * Use some off the shelf tree/graph navigation code?
    * Various view options (e.g. Schedule, Contact, Category, Top Priorities)
    * A search screen (for constructing queries and displaying results)
    * Add, edit, and delete (and/or archive) `Note` items (with a confirmation screen for deletion)

**Miscellaneous UI Notes**:
* Need some kind of prioritization scheme for ordering items in list view (order added?)
  * Support manual ordering (moving list items up/down)?
  * Display in order the items were added by default?
* Support min/max size view based on how many description lines are displayed for each item? E.g. “min” would just display the titles with no description lines, “max” would display all description lines, with gradations of 1-n description lines in between.

**Item detail view**:

We will need to support detail views for the following `Note` types:
* A generic/default note represented by the `NoteBase` class (just an item name and optional description)
  * Fields : `name`, `description`, `categories`, `tags`, `created` (date)
* A more detailed note represented by the `Note` class (extending `NoteBase`)
  * Fields: `childNotes`, `links`, `completed` (plus inherited `NoteBase` fields)
* An entity (person, business, artist) represented by the `Entity` class (extending `Note`)
  * Fields: `type`, `address` (plus inherited `Note` fields)
* A calendar event represented by the `ScheduleItem` class (extending `Note`)
  * Fields: `date`, `status` (plus inherited `Note` fields)
* A product type represented by the `Product` class (extending `Note`)
  * Fields: `owner`, `type`, `productPrice` (plus inherited `Note` fields) 
* A web link represented by the `Link` class (extending `NoteBase`)
  * Fields: `linkURL`, `broken` (plus inherited `NoteBase` fields)

**Potential fields to add?** 
  * Weight or priority rating (for ordering list items)?  
  * Difficulty/points rating (similar to agile board)?  

**Various other view options**:  
  * Single-node view (for a to-do or note item attached to multiple projects)
    * Need some way to display/list the projects it's attached to
  * Archived notes view? (support archiving vs. deletion?)

### **Application Specification**

* I want an app where I can quickly view 2 things:  
  * What are my priorities?  
  * What’s on my schedule?  
* The application will support creating, editing, and deleting to-do list items and notes.  
  * Should the app support archiving in addition to (or instead of ) deletion?  
  * Archiving is probably the better option so you can view your history  
  * Or make archive the default but also support a delete option?  
* Support various **list item types** (or fields):  
  * Generic/default (just an item name and optional description)  
  * Calendar/Event  
  * Web link (just add as sub-list items?)  
  * Contact (person/business with phone number and address)  
  * Artwork/Media-Item/Product (with optional price field)  
* Support a tagging and categorization scheme for note items (to enable searching)  
  * In the actual app I support String categories (for custom, user-defined categorization?) and predefined enum tags  
* Option to reset a deadline or event date (or mark as completed) on calendar/Shedulable item  
  * Should a sub-item automatically inherit tags from its parent?  
  * Assign a "weight" to item tags (extent to which tag matches/defines the item)?  

### **Code Walkthrough**

* Note class hierarchy  
  NoteElement (Contains name, description, created – classes below extend it)  
  	Note (Contains elements – a list of NoteElement, categories, and tags)  
  		Main class which acts as a “bag” of note element fields  
  	Contact (contains phoneNumber, address1, address2, city, state, postalCode)  
  	EventInfo (contains eventDate, status)  
  	Link (contains linkUrl, broken)  
  	MediaItem (contains creator, type)  
  	Price (contains price)  
* Other classes  
  CategoryTag (interface for category type)  
  	MediaType (enum implements CategoryTag)  
  NoteOrganizer (main class containing all notes, and entry point for the program)  
* Test classes  
  	NoteOrganizerTest (main test class – tests NoteOrganizer)  
  	NoteTest (tests the Note class)  
  	NoteTestHelper (contains static test helper classes)  
  	TestOrganizer (extends NoteOrganizer to add special test-only functionality)

### **Old UI Specification**

This is the original UI specification before I re-organized the code in [PR #7](https://github.com/kkraft714/todo_app/pull/7).

Originally I had defined a `Note` as a "bag of elements" (of type `NoteElement`) implemented as children of the current
`Note`, and somehow the UI would figure out how to display it as one unified `Note`. However my idea for the UI was that
there would be a main "Note panel", displaying the currently-selected Note in the Note tree, with the right panel
displaying links to its child-notes (if any).

Clearly there was some confusion in the original design because the child notes were performing two different functions:
they were both fields of the parent `Note` and also its children, and it would be hard for the UI to distinguish which
was which. In the new design, I have defined specific `Note` classes that contain all the necessary fields, and any
child-notes can be used to provide additional details, information, and context.

* The top level, and UI entry point (on the left panel) will be a list of to-do and note items
* The right panel will be an item (to-do or note) detail view
* **Item detail view**:
    * Name
    * Sub-lists of notes or to-do items (at the bottom?)
    * Done/completed flag
    * **Optional fields**:
        * Description/details
        * Web link(s)
        * Due date (for Calendar item)
        * Price
        * Weight or priority rating (for ordering list items)?
        * Difficulty/points rating (similar to agile board)?
* **Various other view options**:
    * Search screens (for constructing queries and displaying results)
    * Calendar view (for scheduled items / items with due dates)
    * Contact view (for people and businesses)
    * Single-node view (for a to-do or note item attached to multiple projects)
        * Need some way to display/list the projects it's attached to
    * Archived notes view? (support archiving vs. deletion?)
* **Navigation**:
    * Will need to be able to navigate arbitrary levels of nested lists
        * Use some off the shelf tree/graph navigation code?
    * Will need a breadcrumb display (on the top left) for navigation/orientation
* **Functionality**:
    * Do we want to be able to drag-and-drop new fields (e.g. due date, price, links) when creating or modifying a Note?
        * This might require some kind of interpretation layer between the UI and the backend to determine what kind of object to create
        * Or maybe this will be relatively straightforward if a Note item is just a bag of sub-notes (e.g. the due date, price, links fields are sub-notes)?
    * Screens for creating, editing, and deleting (or archiving?) to-do list items and notes
        * Would need a warning/confirmation screen for deleting an item
    * Support manual ordering (moving list items up/down)?
    * Support min/max size view based on how many description lines are displayed for each item? E.g. “min” would just display the titles with no description lines, “max” would display all description lines, with gradations of 1-n description lines in between.

### **Mood Board** (No longer used?)

* [https://www.task-master.dev/](https://www.task-master.dev/) (for creating a simple command-line version?)

