v0.2.0-input ======================================================================================
- Added input manager in the main application.
- Added utility directory.
- Added Constants class to centralize constant values.

v0.3.0-tool-repository ============================================================================
- Added ToolRepository.java class.
- Added tool repository fields and logic to repo class.
- Hooked up test command in controller class.

v0.4.0-rental-repository ==========================================================================
- Added RentalRepository.java class.
- Added RentalAgreement.java class.

v0.5.0-help-service ===============================================================================
- Added HelpService.java class.
- Reformatted input loop to relect new help command to give the user a reference point.

v0.6.0-rental-service =============================================================================
- Added RentalService.java class.
- Populated methods to create a rental agreement, retrieve a rental agreement, and retrieve serial numbers for all rental agreements.
- Added ToolService.java class.
- Populated necessary methods in tool service to get rental service functional.
- Tweaked tech design and app diagram to match updated understanding of business requirements.

v0.7.0-tool-service ===============================================================================
- Added method to retrieve entire list of tool information.

v0.8.0-controller =================================================================================
- Restructured and renamed controller to reflect app design intentions.
- Added command parsing for all app commands.
- Added exceptions for bad commands and bad input.

v0.9.0-testing ====================================================================================
- Added test batteries for each functional class.
- Confirmed passing tests.
- Covered a wide swath of happy and sad path testing.

v0.10.0-presentation ==============================================================================
- Added application presentation and instructions in the README.cd.