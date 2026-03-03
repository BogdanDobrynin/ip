# Novichok Task Protocol — User Guide

**Novichok** is a simple task manager. From tracking simple to-dos to managing complex event schedules.

---

## Quick Start
---

## Features

### 1. Adding Tasks
Novichok supports three distinct task types to suit different needs.

*   **ToDo:** A simple task with no specific timeframe.
    *   Format: `todo [description]`
    *   Example: `todo Read a book`
*   **Deadline:** A task that needs to be finished by a specific date and time.
    *   Format: `deadline [description] /by [d/M/yyyy HHmm]`
    *   Example: `deadline Return book /by 19/2/2026 1800`
*   **Event:** A task with a specific start and end time.
    *   Format: `event [description] /from [d/M/yyyy HHmm] /to [d/M/yyyy HHmm]`
    *   Example: `event Book Club /from 20/2/2026 1400 /to 20/2/2026 1600`

### 2. Viewing and Managing Tasks
Keep track of your progress and modify your list on the fly.

*   **List All Tasks:** Displays your entire protocol with status icons (`[X]` for done, `[ ]` for pending).
    *   Format: `list`
*   **Mark Tasks as Done:** Mark one or more tasks as completed.
    *   Format: `mark [index1 index2 ...]`
    *   Example: `mark 1 3` (Marks the 1st and 3rd tasks in the list)
*   **Unmark Tasks:** Revert a task to "incomplete" status.
    *   Format: `unmark [index]`
*   **Delete Tasks:** Permanently remove tasks from the protocol.
    *   Format: `delete [index1 index2 ...]`
    *   Example: `delete 2`

### 3. Advanced Filtering
Search your protocol for specific entries using keywords, dates, or types.

*   **Filter by Name:** Find all tasks containing a specific word.
    *   Format: `filter /name [keyword]`
*   **Filter by Date:** Find deadlines or events occurring on a specific day.
    *   Format: `filter /date [d/M/yyyy HHmm]`
*   **Filter by Type:** View only one category of tasks.
    *   Format: `filter /type [todo | deadline | event]`

### 4. System Commands
*   **Help:** Displays the command manual within the application.
    *   Format: `help`
*   **Exit:** Saves your current list to the local disk and terminates the protocol.
    *   Format: `bye`, `exit`, or `quit`

---

## Data Persistence
Novichok automatically saves your data to `data/list.log` every time the list is modified. When you restart the application, your protocol is reloaded automatically.

> **Warning:** Do not manually edit the `list.log` file, as this may lead to data corruption.

---

## Command Summary

| Action | Command Format |
| :--- | :--- |
| **Add ToDo** | `todo [desc]` |
| **Add Deadline** | `deadline [desc] /by [d/M/yyyy HHmm]` |
| **Add Event** | `event [desc] /from [start] /to [end]` |
| **List** | `list` |
| **Mark** | `mark [index]` |
| **Delete** | `delete [index]` |
| **Filter** | `filter /[name/date/type] [value]` |
| **Exit** | `bye` |