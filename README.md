\# Intelligent Recipes



An Android app that tells you what you can cook \*\*right now\*\* with the ingredients you already have — and shows you which recipes you're just \*\*1–2 ingredients away\*\* from making.



Built with Java, Android SDK, and Material Design.



\## Why



Most recipe apps make you search by name. This one works the other way: you tell it what's in your kitchen, and it does the thinking.



\## Features



\-  \*\*Add ingredients you have\*\* as removable chips

\-  \*\*"You can cook these now"\*\* — recipes where every ingredient is covered

\-  \*\*"You're close"\*\* — recipes missing only 1–2 ingredients, with the missing item highlighted in red

\-  \*\*Recipe detail screen\*\* — full ingredient list with ✓ (you have it) and ✗ (you don't), plus step-by-step instructions

\-  \*\*Works offline\*\* — recipe data is bundled as JSON; no internet required

\-  \*\*Material Design\*\* — chips, cards, and a clean responsive layout



\## Screenshots



\### Input screen

!\[Input screen with ingredient chips](./screenshots/input.png)



\### Results screen

!\[Results with "cook now" and "1 away" sections](./screenshots/results.png)



\### Recipe detail

!\[Recipe detail with ingredient checkmarks](./screenshots/detail.png)



\## How It Works



Three core pieces:



1\. \*\*`RecipeRepository`\*\* — loads a bundled JSON file (`assets/recipes.json`) into memory once at startup

2\. \*\*`RecipeMatcher`\*\* — compares the user's pantry against each recipe:

&#x20;  - \*\*Full match\*\*: every ingredient is in the pantry

&#x20;  - \*\*Almost match\*\*: exactly 1–2 ingredients are missing

3\. \*\*UI layer\*\* — three screens (input, results, detail) connected via `Intent` extras



\## Tech Stack



\- \*\*Java\*\* + \*\*Android SDK\*\* (minSdk 24, targetSdk 36)

\- \*\*Material Components\*\* — ChipGroup, MaterialCardView

\- \*\*RecyclerView\*\* — for the results grid

\- \*\*Glide\*\* — for loading recipe images

\- \*\*Gson\*\* — for parsing the bundled JSON

\- \*\*Gradle\*\* (Kotlin DSL)



\## Project Structure



\## Screenshots



\### Input screen

!\[Input screen with ingredient chips](./screenshots/input.png)



\### Results screen

!\[Results with "cook now" and "1 away" sections](./screenshots/results.png)



\### Recipe detail

!\[Recipe detail with ingredient checkmarks](./screenshots/detail.png)





