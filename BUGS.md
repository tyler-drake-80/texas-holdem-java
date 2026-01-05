# Known Issues (Texas Hold’em - Swing)

Last updated: 2026-01-05

## P0 — Game-breaking
- [ ] Hand does not end immediately when all but one player folds (should award pot and go to Next Hand).
- [ ] Eliminated players can still appear in rotation / dealer button.
- [ ] In some cases, "Next Hand" option/state is missing after a hand ends.
- [ ] With 2 players, repeated check/call can get stuck and never advances streets.

## P1 — Incorrect results
- [ ] Hand comparison bug: some non-tie hands incorrectly split the pot (e.g., higher pair vs lower pair).

## P2 — UI/UX
- [ ] Check vs Call button logic: both can appear when only one should.
- [ ] Blinds (SB/BB) not clearly indicated compared to dealer button.

## P3 — Packaging / Portability
- [ ] Card images / resources may fail when running from a packaged JAR (classpath vs file path loading).
- [ ] Case sensitivity issues in resource paths on non-Windows systems.
