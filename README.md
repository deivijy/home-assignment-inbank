## What I changed

### Frontend throws two exceptions:
1. A RenderFlex overflowed by 52 pixels on the bottom.
2. Incorrect use of ParentDataWidget.

    main.dart
   * Line 47 - remove `Expanded()`and replace it with `SingleChildScrollView()`
   * Line 48,49 - remove `child: SizedBox(
                          height: max(minHeight, bodyHeight),
                        )`

   loan_form.dart
   * Line 64 - replace `Expanded` with `SingleChildScrollView()`

`SingleChildScrollView` was added so form content won't exceed the available screen height. Also `Expanded` was removed because it can be use in Flex layouts.

### loan_form.dart
* Line 15 - LoanForm: Parameter 'key' could be a super parameter. `const LoanForm({Key? key}) : super(key: key);` to `const LoanForm({super.key});`
* Line 18 - remove `_` from `LoanFormState` to make it public. `createState()` method is part of the public API
* Line 8, 27 and 47 - Add `const` before MaterialApp - Use 'const' with the constructor to improve performance.
* Lines 109,110 and 148, 149 - `const` should be before Row. Remove it from lines 110 and 149
* Line 136 and 163 - task description has that maximum loan period is 48 months. 60 => 48
* Line 155 - task description has that minimum loan period is 12 months. 6 => 12

### main.dart
* Line 3 - remove unused import
* Line 18 - InBankForm: Parameter 'key' could be a super parameter. `const InBankForm({Key? key}) : super(key: key);` to `const InBankForm({super.key});`
* Line 42 - `.withOpacity()` is deprecated. Use `.withValues(alpha: ...)`

### national_id_field.dart
* Line 13 - NationalIdTextFormField: Parameter 'key' could be a super parameter.

### loan_form_test.dart
* Line 8, 27 and 47 - add `const` before `MaterialApp`

### DecisionEngineConstants.java
* Line 9 - task description has that maximum loan period is 48 months. 60 => 48

### DecisionEngine.java
* Refactored `getCreditModifier` to use hardcoded personal codes.
  The ID codes provided in the task all end in values < 2500, so this logic would incorrectly categorize them as debt, even though the task explicitly assigns them to segments 1–3.
  Hardcoding ID codes, as a result, the test data in `DecisionEngineTest.java` was no longer aligned with the updated logic, so the test inputs were updated to match ID codes.
* Refactored `highestValidLoanAmount` function because there is used only creditModifier * loanPeriod to calculate the highest valid loan amount. In the tasks description credit score is needed for this with the formula: `credit score = ((credit modifier / loan amount) * loan period) / 10`

## What was good
* Both backend and frontend have tests and they pass.
