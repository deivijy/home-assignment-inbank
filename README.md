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

### main.dart
* Line 3 - remove unused import
* Line 18 - InBankForm: Parameter 'key' could be a super parameter. `const InBankForm({Key? key}) : super(key: key);` to `const InBankForm({super.key});`
* Line 42 - `.withOpacity()` is deprecated. Use `.withValues(alpha: ...)`

### national_id_field.dart
* Line 13 - NationalIdTextFormField: Parameter 'key' could be a super parameter.

### loan_form_test.dart
* Line 8, 27 and 47 - add `const` before `MaterialApp`

## What was good
* Both backend and frontend have tests and they pass.
