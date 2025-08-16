# Database

To view the database diagram, open `rk_user.erd.json` while the 
[ERD Editor VS Code Extension](https://marketplace.visualstudio.com/items?itemName=dineug.vuerd-vscode)
is enabled.

The app's local database applies some (but not all) of the principles from
[The Art of Immutable Architecture](https://michaelperry.net/the-art-of-immutable-architecture/).
As a mobile app, we want to keep database bloat to a minimum, so there will be
some minor exceptions to the rules the book describes. For example, the
deletion pattern will function more like a trash bin on a filesystem, where
rows can be hard-deleted later on. This style could also allow for future
extensibility such as a *Rep King* cloud service that stores and syncs data.

## Notes

"Notes" tables such as `exercise_notes` are mutable. Tracking each update could
quickly bloat the database and is prone to user error. It is more important to
have notes be saved easily than it is to have it sync in a
hypothetical future cloud service.

## Automatically Populated Tables

Some tables will be automatically populated, namely `exercises` and
`muscle_groups`. `muscle_groups` is like an enum in practice, as the user is
not allowed to define their own muscle groups for the sake of UI.

### Exercises, Users, and Exercise-Works-Group

Initial data is attributed to the user with id `dev`.

As a biproduct of the table `exercises` being populated, `exercise_works_group`
is also populated for the initial exercises.

### Muscle Groups

There are some disagreements across the internet about what the primary muscle
groups are. Some or more or less granular. Some allow muscles to be in multiple
groups. Some are incomprehensive and leave out important muscles. Anatomy is 
complicated, so there is good reason for different fitness websites and
influencers to simplify things in different ways.

For the purposes of this app, we will use the following set of muscle groups.

- Biceps
- Calves
- Chest
- Core
- Forearms
    - Wrist and grip exercises will be included in this group
- Glutes
- Hamstrings
- Hips
- Lats
- Lower back
- Shoulders
- Quads
- Traps
- Triceps

A general guideline I've heard for a lot of fitness advice is that you should
generally aim to have [10-20 working sets per muscle group per week](https://builtwithscience.com/fitness-tips/how-many-sets-per-muscle-group-per-week/).
While that may not be attainable for all groups, most users will focus on some
groups and neglect others. Additionally, muscles are partitioned into groups
so there is no overlap between groups. This is important for keeping the app
simple from both a developer and user perspective.

This grouping does lose some granularity, but that won't matter
much to the user experience. Neck muscles are also not included, though most
neck exercises are often unsafe and it's probably not a great idea for this
app to push more people to do them.