Package structure:
    cards -> Card, Deck, enums
    game -> hand checking, engine, table
    players -> basic player (room to add a computer player for ec), position enum

BELOW, put what you are working on currently.
    Tyler - game engine 
    Steven - 

---- Commits ----
1. Never commit directly to main

2. Each person has a dev branch:
   - tyler-dev
   - steven-dev
   -create with:
    git checkout -b tyler-dev
    git push -u origin tyler-dev

3. For every feature:
   git checkout name-dev
   git pull
   git checkout -b feature-name
    git push -u origin feature-name

4. Work → commit → push:
   git add .
   git commit -m "Message"
   git push

5. Open a Pull Request into main and merge after review.

6. After merging:
   git checkout tyler-dev
   git pull origin main

7. Repeat for next feature.
