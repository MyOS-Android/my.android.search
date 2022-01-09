<a name="top"></a>

Release notes
=============
## [0.2-beta2] 
> based on [Setter v.0.1.11](https://github.com/scubajeff/Setter/releases/tag/0.1.11) - [commit](https://github.com/scubajeff/Setter/commit/11ebab0352e0df086d0a32b1e47f90da36155cc0)

- update Setter base
  - upgrade libraries
  - use single instance launch mode
  - handle Intent.ACTION_SEND
- rename `DirectSearchActivity.kt` to `MainActivity.kt`
- add option to select image from MainActivity
- link to Search preferences from Android System

## [0.2-beta1] - Unreleased
> Based on [Setter v.0.1.10](https://github.com/scubajeff/Setter/releases/tag/0.1.10) - [3c4ec84](https://github.com/scubajeff/Setter/commit/3c4ec84580917f1c60083a4a0e3159e738eb361a)

- update Setter base
  - add dark mode
  - remove all cookies after close
  - other improvements
- added 'search in default browser' option back (removed from setter base)
- better UI
  - add setting button
- clear cache 
- clean up
- confirmed same behavior as v0.1

## [0.1-beta2] - Unreleased

- Add Settings shortcut
- Change App icon

## [0.1-beta1] - 2020-03-11
> Based on [Setter v.0.1.6](https://github.com/scubajeff/Setter/releases/tag/0.1.6)

- Handle Android ACTION_WEB_SEARCH
- User can add a Costume Search Engine
- Add privacy friendly alternatives front-end
  - whoogle, searx, nitter, libredd, teddit, invidious
- App Name to `Search` and packagename to `my.android.search`
- Remove `ContextMenu` & Use `OnLongClickListener`
